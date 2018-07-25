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

import com.yanzhenjie.nohttp.CancelerManager;
import com.yanzhenjie.nohttp.HandlerDelivery;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> Request Queue. </p>
 *
 * Created in Oct 19, 2015 8:36:22 AM.
 *
 * @author Yan Zhenjie.
 */
public class RequestQueue {

    private AtomicInteger mInteger = new AtomicInteger(1);
    private final BlockingQueue<Work<? extends Request<?>, ?>> mQueue = new PriorityBlockingQueue<>();
    private final CancelerManager mCancelerManager = new CancelerManager();
    private RequestDispatcher[] mDispatchers;

    /**
     * @param threadPoolSize number of thread pool.
     */
    public RequestQueue(int threadPoolSize) {
        mDispatchers = new RequestDispatcher[threadPoolSize];
    }

    /**
     * All dispatcher in the boot queue, such as the dispatcher that has already been started in the queue,
     * will stop all dispatcher first and restart the equal number of dispatcher.
     *
     * @see #stop()
     */
    public void start() {
        stop();

        for (int i = 0; i < mDispatchers.length; i++) {
            RequestDispatcher dispatcher = new RequestDispatcher(mQueue);
            mDispatchers[i] = dispatcher;
            dispatcher.start();
        }
    }

    /**
     * Add a request to the queue.
     *
     * @param what the {@code what} be returned in the result callback.
     * @param request {@link Request}.
     * @param listener {@link OnResponseListener}.
     * @param <T> {@link T}.
     */
    public <T> void add(int what, final Request<T> request, OnResponseListener<T> listener) {
        Worker<? extends Request<T>, T> worker = new Worker<>(request);
        AsyncCallback<T> callback = new AsyncCallback<T>(listener) {
            @Override
            public void onFinish(int what) {
                mCancelerManager.removeCancel(request);
                super.onFinish(what);
            }
        };
        final Work<? extends Request<T>, T> work = new Work<>(worker, what, callback);
        work.setSequence(mInteger.incrementAndGet());

        callback.setQueue(mQueue);
        callback.setWork(work);

        request.setCancelable(work);

        mCancelerManager.addCancel(request, work);
        mQueue.add(work);
    }

    /**
     * @deprecated use {@link #unFinishSize()} instead.
     */
    @Deprecated
    public int size() {
        return unFinishSize();
    }

    /**
     * The number of requests that have not been executed yet.
     */
    public int unStartSize() {
        return mQueue.size();
    }

    /**
     * The number of all requests, including the request being executed.
     */
    public int unFinishSize() {
        return mCancelerManager.size();
    }

    /**
     * Cancel all requests and stop all dispatchers in the queue.
     */
    public void stop() {
        cancelAll();

        for (RequestDispatcher dispatcher : mDispatchers) {
            if (dispatcher != null) {
                dispatcher.quit();
            }
        }
    }

    /**
     * According to the sign to cancel a task.
     *
     * @see CancelerManager#cancel(Object)
     */
    public void cancelBySign(Object sign) {
        mCancelerManager.cancel(sign);
    }

    /**
     * Cancel all requests.
     */
    public void cancelAll() {
        mCancelerManager.cancelAll();
    }

    static class AsyncCallback<T>
      implements OnResponseListener<T> {

        private final OnResponseListener<T> mCallback;
        private BlockingQueue<Work<? extends Request<?>, ?>> mQueue;
        private Work<? extends Request<?>, ?> mWork;

        AsyncCallback(OnResponseListener<T> callback) {
            this.mCallback = callback;
        }

        public void setQueue(BlockingQueue<Work<? extends Request<?>, ?>> queue) {
            this.mQueue = queue;
        }

        public void setWork(Work<? extends Request<?>, ?> work) {
            this.mWork = work;
        }

        @Override
        public void onStart(final int what) {
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onStart(what);
                }
            });
        }

        @Override
        public void onSucceed(final int what, final Response<T> response) {
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSucceed(what, response);
                }
            });
        }

        @Override
        public void onFailed(final int what, final Response<T> response) {
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFailed(what, response);
                }
            });
        }

        @Override
        public void onFinish(final int what) {
            if (mQueue.contains(mWork)) {
                mQueue.remove(mWork);
            }

            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFinish(what);
                }
            });
        }
    }
}