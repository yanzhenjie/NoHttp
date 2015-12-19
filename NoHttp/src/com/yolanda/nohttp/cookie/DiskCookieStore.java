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

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yolanda.nohttp.Logger;

import android.text.TextUtils;

/**
 * </br>
 * Created in Dec 17, 2015 7:20:52 PM
 * 
 * @author YOLANDA;
 */
public enum DiskCookieStore implements CookieStore {

	INSTANCE;

	private Lock lock = null;

	private CookieDiskManager mManager;

	private final static Executor THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

	/**
	 * Construct a persistent cookie store.
	 *
	 * @param context Context to attach cookie store to
	 */
	DiskCookieStore() {
		lock = new ReentrantLock(false);
		mManager = new CookieDiskManager();
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		lock.lock();
		try {
			if (cookie == null) {
				return;
			}
			uri = getEffectiveURI(uri);
			mManager.replace(new NoHttpCookie(uri, cookie));
			trimSize();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		if (uri == null) {
			return Collections.emptyList();
		}
		uri = getEffectiveURI(uri);
		List<HttpCookie> rt = new ArrayList<HttpCookie>();
		deleteExpiryCookies();
		try {
			Selector<CookieEntity> selector = db.selector(CookieEntity.class);
			Where where = new Where();
			String host = uri.getHost();
			if (!TextUtils.isEmpty(host)) {
				where.set("domain", "=", host);
				int lastDot = host.lastIndexOf(".");
				if (lastDot > 1) {
					lastDot = host.lastIndexOf(".", lastDot - 1);
					if (lastDot > 0) {
						String domain = host.substring(lastDot, host.length());
						if (!TextUtils.isEmpty(domain)) {
							where.or("domain", "=", domain);
						}
					}
				}
			}

			String path = uri.getPath();
			if (!TextUtils.isEmpty(path)) {
				Where subWhere = new Where("path", "=", path).or("path", "=", "/").or("path", "=", null);
				int lastSplit = path.lastIndexOf("/");
				while (lastSplit > 0) {
					path = path.substring(0, lastSplit);
					subWhere.or("path", "=", path);
					lastSplit = path.lastIndexOf("/");
				}
				where.and(subWhere.toString());
			}
			where.or("uri", "=", uri.toString());
			List<CookieEntity> cookieEntityList = selector.where(where).findAll();
			if (cookieEntityList != null) {
				for (CookieEntity cookieEntity : cookieEntityList) {
					rt.add(cookieEntity.toHttpCookie());
				}
			}
		} catch (Throwable ex) {
			LogUtil.e(ex.getMessage(), ex);
		}
		return rt;
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

	private void deleteExpiryCookies() {
		StringBuilder deleteWhere = new StringBuilder(CookieDisker.EXPIRY);
		deleteWhere.append("<");
		deleteWhere.append(System.currentTimeMillis());
		mManager.delete(deleteWhere.toString());
	}

	private void trimSize() {
		THREAD_EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	private URI getEffectiveURI(final URI uri) {
		URI effectiveURI = null;
		try {
			effectiveURI = new URI("http", uri.getHost(), uri.getPath(), null, null);
		} catch (URISyntaxException e) {
			effectiveURI = uri;
		}
		return effectiveURI;
	}
}