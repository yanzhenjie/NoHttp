/*
 * Copyright © Yan Zhenjie
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
package com.yanzhenjie.nohttp.sample.nohttp;

import android.app.Activity;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * <p>针对队列的一个全局单例封装。</p>
 * Created by YanZhenjie on 2017/6/18.
 */
public class CallServer {

    private static CallServer sInstance;

    public static CallServer getInstance() {
        if (sInstance == null)
            synchronized (CallServer.class) {
                if (sInstance == null)
                    sInstance = new CallServer();
            }
        return sInstance;
    }

    private RequestQueue mRequestQueue;
    private DownloadQueue mDownloadQueue;


    private CallServer() {
        mRequestQueue = NoHttp.newRequestQueue(5);
        mDownloadQueue = NoHttp.newDownloadQueue(3);
    }

    public <T> void request(int what, Request<T> request, OnResponseListener<T> listener) {
        mRequestQueue.add(what, request, listener);
    }

    public <T> void request(Activity activity, int what, Request<T> request, HttpListener<T> callback, boolean canCancel, boolean isLoading) {
        mRequestQueue.add(what, request, new HttpResponseListener<>(activity, request, callback, canCancel, isLoading));
    }

    public void download(int what, DownloadRequest request, DownloadListener listener) {
        mDownloadQueue.add(what, request, listener);
    }

}
