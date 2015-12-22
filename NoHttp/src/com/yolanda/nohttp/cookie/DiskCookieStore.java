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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yolanda.nohttp.cookie.Where.Options;

import android.text.TextUtils;

/**
 * </br>
 * Created in Dec 17, 2015 7:20:52 PM
 * 
 * @author YOLANDA;
 */
public enum DiskCookieStore implements CookieStore {

	INSTANCE;

	/**
	 * Cookie max count in disk
	 */
	private final static int MAX_COOKIE_SIZE = 8888;

	private CookieDiskManager mManager;

	DiskCookieStore() {
		mManager = CookieDiskManager.getInstance();
	}

	@Override
	public void add(URI uri, HttpCookie cookie) {
		if (cookie == null) {
			return;
		}
		uri = getEffectiveURI(uri);
		mManager.replace(new CookieEntity(uri, cookie));
		trimSize();
	}

	@Override
	public List<HttpCookie> get(URI uri) {
		if (uri == null)
			return Collections.emptyList();
		uri = getEffectiveURI(uri);
		deleteExpiryCookies();
		Where where = new Where();
		String host = uri.getHost();
		if (!TextUtils.isEmpty(host)) {
			Where subWhere = new Where(CookieDisker.DOMAIN, Options.EQUAL, host);
			int lastDot = host.lastIndexOf(".");
			if (lastDot > 1) {
				lastDot = host.lastIndexOf(".", lastDot - 1);
				if (lastDot > 0) {
					String domain = host.substring(lastDot, host.length());
					if (!TextUtils.isEmpty(domain))
						subWhere.or(CookieDisker.DOMAIN, Options.EQUAL, domain).bracket();
				}
			}
			where.set(subWhere.get());
		}

		String path = uri.getPath();
		if (!TextUtils.isEmpty(path)) {
			Where subWhere = new Where(CookieDisker.PATH, Options.EQUAL, path).or(CookieDisker.PATH, Options.EQUAL, "/").orNull(CookieDisker.PATH);
			int lastSplit = path.lastIndexOf("/");
			while (lastSplit > 0) {
				path = path.substring(0, lastSplit);
				subWhere.or(CookieDisker.PATH, Options.EQUAL, path);
				lastSplit = path.lastIndexOf("/");
			}
			subWhere.bracket();
			where.and(subWhere);
		}

		where.or(CookieDisker.URI, Options.EQUAL, uri.toString());

		List<CookieEntity> cookieList = mManager.get(CookieDisker.ALL, where.toString(), null, null, null);
		List<HttpCookie> returnedCookies = new ArrayList<HttpCookie>();
		for (CookieEntity cookieEntity : cookieList)
			returnedCookies.add(cookieEntity.toHttpCookie());
		return returnedCookies;
	}

	@Override
	public List<HttpCookie> getCookies() {
		List<HttpCookie> rt = new ArrayList<HttpCookie>();
		deleteExpiryCookies();
		List<CookieEntity> cookieEntityList = mManager.getAll();
		for (CookieEntity cookieEntity : cookieEntityList)
			rt.add(cookieEntity.toHttpCookie());
		return rt;
	}

	@Override
	public List<URI> getURIs() {
		List<URI> uris = new ArrayList<URI>();
		List<CookieEntity> uriList = mManager.getAll(CookieDisker.URI);
		for (CookieEntity cookie : uriList) {
			String uri = cookie.getUri();
			if (!TextUtils.isEmpty(uri))
				try {
					uris.add(new URI(uri));
				} catch (Throwable e) {
					e.printStackTrace();
					StringBuilder where = new StringBuilder(CookieDisker.URI).append('=').append(uri);
					mManager.delete(where.toString());
				}
		}
		return uris;
	}

	@Override
	public boolean remove(URI uri, HttpCookie httpCookie) {
		if (httpCookie == null)
			return true;

		CookieEntity cookie = new CookieEntity(uri, httpCookie);
		Where where = new Where(CookieDisker.NAME, Options.EQUAL, cookie.getName());

		String domain = cookie.getDomain();
		if (!TextUtils.isEmpty(domain))
			where.and(CookieDisker.DOMAIN, Options.EQUAL, domain);

		String path = cookie.getPath();
		if (!TextUtils.isEmpty(path))
			where.and(CookieDisker.PATH, Options.EQUAL, path);

		return mManager.delete(where.toString());
	}

	@Override
	public boolean removeAll() {
		return mManager.deleteAll();
	}

	/**
	 * Delete all expired cookies
	 */
	private void deleteExpiryCookies() {
		Where where = new Where(CookieDisker.EXPIRY, Options.THAN_SMALL, System.currentTimeMillis());
		mManager.delete(where.get());
	}

	/**
	 * Trim the Cookie list
	 */
	private void trimSize() {
		int count = mManager.count();
		if (count > MAX_COOKIE_SIZE + 10) {
			List<CookieEntity> rmList = mManager.get(CookieDisker.ALL, null, null, Integer.toString(count - MAX_COOKIE_SIZE), null);
			if (rmList != null)
				mManager.delete(rmList);
		}
	}

	/**
	 * Get effective URI
	 */
	private URI getEffectiveURI(final URI uri) {
		URI effectiveURI = null;
		try {
			effectiveURI = new URI(uri.getScheme(), uri.getHost(), uri.getPath(), null, null);
		} catch (URISyntaxException e) {
			effectiveURI = uri;
		}
		return effectiveURI;
	}
}