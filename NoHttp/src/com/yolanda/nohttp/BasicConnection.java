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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.yolanda.nohttp.security.SecureVerifier;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

/**
 * Package good Http implementation class, establish connection, read and write data</br>
 * Created in Aug 4, 2015 10:12:38 AM
 * 
 * @author YOLANDA
 */
public abstract class BasicConnection {

	/**
	 * Create a Http connection object, but do not establish a connection, where the request header information is set up, including Cookie
	 */
	protected HttpURLConnection getHttpConnection(BasicRequest request) throws IOException, URISyntaxException {
		// 1.Pre operation notice
		request.onPreExecute();

		// 2.Build URL
		String urlStr = request.url();
		Logger.d("Reuqest adress:" + urlStr);
		if (android.os.Build.VERSION.SDK_INT < 9)
			System.setProperty("http.keepAlive", "false");
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if ("https".equalsIgnoreCase(url.getProtocol()))
			SecureVerifier.getInstance().doVerifier((HttpsURLConnection) connection, request);

		// 3. Base attribute
		String requestMethod = request.getRequestMethod().toString();
		Logger.d("Request method:" + requestMethod);
		connection.setRequestMethod(requestMethod);
		connection.setDoInput(true);
		connection.setDoOutput(request.isOutPutMethod());
		connection.setConnectTimeout(request.getConnectTimeout());
		connection.setReadTimeout(request.getReadTimeout());
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);

		// 4.Set request headers
		setHeaders(url.toURI(), connection, request);

