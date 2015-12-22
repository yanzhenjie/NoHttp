/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License";;
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

import com.yolanda.nohttp.NoHttp;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * </br>
 * Created in Dec 18, 2015 6:30:59 PM
 * 
 * @author YOLANDA;
 */
class CookieDisker extends SQLiteOpenHelper {

	public final static String DB_COOKIE_NAME = "_nohttp_cookies_db.db";
	public final static int DB_COOKIE_VERSION = 1;

	public final static String TABLE_NAME = "cookies_table";
	public final static String ALL = "*";
	public final static String ID = "_id";
	public final static String URI = "uri";
	public final static String NAME = "name";
	public final static String VALUE = "value";
	public final static String COMMENT = "comment";
	public final static String COMMENTURL = "comment_url";
	public final static String DISCARD = "discard";
	public final static String DOMAIN = "domain";
	public final static String EXPIRY = "expiry";
	public final static String PATH = "path";
	public final static String PORTLIST = "portlist";
	public final static String SECURE = "secure";
	public final static String VERSION = "version";

	private final static String SQL_CREATE_TABLE = "CREATE TABLE cookies_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, uri TEXT, name TEXT, value TEXT, comment TEXT, comment_url TEXT, discard TEXT, domain TEXT, expiry INTEGER, path TEXT, portlist TEXT, secure TEXT, version INTEGER)";
	private final static String SQL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX cookie_unique_index ON cookies_table(\"name\", \"domain\", \"path\")";
	private final static String SQL_DELETE_TABLE = "DROP TABLE cookies_table";

	public CookieDisker() {
		super(NoHttp.getContext(), DB_COOKIE_NAME, null, DB_COOKIE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(SQL_CREATE_TABLE);
			db.execSQL(SQL_CREATE_UNIQUE_INDEX);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion != oldVersion) {
			db.execSQL(SQL_DELETE_TABLE);
			onCreate(db);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
