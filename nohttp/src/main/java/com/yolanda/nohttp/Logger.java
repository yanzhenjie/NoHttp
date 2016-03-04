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
package com.yolanda.nohttp;

import android.util.Log;

/**
 * Created in Jul 28, 2015 7:32:05 PM.
 *
 * @author YOLANDA;
 */
public class Logger {

    /**
     * Library debug tag.
     */
    private static String STag = "NoHttp";

    /**
     * Library debug sign.
     */
    private static boolean SDebug = false;

    public static void setTag(String tag) {
        STag = tag;
    }

    public static void setDebug(boolean debug) {
        SDebug = debug;
    }

    public static void i(String msg) {
        if (SDebug)
            Log.i(STag, msg);
    }

    public static void i(Throwable e) {
        if (SDebug)
            Log.i(STag, "", e);
    }

    public static void i(Throwable e, String msg) {
        if (SDebug)
            Log.i(STag, msg, e);
    }

    public static void v(String msg) {
        if (SDebug)
            Log.v(STag, msg);
    }

    public static void v(Throwable e) {
        if (SDebug)
            Log.v(STag, "", e);
    }

    public static void v(Throwable e, String msg) {
        if (SDebug)
            Log.v(STag, msg, e);
    }

    public static void d(String msg) {
        if (SDebug)
            Log.d(STag, msg);
    }

    public static void d(Throwable e) {
        if (SDebug)
            Log.d(STag, "", e);
    }

    public static void d(Throwable e, String msg) {
        if (SDebug)
            Log.d(STag, msg, e);
    }

    public static void e(String msg) {
        if (SDebug)
            Log.e(STag, msg);
    }

    public static void e(Throwable e) {
        if (SDebug)
            Log.e(STag, "", e);
    }

    public static void e(Throwable e, String msg) {
        if (SDebug)
            Log.e(STag, msg, e);
    }

    public static void w(String msg) {
        if (SDebug)
            Log.w(STag, msg);
    }

    public static void w(Throwable e) {
        if (SDebug)
            Log.w(STag, "", e);
    }

    public static void w(Throwable e, String msg) {
        if (SDebug)
            Log.w(STag, msg, e);
    }

    public static void wtf(String msg) {
        if (SDebug)
            Log.wtf(STag, msg);
    }

    public static void wtf(Throwable e) {
        if (SDebug)
            Log.wtf(STag, "", e);
    }

    public static void wtf(Throwable e, String msg) {
        if (SDebug)
            Log.wtf(STag, msg, e);
    }

}
