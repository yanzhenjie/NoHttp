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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import android.text.TextUtils;

/**
 * Http header information analysis class</br>
 * Created in Oct 10, 2015 4:58:30 PM
 * 
 * @author YOLANDA
 */
public class HeaderParser {

	private static final String VERSION_ZERO_HEADER = "Set-cookie";

	private static final String VERSION_ONE_HEADER = "Set-cookie2";

	/**
	 * Analysis of cookie from the server side
	 */
	public static List<HttpCookie> parseCookie(Map<String, List<String>> responseHeaders) {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		if (responseHeaders != null)
			for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
				String key = entry.getKey();
				if (key != null && (key.equalsIgnoreCase(VERSION_ZERO_HEADER) || key.equalsIgnoreCase(VERSION_ONE_HEADER))) {
					for (String cookieStr : entry.getValue()) {
						try {
							for (HttpCookie cookie : HttpCookie.parse(cookieStr)) {
								cookies.add(cookie);
							}
						} catch (IllegalArgumentException e) {
							Logger.w(e);
						}
					}
				}
			}
		return cookies;
	}

	/**
	 * Parse Cookie of Response Headers, Only "Set-cookie" and "Set-cookie2" pair will be parsed
	 */
	public static List<HttpCookie> parseCookie(Headers headers) {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		for (int i = 0; headers != null && i < headers.size(); i++) {
			String name = headers.name(i);
			if (name != null && (name.equalsIgnoreCase(VERSION_ZERO_HEADER) || name.equalsIgnoreCase(VERSION_ONE_HEADER))) {
				List<String> cookieValues = headers.values(name);
				for (String cookieStr : cookieValues) {
					try {
						for (HttpCookie cookie : HttpCookie.parse(cookieStr)) {// 这里解析的是set-cookie2和set-cookie
							cookies.add(cookie);
						}
					} catch (IllegalArgumentException e) {
						Logger.w(e);
					}
				}
			}
		}
		return cookies;
	}

	/**
	 * A value of the header information
	 * 
	 * @param responseHeaders http Response Headers
	 * @param key like <code>charset</code>
	 * @param defaultValue list <code>utf-8</code>
	 * @return If you have a value key, you will return the parsed value if you don't return the default value
	 */
	public static String parseHeadValue(Map<String, List<String>> responseHeaders, String key, String defaultValue) {
		if (responseHeaders != null)
			for (String name : responseHeaders.keySet()) {
				List<String> values = responseHeaders.get(name);
				String result = parseHeadValue(values, key, "");
				if (TextUtils.isEmpty(result)) {
					continue;
				} else {
					defaultValue = result;
					break;
				}
			}
		return defaultValue;
	}

	/**
	 * A value of the header information
	 * 
	 * @param responseHeaders http Response Headers
	 * @param key like <code>charset</code>
	 * @param defaultValue list <code>utf-8</code>
	 * @return If you have a value key, you will return the parsed value if you don't return the default value
	 */
	public static String parseHeadValue(Headers responseHeaders, String key, String defaultValue) {
		if (responseHeaders != null)
			for (String name : responseHeaders.names()) {
				List<String> values = responseHeaders.values(name);
				String result = parseHeadValue(values, key, "");
				if (TextUtils.isEmpty(result)) {
					continue;
				} else {
					defaultValue = result;
					break;
				}
			}
		return defaultValue;
	}

	/**
	 * A value of value of one of the corresponding heads of Http
	 * 
	 * @param responseHeaderValues Value of one of the corresponding heads of Http
	 * @param key like <code>charset</code>
	 * @param defaultValue list<code>utf-8</code>
	 * @return If you have a value key, you will return the parsed value if you don't return the default value
	 */
	public static String parseHeadValue(List<String> responseHeaderValues, String key, String defaultValue) {
		for (int i = 0; responseHeaderValues != null && i < responseHeaderValues.size(); i++) {
			String result = parseHeadValue(responseHeaderValues.get(i), key, "");
			if (TextUtils.isEmpty(result)) {
				continue;
			} else {
				defaultValue = result;
				break;
			}
		}
		return defaultValue;
	}

	/**
	 * A value of the header information
	 * 
	 * @param content like <code>text/html;charset=utf-8</code>
	 * @param key like <code>charset</code>
	 * @param defaultValue list<code>utf-8</code>
	 * @return If you have a value key, you will return the parsed value if you don't return the default value
	 */
	public static String parseHeadValue(String content, String key, String defaultValue) {
		if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(key)) {
			StringTokenizer stringTokenizer = new StringTokenizer(content, ";");
			while (stringTokenizer.hasMoreElements()) {
				String valuePair = stringTokenizer.nextToken();
				int index = valuePair.indexOf('=');
				if (index > 0) {
					String name = valuePair.substring(0, index).trim();
					if (key.equalsIgnoreCase(name)) {
						defaultValue = valuePair.substring(index + 1).trim();
						break;
					}
				}
			}
		}
		return defaultValue;
	}

	/**
	 * Whether the content has been compressed
	 */
	public static boolean isGzipContent(String contentEncoding) {
		return contentEncoding != null && contentEncoding.toLowerCase(Locale.getDefault()).contains("gzip");
	}

}
