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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Set;

import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * Created in Jul 28, 2015 7:33:52 PM
 * 
 * @author YOLANDA
 */
public class Request extends BaseRequest implements Serializable {

	private static final long serialVersionUID = 100L;
	/**
	 * connect timeout
	 */
	private int mConnectTimeout = 10 * 1000;
	/**
	 * read timeout
	 */
	private int mReadTimeout = 10 * 1000;
	/**
	 * keep alive
	 */
	private boolean mKeepAlive = true;
	/**
	 * Param collection
	 */
	private LinkedHashMap<String, Object> mParamSets = new LinkedHashMap<>();
	/**
	 * UpLoad file name collection
	 */
	private LinkedHashMap<String, String> mFileNames = new LinkedHashMap<>();
	/**
	 * Request head collection
	 */
	private LinkedHashMap<String, String> mHeads = new LinkedHashMap<>();
	/**
	 * Post data
	 */
	private String mPostData;
	/**
	 * Analytical data charset
	 */
	private String mCharset = "utf-8";

	/**
	 * Create reuqest params
	 * 
	 * @param context Application context
	 * @param url Target adress
	 * @param requestMethod Request method
	 */
	public Request(String url, RequestMethod requestMethod) {
		super(url, requestMethod);
	}

	/**
	 * Get the connection timeout time
	 * 
	 * @return Integer time
	 */
	int getConnectTimeout() {
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
	int getReadTimeout() {
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
	 * http.keepAlive
	 * 
	 * @return Keep alive, return true, otherwise it returns false
	 */
	boolean isKeepAlive() {
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
	Set<String> getHeadKeys() {
		return mHeads.keySet();
	}

	/**
	 * Get a head key corresponding to the value
	 * 
	 * @param key The head key
	 * @return The head value
	 */
	String getHead(String key) {
		return mHeads.get(key);
	}

	/**
	 * Settings you want to post data, if the post directly, then other data
	 * will not be sent
	 * 
	 * @param data Post data
	 */
	public void setPostData(String data) {
		mPostData = data;
	}

	/**
	 * Get post data
	 * 
	 * @return User post data
	 */
	String getPostData() {
		return mPostData;
	}

	/**
	 * Add <code>CharSequence</code> param
	 *
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, CharSequence value) {
		mParamSets.put(key, String.valueOf(value));
	}

	/**
	 * Add <code>Integer</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, int value) {
		mParamSets.put(key, Integer.toString(value));
	}

	/**
	 * Add <code>Long</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, long value) {
		mParamSets.put(key, Long.toString(value));
	}

	/**
	 * Add <code>Boolean</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, boolean value) {
		mParamSets.put(key, String.valueOf(value));
	}

	/**
	 * Add <code>char</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, char value) {
		mParamSets.put(key, String.valueOf(value));
	}

	/**
	 * Add <code>Double</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, double value) {
		mParamSets.put(key, Double.toString(value));
	}

	/**
	 * Add <code>Float</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, float value) {
		mParamSets.put(key, Float.toString(value));
	}

	/**
	 * Add <code>Short</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public void add(String key, short value) {
		mParamSets.put(key, Short.toString(value));
	}

	/**
	 * Add <code>Byte</code> param
	 * 
	 * @param key Param name
	 * @param value Param value 0 x01, for example, the result is 1
	 */
	public void add(String key, byte value) {
		mParamSets.put(key, Integer.toString(value));
	}

	/**
	 * Add <code>Bitmap</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 * @param fileName the filename for server
	 */
	public void add(String key, Bitmap value, String fileName) {
		mParamSets.put(key, value);
		mFileNames.put(key, fileName);
	}

	/**
	 * Add <code>File</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 * @param fileName the filename for server
	 */
	public void add(String key, File file, String fileName) {
		mParamSets.put(key, file);
		mFileNames.put(key, fileName);
	}

	/**
	 * Add <code>ByteArrayOutputStream</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 * @param fileName the filename for server
	 */
	public void add(String key, ByteArrayOutputStream arrayOutputStream, String fileName) {
		mParamSets.put(key, arrayOutputStream);
		mFileNames.put(key, fileName);
	}

	/**
	 * Whether the request have parameter
	 * 
	 * @return Have returns true, no returns false
	 */
	boolean hasParam() {
		return mParamSets.size() > 0;
	}

	/**
	 * If the request were any uploading files
	 * 
	 * @return Have returns true, no returns false
	 */
	boolean hasBinaryData() {
		Set<String> keys = mParamSets.keySet();
		for (String key : keys) {
			Object value = mParamSets.get(key);
			if ((value instanceof File) || (value instanceof Bitmap) || (value instanceof ByteArrayOutputStream)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the parameters set
	 * 
	 * @return The param key set
	 */
	Set<String> getParamKeys() {
		return mParamSets.keySet();
	}

	/**
	 * Get a param key corresponding to the value
	 * 
	 * @param key The param key
	 * @return The param value
	 */
	Object getParam(String key) {
		return mParamSets.get(key);
	}

	/**
	 * Upload the file name
	 * 
	 * @param key The param key
	 * @return filename
	 */
	String getFileName(String key) {
		return mFileNames.get(key);
	}

	/**
	 * Combination of parameters
	 * 
	 * @return Such as:sex=man&age=12
	 */
	StringBuilder buildParam() {
		StringBuilder paramBuilder = new StringBuilder();
		boolean first = true;
		for (String key : mParamSets.keySet()) {
			if (first) {
				first = false;
			} else {
				paramBuilder.append("&");
			}
			Object value = mParamSets.get(key);
			if (value instanceof CharSequence) {
				String param = value.toString();
				try {
					paramBuilder.append(URLEncoder.encode(key, mCharset));
					paramBuilder.append("=");
					paramBuilder.append(URLEncoder.encode(param, mCharset));
				} catch (Throwable e) {
					if (NoHttp.welldebug)
						e.printStackTrace();
				}
			}
		}
		return paramBuilder;
	}

	/**
	 * Set charset of analytical data
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
	String getCharset() {
		return mCharset;
	}
}
