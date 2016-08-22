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
package com.yolanda.nohttp.rest;

import com.yolanda.nohttp.Headers;

/**
 * Created in Jan 6, 2016 5:19:13 PM.
 *
 * @author Yan Zhenjie.
 */
public class ProtocolResult {

    /**
     * Server response header.
     */
    private Headers mResponseHeaders;
    /**
     * Is the data from the cache.
     */
    private boolean isFromCache;
    /**
     * Data.
     */
    private byte[] mResponseBody;
    /**
     * Exception of connection.
     */
    private Exception mException;

    ProtocolResult(Headers responseHeaders, byte[] responseBody, boolean isFromCache, Exception exception) {
        this.mResponseHeaders = responseHeaders;
        this.mResponseBody = responseBody;
        this.isFromCache = isFromCache;
        this.mException = exception;
    }

    /**
     * Get response headers of server.
     *
     * @return response headers.
     */
    public Headers responseHeaders() {
        return mResponseHeaders;
    }

    /**
     * Set the response headers of server.
     *
     * @param responseHeaders {@link Headers}.
     */
    void setResponseHeaders(Headers responseHeaders) {
        this.mResponseHeaders = responseHeaders;
    }

    /**
     * Get Data.
     *
     * @return the responseBody.
     */
    public byte[] responseBody() {
        return mResponseBody;
    }

    /**
     * Set Data.
     *
     * @param responseBody the responseBody to set.
     */
    void setResponseBody(byte[] responseBody) {
        this.mResponseBody = responseBody;
    }

    /**
     * Is the data from the cache.
     *
     * @return the isFromCache.
     */
    public boolean isFromCache() {
        return isFromCache;
    }

    /**
     * Set is from cache.
     *
     * @param isFromCache the isFromCache to set.
     */
    void setFromCache(boolean isFromCache) {
        this.isFromCache = isFromCache;
    }

    /**
     * Get exception of connection.
     *
     * @return exception.
     */
    public Exception exception() {
        return mException;
    }

    /**
     * Set Exception of connection.
     *
     * @param exception the types of error system.
     */
    void setException(Exception exception) {
        this.mException = exception;
    }

}
