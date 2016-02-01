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

import com.yolanda.nohttp.tools.Writer;

/**
 * Analytical {@link ImplClientRequest} NoHttp interface
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
	 * NoHttp need to cache
	 */
	boolean needCache();

	/**
	 * Get key of cache data
	 */
	String getCacheKey();

	/**
	 * Get proxy server
	 */
	Proxy getProxy();

	/**
	 * Get SSLSocketFactory
	 */
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
	 * Get thre redirect handler
	 */
	RedirectHandler getRedirectHandler();

	/**
	 * Get all Heads
	 */
	Headers headers();

	/**
	 * The client wants to accept data types
	 */
	String getAccept();

	/**
	 * The client wants to accept data encoding format
	 */
	String getAcceptCharset();

	/**
	 * The client wants to accept data language types
	 */
	String getAcceptLanguage();

	/**
	 * The length of the request body
	 */
	long getContentLength();

	/**
	 * The type of the request body
	 */
	String getContentType();

	/**
	 * The {@code UseAgent} of the client
	 */
	String getUserAgent();

	/**
	 * Call before perform request, here you can rebuild the request object
	 */
	void onPreExecute();

	/**
	 * Send request body data
	 */
	void onWriteRequestBody(Writer writer);

	/**
	 * should to return the tag of the object
	 */
	Object getTag();

}
