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

import android.os.SystemClock;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NetworkExecutor;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.tools.CacheStore;

/**
 * <p>
 * The response parser, The result of parsing the network layer.
 * </p>
 * Created in Jan 25, 2016 4:17:40 PM.
 *
 * @author Yan Zhenjie.
 */
public class RestParser {

    private RestProtocol mRestProtocol;

    public RestParser(CacheStore<CacheEntity> cache, NetworkExecutor executor) {
        mRestProtocol = new RestProtocol(cache, executor);
    }

    public <T> Response<T> parserRequest(IParserRequest<T> request) {
        long startTime = SystemClock.elapsedRealtime();
        ProtocolResult httpResponse = mRestProtocol.requestNetwork(request);
        boolean isFromCache = httpResponse.isFromCache();
        Headers responseHeaders = httpResponse.responseHeaders();
        Exception exception = httpResponse.exception();
        T result = null;
        byte[] responseBody = httpResponse.responseBody();
        if (exception == null) {
            try {
                result = request.parseResponse(responseHeaders, responseBody);
            } catch (Throwable e) {
                exception = new ParseError("Parse data error: " + e.getMessage());
            }
        }
        return new RestResponse<>(request, isFromCache, responseHeaders, result, SystemClock.elapsedRealtime() - startTime, exception);
    }

}
