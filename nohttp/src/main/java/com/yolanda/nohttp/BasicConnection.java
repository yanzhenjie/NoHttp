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

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

import com.yolanda.nohttp.tools.Writer;

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

/**
 * <p>Package good Http implementation class, establish connection, read and write data.</p>
 * Created in Aug 4, 2015 10:12:38 AM.
 *
 * @author YOLANDA;
 */
public class BasicConnection {

    /**
     * The connection is established, including the head and send the request body.
     *
     * @param request {@link ImplServerRequest}.
     * @return {@link HttpURLConnection} Have been established and the server connection, and send the complete data, you can directly determine the response code and read the data.
     * @throws IOException Can happen when the connection is established and send data.
     */
    protected HttpURLConnection getHttpConnection(ImplServerRequest request) throws IOException {
        // 1.Pre operation notice
        request.onPreExecute();

        // 2.Build URL
        String urlStr = request.url();
        Logger.i("Request address: " + urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection connection;
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
            if (hostnameVerifier != null)
                ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
        }

        // 3. Base attribute
        String requestMethod = request.getRequestMethod().toString();
        Logger.i("Request method: " + requestMethod + ".");
        connection.setRequestMethod(requestMethod);
        connection.setDoInput(true);
        connection.setDoOutput(request.doOutPut());
        connection.setConnectTimeout(request.getConnectTimeout());
        connection.setReadTimeout(request.getReadTimeout());
        connection.setInstanceFollowRedirects(false);

        // 4.Set request headers
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
        }
        setHeaders(uri, connection, request);

        // 5. Write request body
        connection.connect();
        writeRequestBody(connection, request);

        return connection;
    }

    /**
     * Set request headers, here will add cookies.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setHeaders(URI uri, HttpURLConnection connection, ImplServerRequest request) {
        // 1.Build Headers
        Headers headers = request.headers();

        // 2.Base header
        // 2.1 Accept
        String accept = request.getAccept();
        if (!TextUtils.isEmpty(accept))
            headers.set(Headers.HEAD_KEY_ACCEPT, accept);
        headers.set(Headers.HEAD_KEY_ACCEPT_ENCODING, Headers.HEAD_VALUE_ACCEPT_ENCODING);

        // 2.2 Accept-Language
        String acceptLanguage = request.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage))
            headers.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);

        // 2.3 Connection
        // To fix bug: accidental EOFException before API 19
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION_KEEP_ALIVE);
        else
            headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION_CLOSE);

        // 2.4 Content-Length
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

        // 2.5 Content-Type
        String contentType = request.getContentType();
        if (!TextUtils.isEmpty(contentType))
            headers.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);

        // 2.6 Cookie
        if (uri != null)
            headers.addCookie(uri, NoHttp.getDefaultCookieHandler());

        // 3. UserAgent
        String userAgent = request.getUserAgent();
        if (!TextUtils.isEmpty(userAgent))
            headers.set(Headers.HEAD_KEY_USER_AGENT, userAgent);

        Map<String, String> requestHeaders = headers.toRequestHeaders();

        // 4.Adds all request header to httpConnection
        for (Map.Entry<String, String> headerEntry : requestHeaders.entrySet()) {
            String headKey = headerEntry.getKey();
            String headValue = headerEntry.getValue();
            Logger.i(headKey + ": " + headValue);
            connection.setRequestProperty(headKey, headValue);
        }
    }

    /**
     * Parse server response headers, here will save cookies.
     *
     * @param uri             according to the requested URL generated uris.
     * @param responseCode    responseCode.
     * @param responseMessage responseMessage.
     * @param responseHeaders responseHeaders of server.
     * @return ResponseHeaders of server.
     */
    protected Headers parseResponseHeaders(URI uri, int responseCode, String responseMessage, Map<String, List<String>> responseHeaders) {
        // handle cookie
        try {
            NoHttp.getDefaultCookieHandler().put(uri, responseHeaders);
        } catch (IOException e) {
            Logger.e(e, "Save cookie filed: " + uri.toString() + ".");
        }

        // handle headers
        Headers headers = new HttpHeaders();
        headers.set(responseHeaders);
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
     * Send the request body to the server.
     *
     * @param connection {@link HttpURLConnection}.
     * @param request    {@link ImplServerRequest}.
     * @throws IOException To send data when possible.
     */
    private void writeRequestBody(HttpURLConnection connection, ImplServerRequest request) throws IOException {
        if (request.doOutPut()) {
            Logger.i("-------Send request data start-------");
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
     * To read information from the server's response.
     *
     * @param inputStream outputStream from the service, for us is the inputStream, the data read from the inputStream.
     * @return Data from server.
     * @throws IOException To read data when possible.
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
     * This requestMethod and responseCode has ResponseBody ?
     *
     * @param requestMethod it's come from {@link RequestMethod}.
     * @param responseCode  responseCode from server.
     * @return True: there is data, false: no data.
     */
    public static boolean hasResponseBody(RequestMethod requestMethod, int responseCode) {
        return requestMethod != RequestMethod.HEAD && hasResponseBody(responseCode);
    }

    /**
     * According to the response code to judge whether there is data.
     *
     * @param responseCode responseCode.
     * @return True: there is data, false: no data.
     */
    public static boolean hasResponseBody(int responseCode) {
        return !(100 <= responseCode && responseCode < 200) && responseCode != 204 && responseCode != 205 && responseCode != 304;
    }

}
