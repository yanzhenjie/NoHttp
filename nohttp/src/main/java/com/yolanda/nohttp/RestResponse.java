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

import java.net.HttpCookie;
import java.util.List;
import java.util.Set;

/**
 * <p>In response to the class, use generic compatibility with all I to type, and put the parsing operation in {@link Request}.</p>
 * Created in Oct 12, 2015 1:00:46 PM.
 *
 * @author YOLANDA;
 */
public class RestResponse<T> implements Response<T> {

    /**
     * Corresponding request URL.
     */
    private final String url;

    /**
     * RequestMethod.
     */
    private final RequestMethod method;

    /**
     * Whether from the cache.
     */
    private final boolean isFromCache;

    /**
     * Http response Headers
     */
    private final Headers headers;

    /**
     * Http response content, It is an error message when it happens.
     */
    private final byte[] byteArray;

    /**
     * Corresponding request TAG.
     */
    private final Object tag;

    /**
     * Corresponding response results.
     */
    private final T result;
    /**
     * Millisecond of request.
     */
    private final long mNetworkMillis;
    /**
     * The error message.
     */
    private Exception mException;

    public RestResponse(String url, RequestMethod requestMethod, boolean isFromCache, Headers headers, byte[] byteArray, Object tag, T result, long millis, Exception exception) {
        this.url = url;
        this.method = requestMethod;
        this.isFromCache = isFromCache;
        this.headers = headers;
        this.byteArray = byteArray;
        this.tag = tag;
        this.result = result;
        this.mNetworkMillis = millis;
        this.mException = exception;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return method;
    }

    @Override
    public boolean isSucceed() {
        return this.mException == null;
    }

    @Override
    public boolean isFromCache() {
        return isFromCache;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public List<HttpCookie> getCookies() {
        return headers.getCookies();
    }

    @Override
    public byte[] getByteArray() {
        return this.byteArray;
    }

    @Override
    public Object getTag() {
        return this.tag;
    }

    @Override
    public T get() {
        return result;
    }

    @Override
    public Exception getException() {
        return mException;
    }

    @Override
    public long getNetworkMillis() {
        return mNetworkMillis;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Headers headers = getHeaders();
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                List<String> values = headers.getValues(key);
                for (String value : values) {
                    if (key != null) {
                        builder.append(key).append(": ");
                    }
                    builder.append(value).append("\n");
                }
            }
        }
        T result = get();
        if (result != null)
            builder.append(result.toString());
        return builder.toString();
    }
}
