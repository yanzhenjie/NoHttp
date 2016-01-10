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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Set;

import com.yolanda.nohttp.security.Certificate;
import com.yolanda.nohttp.tools.CounterOutputStream;

import android.text.TextUtils;

/**
 * Created in Nov 4, 2015 8:28:50 AM
 * 
 * @author YOLANDA
 */
public abstract class CommonRequest<T> implements ImplRequest, BasicRequest {

	protected final String BOUNDARY = createBoundry();
	protected final String START_BOUNDARY = "--" + BOUNDARY;
	protected final String END_BOUNDARY = START_BOUNDARY + "--";

	/**
	 * Target adress
	 */
	private String url;
	private String buildUrl;
	/**
	 * Request method
	 */
	private RequestMethod mRequestMethod;
	/**
	 * Proxy server
	 */
	private Proxy mProxy;
	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 */
	private boolean isAllowHttps = true;
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
	private Headers mHeaders;
	/**
	 * Https certificate
	 */
	private Certificate mCertificate;
	/**
	 * RequestBody
	 */
	private byte[] mRequestBody;
	/**
	 * Queue tag
	 */
	private boolean inQueue = false;
	/**
	 * The record has started.
	 */
	private boolean isStart = false;
	/**
	 * Has been canceled
	 */
	private boolean isCaneled = false;
	/**
	 * Cancel sign
	 */
	private Object cancelSign;
	/**
	 * Tag of request
	 */
	private Object mTag;

	/* ===== Cache ===== */

	/**
	 * Cache key
	 */
	private String mCacheKey;

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
	public CommonRequest(String url, RequestMethod requestMethod) {
		if (TextUtils.isEmpty(url))
			throw new IllegalArgumentException("url is null");
		this.url = url;
		this.mRequestMethod = requestMethod;
		this.mHeaders = new HttpHeaders();
	}

	@Override
	public String url() {
		if (TextUtils.isEmpty(buildUrl))
			buildUrl = buildUrl();
		return buildUrl;
	}

	/**
	 * Rebuilding the URL, compatible with the GET method, using {@code request.add(key, value);}
	 */
	protected final String buildUrl() {
		StringBuffer urlBuffer = new StringBuffer(url);
		if (!isOutPutMethod() && keySet().size() > 0) {
			StringBuffer paramBuffer = buildCommonParams();
			if (url.contains("?") && url.contains("=") && paramBuffer.length() > 0)
				urlBuffer.append("&");
			else if (paramBuffer.length() > 0)
				urlBuffer.append("?");
			urlBuffer.append(paramBuffer);
		}
		return urlBuffer.toString();
	}

	@Override
	public RequestMethod getRequestMethod() {
		return mRequestMethod;
	}

	@Override
	public void setProxy(Proxy proxy) {
		this.mProxy = proxy;
	}

