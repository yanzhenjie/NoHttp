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

import java.util.Map;

/**
 * Created in Oct 16, 2015 8:22:06 PM
 * 
 * @author YOLANDA
 */
public abstract class Request<T> extends CommonRequest {

	/**
	 * Create a request, RequestMethod is {@link RequestMethod#Get}
	 * 
	 * @param url request adress, like: http://www.google.com
	 */
	public Request(String url) {
		super(url, RequestMethod.GET);
	}

	/**
	 * Create a request
	 * 
	 * @param url request adress, like: http://www.google.com
	 * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}
	 */
	public Request(String url, RequestMethod requestMethod) {
		super(url, requestMethod);
	}

	/**
	 * Add <code>CharSequence</code> param
	 *
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, CharSequence value);

	/**
	 * Add <code>Integer</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, int value);

	/**
	 * Add <code>Long</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, long value);

	/**
	 * Add <code>Boolean</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, boolean value);

	/**
	 * Add <code>char</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, char value);

	/**
	 * Add <code>Double</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, double value);

	/**
	 * Add <code>Float</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, float value);

	/**
	 * Add <code>Short</code> param
	 * 
	 * @param key Param name
	 * @param value Param value
	 */
	public abstract void add(String key, short value);

	/**
	 * Add <code>Byte</code> param
	 * 
	 * @param key Param name
	 * @param value Param value 0 x01, for example, the result is 1
	 */
	public abstract void add(String key, byte value);

	/**
	 * Add <code>File</code> param; NoHttp already has a default implementation: {@link FileBinary}
	 * 
	 * @param key Param name
	 * @param binary Param value
	 */
	public abstract void add(String key, Binary binary);

	/**
	 * add all param
	 * 
	 * @param params params map
	 */
	public abstract void add(Map<String, String> params);
	
	/**
	 * set all param
	 * @param params
	 */
	public abstract void set(Map<String, String> params);

	/**
	 * Remove a request param by key
	 */
	public abstract void remove(String key);

	/**
	 * Remove all request param
	 */
	public abstract void removeAll();

	/**
	 * Parse response
	 */
	public abstract T parseResponse(String url, String contentType, byte[] byteArray);
}
