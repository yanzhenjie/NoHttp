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

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.yolanda.nohttp.security.Certificate;

import android.text.TextUtils;

/**
 * Created in Nov 4, 2015 8:28:50 AM
 * 
 * @author YOLANDA
 */
public abstract class CommonRequest implements CommonRequestAnalyze {

	/**
	 * Target adress
	 */
	protected String url;
	/**
	 * Request method
	 */
	protected int mRequestMethod;
	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 */
	protected boolean isAllowHttps = true;
	/**
	 * Connect http timeout
	 */
	protected int mConnectTimeout = NoHttp.TIMEOUT_8S;
	/**
	 * Read data timeout
	 */
	protected int mReadTimeout = NoHttp.TIMEOUT_8S;
	/**
	 * Request heads
	 */
	protected Headers mheaders;
	/**
	 * Https certificate
	 */
	protected Certificate mCertificate;
	/**
	 * Queue tag
	 */
	protected boolean inQueue = false;
	/**
	 * The record has started.
	 */
	protected boolean isStart = false;
	/**
	 * Has been canceled
	 */
	protected boolean isCaneled;
	/**
	 * Cancel sign
	 */
	protected Object cancelSign;

	/**
	 * Create a request, RequestMethod is {@link RequestMethod#Get}
	 * 
	 * @param url request adress, like: http://www.google.com
	 */
	public CommonRequest(String url) {
		this(url, RequestMethod.GET);
	}

	/**
	 * Create a request
	 * 
	 * @param url request adress, like: http://www.google.com
	 * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}
	 */
	public CommonRequest(String url, int requestMethod) {
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
		checkRequestMethod(requestMethod);
		this.mRequestMethod = requestMethod;
		this.mheaders = new Headers();
	}

	public void setCertificate(Certificate mCertificate) {
		this.mCertificate = mCertificate;
	}

	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 * 
	 * @param isAllowHttps the isAllowHttps to set
	 */
	public void setAllowHttps(boolean isAllowHttps) {
		this.isAllowHttps = isAllowHttps;
	}

	/**
	 * Sets the connection timeout time
	 * 
	 * @param connectTimeout timeout number
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	/**
	 * Sets the read timeout time
	 * 
	 * @param readTimeout timeout number
	 */
	public void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	/**
	 * Sets the header named {@code name} to {@code value}. If this request
	 * already has any headers with that name, they are all replaced.
	 */
	public void setHeader(String name, String value) {
		mheaders.set(name, value);
	}

	/**
	 * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued headers like "Cookie".
	 */
	public void addHeader(String name, String value) {
		mheaders.add(name, value);
	}

	/**
	 * Add cookie to header
	 */
	public void addCookie(HttpCookie cookie) {
		try {
			URI uri = new URI(url);
			if (HttpCookie.domainMatches(cookie.getDomain(), uri.getHost())) {
				mheaders.add(Headers.HEAD_KEY_COOKIE, cookie.getName() + "=" + cookie.getValue());
			}
		} catch (URISyntaxException e) {
			Logger.e(e);
		}
	}

	/**
	 * Add CookieStore to CookieManager of NoHttp, Will replace the old value
	 */
	public void addCookie(CookieStore cookieStore) {
		try {
			URI uri = new URI(url);
			List<HttpCookie> httpCookies = cookieStore.get(uri);
			for (HttpCookie cookie : httpCookies) {
				addCookie(cookie);
			}
		} catch (URISyntaxException e) {
			Logger.e(e);
		}
	}

	/**
	 * Removes a header with {@code name} and {@code value}. If there are multiple keys, will remove all, like "Cookie".
	 */
	public void removeHeader(String name) {
		mheaders.removeAll(name);
	}

	/**
	 * Remove all header
	 */
	public void removeAllHeaders() {
		mheaders.clear();
	}

	@Override
	public void takeQueue(boolean queue) {
		this.inQueue = queue;
	}

	@Override
	public boolean inQueue() {
		return inQueue;
	}

	@Override
	public void start() {
		this.isStart = true;
	}

	@Override
	public boolean isStarted() {
		return isStart && !isCaneled;
	}

	@Override
	public void cancel() {
		this.isCaneled = true;
		this.isStart = false;
	}

	@Override
	public void reverseCancle() {
		this.isCaneled = false;
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
	public boolean isCanceled() {
		return isCaneled;
	}

	/**
	 * Objects that can be identified by the network implementation.
	 */
	public CommonRequestAnalyze getAnalyzeReqeust() {
		return this;
	}
	
	@Override
	public String url() {
		return url;
	}

	@Override
	public int getRequestMethod() {
		return mRequestMethod;
	}

	@Override
	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	@Override
	public int getReadTimeout() {
		return mReadTimeout;
	}

	@Override
	public boolean isAllowHttps() {
		return isAllowHttps;
	}

	@Override
	public Certificate getCertificate() {
		return mCertificate;
	}

	@Override
	public Headers getHeaders() {
		return this.mheaders;
	}

	@Override
	public boolean isOutPutMethod() {
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
	public String getParamsEncoding() {
		return NoHttp.CHARSET_UTF8;
	}

	/**
	 * Check method request
	 */
	public static void checkRequestMethod(int requestMethod) {
		if (requestMethod < RequestMethod.GET || requestMethod > RequestMethod.PATCH)
			throw new RuntimeException("Invalid HTTP method: " + requestMethod);
	}

}