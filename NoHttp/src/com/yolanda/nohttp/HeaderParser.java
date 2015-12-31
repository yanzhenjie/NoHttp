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
import java.util.HashMap;
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

	/**
	 * Parse Cookie of Response Headers, Only "Set-cookie" and "Set-cookie2" pair will be parsed
	 */
	public static List<HttpCookie> parseResponseCookie(Headers headers) {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		for (int i = 0; headers != null && i < headers.size(); i++) {
			String name = headers.name(i);
			if (name != null && (name.equalsIgnoreCase(Headers.HEAD_KEY_SET_COOKIE) || name.equalsIgnoreCase(Headers.HEAD_KEY_SET_COOKIE2))) {
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
	 * The request for analysis in the Cookie head into two pairs: Cookie and Cookie2
	 */
	public static Map<String, String> parseRequestCookie(Headers headers) {
		Map<String, String> map = new HashMap<String, String>();
		if (headers != null) {
			map.put(Headers.HEAD_KEY_COOKIE, "");
			map.put(Headers.HEAD_KEY_COOKIE2, "");
			for (int i = 0; i < headers.size(); i++) {
				if (Headers.HEAD_KEY_COOKIE.equalsIgnoreCase(headers.name(i))) {
					String cookie = map.get(Headers.HEAD_KEY_COOKIE) + headers.value(i) + "; ";
					map.put(Headers.HEAD_KEY_COOKIE, cookie);
				} else if (Headers.HEAD_KEY_COOKIE2.equalsIgnoreCase(headers.name(i))) {
					String cookie2 = map.get(Headers.HEAD_KEY_COOKIE2) + headers.value(i) + "; ";
					map.put(Headers.HEAD_KEY_COOKIE2, cookie2);
				}
			}
		}
		return map;
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
				List<String> value = entry.getValue();
				if ((Headers.HEAD_KEY_COOKIE.equalsIgnoreCase(key) || Headers.HEAD_KEY_COOKIE2.equalsIgnoreCase(key)) && !value.isEmpty()) {
					headers.add(key, TextUtils.join("; ", value));
				}
			}
	}

	/**
	 * A value of the header information
	 * 
	 * @param content like {@code text/html;charset=utf-8}
	 * @param key like {@code charset}
	 * @param defaultValue list {@code utf-8}
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
