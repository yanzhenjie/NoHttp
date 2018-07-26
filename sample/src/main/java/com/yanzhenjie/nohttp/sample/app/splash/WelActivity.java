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
package com.yanzhenjie.nohttp.sample.app.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.sample.App;
import com.yanzhenjie.nohttp.sample.app.BaseActivity;
import com.yanzhenjie.nohttp.sample.app.main.MainPresenter;
import com.yanzhenjie.nohttp.sample.config.UrlConfig;
import com.yanzhenjie.nohttp.sample.http.HttpCallback;
import com.yanzhenjie.nohttp.sample.http.Result;
import com.yanzhenjie.nohttp.sample.http.StringRequest;
import com.yanzhenjie.nohttp.sample.util.Delivery;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

/**
 * Created by YanZhenjie on 2018/3/1.
 */
public class WelActivity
  extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Delivery.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermission();
            }
        }, 1000);
    }

    private void requestPermission() {
        AndPermission.with(this)
          .runtime()
          .permission(Permission.Group.STORAGE)
          .onDenied(new Action<List<String>>() {
              @Override
              public void onAction(List<String> list) {
                  finish();
              }
          })
          .onGranted(new Action<List<String>>() {
              @Override
              public void onAction(List<String> list) {
                  App.get().initialize();
                  tryLogin();
              }
          })
          .start();
    }

    /**
     * Try login.
     */
    private void tryLogin() {
        StringRequest request = new StringRequest(UrlConfig.LOGIN, RequestMethod.POST);
        request.add("name", 123).add("password", 456);
        request(request, false, new HttpCallback<String>() {
            @Override
            public void onResponse(Result<String> response) {
                toLauncher();
            }
        });
    }

    private void toLauncher() {
        startActivity(new Intent(this, MainPresenter.class));
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}