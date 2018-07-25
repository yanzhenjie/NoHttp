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

import java.util.concurrent.Callable;

/**
 * Created by YanZhenjie on 2018/2/13.
 */
public class Worker<T extends DownloadRequest>
  implements Callable<Void> {

    private int mWhat;
    private final T mRequest;
    private DownloadListener mListener;

    public Worker(int what, T request, DownloadListener listener) {
        this.mWhat = what;
        this.mRequest = request;
        this.mListener = listener;
    }

    @Override
    public Void call() throws Exception {
        SyncDownloadExecutor.INSTANCE.execute(mWhat, mRequest, mListener);
        return null;
    }

    public T getRequest() {
        return mRequest;
    }
}