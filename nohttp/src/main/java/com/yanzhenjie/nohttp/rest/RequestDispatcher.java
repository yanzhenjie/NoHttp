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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p> Request queue polling thread. </p>
 *
 * Created in Oct 19, 2015 8:35:35 AM.
 *
 * @author Yan Zhenjie.
 */
public class RequestDispatcher
  extends Thread {

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Request #" + mCount.getAndIncrement());
        }
    };

    private final Executor mExecutor = Executors.newCachedThreadPool(THREAD_FACTORY);
    private final BlockingQueue<Work<? extends Request<?>, ?>> mQueue;
    private boolean mQuit = false;

    public RequestDispatcher(BlockingQueue<Work<? extends Request<?>, ?>> queue) {
        this.mQueue = queue;
    }

    public void quit() {
        this.mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        while (!mQuit) {
            final Work<? extends Request<?>, ?> work;
            try {
                work = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    Logger.w("Queue exit, stop blocking.");
                    break;
                }
                Logger.e(e);
                continue;
            }

            synchronized (this) {
                work.setLock(this);
                mExecutor.execute(work);
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
