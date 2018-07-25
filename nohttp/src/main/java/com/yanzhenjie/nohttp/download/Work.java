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
package com.yanzhenjie.nohttp.download;

import com.yanzhenjie.nohttp.Priority;
import com.yanzhenjie.nohttp.able.Cancelable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by YanZhenjie on 2018/2/13.
 */
final class Work<T extends DownloadRequest>
  extends FutureTask<Void>
  implements Cancelable, Comparable<Work<? extends DownloadRequest>> {

    private Worker<T> mWorker;
    private final int mWhat;
    private final DownloadListener mCallback;

    private int mSequence;

    private Object mLock;

    public Work(Worker<T> worker, int what, final DownloadListener callback) {
        super(worker);
        this.mWorker = worker;
        this.mWhat = what;
        this.mCallback = callback;
    }

    public void setLock(Object lock) {
        if (mLock != null) throw new IllegalStateException("The lock has been set.");
        this.mLock = lock;
    }

    public void setSequence(int sequence) {
        mSequence = sequence;
    }

    @Override
    public void run() {
        if (mLock == null) throw new IllegalStateException("The lock is null.");
        synchronized (mLock) {
            super.run();
            mLock.notify();
        }
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (CancellationException e) {
            mCallback.onCancel(mWhat);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (isCancelled()) {
                mCallback.onCancel(mWhat);
            } else if (cause != null && cause instanceof Exception) {
                mCallback.onDownloadError(mWhat, (Exception)cause);
            } else {
                mCallback.onDownloadError(mWhat, new Exception(cause));
            }
        } catch (Exception e) {
            if (isCancelled()) {
                mCallback.onCancel(mWhat);
            } else {
                mCallback.onDownloadError(mWhat, e);
            }
        }
    }

    @Override
    public void cancel() {
        cancel(true);
    }

    @Override
    public boolean isCanceled() {
        return isCancelled();
    }

    @Override
    public int compareTo(Work<? extends DownloadRequest> o) {
        DownloadRequest mr = mWorker.getRequest();
        DownloadRequest or = o.mWorker.getRequest();
        final Priority me = mr.getPriority();
        final Priority it = or.getPriority();
        return me == it ? mSequence - o.mSequence : it.ordinal() - me.ordinal();
    }
}