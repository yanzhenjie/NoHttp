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
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yolanda.nohttp.security.Certificate;

import android.text.TextUtils;

/**
 * 
 * Created in Oct 20, 2015 4:24:27 PM
 * 
 * @author YOLANDA
 */
public abstract class RestRequestor<T> implements Request<T>, AnalyzeRequest {

	/**
	 * Target adress
	 */
	protected String url;
	/**
	 * Ever create a url
	 */
	private boolean urlBuilded = false;
	/**
	 * Request method
	 */
	protected int mRequestMethod;
	/**
	 * Connect http timeout
	 */
	private int mConnectTimeout = NoHttp.TIMEOUT_8S;
	/**
	 * Read data timeout
	 */
	private int mReadTimeout = NoHttp.TIMEOUT_8S;
	/**
	 * Request heads
	 */
	private Headers mheaders;
	/**
	 * Https certificate
	 */
	private Certificate mCertificate;
	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 */
	private boolean isAllowHttps = true;
	/**
	 * Param collection
	 */
	protected Map<String, Object> mParamMap = null;
	/**
	 * Post data
	 */
	private String requestBody = "";
	/**
	 * Tag of tag
	 */
	private Object tag;
	/**
	 * Cancel sign
	 */
	private Object cancelSign;

	/**
	 * Has been canceled
	 */
	private boolean isCaneled;

	/**
	 * Create a request, RequestMethod is {@link RequestMethod#Get}
	 * 
	 * @param url request adress, like: http://www.google.com
	 */
	public RestRequestor(String url) {
		this(url, RequestMethod.GET);
	}

	/**
	 * Create a request
	 * 
	 * @param url request adress, like: http://www.google.com
	 * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}
	 */
	public RestRequestor(String url, int requestMethod) {
		BasicConnection.checkRequestMethod(requestMethod);
		if (TextUtils.isEmpty(url))
			throw new IllegalArgumentException("url is null");
		if (requestMethod < RequestMethod.GET || requestMethod > RequestMethod.PATCH)
			throw new IllegalArgumentException("RequestMethod error, value shuld from RequestMethod");
		if (url.regionMatches(true, 0, "ws://", 0, 5)) {
			url = "http" + url.substring(2);
		} else if (url.regionMatches(true, 0, "wss://", 0, 6)) {
			url = "https" + url.substring(3);
		}
		this.url = url;
		this.mRequestMethod = requestMethod;
		this.mheaders = new Headers();
		this.mParamMap = new LinkedHashMap<>();
	}

	@Override
	public final String url() {
		if (!urlBuilded) {
			urlBuilded = true;
			StringBuffer urlBuffer = new StringBuffer(url);
			if (!isOutPut() && mParamMap.size() > 0) {
				StringBuffer paramBuffer = buildReuqestParam();
				if (url.contains("?") && url.contains("=") && paramBuffer.length() > 0)
					urlBuffer.append("&");
				else if (paramBuffer.length() > 0)
					urlBuffer.append("?");
				urlBuffer.append(paramBuffer);
				url = urlBuffer.toString();
			}
		}
		return url;
	}

	@Override
	public final int getRequestMethod() {
		return mRequestMethod;
	}

	@Override
	public void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	@Override
	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	@Override
	public void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	@Override
	public int getReadTimeout() {
		return mReadTimeout;
	}

	@Override
	public void setCertificate(Certificate mCertificate) {
		this.mCertificate = mCertificate;
	}

	@Override
	public Certificate getCertificate() {
		return mCertificate;
	}

	@Override
	public void setAllowHttps(boolean isAllowHttps) {
		this.isAllowHttps = isAllowHttps;
	}

	@Override
	public boolean isAllowHttps() {
		return isAllowHttps;
	}

