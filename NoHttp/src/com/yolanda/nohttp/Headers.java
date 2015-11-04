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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import android.text.TextUtils;

/**
 * Head of http, reqeust or response</br>
 * Created in Oct 8, 2015 10:48:16 AM
 * 
 * @author YOLANDA
 */
public final class Headers {

	public static final String HEAD_KEY_ACCEPT = "Accept";

	public static final String HEAD_VALUE_ACCEPT = "*/*";

	public static final String HEAD_KEY_CONTENT_TYPE = "Content-Type";

	public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";

	public static final String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";

	public static final String HEAD_VALUE_ACCEPT_ENCODING = "gzip";

	public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

	public static final String HEAD_VALUE_CACHE_CONTROL = "no-cache";

	public static final String HEAD_KEY_CONNECTION = "Connection";

	public static final String HEAD_VALUE_CONNECTION = "Keep-Alive";

	public static final String HEAD_KEY_USER_AGENT = "User-Agent";

	public static final String HEAD_KEY_COOKIE = "Cookie";

	public static final String HEAD_KEY_COOKIE2 = "Cookie2";

	private final List<String> namesAndValues = new ArrayList<>(20);

	/**
	 * Set a field with the specified value. If the field is not found, it is
	 * added. If the field is found, the existing values are replaced.
	 */
	public void set(String name, String value) {
		checkNameAndValue(name, value);
		removeAll(name);
		addSummation(name, value);
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
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("name == null or name is empty");
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
		TreeSet<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);// 因为头的关系，不区分大小写升序排列
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
					result = new ArrayList<>(2);
				result.add(value(i));
			}
		}
		return result != null ? Collections.unmodifiableList(result) : Collections.<String> emptyList();
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

	private static final Comparator<String> FIELD_NAME_COMPARATOR = new Comparator<String>() {
		// @FindBugsSuppressWarnings("ES_COMPARING_PARAMETER_STRING_WITH_EQ")
		@Override
		public int compare(String a, String b) {
			if (a == b) {
				return 0;
			} else if (a == null) {
				return -1;
			} else if (b == null) {
				return 1;
			} else {
				return String.CASE_INSENSITIVE_ORDER.compare(a, b);
			}
		}
	};

	/**
	 * Get all the requests
	 */
	public static Map<String, List<String>> toMultimap(Headers headers) {
		Map<String, List<String>> result = new TreeMap<>(FIELD_NAME_COMPARATOR);
		for (int i = 0, size = headers.size(); i < size && headers != null; i++) {
			String name = headers.name(i);
			String value = headers.value(i);

			List<String> allValues = new ArrayList<>();
			List<String> otherValues = result.get(name);
			if (otherValues != null)
				allValues.addAll(otherValues);
			allValues.add(value);
			result.put(name, Collections.unmodifiableList(allValues));
		}
		return Collections.unmodifiableMap(result);
	}

	/**
	 * Parse Header from Map to {@link Headers}
	 */
	public static Headers parseMultimap(Map<String, List<String>> headers) {
		Headers returnHeaders = new Headers();
		if (headers != null)
			for (Map.Entry<String, List<String>> headEntry : headers.entrySet()) {
				String name = headEntry.getKey();
				if (!TextUtils.isEmpty(name))
					for (String value : headEntry.getValue())
						if (!TextUtils.isEmpty(value))
							returnHeaders.add(name, value);
			}
		return returnHeaders;
	}

	/**
	 * Add "Cookie" of the headers to request header
	 */
	public static void addCookiesToHeaders(Headers headers, Map<String, List<String>> cookieHeader) {
		if (cookieHeader != null && headers != null)
			for (Map.Entry<String, List<String>> entry : cookieHeader.entrySet()) {
				String key = entry.getKey();
				if ((HEAD_KEY_COOKIE.equalsIgnoreCase(key) || HEAD_KEY_COOKIE2.equalsIgnoreCase(key)) && !entry.getValue().isEmpty())
					headers.add(key, buildCookieHeader(entry.getValue()));
			}
	}

	/**
	 * The request for analysis in the Cookie head into two pairs: Cookie and Cookie2
	 */
	public static Map<String, String> parseRequestCookie(Headers headers) {
		Map<String, String> map = new HashMap<>();
		if (headers != null) {
			map.put(HEAD_KEY_COOKIE, "");
			map.put(HEAD_KEY_COOKIE2, "");
			if (headers != null && headers.size() > 0) {
				for (int i = 0; i < headers.size(); i++) {
					if (HEAD_KEY_COOKIE.equalsIgnoreCase(headers.name(i))) {
						String cookie = map.get(HEAD_KEY_COOKIE) + headers.value(i) + "; ";
						map.put(HEAD_KEY_COOKIE, cookie);
					} else if (HEAD_KEY_COOKIE2.equalsIgnoreCase(headers.name(i))) {
						String cookie2 = map.get(HEAD_KEY_COOKIE2) + headers.value(i) + "; ";
						map.put(HEAD_KEY_COOKIE2, cookie2);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Send all cookies in one big header, as recommended by
	 * <a href="http://tools.ietf.org/html/rfc6265#section-4.2.1">RFC 6265</a>.
	 */
	public static String buildCookieHeader(List<String> cookies) {
		if (cookies.size() == 1)
			return cookies.get(0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0, size = cookies.size(); i < size; i++) {
			if (i > 0)
				sb.append("; ");
			sb.append(cookies.get(i));
		}
		return sb.toString();
	}
}
