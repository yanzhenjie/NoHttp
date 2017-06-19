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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.able.Queueable;

/**
 * <p>
 * Support the characteristics of the queue.
 * </p>
 * Created by Yan Zhenjie on Oct 16, 2015 8:22:06 PM.
 */
public abstract class Request<T> extends ProtocolRequest<Request, T> implements Queueable {

    /**
     * Create a request, request method is {@link RequestMethod#GET}.
     *
     * @param url request address, like: http://www.nohttp.net.
     */
    public Request(String url) {
        super(url);
    }

    /**
     * Create a request
     *
     * @param url           request address, like: http://www.nohttp.net.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public Request(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param responseListener {@link OnResponseListener}.
     */
    abstract void onPreResponse(int what, OnResponseListener<T> responseListener);

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, OnResponseListener)
     */
    public abstract int what();

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, OnResponseListener)} incoming credit.
     * @see #onPreResponse(int, OnResponseListener)
     */
    public abstract OnResponseListener<T> responseListener();
}