	@Override
	public void setHeader(String name, String value) {
		mheaders.set(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		mheaders.add(name, value);
	}

	@Override
	public void addCookie(HttpCookie cookie) {
		try {
			URI uri = new URI(url);
			if (HttpCookie.domainMatches(cookie.getDomain(), uri.getHost())) {
				mheaders.add(Headers.HEAD_KEY_COOKIE, cookie.getName() + "=" + cookie.getValue());
			}
		} catch (URISyntaxException e) {
			Logger.throwable(e);
		}
	}

	@Override
	public void addCookie(CookieStore cookieStore) {
		try {
			URI uri = new URI(url);
			List<HttpCookie> httpCookies = cookieStore.get(uri);
			for (HttpCookie cookie : httpCookies) {
				addCookie(cookie);
			}
		} catch (URISyntaxException e) {
			Logger.throwable(e);
		}
	}

	@Override
	public void removeHeader(String name) {
		mheaders.removeAll(name);
	}

	@Override
	public void removeAllHeaders() {
		mheaders.clear();
	}

	@Override
	public void setRequestBody(String data) {
		if (!TextUtils.isEmpty(data) && isOutPut()) {
			mParamMap.clear();
			requestBody = data;
		}
	}

	@Override
	public void add(String key, CharSequence value) {
		mParamMap.put(key, String.valueOf(value));
	}

	@Override
	public void add(String key, int value) {
		mParamMap.put(key, Integer.toString(value));
	}

	@Override
	public void add(String key, long value) {
		mParamMap.put(key, Long.toString(value));
	}

	@Override
	public void add(String key, boolean value) {
		mParamMap.put(key, String.valueOf(value));
	}

	@Override
	public void add(String key, char value) {
		mParamMap.put(key, String.valueOf(value));
	}

	@Override
	public void add(String key, double value) {
		mParamMap.put(key, Double.toString(value));
	}

	@Override
	public void add(String key, float value) {
		mParamMap.put(key, Float.toString(value));
	}

	@Override
	public void add(String key, short value) {
		mParamMap.put(key, Short.toString(value));
	}

	@Override
	public void add(String key, byte value) {
		mParamMap.put(key, Integer.toString(value));
	}

	@Override
	public void add(String key, Binary binary) {
		mParamMap.put(key, binary);
	}

	@Override
	public void add(Map<String, String> params) {
		if (params != null && params.size() > 0)
			this.mParamMap.putAll(params);
	}

	@Override
	public void remove(String key) {
		mParamMap.remove(key);
	}

	@Override
	public void removeAll() {
		mParamMap.clear();
	}

	@Override
	public Headers getHeaders() {
		return this.mheaders;
	}

	@Override
	public String getParamsEncoding() {
		return NoHttp.CHARSET_UTF8;
	}

	@Override
	public final boolean isOutPut() {
		switch (mRequestMethod) {
		case RequestMethod.GET:
			return false;
		case RequestMethod.POST:
		case RequestMethod.PUT:
			return true;
		case RequestMethod.DELETE:// DELETE
		case RequestMethod.HEAD:// HEAD
		case RequestMethod.OPTIONS:// OPTIONS
		case RequestMethod.TRACE:// TRACE
			return false;
		case RequestMethod.PATCH:// PATCH
			return true;
		default:
			return false;
		}
	}

	@Override
	public final byte[] getRequestBody() {
		StringBuffer buffer = buildReuqestParam();
		if (buffer.length() == 0)
			try {
				buffer.append(URLEncoder.encode(requestBody, getParamsEncoding()));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("ParamEncoding Error: " + getParamsEncoding(), e);
			}
		String requestBody = buffer.toString();
		Logger.d("RequestBody: " + requestBody);
		return requestBody.getBytes();
	}

	@Override
	public boolean hasBinary() {
		Set<String> keys = mParamMap.keySet();
		for (String key : keys) {
			Object value = mParamMap.get(key);
			if (value instanceof Binary) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<String> keySet() {
		return mParamMap.keySet();
	}

	@Override
	public Object value(String key) {
		return mParamMap.get(key);
	}

	@Override
	public void setCancelSign(Object sign) {
		this.cancelSign = sign;
	}

	@Override
	public void cancelBySign(Object sign) {
		if (cancelSign == sign)
			cancel();
	}

	@Override
	public void cancel() {
		isCaneled = true;
	}

	@Override
	public boolean isCanceled() {
		return isCaneled;
	}

	@Override
	public void setTag(Object tag) {
		this.tag = tag;
	}

	@Override
	public Object getTag() {
		return this.tag;
	}

	@Override
	public AnalyzeRequest getAnalyzeRequest() {
		return this;
	}

	protected StringBuffer buildReuqestParam() {
		StringBuffer paramBuffer = new StringBuffer();
		boolean first = true;
		Set<String> keySet = mParamMap.keySet();
		for (String key : keySet) {
			Object value = mParamMap.get(key);
			if (value != null && value instanceof CharSequence) {
				if (first) {
					first = false;
				} else {
					paramBuffer.append("&");
				}
				try {
					String paramEncoding = getParamsEncoding();
					paramBuffer.append(URLEncoder.encode(key, paramEncoding));
					paramBuffer.append("=");
					paramBuffer.append(URLEncoder.encode(value.toString(), paramEncoding));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Encoding " + getParamsEncoding() + " format is not supported by the system");
				}
			}
		}
		return paramBuffer;
	}
}
