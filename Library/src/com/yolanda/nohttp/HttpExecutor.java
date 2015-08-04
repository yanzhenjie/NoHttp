/**
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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.yolanda.nohttp.base.BaseExecutor;
import com.yolanda.nohttp.base.BaseResponse;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

/**
 * Created in Jul 28, 2015 7:33:22 PM
 * 
 * @author YOLANDA
 */
class HttpExecutor extends BaseExecutor {

	/**
	 * border sign
	 */
	private final String BOUNDARY = getBoundry();
	private final String START_BOUNDARY = "--" + BOUNDARY;
	private final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";

	/**
	 * sigle model
	 */
	private static HttpExecutor _HttpExecutor;

	/**
	 * lock public
	 */
	private HttpExecutor() {
	}

	/**
	 * To create a singleton pattern entrance
	 * 
	 * @return Return my implementation
	 */
	public static HttpExecutor getInstance() {
		if (_HttpExecutor == null) {
			_HttpExecutor = new HttpExecutor();
		}
		return _HttpExecutor;
	}

	/**
	 * The request string
	 * 
	 * @param request request parameters
	 */
	public BaseResponse request(Request request) {
		Logger.d("---------------Reuqest start---------------");
		BaseResponse baseResponse = null;
		if (!URLUtil.isValidUrl(request.getUrl())) {
			baseResponse = new ResponseError();
			baseResponse.setResponseCode(ResponseCode.CODE_ERROR_URL);
			((ResponseError) baseResponse).setErrorInfo("URL address is wrong");
		} else {
			baseResponse = new Response();
			ResponseCode responseCode = ResponseCode.NONE;
			Throwable throwable = null;
			try {
				HttpURLConnection httpURLConnection = buildHttpAttribute(request);
				sendRequestParam(httpURLConnection, request);
				readResponseResult(httpURLConnection, (Response) baseResponse);
			} catch (SecurityException e) {
				responseCode = ResponseCode.CODE_ERROR_INTNET_PERMISSION;
				throwable = e;
				if (NoHttp.isDebug())
					e.printStackTrace();
			} catch (SocketTimeoutException e) {
				responseCode = ResponseCode.CODE_ERROR_TIMEOUT;
				throwable = e;
				if (NoHttp.isDebug())
					e.printStackTrace();
			} catch (UnknownHostException e) {
				responseCode = ResponseCode.CODE_ERROR_NOSERVER;
				throwable = e;
				if (NoHttp.isDebug())
					e.printStackTrace();
			} catch (Throwable e) {
				responseCode = ResponseCode.CODE_ERROR_OTHER;
				throwable = e;
				if (NoHttp.isDebug())
					e.printStackTrace();
			}
			if (baseResponse.isSuccessful()) {
				((Response) baseResponse).setCharset(request.getCharset());
			} else {
				baseResponse = new ResponseError();
				((ResponseError) baseResponse).setErrorInfo(throwable.getMessage());
				baseResponse.setResponseCode(responseCode);
			}
		}
		Logger.d("---------------Reqeust Finish---------------");
		return baseResponse;
	}

	/**
	 * If the connection is established successfully, and write parameters,
	 * return httpUrlConnection, if she fails, will throw an exception
	 * 
	 * @param httpURLConnection Contains the attribute of HTTP requests
	 * @param request Request parameters, which is used to write the request parameters, file
	 * @return HttpCURLConnection
	 * @throws SocketTimeoutException Thrown when the connection timeout
	 * @throws Throwable Other unpredictable exceptions occur
	 */
	private void sendRequestParam(HttpURLConnection httpURLConnection, Request request) throws Throwable {
		/*
		 * if (request.isKeepAlive())
		 * System.setProperty("http.keepAlive", "true");
		 */
		switch (request.getRequestMethod()) {
		case DELETE:
		case GET:
		case HEAD:
		case OPTIONS:
		case TRACE:
			httpURLConnection.connect();
			break;
		case PATCH:
		case POST:
		case PUT:
		default:
			OutputStream outputStream = null;
			if (request.hasBinaryData()) {// 如果有文件或者图片
				httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
				httpURLConnection.setDoOutput(true);
				outputStream = httpURLConnection.getOutputStream();
				buildParams(outputStream, request);
			} else {// 如果只是普通参数
				httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				httpURLConnection.setDoOutput(true);
				outputStream = httpURLConnection.getOutputStream();
				StringBuilder postParam = new StringBuilder();
				String postObj = request.getPostData();
				if (TextUtils.isEmpty(postObj)) {
					postParam = request.buildParam();
				} else {
					postParam.append(postObj);
				}
				Logger.d("Post data :" + postParam);
				outputStream.write(postParam.toString().getBytes(request.getCharset()));
			}
			break;
		}
		Logger.d("Http send data finish");
	}

