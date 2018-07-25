/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.nohttp.cookie;

import android.content.Context;
import android.text.TextUtils;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.db.BaseDao;
import com.yanzhenjie.nohttp.db.Where;
import com.yanzhenjie.nohttp.db.Where.Options;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created in Dec 17, 2015 7:20:52 PM.
 *
 * @author Yan Zhenjie.
 */
public class DBCookieStore
  extends BasicStore<DBCookieStore> {

    private final static int MAX_COOKIE_SIZE = 8888;

    private Context mContext;
    private Lock mLock;
    private BaseDao<CookieEntity> mCookieEntityDao;
    private CookieStoreListener mCookieStoreListener;

    /**
     * @param context {@link Context}.
     */
    public DBCookieStore(Context context) {
        this.mContext = context;
        this.mLock = new ReentrantLock();
    }

    private void checkInitialization() {
        mLock.lock();

        if (isEnable() && mCookieEntityDao == null) {
            mCookieEntityDao = new CookieEntityDao(mContext);
            // Delete temp cookie.
            Where where = new Where(CookieSQLHelper.EXPIRY, Options.EQUAL, -1L);
            mCookieEntityDao.delete(where.get());
        }

        mLock.unlock();
    }

    /**
     * The callback when adding and deleting cookies.
     *
     * @param cookieStoreListener {@link CookieStoreListener}.
     *
     * @return {@link CookieStore}.
     */
    public CookieStore setCookieStoreListener(CookieStoreListener cookieStoreListener) {
        this.mCookieStoreListener = cookieStoreListener;
        return this;
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        checkInitialization();

        mLock.lock();
        try {
            if (isEnable() && uri != null && cookie != null) {
                uri = getEffectiveURI(uri);
                if (mCookieStoreListener != null) mCookieStoreListener.onSaveCookie(uri, cookie);
                mCookieEntityDao.replace(new CookieEntity(uri, cookie));
                trimSize();
            }
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        checkInitialization();

        mLock.lock();
        try {
            if (uri == null || !isEnable()) return Collections.emptyList();

            uri = getEffectiveURI(uri);
            Where where = new Where();

            String host = uri.getHost();
            if (!TextUtils.isEmpty(host)) {
                Where subWhere =
                  new Where(CookieSQLHelper.DOMAIN, Options.EQUAL, host).or(CookieSQLHelper.DOMAIN,
                                                                            Options.EQUAL, "." + host);

                int firstDot = host.indexOf(".");
                int lastDot = host.lastIndexOf(".");
                if (firstDot > 0 && lastDot > firstDot) {
                    String domain = host.substring(firstDot, host.length());
                    if (!TextUtils.isEmpty(domain)) {
                        subWhere.or(CookieSQLHelper.DOMAIN, Options.EQUAL, domain);
                    }
                }
                where.set(subWhere.get());
            }

            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                Where subWhere =
                  new Where(CookieSQLHelper.PATH, Options.EQUAL, path).or(CookieSQLHelper.PATH, Options.EQUAL,
                                                                          "/").orNull(CookieSQLHelper.PATH);
                int lastSplit = path.lastIndexOf("/");
                while (lastSplit > 0) {
                    path = path.substring(0, lastSplit);
                    subWhere.or(CookieSQLHelper.PATH, Options.EQUAL, path);
                    lastSplit = path.lastIndexOf("/");
                }
                subWhere.bracket();
                where.and(subWhere);
            }

            where.or(CookieSQLHelper.URI, Options.EQUAL, uri.toString());

            List<CookieEntity> cookieList = mCookieEntityDao.getList(where.get(), null, null, null);
            List<HttpCookie> returnedCookies = new ArrayList<>();
            for (CookieEntity cookieEntity : cookieList) {
                if (!cookieEntity.isExpired()) returnedCookies.add(cookieEntity.toHttpCookie());
            }
            return returnedCookies;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public List<HttpCookie> getCookies() {
        checkInitialization();

        mLock.lock();
        try {
            if (!isEnable()) return Collections.emptyList();
            List<HttpCookie> rt = new ArrayList<>();
            List<CookieEntity> cookieEntityList = mCookieEntityDao.getAll();
            for (CookieEntity cookieEntity : cookieEntityList) {
                if (!cookieEntity.isExpired()) rt.add(cookieEntity.toHttpCookie());
            }
            return rt;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public List<URI> getURIs() {
        checkInitialization();

        mLock.lock();
        try {
            if (!isEnable()) return Collections.emptyList();

            List<URI> uris = new ArrayList<>();
            List<CookieEntity> uriList = mCookieEntityDao.getAll();
            for (CookieEntity cookie : uriList) {
                String uri = cookie.getUri();
                if (!TextUtils.isEmpty(uri)) try {
                    uris.add(new URI(uri));
                } catch (Throwable e) {
                    Logger.w(e);
                    mCookieEntityDao.delete(CookieSQLHelper.URI + '=' + uri);
                }
            }
            return uris;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        checkInitialization();

        mLock.lock();
        try {
            if (httpCookie == null || !isEnable()) return true;
            if (mCookieStoreListener != null) mCookieStoreListener.onRemoveCookie(uri, httpCookie);
            Where where = new Where(CookieSQLHelper.NAME, Options.EQUAL, httpCookie.getName());

            String domain = httpCookie.getDomain();
            if (!TextUtils.isEmpty(domain)) where.and(CookieSQLHelper.DOMAIN, Options.EQUAL, domain);

            String path = httpCookie.getPath();
            if (!TextUtils.isEmpty(path)) {
                if (path.length() > 1 && path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                where.and(CookieSQLHelper.PATH, Options.EQUAL, path);
            }
            return mCookieEntityDao.delete(where.toString());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public boolean removeAll() {
        checkInitialization();

        mLock.lock();
        try {
            if (!isEnable()) return true;
            return mCookieEntityDao.deleteAll();
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Trim the Cookie list.
     */
    private void trimSize() {
        int count = mCookieEntityDao.count();
        if (count > MAX_COOKIE_SIZE + 10) {
            List<CookieEntity> rmList =
              mCookieEntityDao.getList(null, null, Integer.toString(count - MAX_COOKIE_SIZE), null);
            if (rmList != null) mCookieEntityDao.delete(rmList);
        }
    }

    /**
     * Get effective URI.
     *
     * @param uri cookie corresponding uri.
     */
    private URI getEffectiveURI(final URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI("http", uri.getHost(), uri.getPath(), null, null);
        } catch (URISyntaxException e) {
            effectiveURI = uri;
        }
        return effectiveURI;
    }

    public interface CookieStoreListener {

        /**
         * When saving a Cookie callback.
         *
         * @param uri cookie corresponding uri.
         * @param cookie {@link HttpCookie}.
         */
        void onSaveCookie(URI uri, HttpCookie cookie);

        /**
         * The callback when deleting cookies.
         *
         * @param uri cookie corresponding uri.
         * @param cookie {@link HttpCookie}.
         */
        void onRemoveCookie(URI uri, HttpCookie cookie);

    }
}