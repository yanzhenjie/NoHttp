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

import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.Map;

import com.yolanda.nohttp.security.Certificate;

/**
 * Created in Oct 16, 2015 8:22:06 PM
 * 
 * @author YOLANDA
 */
public abstract interface Request<T> {

	/**
	 * @param mCertificate the mCertificate to set
	 */
	public abstract void setCertificate(Certificate mCertificate);

	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 * 
	 * @param isAllowHttps the isAllowHttps to set
	 */
	public abstract void setAllowHttps(boolean isAllowHttps);

	/**
	 * Sets the connection timeout time
	 * 
	 * @param connectTimeout timeout number
	 */
	public abstract void setConnectTimeout(int connectTimeout);

	/**
	 * Sets the read timeout time
	 * 
	 * @param readTimeout timeout number
	 */
	public abstract void setReadTimeout(int readTimeout);

	/**
	 * Sets the header named {@code name} to {@code value}. If this request
	 * already has any headers with that name, they are all replaced.
	 */
	public abstract void setHeader(String name, String value);

	/**
	 * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued headers like "Cookie".
	 */
	public abstract void addHeader(String name, String value);

	/**
	 * Add cookie to header
	 */
	public abstract void addCookie(HttpCookie cookie);

	/**
	 * Add CookieStore to CookieManager of NoHttp, Will replace the old value
	 */
	public abstract void addCookie(CookieStore cookieStore);

	/**
	 * Removes a header with {@code name} and {@code value}. If there are multiple keys, will remove all, like "Cookie".
	 */
	public abstract void removeHeader(String name);

	/**
	 * Remove all header
	 */
	public abstract void removeAllHeaders();

	/**
	 * Settings you want to post data, if the post directly, then other data
	 * will not be sent
	 * 
	 * @param data Post data
	 */
	public abstract void setRequestBody(String data);

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
	 * @param params params map
	 */
	public abstract void add(Map<String, String> params);

	/**
	 * Remove a request param by key
	 */
	public abstract void remove(String key);

	/**
	 * Remove all request param
	 */
	public abstract void removeAll();

	/**
	 * Set off the sign
	 */
	public abstract void setCancelSign(Object sign);

	/**
	 * According to Sign request
	 */
	public abstract void cancelBySign(Object sign);

	/**
	 * Cancel this request
	 */
	public abstract void cancel();

	/**
	 * This request has been canceled.
	 */
	public abstract boolean isCanceled();

	/**
	 * Set tag of task, Will return to you at the time of the task response
	 */
	public abstract void setTag(Object tag);

	/**
	 * The interpreter is a parse, and the Http request occurs.
	 */
	public abstract AnalyzeRequest getAnalyzeRequest();

	/**
	 * Parse response
	 */
	public abstract T parseResponse(String url, String contentType, byte[] byteArray);
}
