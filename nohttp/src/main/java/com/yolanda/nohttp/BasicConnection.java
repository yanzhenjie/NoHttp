/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp;

import android.os.Build;
import android.text.TextUtils;

import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.HttpResponse;
import com.yolanda.nohttp.rest.ImplServerRequest;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.StringRequest;
import com.yolanda.nohttp.tools.AndroidVersion;
import com.yolanda.nohttp.tools.HeaderUtil;
import com.yolanda.nohttp.tools.IOUtils;
import com.yolanda.nohttp.tools.NetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>
 * Package good Http implementation class, establish connection, read and write data.
 * </p>
 * Created in Aug 4, 2015 10:12:38 AM.
 *
 * @author Yan Zhenjie.
 */
public class BasicConnection {

    /**
     * Send the request, send only head, parameters, such as file information.
     *
     * @param request {@link ImplServerRequest}.
     * @return {@link HttpResponse}.
     */
    protected Connection getConnection(BasicServerRequest request) {
        Logger.d("--------------Request start--------------");

        Headers responseHeaders = new HttpHeaders();
        InputStream inputStream = null;
        Exception exception = null;

        HttpURLConnection urlConnection = null;
        String url = request.url();
        try {
            if (!NetUtil.isNetworkAvailable())
                throw new NetworkError("The network is not available, please check the network. The requested url is: " + url);

            // MalformedURLException, IOException, ProtocolException, UnknownHostException, SocketTimeoutException
            urlConnection = createHttpURLConnection(request);
            Logger.d("-------Response start-------");
            int responseCode = urlConnection.getResponseCode();
            responseHeaders = parseResponseHeaders(new URI(request.url()), responseCode, urlConnection.getResponseMessage(), urlConnection.getHeaderFields());

            // handle body
            if (responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307) {
                Connection redirectConnectiont = handleRedirect(request, responseHeaders);
                responseHeaders = redirectConnectiont.responseHeaders();
                inputStream = redirectConnectiont.serverStream();
                exception = redirectConnectiont.exception();
            } else if (hasResponseBody(request.getRequestMethod(), responseCode)) {
                inputStream = getServerStream(responseCode, responseHeaders.getContentEncoding(), urlConnection);
            }
            Logger.d("-------Response end-------");
        } catch (MalformedURLException e) {
            exception = new URLError("The url is malformed: " + url + ".");
        } catch (UnknownHostException e) {
            exception = new UnKnownHostError("Hostname can not be resolved: " + url + ".");
        } catch (SocketTimeoutException e) {
            exception = new TimeoutError("Request time out: " + url + ".");
        } catch (Exception e) {
            exception = e;
        } finally {
            if (exception != null)
                Logger.e(exception);
        }
        Logger.d("--------------Request finish--------------");
        return new Connection(urlConnection, responseHeaders, inputStream, exception);
    }

    /**
     * The redirection process any response.
     *
     * @param oldRequest      need to redirect the {@link Request}.
     * @param responseHeaders need to redirect the request of the responding head.
     * @return {@link HttpResponse}.
     */
    private Connection handleRedirect(BasicServerRequest oldRequest, Headers responseHeaders) {
        // redirect request
        Request<?> redirectRequest = null;
        RedirectHandler redirectHandler = oldRequest.getRedirectHandler();
        if (redirectHandler != null) {
            if (redirectHandler.isDisallowedRedirect(responseHeaders))
                return new Connection(null, responseHeaders, null, null);
            else
                redirectRequest = redirectHandler.onRedirect(responseHeaders);
        }
        if (redirectRequest == null) {
            redirectRequest = new StringRequest(responseHeaders.getLocation(), oldRequest.getRequestMethod());
            redirectRequest.setSSLSocketFactory(oldRequest.getSSLSocketFactory());
            redirectRequest.setProxy(oldRequest.getProxy());
        }
        return getConnection(redirectRequest);
    }

