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

import android.os.Process;

import com.yanzhenjie.nohttp.Logger;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * Request queue polling thread.
 * </p>
 * Created in Oct 19, 2015 8:35:35 AM.
 *
 * @author Yan Zhenjie.
 */
public class RequestDispatcher extends Thread {

    private final BlockingQueue<Request<?>> mRequestQueue;
    private final Map<Request<?>, Messenger<?>> mMessengerMap;

    /**
     * Whether the current handle queue polling thread is out of.
     */
    private volatile boolean mQuit = false;

    public RequestDispatcher(BlockingQueue<Request<?>> requestQueue, Map<Request<?>, Messenger<?>> messengerMap) {
        this.mRequestQueue = requestQueue;
        this.mMessengerMap = messengerMap;
    }

    /**
     * Exit polling thread.
     */
    public void quit() {
        this.mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (!mQuit) {
            final Request<?> request;
            try {
                request = mRequestQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    Logger.w("Queue exit, stop blocking.");
                    break;
                }
                Logger.e(e);
                continue;
            }

            if (request.isCanceled()) {
                mRequestQueue.remove(request);
                mMessengerMap.remove(request);
                Logger.d(request.url() + " is canceled.");
                continue;
            }

            // start.
            request.start();
            mMessengerMap.get(request).start();

            // handle.
            Response response = SyncRequestExecutor.INSTANCE.execute(request);

            // response.
            if (request.isCanceled()) {
                Logger.d(request.url() + " finish, but it's canceled.");
            } else {
                // noinspection unchecked
                mMessengerMap.get(request).response(response);
            }

            // finish.
            request.finish();
            mMessengerMap.get(request).finish();

            // remove it from queue.
            mRequestQueue.remove(request);
            mMessengerMap.remove(request);
        }
    }
}
