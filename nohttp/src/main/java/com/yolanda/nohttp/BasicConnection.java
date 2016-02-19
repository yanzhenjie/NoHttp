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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.yolanda.nohttp.tools.Writer;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

/**
 * Package good Http implementation class, establish connection, read and write data
 * </br>
 * Created in Aug 4, 2015 10:12:38 AM
 *
 * @author YOLANDA
 */
public class BasicConnection {

    /**
     * The connection is established, including the head and send the request body
     */
    protected HttpURLConnection getHttpConnection(ImplServerRequest request) throws IOException, URISyntaxException {
        // 1.Pre operation notice
        request.onPreExecute();

        // 2.Build URL
        String urlStr = request.url();
        Logger.i("Reuqest adress: " + urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection connection = null;
        Proxy proxy = request.getProxy();
        if (proxy == null)
            connection = (HttpURLConnection) url.openConnection();
        else
            connection = (HttpURLConnection) url.openConnection(proxy);

        if (connection instanceof HttpsURLConnection) {
            SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
            if (sslSocketFactory != null)
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            HostnameVerifier hostnameVerifier = request.getHostnameVerifier();
            if(hostnameVerifier != null)
                ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
        }

        // 3. Base attribute
        String requestMethod = request.getRequestMethod().toString();
        Logger.i("Request method: " + requestMethod);
        connection.setRequestMethod(requestMethod);
        connection.setDoInput(true);
        connection.setDoOutput(request.doOutPut());
        connection.setConnectTimeout(request.getConnectTimeout());
        connection.setReadTimeout(request.getReadTimeout());
        connection.setInstanceFollowRedirects(false);

        // 4.Set request headers
        setHeaders(url.toURI(), connection, request);

        // 5. Write request body
        connection.connect();
        writeRequestBody(connection, request);

        return connection;
    }

    /**
     * Set request headers, here will add cookies
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setHeaders(URI uri, HttpURLConnection connection, ImplServerRequest request) {
        // 1.Build Headers
        Headers headers = request.headers();

        // 2.Base header
        // 2.1 Accept-*
        String accept = request.getAccept();
        if (!TextUtils.isEmpty(accept))
            headers.set(Headers.HEAD_KEY_ACCEPT, accept);
        headers.set(Headers.HEAD_KEY_ACCEPT_ENCODING, Headers.HEAD_VALUE_ACCEPT_ENCODING);

        String acceptCharset = request.getAcceptCharset();
        if (!TextUtils.isEmpty(acceptCharset))
            headers.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, acceptCharset);

        String acceptLanguget = request.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguget))
            headers.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguget);

        // 2.2 Connection
        // To fix bug: accidental EOFException before API 19
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION_KEEP_ALIVE);
        else
            headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION_CLOSE);
        // 2.3 Content-*
        if (request.doOutPut()) {
            long contentLength = request.getContentLength();
            if (contentLength < Integer.MAX_VALUE && contentLength > 0)
                connection.setFixedLengthStreamingMode((int) contentLength);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                connection.setFixedLengthStreamingMode(contentLength);
            else
                connection.setChunkedStreamingMode(256 * 1024);
            headers.set(Headers.HEAD_KEY_CONTENT_LENGTH, Long.toString(contentLength));
        }

        String contentType = request.getContentType();
        if (!TextUtils.isEmpty(contentType))
            headers.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);

        // 2.4 Cookie
        try {
            headers.addCookie(uri, NoHttp.getDefaultCookieHandler());
        } catch (IOException e) {
            Logger.e(e, "Add cookie filed: " + uri.toString());
        }

        // 3. UserAgent
        headers.set(Headers.HEAD_KEY_USER_AGENT, request.getUserAgent());

        Map<String, String> requestHeaders = headers.toRequestHeaders();

        // 4.Adds all request header to httoConnection
        for (Map.Entry<String, String> headerEntry : requestHeaders.entrySet()) {
            String headKey = headerEntry.getKey();
            String headValue = headerEntry.getValue();
            Logger.i(headKey + ": " + headValue);
            connection.setRequestProperty(headKey, headValue);
        }
    }

    /**
     * Parse server response headers, here will save cookies
     */
    protected Headers parseResponseHeaders(URI uri, int responseCode, String responseMessage, Map<String, List<String>> reponseHeaders) {
        // handle cookie
        try {
            NoHttp.getDefaultCookieHandler().put(uri, reponseHeaders);
        } catch (IOException e) {
            Logger.e(e, "Save cookie filed: " + uri.toString());
        }

        // handle headers
        Headers headers = new HttpHeaders();
        headers.set(reponseHeaders);
        headers.set(Headers.HEAD_KEY_RESPONSE_MESSAGE, responseMessage);
        headers.set(Headers.HEAD_KEY_RESPONSE_CODE, Integer.toString(responseCode));
        // print
        for (String headKey : headers.keySet()) {
            List<String> headValues = headers.getValues(headKey);
            for (String headValue : headValues) {
                StringBuilder builder = new StringBuilder();
                if (!TextUtils.isEmpty(headKey))
                    builder.append(headKey).append(": ");
                if (!TextUtils.isEmpty(headValue))
                    builder.append(headValue);
                Logger.i(builder.toString());
            }
        }
        return headers;
    }

	/* ====================Wirte request body==================== */

    /**
     * Send the request body to the server
     */
    private void writeRequestBody(HttpURLConnection connection, ImplServerRequest request) throws IOException {
        if (request.doOutPut()) {
            Logger.i("-------Send reqeust data start-------");
            BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            Writer writer = new Writer(outputStream, true);
            request.onWriteRequestBody(writer);
            outputStream.flush();
            outputStream.close();
            Logger.i("-------Send request data end-------");
        }
    }

	/* ====================Read response body=================== */

    /**
     * To read information from the server's response
     */
    protected byte[] readResponseBody(InputStream inputStream) throws IOException {
        int readBytes;
        byte[] buffer = new byte[1024];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        while ((readBytes = bufferedInputStream.read(buffer)) != -1)
            content.write(buffer, 0, readBytes);
        content.flush();
        content.close();
        return content.toByteArray();
    }

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
