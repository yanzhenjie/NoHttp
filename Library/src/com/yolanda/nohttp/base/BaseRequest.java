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

import java.util.LinkedHashMap;
import java.util.Set;

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
	 * Analytical data charset
	 */
	private String mCharset = "utf-8";
	/**
	 * Connect http timeout
	 */
	private int mConnectTimeout;
	/**
	 * Read data timeout
	 */
	private int mReadTimeout;
	/**
	 * keep alive
	 */
	private boolean mKeepAlive = true;
	/**
	 * Request head collection
	 */
	private LinkedHashMap<String, String> mHeads = new LinkedHashMap<>();

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
	 * http.keepAlive
	 * 
	 * @return Keep alive, return true, otherwise it returns false
	 */
	public boolean isKeepAlive() {
		return mKeepAlive;
	}

	/**
	 * Set whether to keep alive
	 * 
	 * @param keepAlive
	 */
	public void setKeppAlive(boolean keepAlive) {
		this.mKeepAlive = keepAlive;
	}

	/**
	 * Set charset of analytical data,The default value is utf-8
	 * 
	 * @param the charset, such as:"utf-8"、"gbk"、"gb2312"
	 */
	public void setCharset(String charset) {
		if (!TextUtils.isEmpty(charset))
			this.mCharset = charset;
	}

	/**
	 * Get the charset analytical data
	 * 
	 * @return Returns the encoding type
	 */
	public String getCharset() {
		return mCharset;
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
	 * Add request head
	 * 
	 * @param key The head name
	 * @param value The head value
	 */
	public void addHeader(String key, String value) {
		mHeads.put(key, value);
	}

	/**
	 * Get the heads set
	 * 
	 * @return The head key set
	 */
	public Set<String> getHeadKeys() {
		return mHeads.keySet();
	}

	/**
	 * Get a head key corresponding to the value
	 * 
	 * @param key The head key
	 * @return The head value
	 */
	public String getHead(String key) {
		return mHeads.get(key);
	}

	/**
	 * Whether the request have parameter
	 * 
	 * @return Have returns true, no returns false
	 */
	public boolean hasParam() {
		return false;
	}

	public StringBuilder buildParam() {
		return new StringBuilder();
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
