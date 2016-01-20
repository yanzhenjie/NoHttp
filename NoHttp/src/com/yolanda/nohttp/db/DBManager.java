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
import android.util.Log;

/**
 * Database management generic class, has realized the basic functions, inheritance of the subclass only need to implement {@link #replace(DBId)}, {@link #get(String)} and
 * {@link #getTableName(String)}
 * </br>
 * Created in Jan 10, 2016 8:18:28 PM
 * 
 * @author YOLANDA
 */
public abstract class DBManager<T extends DBId> {

	private static final boolean DEBUG = false;
	/**
	 * A helper class to manage database creation and version management.
	 */
	private SQLiteOpenHelper disker;

	public DBManager(SQLiteOpenHelper disker) {
		this.disker = disker;
	}

	/**
	 * Open the database when the read data
	 */
	protected final SQLiteDatabase openReader() {
		return disker.getReadableDatabase();
	}

	/**
	 * Open the database when the write data
	 */
	protected final SQLiteDatabase openWriter() {
		return disker.getWritableDatabase();
	}

	/**
	 * Close the database when reading data
	 */
	protected final void readFinish(SQLiteDatabase execute, Cursor cursor) {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		writeFinish(execute);
	}

	/**
	 * Close the database when writing data
	 */
	protected final void writeFinish(SQLiteDatabase execute) {
		if (execute != null && execute.isOpen()) {
			execute.close();
		}
	}

	/**
	 * The query id number
	 */
	public final int count() {
		return countColumn(Field.ID);
	}

	/**
	 * According to the "column" query "column" number
	 */
	public final int countColumn(String columnName) {
		StringBuilder sqlBuild = new StringBuilder("SELECT COUNT(").append(columnName).append(") FROM ").append(getTableName());
		return count(sqlBuild.toString());
	}

	/**
	 * According to the "column" query number
	 */
	public final int count(String sql) {
		SQLiteDatabase execute = openReader();
		print(sql);
		Cursor cursor = execute.rawQuery(sql, null);
		int count = 0;
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		readFinish(execute, cursor);
		return count;
	}

	/**
	 * Delete all data
	 */
	public final boolean deleteAll() {
		return delete("1=1");
	}

	/**
	 * Delete the data list
	 */
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

	/**
	 * According to the where to delete data
	 */
	public final boolean delete(String where) {
		if (TextUtils.isEmpty(where))
			return true;
		SQLiteDatabase execute = openWriter();
		StringBuilder sqlBuild = new StringBuilder("DELETE FROM ").append(getTableName()).append(" WHERE ").append(where);
		boolean result = true;
		try {
			String sql = sqlBuild.toString();
			print(sql);
			execute.execSQL(sql);
		} catch (SQLException e) {
			Logger.e(e);
			result = false;
		}
		writeFinish(execute);
		return result;
	}

	/**
	 * Query all data
	 */
	public final List<T> getAll() {
		return getAll(Field.ALL);
	}

	/**
	 * All the data query a column
	 * 
	 * @param columnName ColumnName
	 */
	public final List<T> getAll(String columnName) {
		return get(columnName, null, null, null, null);
	}

	/**
	 * All the data query a column
	 * 
	 * @param columnName Such as: *
	 * @param where Such as: age > 20
	 * @param orderBy Such as: age
	 * @param limit Such as
	 * @param offset
	 * @return
	 */
	public final List<T> get(String columnName, String where, String orderBy, String limit, String offset) {
		return get(getSelectSql(columnName, where, orderBy, limit, offset));
	}

	/**
	 * Create query sql
	 */
	private final String getSelectSql(String columnName, String where, String orderBy, String limit, String offset) {
		StringBuilder sqlBuild = new StringBuilder("SELECT ").append(columnName).append(" FROM ").append(getTableName());
		if (!TextUtils.isEmpty(where)) {
			sqlBuild.append(" WHERE ");
			sqlBuild.append(where);
		}
		if (!TextUtils.isEmpty(orderBy)) {
			sqlBuild.append(" ORDER BY ");
			sqlBuild.append(orderBy);
		}
		if (!TextUtils.isEmpty(limit)) {
			sqlBuild.append(" LIMIT ");
			sqlBuild.append(limit);
		}
		if (!TextUtils.isEmpty(limit) && !TextUtils.isEmpty(offset)) {
			sqlBuild.append(" OFFSET ");
			sqlBuild.append(offset);
		}
		String sql = sqlBuild.toString();
		print(sql);
		return sqlBuild.toString();
	}

	/**
	 * According to the SQL query data list
	 */
	public abstract List<T> get(String querySql);

	/**
	 * According to the unique index adds or updates a row data
	 */
	public abstract long replace(T t);

	/**
	 * Table name should be
	 */
	protected abstract String getTableName();

	/**
	 * Print the test data
	 */
	protected void print(String print) {
		if (DEBUG)
			Log.d("NoHttp", print);
	}

}