    /**
     * The connection is established, including the head and send the request body.
     *
     * @param request {@link BasicServerRequest}.
     * @return {@link HttpURLConnection} Have been established and the server connection, and send the complete data, you can directly determine the response code and read the data.
     * @throws IOException        can happen when the connection is established and send data.
     * @throws URISyntaxException url error.
     */
    protected HttpURLConnection createHttpURLConnection(BasicServerRequest request) throws IOException, URISyntaxException {
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

        connection.setConnectTimeout(request.getConnectTimeout());
        connection.setReadTimeout(request.getReadTimeout());
        connection.setInstanceFollowRedirects(false);

        if (connection instanceof HttpsURLConnection) {
            SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
            if (sslSocketFactory != null)
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
            HostnameVerifier hostnameVerifier = request.getHostnameVerifier();
            if (hostnameVerifier != null)
                ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
        }

        // 3. Base attribute
        RequestMethod requestMethod = request.getRequestMethod();
        Logger.i("Request method: " + requestMethod.toString());
        // Fix delete patch error.
        try {
            connection.setRequestMethod(requestMethod.toString());
        } catch (ProtocolException protocol) {
            try {
                Field methodField = connection.getClass().getDeclaredField("method");
                methodField.setAccessible(true);
                methodField.set(connection, requestMethod.toString());
            } catch (Exception noSuchFieldIllegalAccess) {
                throw protocol;
            }
        }

        connection.setDoInput(true);
        connection.setDoOutput(requestMethod.allowRequestBody());

        // 4.Set request headers
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            Logger.w(e);
        }
        setHeaders(uri, connection, request);

        // 5. Connect
        connection.connect();

        // 6. Write request body
        if (requestMethod.allowRequestBody()) {
            Logger.i("-------Send request data start-------");
            OutputStream outputStream = IOUtils.toBufferedOutputStream(connection.getOutputStream());
            request.onWriteRequestBody(outputStream);
            IOUtils.flushQuietly(outputStream);
            IOUtils.closeQuietly(outputStream);
            Logger.i("-------Send request data end-------");
        }

