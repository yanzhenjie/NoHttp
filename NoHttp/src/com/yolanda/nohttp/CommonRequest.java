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

import com.yolanda.nohttp.able.Cancelable;
import com.yolanda.nohttp.able.Queueable;
import com.yolanda.nohttp.able.Startable;
import com.yolanda.nohttp.security.Certificate;

/**
 * Created in Nov 4, 2015 8:28:50 AM
 * 
 * @author YOLANDA
 */
public interface CommonRequest extends Queueable, Startable, Cancelable {

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
	 * Objects that can be identified by the network implementation.
	 */
	public abstract BasicAnalyzeRequest getAnalyzeReqeust();

}