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

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;

/**
 * Created in Jan 31, 2016 4:30:31 PM
 *
 * @author YOLANDA
 */
public class RedirectActivity extends BaseActivity implements View.OnClickListener, HttpListener<String> {

    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[6]);
        setContentView(R.layout.activity_redirect);

        mTvResult = findView(R.id.tv_result);
        findView(R.id.btn_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_REDIRECT);
        CallServer.getRequestInstance().add(this, 0, request, this, false, true);
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        StringBuilder builder = new StringBuilder("请求成功\n");
        builder.append("响应码: ").append(response.getHeaders().getResponseCode()).append("\n");
        builder.append("被重定向到: ").append(response.getHeaders().getLocation()).append("\n").append(response.get());
        mTvResult.setText(builder.toString());
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        mTvResult.setText("请求失败");
    }

}
