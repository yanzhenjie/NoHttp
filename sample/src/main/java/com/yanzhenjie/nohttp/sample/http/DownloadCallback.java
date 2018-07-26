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
package com.yanzhenjie.nohttp.sample.http;

import android.content.Context;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.sample.R;

/**
 * Created by YanZhenjie on 2018/7/25.
 */
public abstract class DownloadCallback
  implements DownloadListener {

    private Context mContext;

    public DownloadCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onDownloadError(int what, Exception e) {
        String message;
        if (e instanceof NetworkError) {
            message = mContext.getString(R.string.http_exception_network);
        } else if (e instanceof URLError) {
            message = mContext.getString(R.string.http_exception_url);
        } else if (e instanceof UnKnownHostError) {
            message = mContext.getString(R.string.http_exception_host);
        } else if (e instanceof TimeoutError) {
            message = mContext.getString(R.string.http_exception_connect_timeout);
        } else {
            message = mContext.getString(R.string.http_exception_unknow_error);
        }
        Logger.e(e);
        onException(message);
    }

    public abstract void onException(String message);
}