		return connection;
	}

	/**
	 * Set request headers
	 */
	private void setHeaders(URI uri, HttpURLConnection connection, BasicRequest request) throws IOException {
		// 1.Build Headers
		Headers headers = request.headers();

		// 2.Set content Length
		if (request.isOutPutMethod()) {
			long contentLength = request.getContentLength();
			setContentLength(connection, contentLength);
			headers.set(Headers.HEAD_KEY_CONTENT_LENGTH, Long.toString(contentLength));
		}

		// TODO Authorization:

		// 3.Base header
		headers.set(Headers.HEAD_KEY_ACCEPT_ENCODING, Headers.HEAD_VALUE_ACCEPT_ENCODING);// gzip, deflate, sdch;
		headers.set(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_VALUE_ACCEPT); // text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
		if (headers.get(Headers.HEAD_KEY_CACHE_CONTROL) == null)
			headers.set(Headers.HEAD_KEY_CACHE_CONTROL, Headers.HEAD_VALUE_CACHE_CONTROL);
		if (headers.get(Headers.HEAD_KEY_CONNECTION) == null)
			headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION);
		if (headers.get(Headers.HEAD_KEY_USER_AGENT) == null)
			headers.set(Headers.HEAD_KEY_USER_AGENT, getUserAgent());

		// 4.Add cookie to headers
		setCookies(uri, headers);

		// 5.Adds all request header to httoConnection
		Logger.i("-------Set request headers start-------");
		for (int i = 0; i < headers.size(); i++) {
			String name = headers.name(i);
			String value = headers.value(i);
			Logger.i(name + ": " + value);
			connection.addRequestProperty(name, value);
		}

		if (request.isOutPutMethod() && request.hasBinary()) {
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + request.getBoundary());
		} else {
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + request.getParamsEncoding());
		}
		Logger.i("-------Set request headers end-------");
	}

	/**
	 * Set content length
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setContentLength(HttpURLConnection connection, long contentLength) {
		if (contentLength < Integer.MAX_VALUE && contentLength > 0) {
			connection.setFixedLengthStreamingMode((int) contentLength);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			connection.setFixedLengthStreamingMode(contentLength);
		} else {
			connection.setChunkedStreamingMode(256 * 1024);
		}
	}

	/**
	 * Set cookie
	 */
	private void setCookies(URI uri, Headers headers) throws IOException {
		CookieManager cookieManager = NoHttp.getDefaultCookieManager();
		Map<String, List<String>> cookieMaps = cookieManager.get(uri, Collections.<String, List<String>> emptyMap());
		// Add any new cookies to the request.
		HeaderParser.addCookiesToHeaders(headers, cookieMaps);

		Map<String, String> cookies = HeaderParser.parseRequestCookie(headers);
		headers.removeAll(Headers.HEAD_KEY_COOKIE);
		headers.removeAll(Headers.HEAD_KEY_COOKIE2);
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value))
				headers.add(name, value);
		}
	}

	/**
	 * Send the request data to the server
	 */
	protected <T> void writeRequestBody(HttpURLConnection connection, Request<T> request) throws IOException {
		if (request.isOutPutMethod()) {
			Logger.i("-------Send reqeust data start-------");
			BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
			request.onWriteRequestBody(outputStream);
			outputStream.close();
			Logger.i("-------Send request data end-------");
		}
	}

	/**
	 * To read information from the server's response
	 */
	protected byte[] readResponseBody(InputStream inputStream) throws IOException {
		int readBytes;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream content = new ByteArrayOutputStream();
		while ((readBytes = inputStream.read(buffer)) != -1) {
			content.write(buffer, 0, readBytes);
		}
		content.flush();
		content.close();
		return content.toByteArray();
	}

	/**
	 * Get User-Agent
	 */
	protected abstract String getUserAgent();

	/**
	 * this requestMethod and responseCode has ResponseBody ?
	 */
	public static boolean hasResponseBody(RequestMethod requestMethod, int responseCode) {
		return requestMethod != RequestMethod.HEAD && hasResponseBody(responseCode);
	}

	/**
	 * ser has response
	 */
	public static boolean hasResponseBody(int responseCode) {
		/* ===临时响应=== */
		// 100 服务器已接受到第一部分，继续等待其余部分
		// 101 请求者已要求服务器切换协议，服务器已确认并准备切换
		/* ===成功=== */
		// 200 成功
		// 201 已创建，成功，并创建了新资源
		// 202 已接受，但没处理
		// 203 非授权信息，服务器已成功处理了请求，但返回的信息可能来自另一来源
		// 204 *无内容，成功处理了请求，没返回任何内容
		// 205 重置内容，成功处理了请求，没返回任何内容
		// 206 处理了部分get请求
		/* ===重定向=== */
		// 300 多种选择，服务器可根据请求者User-Agent选择一项，活提供列表给请求者
		// 301永久移动，请求的网页已被移动到新位置
		// 302临时移动
		// 303查看其它位置，请求者应当对不同位置使用单独get来检索响应
		// 304为修改，上次请求后没修改过，不会返回任何内容
		// 305只能使用代理来请求
		// 306上个版本的响应码，现在已抛弃
		// 307临时重定向
		/* ===请求错误=== */
		// 400 （错误请求） 服务器不理解请求的语法。
		// 401 （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。
		// 403 （禁止） 服务器拒绝请求。
		// 404 （未找到） 服务器找不到请求的网页。
		// 405 （方法禁用） 禁用请求中指定的方法。
		// 406 （不接受） 无法使用请求的内容特性响应请求的网页。
		// 407 （需要代理授权） 此状态代码与 401（未授权）类似，但指定请求者应当授权使用代理。
		// 408 （请求超时） 服务器等候请求时发生超时。
		// 409 （冲突） 服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息。
		// 410 （已删除） 如果请求的资源已永久删除，服务器就会返回此响应。
		// 411 （需要有效长度） 服务器不接受不含有效内容长度标头字段的请求。
		// 412 （未满足前提条件） 服务器未满足请求者在请求中设置的其中一个前提条件。
		// 413 （请求实体过大） 服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。
		// 414 （请求的 URI 过长） 请求的 URI（通常为网址）过长，服务器无法处理。
		// 415 （不支持的媒体类型） 请求的格式不受请求页面的支持。
		// 416 （请求范围不符合要求） 如果页面无法提供请求的范围，则服务器会返回此状态代码。
		// 417 （未满足期望值） 服务器未满足"期望"请求标头字段的要求。
		/* ===服务器错误=== */
		// 500 服务器内部错误
		// 501 尚未实施，服务器无法识别
		// 502 错误网关
		// 503 服务不可用
		// 504 网关超时
		// 505 Http版本不支持

		return !(100 <= responseCode && responseCode < 200) && responseCode != 204 && responseCode != 205 && responseCode != 304;
	}

	protected String getExcetionMessage(Throwable e) {
		StringBuilder exceptionInfo = new StringBuilder();
		if (e != null) {
			exceptionInfo.append(e.getClass().getName());
			exceptionInfo.append(": ");
			exceptionInfo.append(e.getMessage());
		}
		return exceptionInfo.toString();
	}
}
