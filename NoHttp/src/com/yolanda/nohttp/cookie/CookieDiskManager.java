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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * </br>
 * Created in Dec 18, 2015 7:01:31 PM
 * 
 * @author YOLANDA;
 */
public class CookieDiskManager {

	private CookieDatabase cookieDatabase;
	private SQLiteDatabase execute;

	public CookieDiskManager() {
		cookieDatabase = new CookieDatabase();
	}

	public long add(NoHttpCookie httpCookie) {
		getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CookieDatabase.URI, httpCookie.getUri());
		values.put(CookieDatabase.NAME, httpCookie.getName());
		values.put(CookieDatabase.VALUE, httpCookie.getValue());
		values.put(CookieDatabase.COMMENT, httpCookie.getComment());
		values.put(CookieDatabase.COMMENTURL, CookieDatabase.COMMENTURL);
		values.put(CookieDatabase.DISCARD, httpCookie.isDiscard() ? 1 : 0);
		values.put(CookieDatabase.DOMAIN, httpCookie.getDomain());
		values.put(CookieDatabase.EXPIRY, httpCookie.getExpiry());
		values.put(CookieDatabase.PORTLIST, httpCookie.getPortList());
		values.put(CookieDatabase.SECURE, httpCookie.isSecure() ? 1 : 0);
		values.put(CookieDatabase.VERSION, httpCookie.getVersion());
		long id = execute.insert(CookieDatabase.TABLE_NAME, null, values);
		finish();
		return id;
	}
	
	public long replace(NoHttpCookie httpCookie) {
		getWritableDatabase();
		
		long id = 0;
		finish();
		return id;
	}

	public List<NoHttpCookie> getAll(String where, String orderBy) {
		getReadableDatabase();
		
		List<NoHttpCookie> httpCookies = new ArrayList<NoHttpCookie>();
		StringBuilder sql = new StringBuilder("select * from ");
		sql.append(CookieDatabase.TABLE_NAME);
		if (!TextUtils.isEmpty(where)) {
			sql.append(" where ");
			sql.append(where);
		}
		if (!TextUtils.isEmpty(orderBy)) {
			sql.append(" order by ");
			sql.append(orderBy);
		}
		Cursor cursor = execute.rawQuery(sql.toString(), null);
		if (cursor.moveToNext()) {
			NoHttpCookie httpCookie = new NoHttpCookie();
			httpCookie.setId(cursor.getInt(cursor.getColumnIndex(CookieDatabase.ID)));
			httpCookie.setUri(cursor.getString(cursor.getColumnIndex(CookieDatabase.URI)));
			httpCookie.setName(cursor.getString(cursor.getColumnIndex(CookieDatabase.NAME)));
			httpCookie.setValue(cursor.getString(cursor.getColumnIndex(CookieDatabase.VALUE)));
			httpCookie.setComment(cursor.getString(cursor.getColumnIndex(CookieDatabase.COMMENT)));
			httpCookie.setCommentURL(cursor.getString(cursor.getColumnIndex(CookieDatabase.COMMENTURL)));
			httpCookie.setDiscard(cursor.getInt(cursor.getColumnIndex(CookieDatabase.DISCARD)) == 1);
			httpCookie.setDomain(cursor.getString(cursor.getColumnIndex(CookieDatabase.DOMAIN)));
			httpCookie.setExpiry(cursor.getLong(cursor.getColumnIndex(CookieDatabase.EXPIRY)));
			httpCookie.setPath(cursor.getString(cursor.getColumnIndex(CookieDatabase.PATH)));
			httpCookie.setPortList(cursor.getString(cursor.getColumnIndex(CookieDatabase.PORTLIST)));
			httpCookie.setSecure(cursor.getInt(cursor.getColumnIndex(CookieDatabase.SECURE)) == 1);
			httpCookie.setVersion(cursor.getInt(cursor.getColumnIndex(CookieDatabase.VERSION)));
			httpCookies.add(httpCookie);
		}
		cursor.close();
		finish();
		return httpCookies;
	}

	private void getReadableDatabase() {
		execute = cookieDatabase.getReadableDatabase();
	}

	private void getWritableDatabase() {
		execute = cookieDatabase.getWritableDatabase();
	}

	private void finish() {
		if (execute != null && execute.isOpen()) {
			execute.close();
		}
	}

}
