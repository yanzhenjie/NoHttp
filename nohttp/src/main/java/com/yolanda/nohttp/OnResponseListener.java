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

/**
 * Created in Jul 28, 2015 7:32:53 PM.
 *
 * @author YOLANDA;
 */
public interface OnResponseListener<T> {

    /**
     * When the request starts.
     *
     * @param what the credit of the incoming request is used to distinguish between multiple requests.
     */
    void onStart(int what);

    /**
     * Server correct response to callback when an HTTP request.
     *
     * @param what     the credit of the incoming request is used to distinguish between multiple requests.
     * @param response in response to the results.
     */
    void onSucceed(int what, Response<T> response);

    /**
     * When there was an error correction.
     *
     * @param what          the credit of the incoming request is used to distinguish between multiple requests.
     * @param url           url.
     * @param tag           tag of request callback.
     * @param exception     error message for request.
     * @param responseCode  server response code.
     * @param networkMillis request process consumption time.
     */
    void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis);

    /**
     * When the request finish.
     *
     * @param what the credit of the incoming request is used to distinguish between multiple requests.
     */
    void onFinish(int what);

}