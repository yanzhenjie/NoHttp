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
	public HttpResponse request(BasicRequest request) {
		if (request == null)
			throw new IllegalArgumentException("reqeust == null");

		Logger.d("--------------Reuqest start--------------");

		int responseCode = 0;
		boolean isSucceed = false;
		Headers responseHeaders = null;
		byte[] responseBody = null;

		String url = request.url();
		if (!URLUtil.isValidUrl(url))
			responseBody = new StringBuffer("URL error: ").append(url).toString().getBytes();
		else if (!NetUtil.isNetworkAvailable(mContext)) {
			responseBody = "Network error".getBytes();
		} else {
			HttpURLConnection httpConnection = null;
			try {
				httpConnection = getHttpConnection(request);
				httpConnection.connect();
				// write request body to stream
				writeRequestBody(httpConnection, request);

				Logger.i("-------Response start-------");
				responseCode = httpConnection.getResponseCode();
				Logger.d("ResponseCode: " + responseCode);

				// handle headers
				Map<String, List<String>> httpHeaders = httpConnection.getHeaderFields();
				responseHeaders = HeaderParser.parseMultimap(httpHeaders);
				responseHeaders.add(Headers.HEAD_KEY_RESPONSE_CODE, Integer.toString(responseCode));

				// handle cookie
				CookieManager cookieManager = NoHttp.getDefaultCookieManager();
				cookieManager.put(new URI(url), httpHeaders);

				// handle body
				if (hasResponseBody(request.getRequestMethod(), responseCode)) {
					InputStream inputStream = null;
					try {
						inputStream = httpConnection.getInputStream();
					} catch (IOException e) {
						inputStream = httpConnection.getErrorStream();
					}
					String contentEncoding = httpConnection.getContentEncoding();
					if (HeaderParser.isGzipContent(contentEncoding))
						inputStream = new GZIPInputStream(inputStream);
					responseBody = readResponseBody(inputStream);
					inputStream.close();
				}

				isSucceed = true;// Deal successfully with all
			} catch (Exception e) {
				String exceptionInfo = getExcetionMessage(e);
				responseBody = exceptionInfo.getBytes();
				Logger.e(e);
			} finally {
				if (httpConnection != null)
					httpConnection.disconnect();
				Logger.i("-------Response end-------");
			}
		}
		Logger.d("--------------Reqeust finish--------------");
		return new HttpResponse(isSucceed, responseCode, responseHeaders, responseBody);
	}

	@Override
	protected String getUserAgent() {
		return userAgent;
	}

}
