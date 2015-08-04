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

import com.yolanda.nohttp.base.BaseRequest;
import com.yolanda.nohttp.base.RequestMethod;

import android.graphics.Bitmap;

/**
 * Created in Jul 28, 2015 7:33:52 PM
 * 
 * @author YOLANDA
 */
public class Request extends BaseRequest implements Serializable {

	private static final long serialVersionUID = 100L;
	/**
	 * Param collection
	 */
	private LinkedHashMap<String, Object> mParamSets = new LinkedHashMap<>();
	/**
	 * UpLoad file name collection
	 */
	private LinkedHashMap<String, String> mFileNames = new LinkedHashMap<>();
	/**
	 * Post data
	 */
	private String mPostData;

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
	public String getPostData() {
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
	@Override
	public boolean hasParam() {
		return mParamSets.size() > 0;
	}

	/**
	 * If the request were any uploading files
	 * 
	 * @return Have returns true, no returns false
	 */
	public boolean hasBinaryData() {
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
	public Set<String> getParamKeys() {
		return mParamSets.keySet();
	}

	/**
	 * Get a param key corresponding to the value
	 * 
	 * @param key The param key
	 * @return The param value
	 */
	public Object getParam(String key) {
		return mParamSets.get(key);
	}

	/**
	 * Upload the file name
	 * 
	 * @param key The param key
	 * @return filename
	 */
	public String getFileName(String key) {
		return mFileNames.get(key);
	}

	/**
	 * Combination of parameters
	 * 
	 * @return Such as:sex=man&age=12
	 */
	@Override
	public StringBuilder buildParam() {
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
					paramBuilder.append(URLEncoder.encode(key, getCharset()));
					paramBuilder.append("=");
					paramBuilder.append(URLEncoder.encode(param, getCharset()));
				} catch (Throwable e) {
					if (NoHttp.isDebug())
						e.printStackTrace();
				}
			}
		}
		return paramBuilder;
	}
}
