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

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * Based on the implementation of the queue request.
 * </p>
 * Created by YanZhenjie on Oct 20, 2015 4:24:27 PM.
 */
public abstract class RestRequest<T> extends Request<T> {

    /**
     * The callback mark.
     */
    private int what;
    /**
     * The request of the listener.
     */
    private WeakReference<OnResponseListener<T>> responseListener;

    /**
     * Request queue
     */
    private BlockingQueue<?> blockingQueue;

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request address, like: {@code http://www.nohttp.net}.
     */
    public RestRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a request
     *
     * @param url           request address, like: {@code http://www.nohttp.net}.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public RestRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public void onPreResponse(int what, OnResponseListener<T> responseListener) {
        this.what = what;
        this.responseListener = new WeakReference<>(responseListener);
    }

    @Override
    public int what() {
        return what;
    }

    @Override
    public OnResponseListener<T> responseListener() {
        if (responseListener != null)
            return responseListener.get();
        return null;
    }

    @Override
    public void setQueue(BlockingQueue<?> queue) {
        blockingQueue = queue;
    }

    @Override
    public boolean inQueue() {
        return blockingQueue != null && blockingQueue.contains(this);
    }

    @Override
    public void cancel() {
        if (blockingQueue != null)
            blockingQueue.remove(this);
        super.cancel();
    }
}