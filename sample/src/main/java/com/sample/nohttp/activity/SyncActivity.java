/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.nohttp.activity;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

/**
 * <p>同步请求.</p>
 * Created in Oct 23, 2015 1:13:06 PM.
 *
 * @author YOLANDA;
 */
public class SyncActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[10]);
        setContentView(R.layout.activity_sync);

        findView(R.id.btn_start).setOnClickListener(this);
        mTvResult = findView(R.id.tv_result);
    }

    /**
     * 解析响应.
     */
    private void response(Response<String> response) {
        if (response.isSucceed()) {
            mTvResult.setText("请求成功: " + response.get());
        } else {
            mTvResult.setText("请求失败: " + response.getException());
        }
    }

    /**
     * handler接受子线程结果.
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {// 如果是同步请求发回来的结果
                @SuppressWarnings("unchecked")
                Response<String> response = (Response<String>) msg.obj;
                response(response);
            }
        }
    };

    @Override
    public void onClick(View v) {
        new Thread() {
            public void run() {
                // 在子线程中可以使用同步请求
                Request<String> request = NoHttp.createStringRequest("http://www.baidu.com", RequestMethod.POST);
                Response<String> response = NoHttp.startRequestSync(request);
                handler.obtainMessage(0, response).sendToTarget();
            }
        }.start();
    }

}