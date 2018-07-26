/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.nohttp.sample.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.sample.http.AbstractRequest;
import com.yanzhenjie.nohttp.sample.http.CallServer;
import com.yanzhenjie.nohttp.sample.http.HttpCallback;
import com.yanzhenjie.nohttp.sample.mvp.Bye;
import com.yanzhenjie.nohttp.sample.util.DisplayUtils;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public abstract class BaseActivity
  extends AppCompatActivity
  implements Bye {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.initScreen(this);
        Logger.e("onCreate() " + getClass().getName());
    }

    /**
     * 异步请求，显示dialog。
     */
    public <T> void request(@NonNull AbstractRequest<T> request, HttpCallback<T> httpCallback) {
        request.setCancelSign(this);
        CallServer.getInstance().request(this, request, httpCallback, true);
    }

    /**
     * 异步请求，是否显示dialog。
     */
    public <T> void request(@NonNull AbstractRequest<T> request, boolean dialog,
                            HttpCallback<T> httpCallback) {
        request.setCancelSign(this);
        CallServer.getInstance().request(this, request, httpCallback, dialog);
    }

    @Override
    public void bye() {
        finish();
    }

    @Override
    protected void onDestroy() {
        Logger.e("onDestroy() " + getClass().getName());
        CallServer.getInstance().cancelBySign(this);
        super.onDestroy();
    }
}