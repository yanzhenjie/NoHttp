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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;

/**
 * <p>
 * Synchronization handle executor.
 * </p>
 * Created by Yan Zhenjie on 2016/10/12.
 */
public enum SyncRequestExecutor {

    INSTANCE;

    private RequestHandler mRequestHandler;

    SyncRequestExecutor() {
        InitializationConfig initializationConfig = NoHttp.getInitializeConfig();
        mRequestHandler = new RequestHandler(
                initializationConfig.getCacheStore(),
                initializationConfig.getNetworkExecutor(),
                initializationConfig.getInterceptor()
        );
    }

    /**
     * Perform a handle.
     */
    public <T> Response<T> execute(Request<T> request) {
        return mRequestHandler.handle(request);
    }
}
