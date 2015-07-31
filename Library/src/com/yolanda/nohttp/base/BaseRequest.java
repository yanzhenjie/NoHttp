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
package com.yolanda.nohttp.base;

import android.text.TextUtils;

/**
 * Created in Jul 28, 2015 7:29:26 PM
 * 
 * @author YOLANDA
 */
public abstract class BaseRequest {

	/**
	 * Target adress
	 */
	private String url;
	/**
	 * Request method
	 */
	private RequestMethod requestMethod;
	/**
	 * Connect http timeout
	 */
	private int mConnectTimeout;
	/**
	 * Read data timeout
	 */
	private int mReadTimeout;

	/**
	 * Create reuqest params
	 * 
	 * @param context Application context
	 * @param url Target adress
	 * @param requestMethod Request method
	 */
	public BaseRequest(String url, RequestMethod requestMethod) {
		super();
		if (TextUtils.isEmpty(url) || null == requestMethod)
			throw new NullPointerException("Request address and cannot be empty");
		this.url = url;
		this.requestMethod = requestMethod;
	}

	/**
	 * Get URL
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get request method
	 * 
	 * @return the requestMethod
	 */
	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	/**
	 * Get the connection timeout time
	 * 
	 * @return Integer time
	 */
	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	/**
	 * Sets the connection timeout time
	 * 
	 * @param connectTimeout timeout number
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	/**
	 * Get the read timeout time
	 * 
	 * @return Integer time
	 */
	public int getReadTimeout() {
		return mReadTimeout;
	}

	/**
	 * Sets the read timeout time
	 * 
	 * @param readTimeout timeout number
	 */
	public void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	/**
	 * Sets the flag indicating whether this URLConnection allows output. It cannot be set after the connection is
	 * established.
	 * 
	 * @return
	 */
	public boolean isOutPut() {
		switch (requestMethod) {
		case PATCH:
		case POST:
		case PUT:
			return true;
		case DELETE:
		case GET:
		case HEAD:
		case OPTIONS:
		case TRACE:
		default:
			return false;
		}
	}

}
