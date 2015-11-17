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

import java.util.Locale;

import android.util.Log;

/**
 * Created in Jul 28, 2015 7:32:05 PM
 * 
 * @author YOLANDA
 */
public class Logger {

	/**
	 * library debug tag
	 */
	public static String STag = "NoHttp";

	/**
	 * library debug sign
	 */
	public static Boolean SDebug = false;

	public static void i(String msg) {
		if (SDebug)
			Log.i(STag, msg);
	}

	public static void i(String format, Object... obj) {
		if (SDebug)
			Log.i(STag, buildMessage(format, obj));
	}

	public static void v(String msg) {
		if (SDebug)
			Log.v(STag, msg);
	}

	public static void v(String format, Object... obj) {
		if (SDebug)
			Log.v(STag, buildMessage(format, obj));
	}

	public static void d(String msg) {
		if (SDebug)
			Log.d(STag, msg);
	}

	public static void d(String format, Object... obj) {
		if (SDebug)
			Log.d(STag, buildMessage(format, obj));
	}

	public static void e(String msg) {
		if (SDebug)
			Log.e(STag, msg);
	}

	public static void e(String format, Object... obj) {
		if (SDebug)
			Log.e(STag, buildMessage(format, obj));
	}

	public static void e(Throwable e) {
		if (SDebug)
			Log.e(STag, "", e);
	}

	public static void e(Throwable e, String format, Object... obj) {
		if (SDebug)
			Log.e(STag, buildMessage(format, obj), e);
	}

	public static void w(String msg) {
		if (SDebug)
			Log.w(STag, msg);
	}

	public static void w(String format, Object... obj) {
		if (SDebug)
			Log.w(STag, buildMessage(format, obj));
	}

	public static void w(Throwable e) {
		if (SDebug)
			Log.w(STag, "", e);
	}

	public static void w(Throwable e, String format, Object... obj) {
		if (SDebug)
			Log.w(STag, buildMessage(format, obj), e);
	}

	public static void wtf(String msg) {
		if (SDebug)
			Log.wtf(STag, msg);
	}

	public static void wtf(String format, Object... obj) {
		if (SDebug)
			Log.wtf(STag, buildMessage(format, obj));
	}

	public static void wtf(Throwable e) {
		if (SDebug)
			Log.wtf(STag, "", e);
	}

	public static void wtf(Throwable e, String msg) {
		if (SDebug)
			Log.wtf(STag, msg, e);
	}

	public static void throwable(Throwable e) {
		if (SDebug)
			e.printStackTrace();
	}

	protected static String buildMessage(String format, Object... args) {
		String msg = (args == null) ? format : String.format(Locale.US, format, args);
		StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

		String caller = "<unknown>";
		for (int i = 2; i < trace.length; i++) {
			Class<?> clazz = trace[i].getClass();
			if (!clazz.equals(Logger.class)) {
				String callingClass = trace[i].getClassName();
				callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
				callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
				caller = callingClass + "." + trace[i].getMethodName();
				break;
			}
		}
		return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg);
	}

}
