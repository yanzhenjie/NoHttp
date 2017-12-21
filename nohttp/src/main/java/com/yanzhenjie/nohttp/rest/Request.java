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

import android.text.TextUtils;

import com.yanzhenjie.nohttp.BasicRequest;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;

/**
 * <p>
 * Support the characteristics of the queue.
 * </p>
 * Created by Yan Zhenjie on Oct 16, 2015 8:22:06 PM.
 */
public abstract class Request<Result> extends BasicRequest<Request> {
    /**
     * Cache key.
     */
    private String mCacheKey;
    /**
     * If just read from cache.
     */
    private CacheMode mCacheMode = CacheMode.DEFAULT;

    /**
     * Create a handle, handle method is {@link RequestMethod#GET}.
     *
     * @param url handle address, like: http://www.nohttp.net.
     */
    public Request(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a handle
     *
     * @param url           handle address, like: http://www.nohttp.net.
     * @param requestMethod handle method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public Request(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    /**
     * Set the handle cache primary key, it should be globally unique.
     *
     * @param key unique key.
     */
    public Request setCacheKey(String key) {
        this.mCacheKey = key;
        return this;
    }

    /**
     * Get key of cache data.
     *
     * @return cache key.
     */
    public String getCacheKey() {
        return TextUtils.isEmpty(mCacheKey) ? url() : mCacheKey;
    }

    /**
     * Set the cache mode.
     *
     * @param cacheMode The value from {@link CacheMode}.
     */
    public Request setCacheMode(CacheMode cacheMode) {
        this.mCacheMode = cacheMode;
        return this;
    }

    /**
     * He got the handle cache mode.
     *
     * @return value from {@link CacheMode}.
     */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /**
     * Parse handle results for generic objects.
     *
     * @param responseHeaders response headers of server.
     * @param responseBody    response data of server.
     * @return your response result.
     * @throws Exception parse error.
     */
    public abstract Result parseResponse(Headers responseHeaders, byte[] responseBody) throws Exception;

}
