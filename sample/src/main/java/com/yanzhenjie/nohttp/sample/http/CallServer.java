/*
 * Copyright © 2018 Yan Zhenjie.
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
package com.yanzhenjie.nohttp.sample.http;

import android.content.Context;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * Created by YanZhenjie on 2018/7/25.
 */
public class CallServer {

    private static CallServer sInstance;

    public static CallServer getInstance() {
        if (sInstance == null) synchronized (CallServer.class) {
            if (sInstance == null) sInstance = new CallServer();
        }
        return sInstance;
    }

    private RequestQueue mRequestQueue;


    private CallServer() {
        mRequestQueue = NoHttp.newRequestQueue(3);
    }

    public <T> void request(Context context, AbstractRequest<T> request, HttpCallback<T> callback,
                            boolean dialog) {
        mRequestQueue.add(0, request, new DefaultResponseListener<>(context, callback, dialog));
    }

    public void cancelBySign(Object sign) {
        mRequestQueue.cancelBySign(sign);
    }

    public void cancelAll() {
        mRequestQueue.cancelAll();
    }
}