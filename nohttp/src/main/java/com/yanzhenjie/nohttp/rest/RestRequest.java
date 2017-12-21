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

/**
 * <p>
 * Based on the implementation of the queue handle.
 * </p>
 * Created by YanZhenjie on Oct 20, 2015 4:24:27 PM.
 *
 * @deprecated use {@link Request} instead.
 */
@Deprecated
public abstract class RestRequest<Result> extends Request<Result> {

    /**
     * Create a handle, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url handle address, like: {@code http://www.nohttp.net}.
     */
    public RestRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a handle
     *
     * @param url           handle address, like: {@code http://www.nohttp.net}.
     * @param requestMethod handle method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public RestRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

}