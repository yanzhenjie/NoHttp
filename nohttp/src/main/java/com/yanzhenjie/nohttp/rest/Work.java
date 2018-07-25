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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.Priority;
import com.yanzhenjie.nohttp.able.Cancelable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by YanZhenjie on 2018/2/13.
 */
final class Work<T extends Request<S>, S>
  extends FutureTask<Response<S>>
  implements Cancelable, Comparable<Work<? extends Request<?>, ?>> {

    private Worker<T, S> mWorker;
    private final int mWhat;
    private final OnResponseListener<S> mCallback;

    private int mSequence;

    private boolean isStart;
    private Object mLock;

    public Work(Worker<T, S> worker, int what, OnResponseListener<S> callback) {
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
            mWorker.getRequest().start();

            isStart = true;
            mCallback.onStart(mWhat);
            super.run();
            mLock.notify();
        }
    }

    @Override
    protected void done() {
        try {
            Response<S> response = get();
            if (response.isSucceed()) {
                mCallback.onSucceed(mWhat, response);
            } else {
                mCallback.onFailed(mWhat, response);
            }
        } catch (CancellationException e) {
            if (!isStart) {
                isStart = true;
                mCallback.onStart(mWhat);
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (!isCancelled()) {
                if (cause != null && cause instanceof Exception) {
                    Exception ee = (Exception)cause;
                    Response<S> response = new RestResponse<>(mWorker.getRequest(), false, null, null, 0, ee);
                    mCallback.onFailed(mWhat, response);
                } else {
                    Exception ee = new Exception(cause);
                    Response<S> response = new RestResponse<>(mWorker.getRequest(), false, null, null, 0, ee);
                    mCallback.onFailed(mWhat, response);
                }
            }
        } catch (Exception e) {
            if (!isCancelled()) {
                Response<S> response = new RestResponse<>(mWorker.getRequest(), false, null, null, 0, e);
                mCallback.onFailed(mWhat, response);
            }
        }
        mWorker.getRequest().finish();
        mCallback.onFinish(mWhat);
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
    public int compareTo(Work<? extends Request<?>, ?> o) {
        Request<?> mr = mWorker.getRequest();
        Request<?> or = o.mWorker.getRequest();
        final Priority me = mr.getPriority();
        final Priority it = or.getPriority();
        return me == it ? mSequence - o.mSequence : it.ordinal() - me.ordinal();
    }
}