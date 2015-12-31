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

import java.io.OutputStream;

import com.yolanda.nohttp.able.Queueable;
import com.yolanda.nohttp.able.SignCancelable;
import com.yolanda.nohttp.able.Startable;
import com.yolanda.nohttp.security.Certificate;

/**
 * </br>
 * Created in Dec 21, 2015 3:34:59 PM
 * 
 * @author YOLANDA;
 */
public interface BasicRequest extends Queueable, Startable, SignCancelable {

	/**
	 * Return url of request
	 */
	String url();

	/**
	 * return method of request
	 */
	RequestMethod getRequestMethod();

	/**
	 * If you are allowed to access the Https directly, then the true will be returned if the certificate is required to
	 * return false
	 */
	boolean isAllowHttps();

	/**
	 * If the request is HTTPS, and the {@link #isAllowHttps()} return false, then the certificate must be returned,
	 * otherwise HTTPS cannot be accessed.
	 */
	Certificate getCertificate();

	/**
	 * If the request is POST, PUT, PATCH, the true should be returned.
	 */
	boolean isOutPutMethod();

	/**
	 * Get the connection timeout time, Unit is a millisecond
	 */
	int getConnectTimeout();

	/**
	 * Get the read timeout time, Unit is a millisecond
	 */
	int getReadTimeout();

	/**
	 * If the argument contains {@code Binary}
	 */
	boolean hasBinary();

	/**
	 * Get Boundary of data
	 */
	String getBoundary();

	/**
	 * Get Encoding of request param
	 */
	String getParamsEncoding();

	/**
	 * Get content length
	 */
	long getContentLength();

	/**
	 * Get all Heads
	 */
	Headers headers();

	/**
	 * When you start the request
	 */
	void onPreExecute();

	/**
	 * Send request data, give priority to RequestBody, and then send the form data
	 */
	void onWriteRequestBody(OutputStream outputStream);

	/**
	 * Get get of this request
	 */
	Object getTag();

	/**
	 * Get of cache data
	 */
	String getCacheKey();

}
