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

import com.yanzhenjie.nohttp.able.Cancelable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> Asynchronous handle executor.
 *
 * </p> Created by Yan Zhenjie on 2017/2/15.
 */
public enum AsyncRequestExecutor {

    INSTANCE;

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Request #" + mCount.getAndIncrement());
        }
    };

    private static final Executor EXECUTOR = Executors.newCachedThreadPool(THREAD_FACTORY);

    public <T> Cancelable execute(int what, Request<T> request, OnResponseListener<T> callback) {
        Worker<? extends Request<T>, T> worker = new Worker<>(request);
        Work work = new Work<>(worker, what, callback);
        request.setCancelable(work);

        EXECUTOR.execute(work);
        return work;
    }
}