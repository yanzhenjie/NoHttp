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

/**
 * <p>Request network and parse the results.</p>
 * Created in Jan 25, 2016 3:57:45 PM.
 *
 * @author Yan Zhenjie.
 */
public interface IRestParser {

    /**
     * Request network and parse the results.
     *
     * @param request request.
     * @param <T>     T.
     * @return {@link Response}.
     */
    <T> Response<T> parserRequest(IParserRequest<T> request);

}
