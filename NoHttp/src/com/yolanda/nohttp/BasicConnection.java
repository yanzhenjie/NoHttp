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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.yolanda.nohttp.security.SecureVerifier;

import android.text.TextUtils;

/**
 * Package good Http implementation class, establish connection, read and write data</br>
 * Created in Aug 4, 2015 10:12:38 AM
 * 
 * @author YOLANDA
 */
public abstract class BasicConnection {

	protected final String BOUNDARY = createBoundry();
	protected final String START_BOUNDARY = "--" + BOUNDARY;
	protected final String END_BOUNDARY = "--" + BOUNDARY + "--";

	/**
	 * Create a Http connection object, but do not establish a connection, where the request header information is set up, including Cookie
	 */
	protected HttpURLConnection getHttpConnection(CommonRequestAnalyze analyzeRequest) throws IOException, URISyntaxException {
		String urlStr = analyzeRequest.url();
		Logger.d("Reuqest adress:" + urlStr);
		if (android.os.Build.VERSION.SDK_INT < 9)
			System.setProperty("http.keepAlive", "false");

		URL url = new URL(urlStr);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		if ("https".equals(url.getProtocol()))
			SecureVerifier.getInstance().doVerifier((HttpsURLConnection) httpConnection, analyzeRequest);
		int requestMethod = analyzeRequest.getRequestMethod();
		String method = RequestMethod.METHOD[requestMethod];
		Logger.d("Request method:" + method);
		httpConnection.setRequestMethod(method);
		httpConnection.setDoInput(true);
		httpConnection.setDoOutput(analyzeRequest.isOutPutMethod());
		httpConnection.setConnectTimeout(analyzeRequest.getConnectTimeout());
		httpConnection.setReadTimeout(analyzeRequest.getReadTimeout());

		Headers headers = analyzeRequest.getHeaders();
		if (headers == null)
			headers = new Headers();

		// Accept:text/html
		// Authorization:
		// Accept-Language:zh-CN,zh;q=0.8

		headers.set(Headers.HEAD_KEY_ACCEPT_ENCODING, Headers.HEAD_VALUE_ACCEPT_ENCODING);// gzip, deflate, sdch; default: gzip
		if (headers.get(Headers.HEAD_KEY_ACCEPT) == null)
			headers.set(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_VALUE_ACCEPT); // text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8

		// 请求时：no-cache、no-store、max-age=0、max-stale、min-fresh、only-if-cached
		// 响应时：public、private、no-cache、no-store、no-transform、must-revalidate、proxy-revalidate、max-age、s-maxage
		if (headers.get(Headers.HEAD_KEY_CACHE_CONTROL) == null)
			headers.set(Headers.HEAD_KEY_CACHE_CONTROL, Headers.HEAD_VALUE_CACHE_CONTROL);
		if (headers.get(Headers.HEAD_KEY_CONNECTION) == null)
			headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION);
		if (headers.get(Headers.HEAD_KEY_USER_AGENT) == null)
			headers.set(Headers.HEAD_KEY_USER_AGENT, getUserAgent());

		// 1.Parse cookie of CookieManger
		// Capture the request headers added so far so that they can be offered to the CookieHandler.
		// This is mostly to stay close to the RI; it is unlikely any of the headers above would
		// affect cookie choice besides "Host".
		CookieManager cookieManager = NoHttp.getDefaultCookieManager();
		if (cookieManager != null) {
			URI uri = new URI(analyzeRequest.url());
			Map<String, List<String>> cookies = cookieManager.get(uri, Headers.toMultimap(headers));

			// Add any new cookies to the request.
			Headers.addCookiesToHeaders(headers, cookies);
		}

