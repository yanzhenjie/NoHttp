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
package com.yolanda.nohttp.cookie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.db.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Cookie database manager.</p>
 * Created in Dec 18, 2015 7:01:31 PM.
 *
 * @author Yan Zhenjie.
 */
class CookieDiskManager extends DBManager<CookieEntity> {

    private static DBManager<CookieEntity> _Instance;

    private CookieDiskManager() {
        super(new CookieDisk());
    }

    public synchronized static DBManager<CookieEntity> getInstance() {
        if (_Instance == null)
            _Instance = new CookieDiskManager();
        return _Instance;
    }

    /**
     * Add or update by index(name, domain, path).
     *
     * @param cookie cookie entity.
     */
    @Override
    public long replace(CookieEntity cookie) {
        SQLiteDatabase execute = getWriter();
        ContentValues values = new ContentValues();
        values.put(CookieDisk.URI, cookie.getUri());
        values.put(CookieDisk.NAME, cookie.getName());
        values.put(CookieDisk.VALUE, cookie.getValue());
        values.put(CookieDisk.COMMENT, cookie.getComment());
        values.put(CookieDisk.COMMENT_URL, cookie.getCommentURL());
        values.put(CookieDisk.DISCARD, String.valueOf(cookie.isDiscard()));
        values.put(CookieDisk.DOMAIN, cookie.getDomain());
        values.put(CookieDisk.EXPIRY, cookie.getExpiry());
        values.put(CookieDisk.PATH, cookie.getPath());
        values.put(CookieDisk.PORT_LIST, cookie.getPortList());
        values.put(CookieDisk.SECURE, String.valueOf(cookie.isSecure()));
        values.put(CookieDisk.VERSION, cookie.getVersion());
        long id = -1;
        try {
            print(values.toString());
            id = execute.replace(CookieDisk.TABLE_NAME, null, values);
        } catch (Throwable e) {
            Logger.w(e);
        }
        closeWriter(execute);
        return id;
    }

    @Override
    public List<CookieEntity> get(String querySql) {
        SQLiteDatabase execute = getReader();

        List<CookieEntity> cookies = new ArrayList<CookieEntity>();
        Cursor cursor = null;
        try {
            cursor = execute.rawQuery(querySql, null);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                try {
                    CookieEntity cookie = new CookieEntity();
                    int idIndex = cursor.getColumnIndex(CookieDisk.ID);
                    if (idIndex >= 0)
                        cookie.setId(cursor.getInt(idIndex));

                    int uriIndex = cursor.getColumnIndex(CookieDisk.URI);
                    if (uriIndex >= 0)
                        cookie.setUri(cursor.getString(uriIndex));

                    int nameIndex = cursor.getColumnIndex(CookieDisk.NAME);
                    if (nameIndex >= 0)
                        cookie.setName(cursor.getString(nameIndex));

                    int valueIndex = cursor.getColumnIndex(CookieDisk.VALUE);
                    if (valueIndex >= 0)
                        cookie.setValue(cursor.getString(valueIndex));

                    int commentIndex = cursor.getColumnIndex(CookieDisk.COMMENT);
                    if (commentIndex >= 0)
                        cookie.setComment(cursor.getString(commentIndex));

                    int commentUriIndex = cursor.getColumnIndex(CookieDisk.COMMENT_URL);
                    if (commentUriIndex >= 0)
                        cookie.setCommentURL(cursor.getString(commentUriIndex));

                    int discardIndex = cursor.getColumnIndex(CookieDisk.DISCARD);
                    if (discardIndex >= 0)
                        cookie.setDiscard("true".equals(cursor.getString(discardIndex)));

                    int domainIndex = cursor.getColumnIndex(CookieDisk.DOMAIN);
                    if (domainIndex >= 0)
                        cookie.setDomain(cursor.getString(domainIndex));

                    int expiryIndex = cursor.getColumnIndex(CookieDisk.EXPIRY);
                    if (expiryIndex >= 0)
                        cookie.setExpiry(cursor.getLong(expiryIndex));

                    int pathIndex = cursor.getColumnIndex(CookieDisk.PATH);
                    if (pathIndex >= 0)
                        cookie.setPath(cursor.getString(pathIndex));

                    int portListIndex = cursor.getColumnIndex(CookieDisk.PORT_LIST);
                    if (portListIndex >= 0)
                        cookie.setPortList(cursor.getString(portListIndex));

                    int secureIndex = cursor.getColumnIndex(CookieDisk.SECURE);
                    if (secureIndex >= 0)
                        cookie.setSecure("true".equals(cursor.getString(secureIndex)));

                    int versionIndex = cursor.getColumnIndex(CookieDisk.VERSION);
                    if (versionIndex >= 0)
                        cookie.setVersion(cursor.getInt(versionIndex));

                    print(cookie.toString());
                    cookies.add(cookie);
                } catch (Throwable e) {
                    Logger.e(e);
                }
            }
        } catch (Throwable e) {
            Logger.e(e);
        }
        closeReader(execute, cursor);
        return cookies;
    }

    @Override
    protected String getTableName() {
        return CookieDisk.TABLE_NAME;
    }
}