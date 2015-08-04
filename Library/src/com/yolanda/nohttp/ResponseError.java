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

import java.io.Serializable;

import com.yolanda.nohttp.base.BaseResponse;

/**
 * Created in Jul 28, 2015 9:37:22 PM
 * 
 * @author YOLANDA
 */
public class ResponseError extends BaseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 102L;
	/**
	 * error info
	 */
	private String errorInfo;

	/**
	 * @return the errorInfo
	 */
	public String getErrorInfo() {
		return errorInfo;
	}

	/**
	 * @param errorInfo the errorInfo to set
	 */
	void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

}