		Map<String, String> cookies = Headers.parseRequestCookie(headers);
		headers.removeAll(Headers.HEAD_KEY_COOKIE);
		headers.removeAll(Headers.HEAD_KEY_COOKIE2);
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value))
				headers.add(name, value);
		}

		// 2.Adds all request header to httoConnection
		Logger.i("-------Request Headers Start-------");
		for (int i = 0; i < headers.size(); i++) {
			String name = headers.name(i);
			String value = headers.value(i);
			Logger.i(name + ": " + value);
			httpConnection.addRequestProperty(name, value);
		}
		Logger.i("-------Request Headers End-------");
		if (analyzeRequest.isOutPutMethod() && analyzeRequest.hasBinary())
			httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		else
			httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + analyzeRequest.getParamsEncoding());
		return httpConnection;
	}

	/**
	 * Randomly generated boundary mark
	 * 
	 * @return random code
	 */
	protected String createBoundry() {
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

	/**
	 * If the connection is established successfully, and write parameters,
	 * return httpUrlConnection, if it fails, will throw an exception.
	 * 
	 * @param httpConnection Http objects that have been built up to connect
	 * @param analyzeRequest Request object
	 * @throws UnsupportedEncodingException Throw this exception when the request object's Encoding is not supported.
	 * @throws IOException
	 */
	protected void sendRequestParam(HttpURLConnection httpConnection, CommonRequestAnalyze analyzeRequest) throws UnsupportedEncodingException, IOException {
		if (analyzeRequest.isOutPutMethod())
			if (analyzeRequest.hasBinary()) {
				writeFormStreamData(httpConnection.getOutputStream(), analyzeRequest);
			} else {
				byte[] requestBodyArray = analyzeRequest.getRequestBody();
				if (requestBodyArray != null)
					httpConnection.getOutputStream().write(requestBodyArray);
			}
	}

	/**
	 * When using POST, PUT, PATCH request method, the simulation form to write data should call this method
	 */
	protected void writeFormStreamData(OutputStream outputStream, CommonRequestAnalyze request) throws UnsupportedEncodingException, IOException {
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		String paramEncoding = request.getParamsEncoding();
		Set<String> keys = request.keySet();
		for (String key : keys) {// 文件或者图片
			Object value = request.value(key);
			if (value != null && value instanceof String)
				writeFormString(dataOutputStream, key, value.toString(), paramEncoding);
			if (value != null && value instanceof Binary)
				writeFormFile(dataOutputStream, key, (Binary) value);
		}
		dataOutputStream.write(("\r\n" + END_BOUNDARY + "\r\n").getBytes());
		dataOutputStream.flush();
		dataOutputStream.close();
	}

	/**
	 * Write out the form {@code String} data
	 * 
	 * @param outputStream output stream
	 * @param key param name
	 * @param value param value
	 * @param charset param charset
	 * @throws UnsupportedEncodingException Throw this exception when the request object's Encoding is not supported.
	 * @throws IOException
	 */
	private void writeFormString(OutputStream outputStream, String key, String value, String charset) throws UnsupportedEncodingException, IOException {
		Logger.i(key + " = " + value);
		String formString = createFormStringField(key, value, charset);
		outputStream.write(formString.getBytes());
		outputStream.write("\r\n".getBytes());
	}

	/**
	 * Write out the form {@code Binary} data
	 * 
	 * @param outputStream output stream
	 * @param key param name
	 * @param binary param value, this is binary
	 * @throws IOException
	 */
	private void writeFormFile(OutputStream outputStream, String key, Binary binary) throws IOException {
		Logger.i(key + " is File");
		outputStream.write(createFormFileField(key, binary, binary.getCharset()).getBytes());
		outputStream.write(binary.getByteArray());
		outputStream.write("\r\n".getBytes());
	}

	/**
	 * When using POST, PUT, PATCH request method, Create a raw data from {@code String}
	 * 
	 * @param key param name
	 * @param value param value
	 * @param charset param charset
	 * @return Returns a row data for a parameter
	 * @throws UnsupportedEncodingException
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
	 * Analog form submission files
	 * 
	 * @param key File the field names
	 * @param fileName file name
	 * @param charset stream charset
	 * @return
	 */
	protected String createFormFileField(String key, Binary binary, String charset) {
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
	 * Read from the HttpURLConnection server response
	 * 
	 * @param inputStream Stream from HttpConnection
	 * @return Return good results: the corresponding ResponseResult
	 * @throws SocketTimeoutException If read timeout thrown
	 * @throws Throwable Other unpredictable exceptions
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
		inputStream.close();
		return content.toByteArray();
	}

	/**
	 * Get User-Agent
	 */
	protected abstract String getUserAgent();

	/**
	 * this requestMethod and responseCode has ResponseBody ?
	 */
	public static boolean hasResponseBody(int requestMethod, int responseCode) {
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