	/**
	 * Read from the HttpURLConnection server response
	 * 
	 * @param httpURLConnection Established connection HttpURLConnection
	 * @return Return good results: the corresponding ResponseResult
	 * @throws SocketTimeoutException If read timeout thrown
	 * @throws Throwable Other unpredictable exceptions
	 */
	private void readResponseResult(HttpURLConnection httpURLConnection, Response response) throws Throwable {
		if (httpURLConnection != null) {
			int statusCode = httpURLConnection.getResponseCode();
			Logger.d("Http responseCode:" + statusCode);
			// 200,201,304;成功，创建，没有修改
			if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED
					|| statusCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
				Logger.d("Http read start");
				int contentLength = httpURLConnection.getContentLength();
				response.setContentLength(contentLength);
				String contentType = httpURLConnection.getContentType();
				response.setContentType(contentType);
				response.setHeaders(httpURLConnection.getHeaderFields());
				InputStream inputStream = httpURLConnection.getInputStream();
				String contentEncode = httpURLConnection.getHeaderField("Content-Encoding");// connection.getContentEncoding();
				if (!TextUtils.isEmpty(contentEncode)
						&& (contentEncode.toLowerCase(Locale.getDefault()).contains("gzip"))) {
					inputStream = new GZIPInputStream(inputStream);
				}
				int readBytes;
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream content = new ByteArrayOutputStream();
				while ((readBytes = inputStream.read(buffer)) != -1) {
					content.write(buffer, 0, readBytes);
				}
				response.setBytes(content.toByteArray());
				content.close();
				inputStream.close();
				response.setResponseCode(ResponseCode.CODE_SUCCESSFUL);
				Logger.d("Http read finish");
			}
			httpURLConnection.disconnect();
		}
	}

	/**
	 * If there is need to file uploads
	 * 
	 * @param outputStream http outPutStream
	 * @param request http request params
	 */
	private void buildParams(OutputStream outputStream, Request request) throws Throwable {
		Set<String> keys = request.getParamKeys();
		for (String key : keys) {// 普通参数
			Object value = request.getParam(key);
			if ((value instanceof String)) {
				StringBuilder sb = new StringBuilder(100);
				sb.setLength(0);
				sb.append(START_BOUNDARY).append("\r\n");
				sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
				sb.append(value).append("\r\n");
				outputStream.write(sb.toString().getBytes());
			}
		}
		for (String key : keys) {// 文件或者图片
			Object value = request.getParam(key);
			if ((value instanceof Bitmap)) {// 图片
				outputStream.write(createFileInfo(key, request.getFileName(key), request.getCharset()).getBytes());
				Bitmap bmp = (Bitmap) value;
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] bytes = stream.toByteArray();
				outputStream.write(bytes);
				outputStream.write("\r\n".getBytes());
				stream.flush();
				stream.close();
			} else if ((value instanceof File)) {// 文件
				File tempFile = (File) value;
				outputStream.write(createFileInfo(key, request.getFileName(key), request.getCharset()).getBytes());
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				InputStream inputStream = new FileInputStream(tempFile);
				byte[] upBuffer = new byte[1024];
				int tempLen;
				while ((tempLen = inputStream.read(upBuffer)) != -1) {
					stream.write(upBuffer, 0, tempLen);
				}
				inputStream.close();
				outputStream.write(stream.toByteArray());
				outputStream.write("\r\n".getBytes());
				stream.flush();
				stream.close();
			} else if ((value instanceof ByteArrayOutputStream)) {// 二进制
				outputStream.write(createFileInfo(key, request.getFileName(key), request.getCharset()).getBytes());
				ByteArrayOutputStream stream = (ByteArrayOutputStream) value;
				outputStream.write(stream.toByteArray());
				outputStream.write("\r\n".getBytes());
				stream.flush();
				stream.close();
			}
		}
		outputStream.write(("\r\n" + END_MP_BOUNDARY + "\r\n").getBytes());
	}

	/**
	 * Analog form submission files
	 * 
	 * @param key File the field names
	 * @param fileName file name
	 * @param charset stream charset
	 * @return
	 */
	private String createFileInfo(String key, String fileName, String charset) {
		StringBuilder sb = new StringBuilder();
		sb.append(START_BOUNDARY).append("\r\n");
		sb.append("Content-Disposition: form-data; name=\"").append(key)
				.append("\"; filename=\"" + fileName + "\"\r\n");
		sb.append("Content-Type: application/octet-stream; charset=" + charset + "\r\n\r\n");// application/octet-stream、multipart/form-data
		return sb.toString();
	}

	/**
	 * Get the file name from url
	 * 
	 * @param url taget url
	 * @return filename
	 */
	public BaseResponse requestFilename(Request request) {
		Response responseResult = new Response();
		responseResult.setCharset(request.getCharset());
		String urlStr = request.getUrl();
		String fileName = "";
		HttpURLConnection httpURLConnection = null;
		ResponseCode responseCode = ResponseCode.NONE;
		try {
			httpURLConnection = buildHttpAttribute(request);
			httpURLConnection.connect();
			responseResult.setContentLength(httpURLConnection.getContentLength());
			responseResult.setContentType(httpURLConnection.getContentType());
			Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
			responseResult.setHeaders(headers);
			if (headers != null) {
				Set<String> key = headers.keySet();
				for (String skey : key) {
					List<String> values = headers.get(skey);
					for (String result : values) {
						int location = result.indexOf("filename");
						if (location >= 0) {
							Logger.d(result);
							result = result.substring(location + "filename".length());
							result = result.substring(result.indexOf("=") + 1).replace("\"", "");// 替换双引号
							responseResult.setResponseCode(ResponseCode.CODE_SUCCESSFUL);
							responseResult.setBytes(result.getBytes(request.getCharset()));
							return responseResult;
						}
					}
				}
			}
		} catch (SecurityException e) {
			responseCode = ResponseCode.CODE_ERROR_INTNET_PERMISSION;
			if (NoHttp.isDebug())
				e.printStackTrace();
		} catch (SocketTimeoutException e) {
			responseCode = ResponseCode.CODE_ERROR_TIMEOUT;
			if (NoHttp.isDebug())
				e.printStackTrace();
		} catch (UnknownHostException e) {
			responseCode = ResponseCode.CODE_ERROR_NOSERVER;
			if (NoHttp.isDebug())
				e.printStackTrace();
		} catch (Throwable e) {
			responseCode = ResponseCode.CODE_ERROR_OTHER;
			if (NoHttp.isDebug())
				e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		responseResult.setResponseCode(responseCode);
		/** 文件名 **/
		if (TextUtils.isEmpty(fileName)) {
			Logger.e("Http filename is unkonw");
			fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);// 最后一个/后面的字符
			if (fileName.contains(".")) {// 只截取有扩展名的
				fileName = fileName.substring(0, fileName.lastIndexOf("."));// 截取最后一个.之前的名称
			} else {// 没有扩展名的返回null
				return responseResult;
			}
			Logger.d("Regular Filename is " + fileName);
			/** 扩展名 **/
			String newExtension = MimeTypeMap.getFileExtensionFromUrl(urlStr);// 得到扩展名
			if (!TextUtils.isEmpty(newExtension)) {// 是否有扩展名
				fileName += ("." + newExtension);
			}
			Logger.d("Regular Extension is " + newExtension);
			try {
				responseResult.setBytes(fileName.getBytes(request.getCharset()));
				responseResult.setResponseCode(ResponseCode.CODE_SUCCESSFUL);
			} catch (UnsupportedEncodingException e) {
				if (NoHttp.isDebug())
					e.printStackTrace();
				responseResult.setResponseCode(ResponseCode.CODE_ERROR_OTHER);
			}
		}
		return responseResult;
	}

	/**
	 * Randomly generated boundary mark
	 * 
	 * @return random code
	 */
	private String getBoundry() {
		// return "---------------------------" + UUID.randomUUID().toString();
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
