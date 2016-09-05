/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
import com.yolanda.nohttp.rest.ProtocolResult;
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
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Yan Zhenjie on 2016/9/4.
 */
public class RestConnection implements IRestConnection {

    private static IRestConnection instance;

    public static IRestConnection getInstance() {
        synchronized (RestConnection.class) {
            if (instance == null)
                instance = new RestConnection();
            return instance;
        }
    }

    private RestConnection() {
    }

    /**
     * Send the request, send only head, parameters, such as file information.
     *
     * @param request {@link IBasicRequest}.
     * @return {@link ProtocolResult}.
     */
    @Override
    public Connection getConnection(IBasicRequest request) {
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
            urlConnection = createConnectionAndWriteData(request);
            Logger.d("-------Response start-------");
            int responseCode = urlConnection.getResponseCode();
            responseHeaders = parseResponseHeaders(new URI(request.url()), responseCode, urlConnection.getResponseMessage(), urlConnection.getHeaderFields());

            // handle body
            if (responseCode == 301 || responseCode == 302 || responseCode == 303 || responseCode == 307) {
                Connection redirectConnection = handleRedirect(request, responseHeaders);
                responseHeaders = redirectConnection.responseHeaders();
                inputStream = redirectConnection.serverStream();
                exception = redirectConnection.exception();
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
        return new DefaultConnection(urlConnection, responseHeaders, inputStream, exception);
    }

    /**
     * Handle retries, and complete the request network here.
     *
     * @param request {@link IBasicRequest}.
     * @return {@link ProtocolResult}.
     * @throws Exception {@link #createHttpURLConnection(IBasicRequest)}.
     */
    private HttpURLConnection createConnectionAndWriteData(IBasicRequest request) throws Exception {
        HttpURLConnection connection = null;
        Exception exception = null;
        int retryCount = request.getRetryCount() + 1;
        boolean failed = true;
        for (; failed && retryCount > 0; retryCount--) {
            try {
                connection = createHttpURLConnection(request);
                exception = null;
                failed = false;
            } catch (Exception e) {
                exception = e;
            }
        }
        if (failed) {
            throw exception;
        } else if (isAllowHasBody(request.getRequestMethod())) {
            writeRequestBody(request, connection.getOutputStream());
        }
        return connection;
    }

    /**
     * The connection is established, including the head and send the request body.
     *
     * @param request {@link IBasicRequest}.
     * @return {@link HttpURLConnection} Have been established and the server connection, and send the complete data, you can directly determine the response code and read the data.
     * @throws Exception can happen when the connection is established and send data.
     */
    private HttpURLConnection createHttpURLConnection(IBasicRequest request) throws Exception {
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
        String requestMethodStr = requestMethod.toString();
        Logger.i("Request method: " + requestMethodStr);
        // Fix delete patch error.
        try {
            connection.setRequestMethod(requestMethodStr);
        } catch (ProtocolException protocol) {
            try {
                Field methodField = connection.getClass().getDeclaredField("method");
                methodField.setAccessible(true);
                methodField.set(connection, requestMethodStr);
            } catch (Exception noSuchFieldIllegalAccess) {
                throw protocol;
            }
        }

        connection.setDoInput(true);
        connection.setDoOutput(isAllowHasBody(requestMethod));

        // 4.Set request headers
        setHeaders(url.toURI(), connection, request);

        // 5. Connect
        connection.connect();
        return connection;
    }

    /**
     * Set request headers, here will add cookies.
     *
     * @param uri        uri.
     * @param connection {@link HttpURLConnection}.
     * @param request    {@link IBasicRequest}.
     */
    private void setHeaders(URI uri, HttpURLConnection connection, IBasicRequest request) {
        Headers headers = request.headers();
        headers.set(Headers.HEAD_KEY_CONTENT_TYPE, request.getContentType());

        // To fix bug: accidental EOFException before API 19
        List<String> values = headers.getValues(Headers.HEAD_KEY_CONNECTION);
        if (values == null || values.size() == 0) {
            headers.set(Headers.HEAD_KEY_CONNECTION, Build.VERSION.SDK_INT > AndroidVersion.KITKAT ? Headers.HEAD_VALUE_CONNECTION_KEEP_ALIVE : Headers.HEAD_VALUE_CONNECTION_CLOSE);
        }

        // Content-Length.
        RequestMethod requestMethod = request.getRequestMethod();
        if (isAllowHasBody(requestMethod)) {
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
            headers.addCookie(uri, NoHttp.getDefaultCookieManager());

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
     * Write request params.
     *
     * @param request      {@link IBasicRequest}.
     * @param outputStream {@link OutputStream}.
     * @throws IOException io exception.
     */
    private void writeRequestBody(IBasicRequest request, OutputStream outputStream) throws IOException {
        // 6. Write request body
        Logger.i("-------Send request data start-------");
        OutputStream realOutputStream = IOUtils.toBufferedOutputStream(outputStream);
        request.onWriteRequestBody(realOutputStream);
        IOUtils.closeQuietly(realOutputStream);
        Logger.i("-------Send request data end-------");
    }

    /**
     * The redirection process any response.
     *
     * @param oldRequest      need to redirect the {@link Request}.
     * @param responseHeaders need to redirect the request of the responding head.
     * @return {@link ProtocolResult}.
     */
    private Connection handleRedirect(IBasicRequest oldRequest, Headers responseHeaders) {
        // redirect request
        IBasicRequest redirectRequest = null;
        RedirectHandler redirectHandler = oldRequest.getRedirectHandler();
        if (redirectHandler != null) {
            if (redirectHandler.isDisallowedRedirect(responseHeaders))
                return new DefaultConnection(null, responseHeaders, null, null);
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
     * Allow has body.
     *
     * @param requestMethod {@link RequestMethod}.
     * @return true, other wise is false.
     */
    private boolean isAllowHasBody(RequestMethod requestMethod) {
        boolean allowRequestBody = requestMethod.allowRequestBody();
        // Fix Android bug.
        if (Build.VERSION.SDK_INT < AndroidVersion.LOLLIPOP)
            return allowRequestBody && requestMethod != RequestMethod.DELETE;
        return allowRequestBody;
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
    private InputStream getServerStream(int responseCode, String contentEncoding, HttpURLConnection urlConnection) throws IOException {
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
    private InputStream getInputStream(String contentEncoding, HttpURLConnection urlConnection) throws IOException {
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
    private InputStream getErrorStream(String contentEncoding, HttpURLConnection urlConnection) throws IOException {
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
    private InputStream gzipInputStream(String contentEncoding, InputStream inputStream) throws IOException {
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
    private Headers parseResponseHeaders(URI uri, int responseCode, String responseMessage, Map<String, List<String>> responseHeaders) {
        // handle cookie
        if (NoHttp.isEnableCookie())
            try {
                NoHttp.getDefaultCookieManager().put(uri, responseHeaders);
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
     * According to the response code to judge whether there is download data.
     *
     * @param responseCode responseCode.
     * @return true: there is data, false: no data.
     */
    public static boolean hasDownload(int responseCode) {
        return 200 <= responseCode && responseCode < 300 && responseCode != 204 && responseCode != 205;
    }

}
