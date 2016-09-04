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

import com.yolanda.nohttp.tools.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yan Zhenjie on 2016/9/4.
 */
public class DefaultConnection implements Connection {

    /**
     * HttpURLConnection
     */
    private HttpURLConnection connection;
    /**
     * Server response header.
     */
    private Headers mResponseHeaders;
    /**
     * Server data stream.
     */
    private InputStream mInputStream;
    /**
     * Exception of connection.
     */
    private Exception mException;

    /**
     * Create a response.
     *
     * @param connection      {@link HttpURLConnection}.
     * @param responseHeaders response headers.
     * @param inputStream     According to the response code, the incoming data stream server.
     * @param exception       connection exceptions that occur in the process.
     */
    public DefaultConnection(HttpURLConnection connection, Headers responseHeaders, InputStream inputStream, Exception exception) {
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
    @Override
    public URL getURL() {
        return connection.getURL();
    }

    /**
     * Get response headers.
     *
     * @return the responseHeaders.
     */
    @Override
    public Headers responseHeaders() {
        return mResponseHeaders;
    }

    /**
     * Get stream from server.
     *
     * @return the inputStream.
     */
    @Override
    public InputStream serverStream() {
        return mInputStream;
    }

    /**
     * Get exception for execution.
     *
     * @return the exception.
     */
    public Exception exception() {
        return mException;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(mInputStream);
        IOUtils.closeQuietly(connection);
    }

}
