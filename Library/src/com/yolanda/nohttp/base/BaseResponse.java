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
package com.yolanda.nohttp.base;

import com.yolanda.nohttp.ResponseCode;

/**
 * Created in Jul 28, 2015 7:30:27 PM
 * 
 * @author YOLANDA
 */
public abstract class BaseResponse {

	/**
	 * requst code
	 */
	private ResponseCode responseCode = ResponseCode.NONE;

	/**
	 * The request is successful
	 * 
	 * @return
	 */
	public boolean isSuccessful() {
		return responseCode == ResponseCode.CODE_SUCCESSFUL;
	}

	/**
	 * Returns the response code returned by the remote HTTP server.
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * Set the response code
	 * 
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

}