	@Override
	public Proxy getProxy() {
		return mProxy;
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
	public void setCertificate(Certificate mCertificate) {
		this.mCertificate = mCertificate;
	}

	@Override
	public Certificate getCertificate() {
		return mCertificate;
	}

	@Override
	public boolean isOutPutMethod() {
		switch (mRequestMethod) {
		case GET:
			return false;
		case POST:
		case PUT:
			return true;
		case DELETE:
		case HEAD:
		case OPTIONS:
		case TRACE:
			return false;
		case PATCH:
			return true;
		default:
			return false;
		}
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
	public void setHeader(String name, String value) {
		mHeaders.set(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		mHeaders.add(name, value);
	}

	@Override
	public Headers headers() {
		return this.mHeaders;
	}

	@Override
	public void removeHeader(String name) {
		mHeaders.remove(name);
	}

	@Override
	public void removeAllHeaders() {
		mHeaders.clear();
	}

	@Override
	public long getContentLength() {
		CounterOutputStream outputStream = new CounterOutputStream();
		if (mRequestBody == null && hasBinary()) {
			writeFormStreamData(outputStream);
		} else if (mRequestBody == null) {
			writeCommonStreamData(outputStream);
		} else {
			writePushBody(outputStream);
		}
		long contentLength = outputStream.get();
		return contentLength;
	}

	@Override
	public final String getBoundary() {
		return BOUNDARY;
	}

	@Override
	public String getParamsEncoding() {
		return NoHttp.CHARSET_UTF8;
	}

	@Override
	public boolean hasBinary() {
		Set<String> keys = keySet();
		for (String key : keys) {
			Object value = value(key);
			if (value instanceof Binary) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setRequestBody(byte[] requestBody) {
		this.mRequestBody = requestBody;
	}

	@Override
	public void setRequestBody(String requestBody) {
		if (!TextUtils.isEmpty(requestBody))
			try {
				this.mRequestBody = URLEncoder.encode(requestBody, getParamsEncoding()).getBytes(NoHttp.CHARSET_UTF8);
			} catch (UnsupportedEncodingException e) {
				Logger.e(e);
			}
	}

	@Override
	public void onPreExecute() {
	}

	@Override
	public void onWriteRequestBody(OutputStream outputStream) {
		if (mRequestBody == null && hasBinary())
			writeFormStreamData(outputStream);
		else if (mRequestBody == null)
			writeCommonStreamData(outputStream);
		else
			writePushBody(outputStream);
	}

	/**
	 * Send request {@code RequestBody}
	 */
	protected void writePushBody(OutputStream outputStream) {
		try {
			Logger.d("RequestBody: " + mRequestBody);
			outputStream.write(mRequestBody);
		} catch (IOException e) {
			Logger.e(e);
		}
	}

	/**
	 * Send non form data
	 */
	protected void writeCommonStreamData(OutputStream outputStream) {
		String requestBody = buildCommonParams().toString();
		Logger.d("RequestBody: " + requestBody);
		try {
			outputStream.write(requestBody.getBytes());
		} catch (IOException e) {
			Logger.e(e);
		}
	}

	/**
	 * split joint non form data
	 */
	protected StringBuffer buildCommonParams() {
		StringBuffer paramBuffer = new StringBuffer();
		Set<String> keySet = keySet();
		for (String key : keySet) {
			Object value = value(key);
			if (value != null && value instanceof CharSequence) {
				paramBuffer.append("&");
				String paramEncoding = getParamsEncoding();
				try {
					paramBuffer.append(URLEncoder.encode(key, paramEncoding));
					paramBuffer.append("=");
					paramBuffer.append(URLEncoder.encode(value.toString(), paramEncoding));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Encoding " + getParamsEncoding() + " format is not supported by the system");
				}
			}
		}
		if (paramBuffer.length() > 0)
			paramBuffer.deleteCharAt(0);
		return paramBuffer;
	}

	/**
	 * Send form data
	 */
	protected void writeFormStreamData(OutputStream outputStream) {
		try {
			Set<String> keys = keySet();
			for (String key : keys) {// 文件或者图片
				Object value = value(key);
				if (value != null && value instanceof CharSequence) {
					writeFormString(outputStream, key, value.toString());
				} else if (value != null && value instanceof Binary) {
					writeFormBinary(outputStream, key, (Binary) value);
				}
			}
			outputStream.write(("\r\n" + END_BOUNDARY + "\r\n").getBytes());
		} catch (IOException e) {
			Logger.e(e);
		}
	}

	/**
	 * Send text data in a form
	 */
	private void writeFormString(OutputStream outputStream, String key, String value) throws IOException {
		Logger.i(key + " = " + value);

		StringBuilder stringFieldBuilder = new StringBuilder(START_BOUNDARY).append("\r\n");

		stringFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n");
		stringFieldBuilder.append("Content-Type: text/plain; charset=").append(getParamsEncoding()).append("\r\n\r\n");

		outputStream.write(stringFieldBuilder.toString().getBytes());

		outputStream.write(value.getBytes());
		outputStream.write("\r\n".getBytes());
	}

	/**
	 * Send binary data in a form
	 */
	private void writeFormBinary(OutputStream outputStream, String key, Binary value) throws IOException {
		Logger.i(key + " is File");

		StringBuilder binaryFieldBuilder = new StringBuilder(START_BOUNDARY).append("\r\n");
		binaryFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"; filename=\"").append(value.getFileName()).append("\"\r\n");

		binaryFieldBuilder.append("Content-Type: ").append(value.getMimeType()).append("\r\n");
		binaryFieldBuilder.append("Content-Transfer-Encoding: binary\r\n\r\n");

		outputStream.write(binaryFieldBuilder.toString().getBytes());

		value.onWriteBinary(this, outputStream);
		outputStream.write("\r\n".getBytes());
	}

	@Override
	public void setTag(Object tag) {
		this.mTag = tag;
	}

	@Override
	public Object getTag() {
		return this.mTag;
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
	 * Get the parameters set
	 */
	protected abstract Set<String> keySet();

	/**
	 * Return {@link #keySet()} key corresponding to value
	 * 
	 * @param key from {@link #keySet()}
	 */
	protected abstract Object value(String key);

	/**
	 * Randomly generated boundary mark
	 * 
	 * @return random code
	 */
	public static final String createBoundry() {
		StringBuffer sb = new StringBuffer("--------");
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3L == 0L) {
				sb.append((char) (int) time % '\t');
			} else if (time % 3L == 1L) {
				sb.append((char) (int) (65L + time % 26L));
			} else {
				sb.append((char) (int) (97L + time % 26L));
			}
		}
		return sb.toString();
	}

	/* ======Cache===== */

	@Override
	public boolean needCache() {
		return RequestMethod.GET == getRequestMethod();
	}

	@Override
	public void setCacheKey(String key) {
		this.mCacheKey = key;
	}

	@Override
	public String getCacheKey() {
		return TextUtils.isEmpty(mCacheKey) ? buildUrl() : mCacheKey;
	}

	/**
	 * Parse response
	 */
	public abstract T parseResponse(String url, Headers responseHeaders, byte[] responseBody);
}