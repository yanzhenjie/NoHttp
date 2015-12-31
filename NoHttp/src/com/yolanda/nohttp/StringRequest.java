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

import java.io.UnsupportedEncodingException;

/**
 * Created in Jul 28, 2015 7:33:52 PM
 * 
 * @author YOLANDA
 */
public class StringRequest extends RestRequestor<String> {

	public StringRequest(String url) {
		this(url, RequestMethod.GET);
	}

	public StringRequest(String url, RequestMethod requestMethod) {
		super(url, requestMethod);
	}

	@Override
	public String parseResponse(String url, String contentType, byte[] byteArray) {
		String result = null;
		if (byteArray != null && byteArray.length > 0) {
			try {
				String charset = HeaderParser.parseHeadValue(contentType, "charset", "");
				result = new String(byteArray, charset);
			} catch (UnsupportedEncodingException e) {
				Logger.w("Charset error in ContentType returned by the server：" + contentType);
				result = new String(byteArray);
			}
		}
		return result;
	}
}
