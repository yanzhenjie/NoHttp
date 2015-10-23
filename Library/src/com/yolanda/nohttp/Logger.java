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
	static String sLogTag = "NoHttp";

	/**
	 * library debug sign
	 */
	static Boolean isDebug = false;

	public static void i(String msg) {
		if (isDebug)
			Log.i(sLogTag, msg);
	}

	public static void i(String format, Object... obj) {
		if (isDebug)
			Log.i(sLogTag, buildMessage(format, obj));
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(sLogTag, msg);
	}

	public static void v(String format, Object... obj) {
		if (isDebug)
			Log.v(sLogTag, buildMessage(format, obj));
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(sLogTag, msg);
	}

	public static void d(String format, Object... obj) {
		if (isDebug)
			Log.d(sLogTag, buildMessage(format, obj));
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(sLogTag, msg);
	}

	public static void e(String format, Object... obj) {
		if (isDebug)
			Log.e(sLogTag, buildMessage(format, obj));
	}

	public static void e(Throwable e) {
		if (isDebug)
			Log.e(sLogTag, "", e);
	}

	public static void e(Throwable e, String format, Object... obj) {
		if (isDebug)
			Log.e(sLogTag, buildMessage(format, obj), e);
	}

	public static void w(String msg) {
		if (isDebug)
			Log.w(sLogTag, msg);
	}

	public static void w(String format, Object... obj) {
		if (isDebug)
			Log.w(sLogTag, buildMessage(format, obj));
	}

	public static void w(Throwable e) {
		if (isDebug)
			Log.w(sLogTag, "", e);
	}

	public static void w(Throwable e, String format, Object... obj) {
		if (isDebug)
			Log.w(sLogTag, buildMessage(format, obj), e);
	}

	public static void wtf(String msg) {
		if (isDebug)
			Log.wtf(sLogTag, msg);
	}

	public static void wtf(String format, Object... obj) {
		if (isDebug)
			Log.wtf(sLogTag, buildMessage(format, obj));
	}

	public static void wtf(Throwable e) {
		if (isDebug)
			Log.wtf(sLogTag, "", e);
	}

	public static void wtf(Throwable e, String msg) {
		if (isDebug)
			Log.wtf(sLogTag, msg, e);
	}

	public static void throwable(Throwable e) {
		if (isDebug)
			e.printStackTrace();
	}

	/**
	 * msgs the caller's provided message and prepends useful info like
	 * calling thread ID and method name.
	 */
	protected static String buildMessage(String format, Object... args) {
		String msg = (args == null) ? format : String.format(Locale.US, format, args);
		StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

		String caller = "<unknown>";
		// Walk up the stack looking for the first caller outside of VolleyLog.
		// It will be at least two frames up, so start there.
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
