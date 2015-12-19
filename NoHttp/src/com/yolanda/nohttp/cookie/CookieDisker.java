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

	private static final String COOKIE_DB_NAME = "_nohttp_cookies_db_name_";

	private static final int COOKIE_DB_VERSION = 1;

	public static final String TABLE_NAME = "_cookies_table_name_";

	public final static String ID = "_id";
	public final static String URI = "uri";
	public final static String NAME = "name";
	public final static String VALUE = "value";
	public final static String COMMENT = "comment";
	public final static String COMMENTURL = "commentURL";
	public final static String DISCARD = "discard";
	public final static String DOMAIN = "domain";
	public final static String EXPIRY = "expiry";
	public final static String PATH = "path";
	public final static String PORTLIST = "portList";
	public final static String SECURE = "secure";
	public final static String VERSION = "version";

	public CookieDisker() {
		super(NoHttp.getContext(), COOKIE_DB_NAME, null, COOKIE_DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder("create table ");
		sql.append(CookieDisker.TABLE_NAME);
		sql.append("(");
		sql.append(ID);
		sql.append(" Integer primary key autoincrement, ");
		sql.append(URI);
		sql.append(" text, ");
		sql.append(NAME);
		sql.append(" text, ");
		sql.append(VALUE);
		sql.append(" text, ");
		sql.append(COMMENT);
		sql.append(" text, ");
		sql.append(COMMENTURL);
		sql.append(" text, ");
		sql.append(DISCARD);
		sql.append(" Integer, ");
		sql.append(DOMAIN);
		sql.append(" text, ");
		sql.append(EXPIRY);
		sql.append(" Integer, ");
		sql.append(PATH);
		sql.append(" text, ");
		sql.append(PORTLIST);
		sql.append(" text, ");
		sql.append(SECURE);
		sql.append(" Integer, ");
		sql.append(VERSION);
		sql.append(" Integer); ");
		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion != oldVersion) {
			StringBuilder sql = new StringBuilder("drop table if exists ");
			sql.append(TABLE_NAME);
			db.execSQL(sql.toString());
		}
	}

}
