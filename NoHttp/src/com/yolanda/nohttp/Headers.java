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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Head of http, reqeust or response</br>
 * Created in Oct 8, 2015 10:48:16 AM
 * 
 * @author YOLANDA
 */
public final class Headers {

	public static final String HEAD_KEY_ACCEPT = "Accept";

	public static final String HEAD_VALUE_ACCEPT_All = "*/*";
	
	public static final String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";

	public static final String HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate, sdch";
	
	public static final String HEAD_KYE_ACCEPT_RANGES = "Accept-Ranges";
	
	public static final String HEAD_KEY_CONTENT_TYPE = "Content-Type";

	public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";

	public static final String HEAD_KEY_CONTENT_RANGE = "Content-Range";
	
	public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

	public static final String HEAD_VALUE_CACHE_CONTROL = "no-cache";

	public static final String HEAD_KEY_CONNECTION = "Connection";

	public static final String HEAD_KEY_DATE = "Date";

	public static final String HEAD_KEY_EXPIRES = "Expires";

	public static final String HEAD_KEY_ETAG = "ETag";
	
	public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";
	
	public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";

	public static final String HEAD_KEY_RESPONSE_CODE = "ResponseCode";

	public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";

	public static final String HEAD_VALUE_CONNECTION = "Keep-Alive";

	public static final String HEAD_KEY_USER_AGENT = "User-Agent";

	public static final String HEAD_KEY_COOKIE = "Cookie";

	public static final String HEAD_KEY_COOKIE2 = "Cookie2";

	public static final String HEAD_KEY_SET_COOKIE = "Set-Cookie";

	public static final String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";

	private final List<String> namesAndValues = new ArrayList<String>(20);

	/**
	 * Set a field with the specified value. If the field is not found, it is
	 * added. If the field is found, the existing values are replaced.
	 */
	public void set(String name, String value) {
		checkNameAndValue(name, value);
		removeAll(name);
		addSummation(name, value);
	}

	public void setAll(Headers headers) {
		int size = headers.size();
		for (int i = 0; i < size; i++) {
			set(headers.name(i), headers.value(i));
		}
	}

	/**
	 * Add an header line containing a field name, a literal colon, and a value.
	 */
	public void addLine(String line) {
		int index = line.indexOf(":");
		if (index == -1) {
			throw new IllegalArgumentException("Unexpected header: " + line);
		}
		add(line.substring(0, index).trim(), line.substring(index + 1));
	}

	/**
	 * Add a field with the specified value.
	 */
	public void add(String name, String value) {
		checkNameAndValue(name, value);
		addSummation(name, value);
	}

	/**
	 * Add a field with the specified value without any validation. Only
	 * appropriate for headers from the remote peer or cache.
	 */
	void addSummation(String name, String value) {
		namesAndValues.add(name);
		namesAndValues.add(value.trim());
	}

	/**
	 * Remove a header with {@code name} and {@code value}. If there are multiple keys, will remove all, like "Cookie".
	 */
	public void removeAll(String name) {
		for (int i = 0; i < namesAndValues.size(); i += 2) {
			if (name.equalsIgnoreCase(namesAndValues.get(i))) {
				namesAndValues.remove(i); // name
				namesAndValues.remove(i); // value
				i -= 2;
			}
		}
	}

	private void checkNameAndValue(String name, String value) {
		if (name == null)
			throw new IllegalArgumentException("name == null");
		for (int i = 0, length = name.length(); i < length; i++) {
			char c = name.charAt(i);
			if (c <= '\u001f' || c >= '\u007f') {
				throw new IllegalArgumentException(String.format("Unexpected char %#04x at %d in header name: %s", (int) c, i, name));
			}
		}
		if (value == null)
			throw new IllegalArgumentException("value == null");
		for (int i = 0, length = value.length(); i < length; i++) {
			char c = value.charAt(i);
			if (c <= '\u001f' || c >= '\u007f') {
				throw new IllegalArgumentException(String.format("Unexpected char %#04x at %d in header value: %s", (int) c, i, value));
			}
		}
	}

	/**
	 * Equivalent to {@code build().get(name)}, but potentially faster.
	 */
	public String get(String name) {
		for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
			if (name.equalsIgnoreCase(namesAndValues.get(i))) {
				return namesAndValues.get(i + 1);
			}
		}
		return null;
	}

	/**
	 * Returns the field at {@code position} or null if that is out of range.
	 */
	public String name(int index) {
		int nameIndex = index * 2;// 偶数都是键
		if (nameIndex < 0 || nameIndex >= namesAndValues.size()) {
			return null;
		}
		return namesAndValues.get(nameIndex);
	}

	/**
	 * Returns the value at {@code index} or null if that is out of range.
	 */
	public String value(int index) {
		int valueIndex = index * 2 + 1;// 奇数都是值
		if (valueIndex < 0 || valueIndex >= namesAndValues.size()) {
			return null;
		}
		return namesAndValues.get(valueIndex);
	}

	/**
	 * Returns an immutable case-insensitive set of header names.
	 */
	public Set<String> names() {
		TreeSet<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);// 因为头的关系，不区分大小写升序排列
		for (int i = 0, size = size(); i < size; i++) {
			result.add(name(i));
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Get All Header value
	 */
	public List<String> values(String name) {
		List<String> result = null;
		for (int i = 0, size = namesAndValues.size(); i < size; i++) {
			if (name.equalsIgnoreCase(name(i))) {
				if (result == null)
					result = new ArrayList<String>(2);
				result.add(value(i));
			}
		}
		if (result == null)
			result = Collections.emptyList();
		return Collections.unmodifiableList(result);
	}

	/**
	 * Returns the number of field values.
	 */
	public int size() {
		return namesAndValues.size() / 2;
	}

	public void clear() {
		this.namesAndValues.clear();
	}

}
