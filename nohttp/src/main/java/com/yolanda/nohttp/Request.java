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

import com.yolanda.nohttp.rest.ImplClientRequest;
import com.yolanda.nohttp.rest.ImplServerRequest;

/**
 * Created on 2016/6/1.
 *
 * @author Yan Zhenjie;
 * @deprecated use {@link com.yolanda.nohttp.rest.Request} instead.
 */
@Deprecated
public interface Request<T> extends ImplClientRequest, ImplServerRequest {

    /**
     * Parse response.
     *
     * @param url             url.
     * @param responseHeaders response {@link Headers} of server.
     * @param responseBody    response data of server.
     * @return your response result.
     */
    T parseResponse(String url, Headers responseHeaders, byte[] responseBody);

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param responseListener {@link com.yolanda.nohttp.rest.OnResponseListener}.
     */
    void onPreResponse(int what, com.yolanda.nohttp.rest.OnResponseListener<T> responseListener);

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, com.yolanda.nohttp.rest.OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, com.yolanda.nohttp.rest.OnResponseListener)
     */
    int what();

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, com.yolanda.nohttp.rest.OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, com.yolanda.nohttp.rest.OnResponseListener)
     */
    com.yolanda.nohttp.rest.OnResponseListener<T> responseListener();
}

