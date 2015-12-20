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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * </br>
 * Created in Dec 18, 2015 7:01:31 PM
 * 
 * @author YOLANDA;
 */
class CookieDiskManager {
	/**
	 * Instance
	 */
	private static CookieDiskManager _Instance;
	/**
	 * Disk manager
	 */
	private CookieDisker mCookieDisker;
	/**
	 * Disk executor
	 */
	private SQLiteDatabase execute;

	private CookieDiskManager() {
		mCookieDisker = new CookieDisker();
	}

	public static CookieDiskManager getInstance() {
		if (_Instance == null) {
			_Instance = new CookieDiskManager();
		}
		return _Instance;
	}

	/**
	 * Add or update by index(name, domain, path)
	 */
	public long replace(CookieEntity cookies) {
		openWriter();
		ContentValues values = new ContentValues();
		values.put(CookieDisker.URI, cookies.getUri());
		values.put(CookieDisker.NAME, cookies.getName());
		values.put(CookieDisker.VALUE, cookies.getValue());
		values.put(CookieDisker.COMMENT, cookies.getComment());
		values.put(CookieDisker.COMMENTURL, CookieDisker.COMMENTURL);
		values.put(CookieDisker.DISCARD, String.valueOf(cookies.isDiscard()));
		values.put(CookieDisker.DOMAIN, cookies.getDomain());
		values.put(CookieDisker.EXPIRY, cookies.getExpiry());
		values.put(CookieDisker.PORTLIST, cookies.getPortList());
		values.put(CookieDisker.SECURE, String.valueOf(cookies.isSecure()));
		values.put(CookieDisker.VERSION, cookies.getVersion());
		long id = -1;
		try {
			id = execute.replace(CookieDisker.TABLE_NAME, null, values);
		} catch (Throwable e) {
			Logger.e(e);
		}
		finish();
		return id;
	}

	/**
	 * Get Cookie List
	 */
	public List<CookieEntity> get(String columnName, String where, String orderBy, String limit, String offset) {
		openReader();

		List<CookieEntity> cookies = new ArrayList<CookieEntity>();
		StringBuilder sql = new StringBuilder("select ").append(columnName).append(" from");
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
		Cursor cursor = null;
		try {
			cursor = execute.rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
				try {
					CookieEntity httpCookie = new CookieEntity();
					httpCookie.setId(cursor.getInt(cursor.getColumnIndex(CookieDisker.ID)));
					httpCookie.setUri(cursor.getString(cursor.getColumnIndex(CookieDisker.URI)));
					httpCookie.setName(cursor.getString(cursor.getColumnIndex(CookieDisker.NAME)));
					httpCookie.setValue(cursor.getString(cursor.getColumnIndex(CookieDisker.VALUE)));
					httpCookie.setComment(cursor.getString(cursor.getColumnIndex(CookieDisker.COMMENT)));
					httpCookie.setCommentURL(cursor.getString(cursor.getColumnIndex(CookieDisker.COMMENTURL)));
					httpCookie.setDiscard("true".equals(cursor.getString(cursor.getColumnIndex(CookieDisker.DISCARD))));
					httpCookie.setDomain(cursor.getString(cursor.getColumnIndex(CookieDisker.DOMAIN)));
					httpCookie.setExpiry(cursor.getLong(cursor.getColumnIndex(CookieDisker.EXPIRY)));
					httpCookie.setPath(cursor.getString(cursor.getColumnIndex(CookieDisker.PATH)));
					httpCookie.setPortList(cursor.getString(cursor.getColumnIndex(CookieDisker.PORTLIST)));
					httpCookie.setSecure("true".equals(cursor.getString(cursor.getColumnIndex(CookieDisker.SECURE))));
					httpCookie.setVersion(cursor.getInt(cursor.getColumnIndex(CookieDisker.VERSION)));
					cookies.add(httpCookie);
				} catch (Throwable e) {
					Logger.e(e);
				}
			}
		} catch (Throwable e) {
			Logger.e(e);
		}
		finish(cursor);
		return cookies;
	}

	/**
	 * Get all cookie in database
	 */
	public List<CookieEntity> getAll() {
		return getAll("*");
	}

	/**
	 * Get all cookie in database
	 */
	public List<CookieEntity> getAll(String columnName) {
		return get(columnName, null, null, null, null);
	}

	/**
	 * delete data
	 */
	public boolean delete(String where) {
		if (TextUtils.isEmpty(where))
			return true;
		openWriter();
		StringBuilder sql = new StringBuilder("delete from ").append(CookieDisker.TABLE_NAME).append(" where ").append(where);
		boolean result = true;
		try {
			execute.execSQL(sql.toString());
		} catch (SQLException e) {
			Logger.e(e);
			result = false;
		}
		finish();
		return result;
	}

	/**
	 * To delete multiple cookies
	 */
	public boolean delete(List<CookieEntity> cookies) {
		StringBuilder where = new StringBuilder(CookieDisker.ID).append(" in(");
		for (CookieEntity cookie : cookies) {
			long id = cookie.getId();
			if (id > 0) {
				where.append(',');
				where.append(id);
			}
		}
		where.append(')');
		int charIndex = CookieDisker.ID.length() + 3;
		if (',' == where.charAt(charIndex)) {
			where.deleteCharAt(charIndex);
		}
		return delete(where.toString());
	}

	/**
	 * delete all data
	 */
	public boolean deleteAll() {
		return delete("1=1");
	}

	/**
	 * Get count all
	 */
	public int count() {
		return count(CookieDisker.ID);
	}

	/**
	 * Get count
	 */
	public int count(String columnName) {
		openReader();
		StringBuilder sql = new StringBuilder("select count(").append(columnName).append(") from ").append(CookieDisker.TABLE_NAME);
		Cursor cursor = execute.rawQuery(sql.toString(), null);
		int count = 0;
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		finish();
		return count;
	}

	private void openReader() {
		execute = mCookieDisker.getReadableDatabase();
	}

	private void openWriter() {
		execute = mCookieDisker.getWritableDatabase();
	}

	private void finish() {
		if (execute != null && execute.isOpen()) {
			execute.close();
		}
	}

	private void finish(Cursor cursor) {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		finish();
	}

}
