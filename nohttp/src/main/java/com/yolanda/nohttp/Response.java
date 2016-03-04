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

/**
 * <p>Http response, Including header information and response packets.</p>
 * Created in Oct 15, 2015 8:55:37 PM.
 *
 * @author YOLANDA;
 */
public interface Response<T> {

    /**
     * Get the requested url.
     *
     * @return URL.
     */
    String url();

    /**
     * RequestMethod.
     *
     * @return {@link RequestMethod}.
     */
    RequestMethod getRequestMethod();

    /**
     * Ask for success.
     *
     * @return True: Succeed, false: failed.
     */
    boolean isSucceed();

    /**
     * Whether from the cache.
     *
     * @return True: the data from cache, false: the data from server.
     */
    boolean isFromCache();

    /**
     * Get http response headers.
     *
     * @return {@link Headers}.
     */
    Headers getHeaders();

    /**
     * Get http response Cookie.
     *
     * @return {@code List<HttpCookie>}.
     */
    List<HttpCookie> getCookies();

    /**
     * Get raw data.
     *
     * @return {@code byte[]}.
     */
    byte[] getByteArray();

    /**
     * Get request results.
     *
     * @return {@link T}.
     */
    T get();

    /**
     * Get Error Message.
     *
     * @return The exception.
     */
    Exception getException();

    /**
     * Gets the tag of request.
     *
     * @return {@link Object}.
     */
    Object getTag();

    /**
     * Gets the millisecond of request.
     *
     * @return {@link Long}.
     */
    long getNetworkMillis();
}
