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

import com.yolanda.nohttp.BasicClientRequest;
import com.yolanda.nohttp.BasicServerRequest;
import com.yolanda.nohttp.Headers;

/**
 * <p>
 * Achieve {@link BasicClientRequest} and {@link BasicServerRequest}, and can add parameters of the interface.
 * </p>
 * Created in Oct 16, 2015 8:22:06 PM.
 *
 * @param <T> a generic, on behalf of you can accept the result type, .It should be with the {@link OnResponseListener}, {@link Response}.
 * @author Yan Zhenjie.
 */
public interface Request<T> extends ImplClientRequest, ImplServerRequest {

    /**
     * Parse response.
     *
     * @param responseHeaders response {@link Headers} of server.
     * @param responseBody    response data of server.
     * @return your response result.
     * @throws Throwable parse error.
     */
    T parseResponse(Headers responseHeaders, byte[] responseBody) throws Throwable;

    /**
     * Parse response.
     *
     * @param url             url.
     * @param responseHeaders response {@link Headers} of server.
     * @param responseBody    response data of server.
     * @return your response result.
     * @deprecated use {@link #parseResponse(Headers, byte[])} instead.
     */
    @Deprecated
    T parseResponse(String url, Headers responseHeaders, byte[] responseBody);

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param responseListener {@link OnResponseListener}.
     */
    void onPreResponse(int what, OnResponseListener<T> responseListener);

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, OnResponseListener)
     */
    int what();

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, OnResponseListener)
     */
    OnResponseListener<T> responseListener();
}