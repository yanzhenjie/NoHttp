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

import android.text.TextUtils;

import com.yolanda.nohttp.BasicRequest;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;

/**
 * <p>
 * The realization method of the parameters.
 * </p>
 * Created in Oct 20, 2015 4:24:27 PM.
 *
 * @param <T> a generics, regulated the analytic results of the Request.It should be with the {@link Response}, {@link OnResponseListener}.
 * @author Yan Zhenjie.
 */
public abstract class RestRequest<T> extends BasicRequest implements Request<T> {

    /**
     * The callback mark.
     */
    private int what;
    /**
     * The request of the listener.
     */
    private OnResponseListener<T> responseListener;
    /**
     * Cache key.
     */
    private String mCacheKey;
    /**
     * If just read from cache.
     */
    private CacheMode mCacheMode = CacheMode.DEFAULT;

    /**
     * After the failure of retries.
     */
    private int mRetryCount;

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request address, like: http://www.google.com.
     */
    public RestRequest(String url) {
        super(url);
    }

    /**
     * Create a request
     *
     * @param url           request address, like: http://www.google.com.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public RestRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public void setCacheKey(String key) {
        this.mCacheKey = key;
    }

    @Override
    public String getCacheKey() {
        return TextUtils.isEmpty(mCacheKey) ? url() : mCacheKey;
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        this.mCacheMode = cacheMode;
    }

    @Override
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    @Override
    public void setRetryCount(int count) {
        this.mRetryCount = count;
    }

    @Override
    public int getRetryCount() {
        return mRetryCount;
    }

    @Override
    public void onPreResponse(int what, OnResponseListener<T> responseListener) {
        this.what = what;
        this.responseListener = responseListener;
    }

    @Override
    public int what() {
        return what;
    }

    @Override
    public OnResponseListener<T> responseListener() {
        return responseListener;
    }

    @Override
    public T parseResponse(Headers responseHeaders, byte[] responseBody) throws Throwable {
        return parseResponse(url(), responseHeaders, responseBody);
    }

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
    @Override
    public T parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        return null;
    }
}