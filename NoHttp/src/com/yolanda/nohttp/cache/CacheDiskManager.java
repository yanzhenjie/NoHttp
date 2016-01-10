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
package com.yolanda.nohttp.cache;

import java.util.ArrayList;
import java.util.List;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.db.DBManager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created in Jan 10, 2016 12:42:29 AM
 * 
 * @author YOLANDA
 */
class CacheDiskManager extends DBManager<CacheEntity> {

	/**
	 * Instance
	 */
	private static DBManager<CacheEntity> _Instance;

	private CacheDiskManager() {
		super(new CacheDisker());
	}

	public synchronized static DBManager<CacheEntity> getInstance() {
		if (_Instance == null) {
			_Instance = new CacheDiskManager();
		}
		return _Instance;
	}

	/**
	 * Add or update by index(key)
	 */
	@Override
	public long replace(CacheEntity cacheEntity) {
		SQLiteDatabase execute = openWriter();
		ContentValues values = new ContentValues();
		values.put(CacheDisker.KEY, cacheEntity.getKey());
		values.put(CacheDisker.HEAD, cacheEntity.getResponseHeadersJson());
		values.put(CacheDisker.DATA, cacheEntity.getDataString());
		values.put(CacheDisker.ETAG, cacheEntity.getEtag());
		values.put(CacheDisker.SERVE_RDATE, cacheEntity.getServerDate());
		values.put(CacheDisker.LAST_MODIFIED, cacheEntity.getLastModified());
		long id = -1;
		try {
			id = execute.replace(getTableName(), null, values);
		} catch (Throwable e) {
			Logger.e(e);
		}
		finish(execute);
		return id;
	}

	@Override
	public List<CacheEntity> get(String columnName, String where, String orderBy, String limit, String offset) {
		SQLiteDatabase execute = openReader();

		List<CacheEntity> cacheEntities = new ArrayList<CacheEntity>();
		Cursor cursor = null;
		try {
			cursor = execute.rawQuery(getSelectSql(columnName, where, orderBy, limit, offset), null);
			while (!cursor.isClosed() && cursor.moveToNext()) {
				try {
					CacheEntity cacheEntity = new CacheEntity();
					int idIndex = cursor.getColumnIndex(DBManager.ID_FIELD);
					if (idIndex >= 0)
						cacheEntity.setId(cursor.getInt(idIndex));

					int keyIndex = cursor.getColumnIndex(CacheDisker.KEY);
					if (keyIndex >= 0)
						cacheEntity.setKey(cursor.getString(keyIndex));

					int headIndex = cursor.getColumnIndex(CacheDisker.HEAD);
					if (headIndex >= 0)
						cacheEntity.setResponseHeadersJson(cursor.getString(headIndex));

					int dataIndex = cursor.getColumnIndex(CacheDisker.DATA);
					if (dataIndex >= 0)
						cacheEntity.setDataString(cursor.getString(dataIndex));

					int etagIndex = cursor.getColumnIndex(CacheDisker.ETAG);
					if (etagIndex >= 0)
						cacheEntity.setEtag(cursor.getString(etagIndex));

					int dateIndex = cursor.getColumnIndex(CacheDisker.SERVE_RDATE);
					if (dateIndex >= 0)
						cacheEntity.setServerDate(cursor.getLong(dateIndex));

					int lastModifiedIndex = cursor.getColumnIndex(CacheDisker.LAST_MODIFIED);
					if (lastModifiedIndex >= 0)
						cacheEntity.setLastModified(cursor.getLong(lastModifiedIndex));
					cacheEntities.add(cacheEntity);
				} catch (Throwable e) {
					Logger.w(e);
				}
			}
		} catch (Throwable e) {
			Logger.e(e);
		}
		finish(execute, cursor);
		return cacheEntities;
	}

	@Override
	protected String getTableName() {
		return CacheDisker.TABLE_NAME;
	}

}
