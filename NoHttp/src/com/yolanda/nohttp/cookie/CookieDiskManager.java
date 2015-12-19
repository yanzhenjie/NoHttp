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

	private CookieDisker mCookieDisker;
	private SQLiteDatabase execute;

	public CookieDiskManager() {
		mCookieDisker = new CookieDisker();
	}

	public long add(NoHttpCookie httpCookie) {
		getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CookieDisker.URI, httpCookie.getUri());
		values.put(CookieDisker.NAME, httpCookie.getName());
		values.put(CookieDisker.VALUE, httpCookie.getValue());
		values.put(CookieDisker.COMMENT, httpCookie.getComment());
		values.put(CookieDisker.COMMENTURL, CookieDisker.COMMENTURL);
		values.put(CookieDisker.DISCARD, httpCookie.isDiscard() ? 1 : 0);
		values.put(CookieDisker.DOMAIN, httpCookie.getDomain());
		values.put(CookieDisker.EXPIRY, httpCookie.getExpiry());
		values.put(CookieDisker.PORTLIST, httpCookie.getPortList());
		values.put(CookieDisker.SECURE, httpCookie.isSecure() ? 1 : 0);
		values.put(CookieDisker.VERSION, httpCookie.getVersion());
		long id = execute.insert(CookieDisker.TABLE_NAME, null, values);
		finish();
		return id;
	}

	public long replace(NoHttpCookie httpCookie) {
		getWritableDatabase();

		long id = 0;
		finish();
		return id;
	}

	public List<NoHttpCookie> get(String where, String orderBy, String limit, String offset) {
		getReadableDatabase();

		List<NoHttpCookie> httpCookies = new ArrayList<NoHttpCookie>();
		StringBuilder sql = new StringBuilder("select * from");
		sql.append(CookieDisker.TABLE_NAME);
		if (!TextUtils.isEmpty(where)) {
			sql.append(" where ");
			sql.append(where);
		}
		if (!TextUtils.isEmpty(orderBy)) {
			sql.append(" order by ");
			sql.append(orderBy);
		}
		if (!TextUtils.isEmpty(limit)) {
			sql.append(" limit ");
			sql.append(limit);
		}
		if (!TextUtils.isEmpty(limit) && !TextUtils.isEmpty(offset)) {
			sql.append(" offset ");
			sql.append(offset);
		}
		Cursor cursor = execute.rawQuery(sql.toString(), null);
		if (cursor.moveToNext()) {
			NoHttpCookie httpCookie = new NoHttpCookie();
			httpCookie.setId(cursor.getInt(cursor.getColumnIndex(CookieDisker.ID)));
			httpCookie.setUri(cursor.getString(cursor.getColumnIndex(CookieDisker.URI)));
			httpCookie.setName(cursor.getString(cursor.getColumnIndex(CookieDisker.NAME)));
			httpCookie.setValue(cursor.getString(cursor.getColumnIndex(CookieDisker.VALUE)));
			httpCookie.setComment(cursor.getString(cursor.getColumnIndex(CookieDisker.COMMENT)));
			httpCookie.setCommentURL(cursor.getString(cursor.getColumnIndex(CookieDisker.COMMENTURL)));
			httpCookie.setDiscard(cursor.getInt(cursor.getColumnIndex(CookieDisker.DISCARD)) == 1);
			httpCookie.setDomain(cursor.getString(cursor.getColumnIndex(CookieDisker.DOMAIN)));
			httpCookie.setExpiry(cursor.getLong(cursor.getColumnIndex(CookieDisker.EXPIRY)));
			httpCookie.setPath(cursor.getString(cursor.getColumnIndex(CookieDisker.PATH)));
			httpCookie.setPortList(cursor.getString(cursor.getColumnIndex(CookieDisker.PORTLIST)));
			httpCookie.setSecure(cursor.getInt(cursor.getColumnIndex(CookieDisker.SECURE)) == 1);
			httpCookie.setVersion(cursor.getInt(cursor.getColumnIndex(CookieDisker.VERSION)));
			httpCookies.add(httpCookie);
		}
		cursor.close();
		finish();
		return httpCookies;
	}

	public List<NoHttpCookie> getAll() {
		return get(null, null, null, null);
	}

	public void delete(String where) {
		if (TextUtils.isEmpty(where))
			throw new NullPointerException("where is null");
		StringBuilder sql = new StringBuilder("delete from ");
		sql.append(CookieDisker.TABLE_NAME);
		sql.append(" where ");
		sql.append(where);
		execute.execSQL(sql.toString());
	}

	public void deleteAll() {
		delete("1=1");
	}

	private void getReadableDatabase() {
		execute = mCookieDisker.getReadableDatabase();
	}

	private void getWritableDatabase() {
		execute = mCookieDisker.getWritableDatabase();
	}

	private void finish() {
		if (execute != null && execute.isOpen()) {
			execute.close();
		}
	}

}
