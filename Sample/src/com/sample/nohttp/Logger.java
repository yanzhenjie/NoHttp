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
package com.sample.nohttp;


/**
 * Created in Jul 28, 2015 11:08:39 PM
 * 
 * @author YOLANDA
 */
public class Logger {

	public static final boolean debug = true;

	public static final String TAG = "NoHttpSample";

	/**
	 * red error message
	 * 
	 * @param msg
	 */
	static void e(String msg) {
		if (debug) {
			android.util.Log.e(TAG, msg);
		}
	}

	/**
	 * orange warn message
	 * 
	 * @param msg
	 */
	static void w(String msg) {
		if (debug) {
			android.util.Log.w(TAG, msg);
		}
	}

	/**
	 * green message
	 * 
	 * @param msg
	 */
	static void i(String msg) {
		if (debug) {
			android.util.Log.i(TAG, msg);
		}
	}

	/**
	 * blue status message
	 * 
	 * @param msg
	 */
	static void d(String msg) {
		if (debug) {
			android.util.Log.d(TAG, msg);
		}
	}

}
