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
	public long replace(CookieEntity cookie) {
		openWriter();
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
			e.printStackTrace();
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
		StringBuilder sql = new StringBuilder("select ").append(columnName).append(" from ");
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
		finish(cursor);
		return cookies;
	}

	/**
	 * Get all cookie in database
	 */
	public List<CookieEntity> getAll() {
		return getAll(CookieDisker.ALL);
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
			e.printStackTrace();
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