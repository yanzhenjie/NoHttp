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

import com.yolanda.nohttp.security.Certificate;

/**
 * Request parameter interface for creating HTTP connections</br>
 * Created in Oct 21, 2015 10:22:49 AM
 * 
 * @author YOLANDA
 */
public interface BasicAnalyzeRequest {

	/**
	 * Return url of request
	 */
	public abstract String url();

	/**
	 * return method of request
	 */
	public abstract int getRequestMethod();

	/**
	 * If the request is HTTPS, and the {@link #isAllowHttps()} return false, then the certificate must be returned, otherwise HTTPS cannot be accessed.
	 */
	public abstract Certificate getCertificate();

	/**
	 * If you are allowed to access the Https directly, then the true will be returned if the certificate is required to return false
	 */
	public abstract boolean isAllowHttps();

	/**
	 * Get the connection timeout time
	 */
	public abstract int getConnectTimeout();

	/**
	 * Get the read timeout time
	 */
	public abstract int getReadTimeout();

	/**
	 * Get all Heads
	 */
	public abstract Headers getHeaders();

	/**
	 * Get Encoding of request param
	 */
	public abstract String getParamsEncoding();

	/**
	 * If the request is POST, PUT, PATCH, the true should be returned.
	 */
	public abstract boolean isOutPut();

	/**
	 * If the argument contains File, Bitmap, ByteArrayOutputStream, true
	 */
	public abstract boolean hasBinary();

}
