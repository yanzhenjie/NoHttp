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

import java.net.Proxy;

import javax.net.ssl.SSLSocketFactory;

import com.yolanda.nohttp.util.Writer;

/**
 * </br>
 * Created in Dec 21, 2015 3:34:59 PM
 * 
 * @author YOLANDA;
 */
public interface ImplServerRequest {

	/**
	 * Return url of request
	 */
	String url();

	/**
	 * return method of request
	 */
	RequestMethod getRequestMethod();

	/**
	 * Do you need to cache
	 */
	boolean needCache();

	/**
	 * Get of cache data
	 */
	String getCacheKey();

	/**
	 * Get proxy server
	 */
	Proxy getProxy();

	SSLSocketFactory getSSLSocketFactory();

	/**
	 * If the request is POST, PUT, PATCH, the true should be returned.
	 */
	boolean doOutPut();

	/**
	 * Get the connection timeout time, Unit is a millisecond
	 */
	int getConnectTimeout();

	/**
	 * Get the read timeout time, Unit is a millisecond
	 */
	int getReadTimeout();

	/**
	 * Get all Heads
	 */
	Headers headers();

	String getAccept();

	String getAcceptLanguage();

	/**
	 * Get content length
	 */
	long getContentLength();

	String getContentType();

	String getUserAgent();

	/**
	 * When you start the request
	 */
	void onPreExecute();

	/**
	 * Send request data, give priority to RequestBody, and then send the form data
	 */
	void onWriteRequestBody(Writer writer);

	/**
	 * Get get of this request
	 */
	Object getTag();

}
