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
public interface Response<T> {

	/**
	 * url
	 */
	String url();

	/**
	 * Ask for success
	 */
	boolean isSucceed();

	/**
	 * Get http response headers
	 */
	Headers getHeaders();

	/**
	 * Get http response Cookie
	 */
	List<HttpCookie> getCookies();

	/**
	 * Get raw data
	 */
	byte[] getByteArray();

	/**
	 * Get request results
	 */
	T get();

	/**
	 * Get Error Message
	 * 
	 * @return
	 */
	String getErrorMessage();

	/**
	 * Gets the tag of request
	 */
	Object getTag();

	/**
	 * Gets the millisecond of request
	 */
	long getNetworkMillis();
}
