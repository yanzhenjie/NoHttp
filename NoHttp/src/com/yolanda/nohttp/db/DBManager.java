/**
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
package com.yolanda.nohttp.db;

import java.util.List;

import com.yolanda.nohttp.Logger;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * Created in Jan 10, 2016 8:18:28 PM
 * 
 * @author YOLANDA
 */
public abstract class DBManager<T extends DBId> {

	private SQLiteOpenHelper disker;

	public DBManager(SQLiteOpenHelper disker) {
		this.disker = disker;
	}

	protected final SQLiteDatabase openReader() {
		return disker.getReadableDatabase();
	}

	protected final SQLiteDatabase openWriter() {
		return disker.getWritableDatabase();
	}

	protected final void finish(SQLiteDatabase execute) {
		if (execute != null && execute.isOpen()) {
			execute.close();
		}
	}

	protected final void finish(SQLiteDatabase execute, Cursor cursor) {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		finish(execute);
	}

	public final int count() {
		return count(Field.ID);
	}

	public final int count(String columnName) {
		SQLiteDatabase execute = openReader();
		StringBuilder sql = new StringBuilder("select count(").append(columnName).append(") from ").append(getTableName());
		Cursor cursor = execute.rawQuery(sql.toString(), null);
		int count = 0;
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		finish(execute, cursor);
		return count;
	}

	public final boolean deleteAll() {
		return delete("1=1");
	}

	public final boolean delete(List<T> ts) {
		StringBuilder where = new StringBuilder(Field.ID).append(" IN(");
		for (T t : ts) {
			long id = t.getId();
			if (id > 0) {
				where.append(',');
				where.append(id);
			}
		}
		where.append(')');
		if (',' == where.charAt(6))
			where.deleteCharAt(6);
		return delete(where.toString());
	}

	public final boolean delete(String where) {
		if (TextUtils.isEmpty(where))
			return true;
		SQLiteDatabase execute = openWriter();
		StringBuilder sql = new StringBuilder("delete from ").append(getTableName()).append(" where ").append(where);
		boolean result = true;
		try {
			execute.execSQL(sql.toString());
		} catch (SQLException e) {
			Logger.w(e);
			result = false;
		}
		finish(execute);
		return result;
	}

	public final List<T> getAll() {
		return getAll(Field.ALL);
	}

	public final List<T> getAll(String columnName) {
		return get(columnName, null, null, null, null);
	}

	public final List<T> get(String columnName, String where, String orderBy, String limit, String offset) {
		return get(getSelectSql(columnName, where, orderBy, limit, offset));
	}

	/**
	 * Create query sql
	 */
	private final String getSelectSql(String columnName, String where, String orderBy, String limit, String offset) {
		StringBuilder sql = new StringBuilder("select ").append(columnName).append(" from ").append(getTableName());
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
		return sql.toString();
	}

	public abstract List<T> get(String querySql);

	public abstract long replace(T t);

	protected abstract String getTableName();

}
