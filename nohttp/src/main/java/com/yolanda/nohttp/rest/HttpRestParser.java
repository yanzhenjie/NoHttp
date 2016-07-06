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
import com.yolanda.nohttp.error.ParseError;

/**
 * <p>
 * The response parser, The result of parsing the network layer.
 * </p>
 * Created in Jan 25, 2016 4:17:40 PM.
 *
 * @author Yan Zhenjie.
 */
public class HttpRestParser implements ImplRestParser {

    private static HttpRestParser _INSTANCE;

    private final ImplRestConnection mImplRestConnection;

    public static ImplRestParser getInstance(ImplRestConnection implRestConnection) {
        synchronized (HttpRestParser.class) {
            if (_INSTANCE == null)
                _INSTANCE = new HttpRestParser(implRestConnection);
            return _INSTANCE;
        }
    }

    private HttpRestParser(ImplRestConnection implRestConnection) {
        this.mImplRestConnection = implRestConnection;
    }

    @Override
    public <T> Response<T> parserRequest(Request<T> request) {
        long startTime = SystemClock.elapsedRealtime();
        HttpResponse httpResponse = mImplRestConnection.requestNetwork(request);
        String url = request.url();
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
        return new DefaultResponse<T>(url, request.getRequestMethod(), isFromCache, responseHeaders, responseBody, request.getTag(), result, SystemClock.elapsedRealtime() - startTime, exception);
    }

}
