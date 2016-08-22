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
package com.yanzhenjie.nohttp.sample.activity.json;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.nohttp.CallServer;
import com.yanzhenjie.nohttp.sample.nohttp.FastJsonRequest;
import com.yanzhenjie.nohttp.sample.nohttp.HttpListener;
import com.yanzhenjie.nohttp.sample.util.Constants;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Locale;

/**
 * Created in Feb 1, 2016 9:14:37 AM.
 *
 * @author Yan Zhenjie.
 */
public class FastJsonActivity extends BaseActivity implements View.OnClickListener, HttpListener<JSONObject> {

    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fastjson);

        findView(R.id.btn_start).setOnClickListener(this);
        mTvResult = findView(R.id.tv_result);
    }

    @Override
    public void onClick(View v) {
        /**
         *  这里的FastJsonRequest就是自定义请求，使用方式：Request<Type> request = new DefineRequest(url, method);
         *  注意：
         *      1. 这里的Type是你的自定义请求的类型，比如我这里的FastJsonRequest，里边的类型是{@link JSONObject}.
         *      2. 这里的DefineRequst就是你定义的自定请求的类。url是请求地址，method是请求方法（GET、POST）。
         */
        Request<JSONObject> request = new FastJsonRequest(Constants.URL_NOHTTP_JSONOBJECT, RequestMethod.GET);
        CallServer.getRequestInstance().add(this, 0, request, this, false, true);
    }

    @Override
    public void onSucceed(int what, Response<JSONObject> response) {
        JSONObject jsonObject = response.get();
        if (0 == jsonObject.getIntValue("error")) {
            String result = getString(R.string.request_json_result);
            result = String.format(Locale.getDefault(), result, response.request().getRequestMethod().toString(), jsonObject.getString("url"), jsonObject.getString("data"), jsonObject.getString("error"));
            mTvResult.setText(result);
        }
    }

    @Override
    public void onFailed(int what, Response<JSONObject> response) {
        showMessageDialog(R.string.request_failed, response.getException().getMessage());
    }

}
