/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp;

import android.os.SystemClock;

/**
 * <p>The response parser, The result of parsing the network layer.</p>
 * Created in Jan 25, 2016 4:17:40 PM.
 *
 * @author YOLANDA;
 */
public class HttpRestParser implements ImplRestParser {

    private static HttpRestParser _INSTANCE;

    private final ImplRestExecutor mImplRestExecutor;

    public static HttpRestParser getInstance(ImplRestExecutor implRestExecutor) {
        if (_INSTANCE == null)
            _INSTANCE = new HttpRestParser(implRestExecutor);
        return _INSTANCE;
    }

    private HttpRestParser(ImplRestExecutor mImplRestExecutor) {
        this.mImplRestExecutor = mImplRestExecutor;
    }

    @Override
    public <T> Response<T> parserRequest(Request<T> request) {
        long startTime = SystemClock.elapsedRealtime();
        HttpResponse httpResponse = mImplRestExecutor.executeRequest(request);
        String url = request.url();
        boolean isFromCache = httpResponse.isFromCache;
        Headers responseHeaders = httpResponse.responseHeaders;
        Exception exception = httpResponse.exception;
        byte[] responseBody = httpResponse.responseBody;
        if (exception == null) {
            T result = request.parseResponse(url, responseHeaders, responseBody);
            return new RestResponse<T>(url, request.getRequestMethod(), isFromCache, responseHeaders, responseBody, request.getTag(), result, SystemClock.elapsedRealtime() - startTime, exception);
        }
        return new RestResponse<T>(url, request.getRequestMethod(), isFromCache, responseHeaders, responseBody, request.getTag(), null, SystemClock.elapsedRealtime() - startTime, exception);
    }

}
