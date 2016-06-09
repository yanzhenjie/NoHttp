/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.cookie;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.db.Field;

/**
 * <p>Cookie database operation class.</p>
 * Created in Dec 18, 2015 6:30:59 PM.
 *
 * @author Yan Zhenjie.
 */
class CookieDisk extends SQLiteOpenHelper implements Field {

    public static final String DB_COOKIE_NAME = "_nohttp_cookies_db.db";
    public static final int DB_COOKIE_VERSION = 2;

    public static final String TABLE_NAME = "cookies_table";
    public static final String URI = "uri";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String COMMENT = "comment";
    public static final String COMMENT_URL = "comment_url";
    public static final String DISCARD = "discard";
    public static final String DOMAIN = "domain";
    public static final String EXPIRY = "expiry";
    public static final String PATH = "path";
    public static final String PORT_LIST = "port_list";
    public static final String SECURE = "secure";
    public static final String VERSION = "version";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE cookies_table(_id INTEGER PRIMARY KEY AUTOINCREMENT, uri TEXT, name TEXT, value TEXT, comment TEXT, comment_url TEXT, discard TEXT, domain TEXT, expiry INTEGER, path TEXT, port_list TEXT, secure TEXT, version INTEGER)";
    private static final String SQL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX cookie_unique_index ON cookies_table(\"name\", \"domain\", \"path\")";
    private static final String SQL_DELETE_TABLE = "DROP TABLE  IF EXISTS cookies_table";
    private static final String SQL_DELETE_UNIQUE_INDEX = "DROP INDEX IF EXISTS cookie_unique_index";

    public CookieDisk() {
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
            db.beginTransaction();
            try {
                db.execSQL(SQL_DELETE_TABLE);
                db.execSQL(SQL_DELETE_UNIQUE_INDEX);
                db.execSQL(SQL_CREATE_TABLE);
                db.execSQL(SQL_CREATE_UNIQUE_INDEX);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
