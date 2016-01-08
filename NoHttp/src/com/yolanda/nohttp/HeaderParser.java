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

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.tools.HttpDateTime;

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
	public static List<HttpCookie> parseResponseCookie(Headers responseHeaders) {
		List<HttpCookie> cookies = new ArrayList<HttpCookie>();
		for (int i = 0; responseHeaders != null && i < responseHeaders.size(); i++) {
			String name = responseHeaders.name(i);
			if (name != null && (name.equalsIgnoreCase(Headers.HEAD_KEY_SET_COOKIE) || name.equalsIgnoreCase(Headers.HEAD_KEY_SET_COOKIE2))) {
				List<String> cookieValues = responseHeaders.values(name);
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
	public static Map<String, String> parseRequestCookie(Headers requestHeaders) {
		Map<String, String> map = new HashMap<String, String>();
		if (requestHeaders != null) {
			map.put(Headers.HEAD_KEY_COOKIE, "");
			map.put(Headers.HEAD_KEY_COOKIE2, "");
			for (int i = 0; i < requestHeaders.size(); i++) {
				if (Headers.HEAD_KEY_COOKIE.equalsIgnoreCase(requestHeaders.name(i))) {
					String cookie = map.get(Headers.HEAD_KEY_COOKIE) + requestHeaders.value(i) + "; ";
					map.put(Headers.HEAD_KEY_COOKIE, cookie);
				} else if (Headers.HEAD_KEY_COOKIE2.equalsIgnoreCase(requestHeaders.name(i))) {
					String cookie2 = map.get(Headers.HEAD_KEY_COOKIE2) + requestHeaders.value(i) + "; ";
					map.put(Headers.HEAD_KEY_COOKIE2, cookie2);
				}
			}
		}
		return map;
	}

	/**
	 * Parse Header from Map to Headers
	 */
	public static Headers parseMultimap(Map<String, List<String>> headers) {
		Headers returnHeaders = new Headers();
		if (headers != null)
			for (Map.Entry<String, List<String>> headEntry : headers.entrySet()) {
				String name = headEntry.getKey();
				for (String value : headEntry.getValue())
					returnHeaders.add(name, value);
			}
		return returnHeaders;
	}

	/**
	 * Add "Cookie" and "Cookie2" of the headers to request header
	 * 
	 * @param requestHeaders From the source
	 * @param cookieHeader To be added to the destination
	 */
	public static void addCookiesToHeaders(Headers requestHeaders, Map<String, List<String>> cookieHeader) {
		if (cookieHeader != null && requestHeaders != null)
			for (Map.Entry<String, List<String>> entry : cookieHeader.entrySet()) {
				String key = entry.getKey();
				List<String> value = entry.getValue();
				if ((Headers.HEAD_KEY_COOKIE.equalsIgnoreCase(key) || Headers.HEAD_KEY_COOKIE2.equalsIgnoreCase(key)) && !value.isEmpty()) {
					requestHeaders.add(key, TextUtils.join("; ", value));
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

	/**
	 * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
	 *
	 * @param response The network response to parse headers from
	 * @param byteArray The network response to parse body from
	 * @return a cache entrance for the given response, or null if the response is not cacheable.
	 */
	public static Cache.Entrance parseCacheHeaders(Headers responseHeaders, byte[] responseBody) {
		long now = System.currentTimeMillis();

		long serverDate = 0;
		long lastModified = 0;
		long serverExpires = 0;
		long softExpire = 0;
		long finalExpire = 0;
		long maxAge = 0;
		long staleWhileRevalidate = 0;
		boolean hasCacheControl = false;
		boolean mustRevalidate = false;

		String serverEtag = null;
		String headerValue;

		headerValue = responseHeaders.get(Headers.HEAD_KEY_DATE);
		if (headerValue != null) {
			serverDate = parseDateAsEpoch(headerValue);
		}

		headerValue = responseHeaders.get(Headers.HEAD_KEY_CACHE_CONTROL);
		if (headerValue != null) {
			hasCacheControl = true;
			String[] tokens = headerValue.split(",");
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i].trim();
				if (token.equals("no-cache") || token.equals("no-store")) {
					return null;
				} else if (token.startsWith("max-age=")) {
					try {
						maxAge = Long.parseLong(token.substring(8));
					} catch (Exception e) {
					}
				} else if (token.startsWith("stale-while-revalidate=")) {
					try {
						staleWhileRevalidate = Long.parseLong(token.substring(23));
					} catch (Exception e) {
					}
				} else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
					mustRevalidate = true;
				}
			}
		}

		headerValue = responseHeaders.get(Headers.HEAD_KEY_EXPIRES);
		if (headerValue != null) {
			serverExpires = parseDateAsEpoch(headerValue);
		}

		headerValue = responseHeaders.get(Headers.HEAD_KEY_LAST_MODIFIED);
		if (headerValue != null) {
			lastModified = parseDateAsEpoch(headerValue);
		}

		serverEtag = responseHeaders.get(Headers.HEAD_KEY_ETAG);

		// Cache-Control takes precedence over an Expires header, even if both exist and Expires
		// is more restrictive.
		if (hasCacheControl) {
			softExpire = now + maxAge * 1000;
			finalExpire = mustRevalidate ? softExpire : softExpire + staleWhileRevalidate * 1000;
		} else if (serverDate > 0 && serverExpires >= serverDate) {
			// Default semantic for Expire header in HTTP specification is softExpire.
			softExpire = now + (serverExpires - serverDate);
			finalExpire = softExpire;
		}

		Cache.Entrance entrance = new Cache.Entrance();
		entrance.data = responseBody;
		entrance.etag = serverEtag;
		entrance.softTtl = softExpire;
		entrance.ttl = finalExpire;
		entrance.serverDate = serverDate;
		entrance.lastModified = lastModified;
		entrance.responseHeaders = responseHeaders;

		return entrance;
	}

	/**
	 * Parse date in RFC1123 format, and return its value as epoch
	 */
	private static long parseDateAsEpoch(String dateStr) {
		try {
			return HttpDateTime.parseToMillis(dateStr);
		} catch (IllegalArgumentException e) {
			return 0;
		}
	}

}
