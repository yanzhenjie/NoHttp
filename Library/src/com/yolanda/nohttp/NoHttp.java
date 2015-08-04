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
package com.yolanda.nohttp;

import com.yolanda.nohttp.base.BaseResponse;
import com.yolanda.nohttp.base.HttpsVerifier;

import android.content.Context;

/**
 * Created in Jul 28, 2015 7:32:22 PM
 * 
 * @author YOLANDA
 */
public class NoHttp {

	/**
	 * library debug sign
	 */
	private static Boolean noHttpDebug = false;
	/**
	 * library debug tag
	 */
	private static String noHttpTag = "NoHttp";
	/**
	 * silge model
	 */
	private static NoHttp _NoHttp;

	/**
	 * To create a singleton pattern, set the task pool concurrency
	 * 
	 * @param concurrentCount Task concurrency
	 */
	private NoHttp() {
	}

	/**
	 * Open the HTTPS, CRT certificate verification
	 * 
	 * @param context Users get AssetManager, load your certificate of CRT
	 * @param fileName Path and name of your certificate of CRT in assets, like file:///android_asset/yolanda.crt
	 * @throws IOException .
	 * @throws NoSuchAlgorithmException .
	 * @throws KeyStoreException .
	 * @throws CertificateException .
	 * @throws KeyManagementException .
	 */
	public static void openHttpsVerify(Context context, String fileName) {
		try {
			HttpsVerifier.initVerify(context, fileName);
		} catch (Exception e) {
			if (noHttpDebug)
				e.printStackTrace();
		}
	}

	/**
	 * Close the Http yes CRT certificate of calibration
	 */
	public static void closeHttpsVerify() {
		HttpsVerifier.closeVerify();
	}

	/**
	 * Set is a debug mode, if it is a debug mode, you can see NoHttp Log information
	 * 
	 * @param debug Set to debug mode is introduced into true, introduced to false otherwise
	 */
	public static void setDebug(Boolean debug) {
		noHttpDebug = debug;
	}

	/**
	 * Set the log of the tag
	 * 
	 * @param tag The incoming string will be NoHttp logtag, also is in development tools logcat tag bar to see
	 */
	public static void setTag(String tag) {
		noHttpTag = tag;
	}

	/**
	 * If debug mode
	 */
	public static boolean isDebug() {
		return noHttpDebug;
	}

	/**
	 * To get the tag of the log
	 */
	public static String getTag() {
		return noHttpTag;
	}

	/**
	 * The singlet entrance
	 * 
	 * @return
	 */
	public static NoHttp getInstance() {
		if (_NoHttp == null) {
			_NoHttp = new NoHttp();
		}
		return _NoHttp;
	}

	/**
	 * Packaging the request
	 * 
	 * @param command Operation command
	 * @param request The packaging of the HTTP request parameter
	 * @param what Http request sign, If multiple requests the Listener is the same, so that I can be used to mark which
	 *        one is the request
	 * @param responseListener Accept the request as a result, no matter wrong or right will be returned to you
	 */
	private void execute(int command, Request request, int what, OnResponseListener responseListener) {
		RequestPoster asyncPoster = new RequestPoster(what, command, responseListener);
		asyncPoster.execute(request);
	}

	/**
	 * In an asynchronous way request data from a url request type String, if
	 * the request correctly, returns the String contained in the data, data
	 * bytes
	 * 
	 * @param request The packaging of the HTTP request parameter
	 * @param what Http request sign, If multiple requests the Listener is the same, so that I can be used to mark which
	 *        one is the request
	 * @param responseListener Accept the request as a result, no matter wrong or right will be returned to you
	 */
	public void requestAsync(Request request, int what, OnResponseListener responseListener) {
		execute(RequestPoster.COMMAND_REQUEST_HTTP, request, what, responseListener);
	}

	/**
	 * Send a synchronous request, recommend generally used in the child thread
	 * 
	 * @param request The packaging of the HTTP request parameter
	 * @return The response of the packaging
	 */
	public BaseResponse requestSync(Request request) {
		return HttpExecutor.getInstance().request(request);
	}

	/**
	 * In an asynchronous way access to download the URL of the file name,
	 * including the suffix
	 * 
	 * @param request The packaging of the HTTP request parameter
	 * @param what Http request sign, If multiple requests the Listener is the same, so that I can be used to mark which
	 *        one is the request
	 * @param responseListener Accept the request as a result, no matter wrong or right will be returned to you
	 */
	public void requestFilenameAsync(Request request, int what, OnResponseListener responseListener) {
		execute(RequestPoster.COMMAND_REQUEST_FILENAME, request, what, responseListener);
	}

	/**
	 * In an synchronous way access to download the URL of the file name, including the suffix
	 * 
	 * @param request The packaging of the HTTP request parameter
	 * @return The response of the packaging
	 */
	public BaseResponse requestFilenameSync(Request request) {
		return HttpExecutor.getInstance().requestFilename(request);
	}
}
