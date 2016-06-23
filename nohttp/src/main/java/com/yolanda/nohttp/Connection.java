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

import com.yolanda.nohttp.tools.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created in May 3, 2016 11:05:03 PM.
 *
 * @author Yan Zhenjie.
 */
public class Connection implements Closeable {

    /**
     * HttpURLConnection
     */
    private HttpURLConnection connection;
    /**
     * Server response header.
     */
    private Headers mResponseHeaders;
    /**
     * Server data steram.
     */
    private InputStream mInputStream;
    /**
     * Exception of connection.
     */
    private Exception mException;

    /**
     * Create a response.
     *
     * @param responseHeaders response headers.
     * @param inputStream     According to the response code, the incoming data stream server.
     * @param exception       Connection exceptions that occur in the process.
     */
    Connection(HttpURLConnection connection, Headers responseHeaders, InputStream inputStream, Exception exception) {
        this.connection = connection;
        this.mResponseHeaders = responseHeaders;
        this.mInputStream = inputStream;
        this.mException = exception;
    }

    /**
     * Get the {@link URL} of connection.
     *
     * @return {@link URL}.
     */
    public URL getURL() {
        return connection.getURL();
    }

    /**
     * Get response headers.
     *
     * @return the responseHeaders.
     */
    public Headers responseHeaders() {
        return mResponseHeaders;
    }

    /**
     * Set response headers.
     *
     * @param responseHeaders the responseHeaders to set.
     */
    void setResponseHeaders(Headers responseHeaders) {
        this.mResponseHeaders = responseHeaders;
    }

    /**
     * Get stream from server.
     *
     * @return the inputStream.
     */
    public InputStream serverStream() {
        return mInputStream;
    }

    /**
     * Set the stream from server.
     *
     * @param inputStream the inputStream to set.
     */
    void setServerStream(InputStream inputStream) {
        this.mInputStream = inputStream;
    }

    /**
     * Get exception for execution.
     *
     * @return the exception.
     */
    public Exception exception() {
        return mException;
    }

    /**
     * Set execetpin for execution.
     *
     * @param exception the exception to set.
     */
    void setException(Exception exception) {
        this.mException = exception;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(mInputStream);
        IOUtils.closeQuietly(connection);
    }

}
