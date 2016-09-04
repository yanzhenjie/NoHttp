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
package com.yolanda.nohttp.rest;

import com.yolanda.nohttp.RequestMethod;

/**
 * Created by Yan Zhenjie on 2016/8/20.
 */
public abstract class ParseRequest<T> extends ProtocolRequest implements IParserRequest<T> {

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request address, like: http://www.yanzhenjie.com.
     */
    public ParseRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a request.
     *
     * @param url           request address, such as: http://www.yanzhenjie.com.
     * @param requestMethod it's come from {@link CacheMode}.
     */
    public ParseRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }
}
