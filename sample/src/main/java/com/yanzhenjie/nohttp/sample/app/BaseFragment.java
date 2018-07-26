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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.nohttp.sample.http.AbstractRequest;
import com.yanzhenjie.nohttp.sample.http.CallServer;
import com.yanzhenjie.nohttp.sample.http.HttpCallback;
import com.yanzhenjie.nohttp.sample.mvp.Bye;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public class BaseFragment
  extends Fragment
  implements Bye {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 异步请求，显示dialog。
     */
    public <T> void request(@NonNull AbstractRequest<T> request, HttpCallback<T> httpCallback) {
        request.setCancelSign(this);
        CallServer.getInstance().request(getContext(), request, httpCallback, true);
    }

    /**
     * 异步请求，是否显示dialog。
     */
    public <T> void request(@NonNull AbstractRequest<T> request, boolean dialog,
                            HttpCallback<T> httpCallback) {
        request.setCancelSign(this);
        CallServer.getInstance().request(getContext(), request, httpCallback, dialog);
    }

    @Override
    public void bye() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void onDestroy() {
        CallServer.getInstance().cancelBySign(this);
        super.onDestroy();
    }
}