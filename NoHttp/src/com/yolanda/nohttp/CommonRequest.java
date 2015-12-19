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
import java.net.URLEncoder;
import java.util.Set;

import com.yolanda.nohttp.able.Queueable;
import com.yolanda.nohttp.able.SignCancelable;
import com.yolanda.nohttp.able.Startable;
import com.yolanda.nohttp.security.Certificate;
import com.yolanda.nohttp.tools.CounterOutputStream;

import android.text.TextUtils;

/**
 * Created in Nov 4, 2015 8:28:50 AM
 * 
 * @author YOLANDA
 */
public abstract class CommonRequest implements Queueable, Startable, SignCancelable {

	protected final static String BOUNDARY = createBoundry();
	protected final static String START_BOUNDARY = "--" + BOUNDARY;
	protected final static String END_BOUNDARY = "--" + BOUNDARY + "--";

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
	 * RequestBody
	 */
	protected byte[] mRequestBody;
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
	protected boolean isCaneled = false;
	/**
	 * Cancel sign
	 */
	protected Object cancelSign;
	/**
	 * Tag of request
	 */
	protected Object mTag;

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

	/**
	 * Return url of request
	 */
	public String url() {
		return url;
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

	/**
	 * return method of request
	 */
	public int getRequestMethod() {
		return mRequestMethod;
	}

	/**
	 * Sets the connection timeout time
	 * 
	 * @param connectTimeout timeout number, Unit is a millisecond
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	/**
	 * Get the connection timeout time, Unit is a millisecond
	 */
	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	/**
	 * Sets the read timeout time
	 * 
	 * @param readTimeout timeout number, Unit is a millisecond
	 */
	public void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	/**
	 * Get the read timeout time, Unit is a millisecond
	 */
	public int getReadTimeout() {
		return mReadTimeout;
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
	 * Get all Heads
	 */
	public Headers headers() {
		return this.mheaders;
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

	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 * 
	 * @param isAllowHttps the isAllowHttps to set
	 */
	public void setAllowHttps(boolean isAllowHttps) {
		this.isAllowHttps = isAllowHttps;
	}

	/**
	 * If you are allowed to access the Https directly, then the true will be returned if the certificate is required to
	 * return false
	 */
	public boolean isAllowHttps() {
		return isAllowHttps;
	}

	/**
	 * Sets the {@code Certificate} of https
	 */
	public void setCertificate(Certificate mCertificate) {
		this.mCertificate = mCertificate;
	}

	/**
	 * If the request is HTTPS, and the {@link #isAllowHttps()} return false, then the certificate must be returned,
	 * otherwise HTTPS cannot be accessed.
	 */
	public Certificate getCertificate() {
		return mCertificate;
	}

	/**
	 * Get content length
	 */
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

	/**
	 * If the request is POST, PUT, PATCH, the true should be returned.
	 */
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

	/**
	 * Get Boundary of data
	 */
	public final String getBoundary() {
		return BOUNDARY;
	}

	/**
	 * Get Encoding of request param
	 */
	public String getParamsEncoding() {
		return NoHttp.CHARSET_UTF8;
	}

	/**
	 * If the argument contains {@code Binary}
	 */
	protected boolean hasBinary() {
		Set<String> keys = keySet();
		for (String key : keys) {
			Object value = value(key);
			if (value instanceof Binary) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Settings you want to post data, if the post directly, then other data
	 * will not be sent
	 */
	public void setRequestBody(byte[] requestBody) {
		this.mRequestBody = requestBody;
	}

	/**
	 * Settings you want to post data, if the post directly, then other data will not be sent
	 */
	public void setRequestBody(String requestBody) {
		if (!TextUtils.isEmpty(requestBody))
			try {
				this.mRequestBody = URLEncoder.encode(requestBody, getParamsEncoding()).getBytes();
			} catch (UnsupportedEncodingException e) {
				Logger.e(e);
			}
	}

	/**
	 * Send request data, give priority to RequestBody, and then send the form data
	 */
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
				if (value != null && value instanceof String) {
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
		String formString = createFormStringField(key, value, getParamsEncoding());
		outputStream.write(formString.getBytes());
		outputStream.write("\r\n".getBytes());
	}

	/**
	 * Send binary data in a form
	 */
	private void writeFormBinary(OutputStream outputStream, String key, Binary value) throws IOException {
		Logger.i(key + " is File");
		outputStream.write(createFormBinaryField(key, value, value.getCharset()).getBytes());
		value.onWriteBinary(this, outputStream);
		outputStream.write("\r\n".getBytes());
	}

	/**
	 * Create a text message in a form
	 */
	protected String createFormStringField(String key, String value, String charset) throws UnsupportedEncodingException {
		StringBuilder stringFieldBuilder = new StringBuilder();
		stringFieldBuilder.append(START_BOUNDARY).append("\r\n");
		stringFieldBuilder.append("Content-Disposition: form-data; name=\"").append(URLEncoder.encode(key, charset)).append("\"\r\n");
		stringFieldBuilder.append("Content-Type: text/plain; charset=").append(charset).append("\r\n\r\n");
		stringFieldBuilder.append(URLEncoder.encode(value, charset));
		return stringFieldBuilder.toString();
	}

	/**
	 * Create a binary message in a form
	 */
	protected String createFormBinaryField(String key, Binary binary, String charset) {
		StringBuilder fileFieldBuilder = new StringBuilder();
		fileFieldBuilder.append(START_BOUNDARY).append("\r\n");
		fileFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\";");
		if (!TextUtils.isEmpty(binary.getFileName())) {
			fileFieldBuilder.append(" filename=\"").append(binary.getFileName()).append("\"");
		}
		fileFieldBuilder.append("\r\n");
		fileFieldBuilder.append("Content-Type: ").append(binary.getMimeType()).append("; charset:").append(charset).append("\r\n");
		fileFieldBuilder.append("Content-Transfer-Encoding: binary\r\n\r\n");
		return fileFieldBuilder.toString();
	}

	/**
	 * Set tag of task, Will return to you at the time of the task response
	 */
	public void setTag(Object tag) {
		this.mTag = tag;
	}

	/**
	 * Get get of this request
	 */
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
	 * Check method request
	 */
	public static void checkRequestMethod(int requestMethod) {
		if (requestMethod < RequestMethod.GET || requestMethod > RequestMethod.PATCH)
			throw new RuntimeException("Invalid HTTP method: " + requestMethod);
	}

	/**
	 * Randomly generated boundary mark
	 * 
	 * @return random code
	 */
	public static String createBoundry() {
		StringBuffer sb = new StringBuffer();
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
}