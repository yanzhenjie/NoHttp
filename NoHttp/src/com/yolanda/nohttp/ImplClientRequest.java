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

import com.yolanda.nohttp.able.Finishable;
import com.yolanda.nohttp.able.Queueable;
import com.yolanda.nohttp.able.SignCancelable;
import com.yolanda.nohttp.able.Startable;

/**
 * </br>
 * Created in Dec 21, 2015 4:17:25 PM
 * 
 * @author YOLANDA;
 */
public interface ImplClientRequest extends Queueable, Startable, SignCancelable, Finishable {

	/**
	 * Set proxy server
	 */
	void setProxy(Proxy proxy);

	/**
	 * Set key of request result.
	 * All the data will be saved in the same folder, so you should ensure that key is the only, otherwise the data will
	 * be replaced
	 * 
	 * @param key Unique key
	 */
	void setCacheKey(String key);

	void setSSLSocketFactory(SSLSocketFactory socketFactory);

	/**
	 * Sets the connection timeout time
	 * 
	 * @param connectTimeout timeout number, Unit is a millisecond
	 */
	void setConnectTimeout(int connectTimeout);

	/**
	 * Sets the read timeout time
	 * 
	 * @param readTimeout timeout number, Unit is a millisecond
	 */
	void setReadTimeout(int readTimeout);

	/**
	 * Sets the header named {@code name} to {@code value}. If this request
	 * already has any headers with that name, they are all replaced.
	 */
	void setHeader(String name, String value);

	/**
	 * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued headers like "Cookie".
	 */
	void addHeader(String name, String value);

	/**
	 * Removes a header with {@code name} and {@code value}. If there are multiple keys, will remove all, like "Cookie".
	 */
	void removeHeader(String name);

	/**
	 * Remove all header
	 */
	void removeAllHeaders();

	/**
	 * Settings you want to post data, if the post directly, then other data
	 * will not be sent
	 */
	void setRequestBody(byte[] requestBody);

	/**
	 * Settings you want to post data, if the post directly, then other data will not be sent
	 */
	void setRequestBody(String requestBody);

	/**
	 * Set tag of task, Will return to you at the time of the task response
	 */
	void setTag(Object tag);

}
