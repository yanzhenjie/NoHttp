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
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

import com.yolanda.nohttp.util.CounterOutputStream;
import com.yolanda.nohttp.util.Writer;

import android.text.TextUtils;

/**
 * Created in Nov 4, 2015 8:28:50 AM
 * 
 * @author YOLANDA
 */
public abstract class BasicRequest<T> implements Request<T> {

	private final String boundary = createBoundry();
	private final String start_boundary = "--" + boundary;
	private final String end_boundary = start_boundary + "--";

	/**
	 * User Agent
	 */
	private static String userAgent;

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

	private SSLSocketFactory mSSLSocketFactory = null;
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
	 * Request is finished
	 */
	private boolean isFinished = false;
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
	public BasicRequest(String url) {
		this(url, RequestMethod.GET);
	}

	/**
	 * Create a request
	 * 
	 * @param url request adress, like: http://www.google.com
	 * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}
	 */
	public BasicRequest(String url, RequestMethod requestMethod) {
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
		if (!doOutPut() && keySet().size() > 0) {
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

	@Override
	public void setProxy(Proxy proxy) {
		this.mProxy = proxy;
	}

	@Override
	public Proxy getProxy() {
		return mProxy;
	}

	@Override
	public void setSSLSocketFactory(SSLSocketFactory socketFactory) {
		mSSLSocketFactory = socketFactory;
	}

	@Override
	public SSLSocketFactory getSSLSocketFactory() {
		return mSSLSocketFactory;
	}

	@Override
	public boolean doOutPut() {
		switch (mRequestMethod) {
		case GET:
		case DELETE:
		case HEAD:
		case OPTIONS:
		case TRACE:
			return false;
		case POST:
		case PUT:
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
	public void removeHeader(String name) {
		mHeaders.remove(name);
	}

	@Override
	public void removeAllHeaders() {
		mHeaders.clear();
	}

	@Override
	public Headers headers() {
		return this.mHeaders;
	}

	@Override
	public long getContentLength() {
		CounterOutputStream outputStream = new CounterOutputStream();
		onWriteRequestBody(new Writer(outputStream));
		long contentLength = outputStream.get();
		return contentLength;
	}

	@Override
	public String getContentType() {
		StringBuilder contentTypeBuild = new StringBuilder();
		if (doOutPut() && hasBinary()) {
			contentTypeBuild.append("multipart/form-data; boundary=").append(boundary);
		} else {
			contentTypeBuild.append("application/x-www-form-urlencoded; charset=").append(getParamsEncoding());
		}
		return contentTypeBuild.toString();
	}

	@Override
	public String getUserAgent() {
		if (TextUtils.isEmpty(userAgent))
			userAgent = UserAgent.getUserAgent(NoHttp.getContext());
		return userAgent;
	}

	@Override
	public void setRequestBody(byte[] requestBody) {
		this.mRequestBody = requestBody;
	}

	@Override
	public void setRequestBody(String requestBody) {
		if (!TextUtils.isEmpty(requestBody))
			try {
				this.mRequestBody = requestBody.getBytes(getParamsEncoding());
			} catch (UnsupportedEncodingException e) {
				Logger.e(e);
			}
	}

	@Override
	public void onPreExecute() {
	}

	@Override
	public void onWriteRequestBody(Writer writer) {
		if (mRequestBody == null && hasBinary())
			writeFormStreamData(writer);
		else if (mRequestBody == null)
			writeCommonStreamData(writer);
		else
			writeRequestBody(writer);
	}

	/**
	 * Send form data
	 */
	protected void writeFormStreamData(Writer writer) {
		try {
			Set<String> keys = keySet();
			for (String key : keys) {// 文件或者图片
				Object value = value(key);
				if (value != null && value instanceof String) {
					writeFormString(writer, key, value.toString());
				} else if (value != null && value instanceof Binary) {
					writeFormBinary(writer, key, (Binary) value);
				}
			}
			writer.write(("\r\n" + end_boundary + "\r\n").getBytes());
		} catch (IOException e) {
			Logger.e(e);
		}
	}

	/**
	 * Send text data in a form
	 */
	private void writeFormString(Writer writer, String key, String value) throws IOException {
		print(writer.isPrint(), key + " = " + value);

		StringBuilder stringFieldBuilder = new StringBuilder(start_boundary).append("\r\n");

		stringFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n");
		stringFieldBuilder.append("Content-Type: text/plain; charset=").append(getParamsEncoding()).append("\r\n\r\n");

		writer.write(stringFieldBuilder.toString().getBytes());

		writer.write(value.getBytes(getParamsEncoding()));
		writer.write("\r\n".getBytes());
	}

	/**
	 * Send binary data in a form
	 */
	private void writeFormBinary(Writer writer, String key, Binary value) throws IOException {
		print(writer.isPrint(), key + " is Binary");

		StringBuilder binaryFieldBuilder = new StringBuilder(start_boundary).append("\r\n");
		binaryFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"; filename=\"").append(value.getFileName()).append("\"\r\n");

		binaryFieldBuilder.append("Content-Type: ").append(value.getMimeType()).append("\r\n");
		binaryFieldBuilder.append("Content-Transfer-Encoding: binary\r\n\r\n");

		writer.write(binaryFieldBuilder.toString().getBytes());

		writer.write(value);
		writer.write("\r\n".getBytes());
	}

	/**
	 * Send non form data
	 */
	protected void writeCommonStreamData(Writer writer) {
		String requestBody = buildCommonParams().toString();
		print(writer.isPrint(), "RequestBody: " + requestBody);
		try {
			writer.write(requestBody.getBytes());
		} catch (IOException e) {
			Logger.e(e);
		}
	}

	/**
	 * Send request {@code RequestBody}
	 */
	protected void writeRequestBody(Writer writer) {
		try {
			print(writer.isPrint(), "RequestBody: " + mRequestBody);
			writer.write(mRequestBody);
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

	@Override
	public void finish() {
		isFinished = true;
	}

	@Override
	public boolean isFinished() {
		return isFinished;
	}

	public String getParamsEncoding() {
		return NoHttp.CHARSET_UTF8;
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

	protected String getBoundry() {
		return boundary;
	}

	private void print(boolean isPrint, String msg) {
		if (isPrint)
			Logger.d(msg);
	}

	/**
	 * Randomly generated boundary mark
	 * 
	 * @return random code
	 */
	protected static final String createBoundry() {
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

}