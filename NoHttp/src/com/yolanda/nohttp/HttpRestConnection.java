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

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.yolanda.nohttp.tools.NetUtil;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.URLUtil;

/**
 * RESTFUL request actuator</br>
 * Created in Jul 28, 2015 7:33:22 PM
 * 
 * @author YOLANDA
 */
public final class HttpRestConnection extends BasicConnection implements BasicConnectionRest {

	/**
	 * context
	 */
	private final Context mContext;
	/**
	 * User-Agent of request
	 */
	private final String userAgent;
	/**
	 * Singleton pattern: Keep the object
	 */
	private static HttpRestConnection _INSTANCE;

	/**
	 * To create a singleton pattern entrance
	 * 
	 * @return Return my implementation
	 */
	public static BasicConnectionRest getInstance(Context context) {
		synchronized (HttpRestConnection.class) {
			if (_INSTANCE == null)
				_INSTANCE = new HttpRestConnection(context.getApplicationContext());
		}
		return _INSTANCE;
	}

	/**
	 * lock public
	 */
	private HttpRestConnection(Context context) {
		this.mContext = context;
		userAgent = UserAgent.getUserAgent(context);
	}

	/**
	 * Initiate the request, and parse the response results
	 */
	@Override
	public <T> Response<T> request(Request<T> request) {
		long startTime = SystemClock.elapsedRealtime();
		if (request == null) {
			throw new IllegalArgumentException("reqeust == null");
		}
		Logger.d("--------------Reuqest start--------------");

		String url = request.url();
		Object tag = request.getTag();
		boolean isSucceed = false;
		int responseCode = -1;
		Headers headers = null;
		byte[] byteArray = null;
		T result = null;

		if (!URLUtil.isValidUrl(request.url()))
			byteArray = "URL error".getBytes();
		else if (!NetUtil.isNetworkAvailable(mContext)) {
			byteArray = "Network error".getBytes();
		} else {
			HttpURLConnection httpConnection = null;
			try {
				httpConnection = getHttpConnection(request);
				httpConnection.connect();
				Logger.i("-------Send reqeust data start-------");
				writeRequestBody(httpConnection, request);
				Logger.i("-------Send request data end-------");
				Logger.i("-------Response start-------");
				responseCode = httpConnection.getResponseCode();
				Logger.d("ResponseCode: " + responseCode);

				Map<String, List<String>> responseHeaders = httpConnection.getHeaderFields();
				headers = Headers.parseMultimap(responseHeaders);
				for (String headName : responseHeaders.keySet()) {
					List<String> headValues = responseHeaders.get(headName);
					for (String headValue : headValues) {
						StringBuffer buffer = new StringBuffer();
						if (!TextUtils.isEmpty(headName)) {
							buffer.append(headName);
							buffer.append(": ");
						}
						if (!TextUtils.isEmpty(headValue))
							buffer.append(headValue);
						Logger.d(buffer.toString());
					}
				}

				CookieManager cookieManager = NoHttp.getDefaultCookieManager();
				// 这里解析的是set-cookie2和set-cookie
				cookieManager.put(new URI(request.url()), responseHeaders);

				isSucceed = true;

				if (hasResponseBody(request.getRequestMethod(), responseCode)) {
					String contentEncoding = httpConnection.getContentEncoding();
					InputStream inputStream = null;
					try {
						inputStream = httpConnection.getInputStream();
					} catch (IOException e) {
						isSucceed = false;
						inputStream = httpConnection.getErrorStream();
					}
					if (HeaderParser.isGzipContent(contentEncoding))
						inputStream = new GZIPInputStream(inputStream);
					byteArray = readResponseBody(inputStream);
					inputStream.close();
				}
			} catch (Exception e) {
				request.takeQueue(false);
				isSucceed = false;
				String exceptionInfo = getExcetionMessage(e);
				byteArray = exceptionInfo.getBytes();
				Logger.e(e);
			} finally {
				if (httpConnection != null)
					httpConnection.disconnect();
				Logger.i("-------Response end-------");
			}
		}
		if (isSucceed && byteArray != null)
			result = request.parseResponse(url, headers.get(Headers.HEAD_KEY_CONTENT_TYPE), byteArray);
		Logger.d("--------------Reqeust finish--------------");
		long endTime = SystemClock.elapsedRealtime();
		return new RestResponser<T>(url, isSucceed, responseCode, headers, byteArray, tag, result, endTime - startTime);
	}

	@Override
	protected String getUserAgent() {
		return userAgent;
	}

}
