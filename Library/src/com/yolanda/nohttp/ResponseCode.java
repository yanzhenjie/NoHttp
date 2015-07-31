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
 * Created in Jul 28, 2015 7:34:11 PM
 * 
 * @author YOLANDA
 */
public enum ResponseCode {
	/**
	 * The Request is successful
	 */
	CODE_SUCCESSFUL,
	/**
	 * Is not a correct url returns
	 */
	CODE_ERROR_URL,
	/**
	 * Return when the server response timeout
	 */
	CODE_ERROR_TIMEOUT,
	/**
	 * Can't find the server
	 */
	CODE_ERROR_NOSERVER,
	/**
	 * The Manifest. XML lack Intnet permissions
	 */
	CODE_ERROR_INTNET_PERMISSION,
	/**
	 * Other abnormal returns
	 */
	CODE_ERROR_OTHER,
	/**
	 * No CODE_ occurs
	 */
	NONE
}
