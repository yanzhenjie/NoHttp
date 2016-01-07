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

/**
 * </br>
 * Created in Jan 6, 2016 5:19:13 PM
 * 
 * @author YOLANDA;
 */
public class HttpResponse {

	public final boolean isSucceed;
	public final int responseCode;
	public final byte[] responseBody;
	public final Headers responseHeaders;

	public HttpResponse(boolean isSucceed, int responseCode, Headers responseHeaders, byte[] responseBody) {
		this.isSucceed = isSucceed;
		this.responseCode = responseCode;
		this.responseHeaders = responseHeaders;
		this.responseBody = responseBody;
	}
}
