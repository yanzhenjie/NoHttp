/*
 * Copyright © YOLANDA. All Rights Reserved
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
package com.yolanda.nohttp.cache;

import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Response;

/**
 * <p>NoHttp caching pattern, the default value is {@link CacheMode#DEFAULT}, other value may be  {@link CacheMode#REQUEST_FAILED_READ_CACHE}, {@link CacheMode#ONLY_READ_CACHE}, {@link CacheMode#IF_NONE_CACHE_REQUEST}.</p>
 * Created by YOLANDA on 2016/3/13.
 *
 * @author YOLANDA;
 */
public enum CacheMode {
    /**
     * The default mode, according to the standard HTTP protocol cache, such as response header is 304.
     */
    DEFAULT,

    /**
     * <p>Request fails to read the cache,
     * if the request to the server success or cache exists invoke {@link OnResponseListener#onSucceed(int, Response)},
     * if the request to the server failure or cache does not exist invoke {@link OnResponseListener#onFailed(int, String, Object, Exception, int, long)}.</p>
     */
    REQUEST_FAILED_READ_CACHE,

    /**
     * <p>If the cache exists invoke {@link OnResponseListener#onSucceed(int, Response)}, otherwise invoke {@link OnResponseListener#onFailed(int, String, Object, Exception, int, long)}.</p>
     */
    ONLY_READ_CACHE,

    /**
     * If there is no cache request, it returns the cache cache exists.
     */
    IF_NONE_CACHE_REQUEST
}
