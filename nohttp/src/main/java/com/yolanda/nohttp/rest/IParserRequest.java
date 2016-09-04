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

import com.yolanda.nohttp.Headers;

/**
 * Created by Yan Zhenjie on 2016/8/20.
 */
public interface IParserRequest<T> extends IProtocolRequest {

    /**
     * Parse request results for generic objects.
     *
     * @param responseHeaders response headers of server.
     * @param responseBody    response data of server.
     * @return your response result.
     * @throws Throwable parse error.
     */
    T parseResponse(Headers responseHeaders, byte[] responseBody) throws Throwable;
}
