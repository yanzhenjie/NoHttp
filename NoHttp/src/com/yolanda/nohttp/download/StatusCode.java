/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.download;

/**
 * Created in Oct 19, 2015 2:46:42 PM
 * @author YOLANDA
 */
public enum StatusCode {

	/**
	 * Network is not available
	 */
	ERROR_NETWORK_NOT_AVAILABLE,
	/**
	 * URL Adress error
	 */
	ERROR_URL_SYNTAX_ERROR,
	/**
	 * Specified folder capacity
	 */
	ERROR_STORAGE_NOT_ENOUGH,
	/**
	 * No server in URL is found in the current network.
	 */
	ERROR_SERVER_NOT_FOUND,
	/**
	 * Server exception
	 */
	ERROR_SERVER_EXCEPTION,
	/**
	 * Download the timeout
	 */
	ERROR_DOWNLOAD_TIMEOUT,
	/**
	 * Other errors
	 */
	ERROR_OTHER

}
