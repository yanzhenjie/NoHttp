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
 * <p>
 * NoHttp caching pattern, the default value is {@link CacheMode#DEFAULT}, other value may be {@link CacheMode#REQUEST_NETWORK_FAILED_READ_CACHE}, {@link CacheMode#ONLY_READ_CACHE},
 * {@link CacheMode#ONLY_REQUEST_NETWORK}, {@link CacheMode#NONE_CACHE_REQUEST_NETWORK}.
 * </p>
 * Created in 2016/3/20 23:17.
 *
 * @author Yan Zhenjie.
 */
public enum CacheMode {

    /**
     * The default mode, according to the standard HTTP protocol cache, such as response header is 304.
     */
    DEFAULT,

    /**
     * <p>
     * Request fails to read the cache, if the request to the server success or cache exists invoke {@link OnResponseListener#onSucceed(int, Response)}, if the request to the server failure or cache
     * does not exist invoke {@link OnResponseListener#onFailed(int, Response)}.
     * </p>
     */
    REQUEST_NETWORK_FAILED_READ_CACHE,

    /**
     * If there is no cache request, it returns the cache cache exists.
     */
    NONE_CACHE_REQUEST_NETWORK,

    /**
     * <p>
     * If the cache exists invoke {@link OnResponseListener#onSucceed(int, Response)}, otherwise invoke {@link OnResponseListener#onFailed(int, Response)}.
     * </p>
     */
    ONLY_READ_CACHE,

    /**
     * It does not deal with anything related to the cache, only to getList the data from the network.
     */
    ONLY_REQUEST_NETWORK
}