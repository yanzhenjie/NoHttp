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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * Asynchronous handle executor.
 * </p>
 * Created by Yan Zhenjie on 2017/2/15.
 */
public enum AsyncRequestExecutor {

    INSTANCE;

    /**
     * ExecutorService.
     */
    private ExecutorService mExecutorService;

    AsyncRequestExecutor() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public <T> void execute(int what, Request<T> request, OnResponseListener<T> listener) {
        mExecutorService.execute(new RequestTask<>(request, Messenger.newInstance(what, listener)));
    }

    private static class RequestTask<T> implements Runnable {

        private Request<T> request;
        private Messenger mMessenger;

        private RequestTask(Request<T> request, Messenger messenger) {
            this.request = request;
            this.mMessenger = messenger;
        }

        @Override
        public void run() {
            if (request.isCanceled()) {
                Logger.d(request.url() + " is canceled.");
                return;
            }

            // start.
            request.start();
            mMessenger.start();

            // handle.
            Response<T> response = SyncRequestExecutor.INSTANCE.execute(request);

            if (request.isCanceled()) {
                Logger.d(request.url() + " finish, but it's canceled.");
            } else {
                //noinspection unchecked
                mMessenger.response(response);
            }

            // finish.
            request.finish();
            mMessenger.finish();
        }
    }

}
