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

import java.util.ArrayList;
import java.util.List;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.db.DBManager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * </br>
 * Created in Dec 18, 2015 7:01:31 PM
 * 
 * @author YOLANDA;
 */
class CookieDiskManager extends DBManager<CookieEntity> {
	/**
	 * Instance
	 */
	private static DBManager<CookieEntity> _Instance;

	private CookieDiskManager() {
		super(new CookieDisker());
	}

	public synchronized static DBManager<CookieEntity> getInstance() {
		if (_Instance == null) {
			_Instance = new CookieDiskManager();
		}
		return _Instance;
	}

	/**
	 * Add or update by index(name, domain, path)
	 */
	@Override
	public long replace(CookieEntity cookie) {
		SQLiteDatabase execute = openWriter();
		ContentValues values = new ContentValues();
		values.put(CookieDisker.URI, cookie.getUri());
		values.put(CookieDisker.NAME, cookie.getName());
		values.put(CookieDisker.VALUE, cookie.getValue());
		values.put(CookieDisker.COMMENT, cookie.getComment());
		values.put(CookieDisker.COMMENTURL, cookie.getCommentURL());
		values.put(CookieDisker.DISCARD, String.valueOf(cookie.isDiscard()));
		values.put(CookieDisker.DOMAIN, cookie.getDomain());
		values.put(CookieDisker.EXPIRY, cookie.getExpiry());
		values.put(CookieDisker.PATH, cookie.getPath());
		values.put(CookieDisker.PORTLIST, cookie.getPortList());
		values.put(CookieDisker.SECURE, String.valueOf(cookie.isSecure()));
		values.put(CookieDisker.VERSION, cookie.getVersion());
		long id = -1;
		try {
			id = execute.replace(CookieDisker.TABLE_NAME, null, values);
		} catch (Throwable e) {
			Logger.w(e);
		}
		finish(execute);
		return id;
	}

	@Override
	public List<CookieEntity> get(String querySql) {
		SQLiteDatabase execute = openReader();

		List<CookieEntity> cookies = new ArrayList<CookieEntity>();
		Cursor cursor = null;
		try {
			cursor = execute.rawQuery(querySql, null);
			while (!cursor.isClosed() && cursor.moveToNext()) {
				try {
					CookieEntity cookie = new CookieEntity();
					int idIndex = cursor.getColumnIndex(CookieDisker.ID);
					if (idIndex >= 0)
						cookie.setId(cursor.getInt(idIndex));

					int uriIndex = cursor.getColumnIndex(CookieDisker.URI);
					if (uriIndex >= 0)
						cookie.setUri(cursor.getString(uriIndex));

					int nameIndex = cursor.getColumnIndex(CookieDisker.NAME);
					if (nameIndex >= 0)
						cookie.setName(cursor.getString(nameIndex));

					int valueIndex = cursor.getColumnIndex(CookieDisker.VALUE);
					if (valueIndex >= 0)
						cookie.setValue(cursor.getString(valueIndex));

					int commentIndex = cursor.getColumnIndex(CookieDisker.COMMENT);
					if (commentIndex >= 0)
						cookie.setComment(cursor.getString(commentIndex));

					int commentUriIndex = cursor.getColumnIndex(CookieDisker.COMMENTURL);
					if (commentUriIndex >= 0)
						cookie.setCommentURL(cursor.getString(commentUriIndex));

					int discardIndex = cursor.getColumnIndex(CookieDisker.DISCARD);
					if (discardIndex >= 0)
						cookie.setDiscard("true".equals(cursor.getString(discardIndex)));

					int domainIndex = cursor.getColumnIndex(CookieDisker.DOMAIN);
					if (domainIndex >= 0)
						cookie.setDomain(cursor.getString(domainIndex));

					int expiryIndex = cursor.getColumnIndex(CookieDisker.EXPIRY);
					if (expiryIndex >= 0)
						cookie.setExpiry(cursor.getLong(expiryIndex));

					int pathIndex = cursor.getColumnIndex(CookieDisker.PATH);
					if (pathIndex >= 0)
						cookie.setPath(cursor.getString(pathIndex));

					int portlistIndex = cursor.getColumnIndex(CookieDisker.PORTLIST);
					if (portlistIndex >= 0)
						cookie.setPortList(cursor.getString(portlistIndex));

					int secureIndex = cursor.getColumnIndex(CookieDisker.SECURE);
					if (secureIndex >= 0)
						cookie.setSecure("true".equals(cursor.getString(secureIndex)));

					int versionIndex = cursor.getColumnIndex(CookieDisker.VERSION);
					if (versionIndex >= 0)
						cookie.setVersion(cursor.getInt(versionIndex));

					cookies.add(cookie);
				} catch (Throwable e) {
					Logger.w(e);
				}
			}
		} catch (Throwable e) {
			Logger.w(e);
		}
		finish(execute, cursor);
		return cookies;
	}

	@Override
	protected String getTableName() {
		return CookieDisker.TABLE_NAME;
	}
}