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

import com.yolanda.nohttp.BasicClientRequest;

/**
 * Created in Mar 23, 2016 10:10:19 PM.
 *
 * @author Yan Zhenjie.
 */
public interface ImplClientRequest extends BasicClientRequest {

    /**
     * Set the request cache primary key, it should be globally unique.
     *
     * @param key unique key.
     */
    void setCacheKey(String key);

    /**
     * Set the cache mode.
     *
     * @param cacheMode The value from {@link CacheMode}.
     */
    void setCacheMode(CacheMode cacheMode);

    /**
     * Set the request fails retry count.The default value is 0, that is to say, after the failure will not go to this to initiate the request again.
     *
     * @param count the retry count, The default value is 0.
     */
    void setRetryCount(int count);
}
