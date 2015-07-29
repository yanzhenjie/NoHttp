/**
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

/**
 * Created in Jul 28, 2015 7:29:26 PM
 * 
 * @author YOLANDA
 */
abstract class BaseRequest {

	/**
	 * Target adress
	 */
	private String url;
	/**
	 * Request method
	 */
	private RequestMethod requestMethod;

	/**
	 * Create reuqest params
	 * 
	 * @param context Application context
	 * @param url Target adress
	 * @param requestMethod Request method
	 */
	BaseRequest(String url, RequestMethod requestMethod) {
		super();
		this.url = url;
		this.requestMethod = requestMethod;
	}

	/**
	 * Get URL
	 * 
	 * @return the url
	 */
	String getUrl() {
		return url;
	}

	/**
	 * Get request method
	 * 
	 * @return the requestMethod
	 */
	RequestMethod getRequestMethod() {
		return requestMethod;
	}

}
