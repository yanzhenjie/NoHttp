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

/**
 * Created in Jul 28, 2015 7:32:22 PM
 * 
 * @author YOLANDA
 */
public class NoHttp {

	/**
	 * library debug sign
	 */
	static Boolean welldebug = false;
	/**
	 * library debug tag
	 */
	static String nohttptag = "NoHttp";
	/**
	 * silge model
	 */
	private static NoHttp _NoHttp;

	/**
	 * lock public
	 */
	private NoHttp() {
	}

	/**
	 * Set is a debug mode, if it is a debug mode, you can see NoHttp Log information
	 * 
	 * @param debug Set to debug mode is introduced into true, introduced to false otherwise
	 */
	public static void setDebug(Boolean debug) {
		welldebug = debug;
	}

	/**
	 * Set the log of the tag
	 * 
	 * @param tag The incoming string will be NoHttp logtag, also is in development tools logcat tag bar to see
	 */
	public static void setTag(String tag) {
		nohttptag = tag;
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
	 * The enforcement request
	 * 
	 * @param executor Packaging good executor
	 */
	private void execut(Executor executor, Request request) {
		RequestControler controler = new RequestControler(executor);
		controler.execute(request);
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
	private void reqeust(int command, Request request, int what, OnResponseListener responseListener) {
		Executor executor = new Executor();
		executor.command = command;
		executor.what = what;
		executor.responseListener = responseListener;
		execut(executor, request);
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
		reqeust(Command.REQUEST_HTTP, request, what, responseListener);
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
		reqeust(Command.REQUEST_FILENAME, request, what, responseListener);
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
