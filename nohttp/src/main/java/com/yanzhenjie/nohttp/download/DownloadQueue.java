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
package com.yanzhenjie.nohttp.download;

import com.yanzhenjie.nohttp.CancelerManager;
import com.yanzhenjie.nohttp.HandlerDelivery;
import com.yanzhenjie.nohttp.Headers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> Download queue. </p>
 *
 * Created in Oct 21, 2015 2:44:19 PM.
 *
 * @author Yan Zhenjie.
 */
public class DownloadQueue {

    private AtomicInteger mInteger = new AtomicInteger(1);
    private final BlockingQueue<Work<? extends DownloadRequest>> mQueue = new PriorityBlockingQueue<>();
    private final CancelerManager mCancelerManager = new CancelerManager();
    private DownloadDispatcher[] mDispatchers;

    /**
     * @param threadPoolSize number of thread pool.
     */
    public DownloadQueue(int threadPoolSize) {
        mDispatchers = new DownloadDispatcher[threadPoolSize];
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
            DownloadDispatcher dispatcher = new DownloadDispatcher(mQueue);
            mDispatchers[i] = dispatcher;
            dispatcher.start();
        }
    }

    /**
     * Add a request to the queue.
     *
     * @param what the {@code what} be returned in the result callback.
     * @param request {@link DownloadRequest}
     * @param listener {@link DownloadListener}
     */
    public void add(int what, final DownloadRequest request, DownloadListener listener) {
        AsyncCallback callback = new AsyncCallback(listener);
        Worker<? extends DownloadRequest> worker = new Worker<>(what, request, callback);
        final Work<? extends DownloadRequest> work = new Work<>(worker, what, callback);
        work.setSequence(mInteger.incrementAndGet());

        callback.setQueue(mQueue);
        callback.setCancelerManager(mCancelerManager);
        callback.setWork(work);
        callback.setRequest(request);

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

        for (DownloadDispatcher dispatcher : mDispatchers) {
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

    private static class AsyncCallback
      implements DownloadListener {

        private final DownloadListener mCallback;
        private BlockingQueue<Work<? extends DownloadRequest>> mQueue;
        private Work<? extends DownloadRequest> mWork;
        private CancelerManager mCancelerManager;
        private DownloadRequest mRequest;

        public AsyncCallback(DownloadListener callback) {
            this.mCallback = callback;
        }

        public void setQueue(BlockingQueue<Work<? extends DownloadRequest>> queue) {
            mQueue = queue;
        }

        public void setWork(Work<? extends DownloadRequest> work) {
            mWork = work;
        }

        public void setCancelerManager(CancelerManager cancelerManager) {
            mCancelerManager = cancelerManager;
        }

        public void setRequest(DownloadRequest request) {
            mRequest = request;
        }

        @Override
        public void onDownloadError(final int what, final Exception exception) {
            removeRequest();
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onDownloadError(what, exception);
                }
            });
        }

        @Override
        public void onStart(final int what, final boolean isResume, final long rangeSize,
                            final Headers headers, final long allCount) {
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onStart(what, isResume, rangeSize, headers, allCount);
                }
            });
        }

        @Override
        public void onProgress(final int what, final int progress, final long fileCount, final long speed) {
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onProgress(what, progress, fileCount, speed);
                }
            });
        }

        @Override
        public void onFinish(final int what, final String filePath) {
            removeRequest();
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onFinish(what, filePath);
                }
            });
        }

        @Override
        public void onCancel(final int what) {
            removeRequest();
            HandlerDelivery.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onCancel(what);
                }
            });
        }

        private void removeRequest() {
            mCancelerManager.removeCancel(mRequest);
            if (mQueue.contains(mWork)) {
                mQueue.remove(mWork);
            }
        }
    }

}