        return connection;
    }

    /**
     * Set request headers, here will add cookies.
     */
    private void setHeaders(URI uri, HttpURLConnection connection, BasicServerRequest request) {
        Headers headers = request.headers();
        headers.set(Headers.HEAD_KEY_CONTENT_TYPE, request.getContentType());

        // try fix EOF Exception.
        if (Build.VERSION.SDK_INT > AndroidVersion.KITKAT)
            headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION_KEEP_ALIVE);
        else
            headers.set(Headers.HEAD_KEY_CONNECTION, Headers.HEAD_VALUE_CONNECTION_CLOSE);

        // Content-Length.
        RequestMethod requestMethod = request.getRequestMethod();
        if (requestMethod.allowRequestBody()) {
            long contentLength = request.getContentLength();
            if (contentLength < Integer.MAX_VALUE)
                connection.setFixedLengthStreamingMode((int) contentLength);
            else if (Build.VERSION.SDK_INT >= AndroidVersion.KITKAT)
                try {
                    Class<?> connectionClass = connection.getClass();
                    Method setFixedLengthStreamingModeMethod = connectionClass.getMethod("setFixedLengthStreamingMode", long.class);
                    setFixedLengthStreamingModeMethod.invoke(connection, contentLength);
                } catch (Throwable e) {
                    Logger.w(e);
                    connection.setChunkedStreamingMode(256 * 1024);
                }
            else
                connection.setChunkedStreamingMode(256 * 1024);
            headers.set(Headers.HEAD_KEY_CONTENT_LENGTH, Long.toString(contentLength));
        }

        // Cookie.
        if (NoHttp.isEnableCookie() && uri != null)
            headers.addCookie(uri, NoHttp.getDefaultCookieHandler());

        Map<String, String> requestHeaders = headers.toRequestHeaders();

        // Adds all request header to httpConnection.
        for (Map.Entry<String, String> headerEntry : requestHeaders.entrySet()) {
            String headKey = headerEntry.getKey();
            String headValue = headerEntry.getValue();
            Logger.i(headKey + ": " + headValue);
            connection.setRequestProperty(headKey, headValue);
        }
    }

    /**
     * Get input stream from connection.
     *
     * @param responseCode    response code of connection.
     * @param contentEncoding {@value Headers#HEAD_KEY_CONTENT_ENCODING} value of the HTTP response headers.
     * @param urlConnection   connection.
     * @return when the normal return the correct input stream, returns the error when the response code is more than 400 input stream.
     * @throws IOException if no InputStream could be created.
     */
    protected InputStream getServerStream(int responseCode, String contentEncoding, HttpURLConnection urlConnection) throws IOException {
        if (responseCode >= 400)
            return getErrorStream(contentEncoding, urlConnection);
        else {
            return getInputStream(contentEncoding, urlConnection);
        }
    }

    /**
     * Get the input stream, and automatically extract.
     *
     * @param contentEncoding {@value Headers#HEAD_KEY_CONTENT_ENCODING} value of the HTTP response headers.
     * @param urlConnection   {@link HttpURLConnection}.
     * @return http input stream.
     * @throws IOException Unpack the stream may be thrown, or if no input stream could be created.
     */
    protected InputStream getInputStream(String contentEncoding, HttpURLConnection urlConnection) throws IOException {
        InputStream inputStream = urlConnection.getInputStream();
        return gzipInputStream(contentEncoding, inputStream);
    }

    /**
     * Get the wrong input stream, and automatically extract.
     *
     * @param contentEncoding {@value Headers#HEAD_KEY_CONTENT_ENCODING} value of the HTTP response headers.
     * @param urlConnection   {@link HttpURLConnection}.
     * @return http error stream.
     * @throws IOException Unpack the stream may be thrown.
     */
    protected InputStream getErrorStream(String contentEncoding, HttpURLConnection urlConnection) throws IOException {
        InputStream inputStream = urlConnection.getErrorStream();
        return gzipInputStream(contentEncoding, inputStream);
    }

    /**
     * Pressure http input stream.
     *
     * @param contentEncoding {@value Headers#HEAD_KEY_CONTENT_ENCODING} value of the HTTP response headers.
     * @param inputStream     {@link InputStream}.
     * @return It can directly read normal data flow
     * @throws IOException if an {@code IOException} occurs.
     */
    protected InputStream gzipInputStream(String contentEncoding, InputStream inputStream) throws IOException {
        if (HeaderUtil.isGzipContent(contentEncoding)) {
            inputStream = new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    /**
     * Parse server response headers, here will save cookies.
     *
     * @param uri             according to the requested URL generated uris.
     * @param responseCode    responseCode.
     * @param responseMessage responseMessage.
     * @param responseHeaders responseHeaders of server.
     * @return response headers of server.
     */
    protected Headers parseResponseHeaders(URI uri, int responseCode, String responseMessage, Map<String, List<String>> responseHeaders) {
        // handle cookie
        if (NoHttp.isEnableCookie())
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

    ////////// Read response body //////////

    /**
     * This requestMethod and responseCode has responseBody ?
     *
     * @param requestMethod it's come from {@link RequestMethod}.
     * @param responseCode  responseCode from server.
     * @return true: there is data, false: no data.
     */
    public static boolean hasResponseBody(RequestMethod requestMethod, int responseCode) {
        return requestMethod != RequestMethod.HEAD && hasResponseBody(responseCode);
    }

    /**
     * According to the response code to judge whether there is data.
     *
     * @param responseCode responseCode.
     * @return true: there is data, false: no data.
     */
    public static boolean hasResponseBody(int responseCode) {
        return !(100 <= responseCode && responseCode < 200) && responseCode != 204 && responseCode != 205 && !(300 <= responseCode && responseCode < 400);
    }

    /**
     * This requestMethod and responseCode has responseBody ?
     *
     * @param requestMethod it's come from {@link RequestMethod}.
     * @param responseCode  responseCode from server.
     * @return true: there is data, false: no data.
     */
    public static boolean hasDownload(RequestMethod requestMethod, int responseCode) {
        return requestMethod != RequestMethod.HEAD && hasDownload(responseCode);
    }

    /**
     * According to the response code to judge whether there is download data.
     *
     * @param responseCode responseCode.
     * @return true: there is data, false: no data.
     */
    public static boolean hasDownload(int responseCode) {
        return 200 <= responseCode && responseCode < 300 && responseCode != 204 && responseCode != 205;
    }

}
