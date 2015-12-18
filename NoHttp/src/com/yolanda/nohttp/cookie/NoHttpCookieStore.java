/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.cookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yolanda.nohttp.Logger;

import android.content.Context;
import android.text.TextUtils;

/**
 * </br>
 * Created in Dec 17, 2015 7:20:52 PM
 * 
 * @author YOLANDA;
 */
class NoHttpCookieStore implements CookieStore {

	private Lock lock = null;
	
	private CookieDiskManager mCookieManager;

	/**
	 * Construct a persistent cookie store.
	 *
	 * @param context Context to attach cookie store to
	 */
	public NoHttpCookieStore(Context context) {
		lock = new ReentrantLock(false);
		mCookieManager = new CookieDiskManager();
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		lock.lock();
		try {
			// host作为key, 保存host下所有cookie
			String host = uri.getHost();
			if (!cookies.containsKey(host)) {
				cookies.put(host, new HashMap<String, HttpCookie>());
			}
			// 根据cookie生成key, 不同的cookie不同的key, 保存到不同host对应的map中
			String token = getCookieToken(uri, cookie);
			if (cookie.hasExpired())
				cookies.get(host).remove(token);
			else
				cookies.get(host).put(token, cookie);

			addToDisk(host, tokenSetString(host));// 更新host下所有cookie对应key
			if (cookie.getMaxAge() > 0) {
				addToDisk(getKeyByToken(token), encodeCookie(new NoHttpCookie(cookie)));
			} else {
				removeFromDisk(getKeyByToken(token));
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
		lock.lock();
		try {
			String host = uri.getHost();
			if (cookies.containsKey(host))
				ret.addAll(cookies.get(host).values());
		} finally {
			lock.unlock();
		}
		return ret;
	}

	@Override
	public List<HttpCookie> getCookies() {
		ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
		Collection<HttpCookie> removeCookieMap = new ArrayList<HttpCookie>();
		for (String host : cookies.keySet()) {
			Collection<HttpCookie> cookieMap = cookies.get(host).values();
			for (HttpCookie httpCookie : cookieMap) {
				if (httpCookie.hasExpired()) {
					removeCookieMap.add(httpCookie);
				} else {
					ret.add(httpCookie);
				}
			}
		}
		removeCookieMap.clear();
		return ret;
	}

	@Override
	public boolean remove(URI uri, HttpCookie cookie) {
		String token = getCookieToken(uri, cookie);
		String host = uri.getHost();
		if (cookies.containsKey(host) && cookies.get(host).containsKey(token)) {
			cookies.get(host).remove(token);
			removeFromDisk(getKeyByToken(token));
			addToDisk(host, tokenSetString(host));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeAll() {
		cookies.clear();
		clearFromDisk();
		return true;
	}

	@Override
	public List<URI> getURIs() {
		ArrayList<URI> ret = new ArrayList<URI>();
		for (String key : cookies.keySet())
			try {
				ret.add(new URI(key));
			} catch (URISyntaxException e) {
				Logger.w(e);
			}

		return ret;
	}

	protected String getCookieToken(URI uri, HttpCookie cookie) {
		return cookie.getName() + cookie.getDomain();
	}

	/**
	 * Serializes Cookie object into String
	 *
	 * @param cookie cookie to be encoded, can be null
	 * @return cookie encoded as String
	 */
	protected String encodeCookie(NoHttpCookie cookie) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(os);
			outputStream.writeObject(cookie);
		} catch (IOException e) {
			Logger.e(e, "IOException in encodeCookie");
			return null;
		}

		return byteArrayToHexString(os.toByteArray());
	}

	/**
	 * Returns cookie decoded from cookie string
	 *
	 * @param cookieString string of cookie as returned from http request
	 * @return decoded cookie or null if exception occured
	 */
	protected HttpCookie decodeCookie(String cookieString) {
		byte[] bytes = hexStringToByteArray(cookieString);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		HttpCookie cookie = null;
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			cookie = ((NoHttpCookie) objectInputStream.readObject()).getCookie();
		} catch (IOException e) {
			Logger.e(e, "IOException in decodeCookie");
		} catch (ClassNotFoundException e) {
			Logger.e(e, "ClassNotFoundException in decodeCookie");
		}
		return cookie;
	}

	/**
	 * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
	 * large Base64 libraries. Can be overridden if you like!
	 *
	 * @param bytes byte array to be converted
	 * @return string containing hex values
	 */
	protected String byteArrayToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte element : bytes) {
			int v = element & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase(Locale.US);
	}

	/**
	 * Converts hex values from strings to byte arra
	 *
	 * @param hexString string of hex-encoded values
	 * @return decoded byte array
	 */
	protected byte[] hexStringToByteArray(String hexString) {
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

	private String getKeyByToken(String name) {
		StringBuilder builder = new StringBuilder(COOKIE_NAME_PREFIX);
		return builder.append(name).toString();
	}

	private String tokenSetString(String host) {
		return TextUtils.join(";", cookies.get(host).keySet());
	}

	private void addToDisk(String key, String value) {
		if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value))
			cookiePrefs.edit().putString(key, value).commit();
	}

	private String getFromDisk(String key) {
		return cookiePrefs.getString(key, "");
	}

	private Map<String, ?> getAllFromDisk() {
		return cookiePrefs.getAll();
	}

	private void removeFromDisk(String key) {
		cookiePrefs.edit().remove(key).commit();
	}

	private void clearFromDisk() {
		cookiePrefs.edit().clear().commit();
	}
}