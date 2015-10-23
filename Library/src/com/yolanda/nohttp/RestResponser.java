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

import java.net.HttpCookie;
import java.util.List;
import java.util.Set;

/**
 * In response to the class, use generic compatibility with all I to type, and put the parsing operation in Request</br>
 * Created in Oct 12, 2015 1:00:46 PM
 * 
 * @author YOLANDA
 */
public class RestResponser<T> implements Response<T> {

	/**
	 * Corresponding request URL
	 */
	private final String url;

	/**
	 * Ask for success
	 */
	private final boolean isSucceed;

	/**
	 * Http response code
	 */
	private final int responseCode;

	/**
	 * Http response Headers
	 */
	private final Headers headers;

	/**
	 * Http response content, It is an error message when it happens.
	 */
	private final byte[] byteArray;

	/**
	 * Corresponding request TAG
	 */
	private final Object tag;

	/**
	 * Corresponding response results
	 */
	private final T result;

	public RestResponser(String url, boolean isSucceed, int responseCode, Headers headers, byte[] byteArray, Object tag, T result) {
		this.url = url;
		this.isSucceed = isSucceed;
		this.responseCode = responseCode;
		this.headers = headers;
		this.byteArray = byteArray;
		this.tag = tag;
		this.result = result;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public boolean isSucceed() {
		return this.isSucceed;
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public Headers getHeaders() {
		return headers;
	}

	@Override
	public List<String> getHeaders(String key) {
		if (headers == null)
			return null;
		return headers.values(key);
	}

	@Override
	public String getContentType() {
		if (headers == null)
			return null;
		return headers.get(Headers.HEAD_KEY_CONTENT_TYPE);
	}

	@Override
	public int getContentLength() {
		int contentLength = 0;
		try {
			if (headers != null)
				contentLength = Integer.valueOf(headers.get(Headers.HEAD_KEY_CONTENT_LENGTH));
		} catch (NumberFormatException e) {
			Logger.throwable(e);
		}
		return contentLength;
	}

	@Override
	public List<HttpCookie> getCookies() {
		return HeaderParser.parseCookie(headers);
	}

	@Override
	public byte[] getByteArray() {
		return this.byteArray;
	}

	@Override
	public Object getTag() {
		return this.tag;
	}

	@Override
	public T get() {
		return result;
	}

	@Override
	public String getErrorMessage() {
		StringBuilder resultBuilder = new StringBuilder();
		if (!isSucceed && byteArray != null && byteArray.length > 0) {
			resultBuilder.append(new String(byteArray));
		}
		return resultBuilder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Headers headers = getHeaders();
		if (headers != null) {
			Set<String> keys = headers.names();
			for (String key : keys) {
				List<String> values = headers.values(key);
				for (String value : values) {
					if (key != null) {
						builder.append(key).append(": ");
					}
					builder.append(value).append("\n");
				}
			}
		}
		T result = get();
		if (result != null)
			builder.append(result.toString());
		return builder.toString();
	}
}
