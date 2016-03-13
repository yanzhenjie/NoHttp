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

import com.yolanda.nohttp.BasicServerRequest;

/**
 * Created in Mar 23, 2016 10:11:39 PM.
 *
 * @author Yan Zhenjie.
 */
public interface ImplServerRequest extends BasicServerRequest {

    /**
     * Get key of cache data.
     *
     * @return Cache key.
     */
    String getCacheKey();

    /**
     * He got the request cache mode.
     *
     * @return Value from {@link CacheMode}.
     */
    CacheMode getCacheMode();

    /**
     * To get the failure after retries.
     *
     * @return The default value is 0.
     */
    int getRetryCount();

}
