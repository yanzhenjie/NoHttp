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

import java.net.HttpCookie;
import java.util.List;

/**
 * Http response, Including header information and response packets</br>
 * Created in Oct 15, 2015 8:55:37 PM
 * 
 * @author YOLANDA
 */
public abstract interface Response<T> {

	/**
	 * url
	 */
	public String url();

	/**
	 * Ask for success
	 */
	public boolean isSucceed();

	/**
	 * Get http responseCode
	 */
	public int getResponseCode();

	/**
	 * Get http response headers
	 */
	public Headers getHeaders();

	/**
	 * Returns the value of the header field specified by {@code key} or {@code
	 * null} if there is no field with this name. The base implementation of
	 * this method returns always {@code null}.
	 * 
	 * @param key the name of the header field.
	 * @return the value of the header field.
	 */
	public List<String> getHeaders(String key);

	/**
	 * Returns the MIME-type of the content specified by the response header
	 * field {@code content-type} or {@code null} if type is unknown.
	 *
	 * @return the value of the response header field {@code content-type}.
	 */
	public String getContentType();

	/**
	 * Returns the content length in bytes specified by the response header
	 * field {@code content-length} or {@code -1} if this field is not set or
	 * cannot be represented as an {@code int}.
	 */
	public int getContentLength();

	/**
	 * Get http response Cookie
	 */
	public List<HttpCookie> getCookies();

	/**
	 * Get raw data
	 */
	public byte[] getByteArray();

	/**
	 * Get request results
	 */
	public T get();

	/**
	 * Get Error Message
	 * 
	 * @return
	 */
	public String getErrorMessage();

	/**
	 * Gets the tag of request
	 */
	public Object getTag();
}
