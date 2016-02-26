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

import org.json.JSONArray;
import org.json.JSONObject;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * </br>
 * Created in Jan 31, 2016 10:16:26 PM
 *
 * @author YOLANDA
 */
public class JsonActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[4]);
        setContentView(R.layout.activity_json);

        findView(R.id.btn_object_reqeust).setOnClickListener(this);
        findView(R.id.btn_array_reqeust).setOnClickListener(this);
        mTvResult = findView(R.id.tv_result);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_object_reqeust) {
            Request<JSONObject> request = NoHttp.createJsonObjectRequest(Constants.URL_NOHTTP_JSONOBJECT);
            CallServer.getRequestInstance().add(this, 0, request, objectListener, true, true);
        } else if (v.getId() == R.id.btn_array_reqeust) {
            Request<JSONArray> request = NoHttp.createJsonArrayRequest(Constants.URL_NOHTTP_JSONARRAY);
            CallServer.getRequestInstance().add(this, 1, request, arrayListener, true, true);
        }
    }

    private HttpListener<JSONObject> objectListener = new HttpListener<JSONObject>() {
        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            JSONObject jsonObject = response.get();
            if (0 == jsonObject.optInt("error", -1)) {
                StringBuilder builder = new StringBuilder(jsonObject.toString());
                builder.append("\n\n解析数据: \n\n请求方法: ").append(jsonObject.optString("method")).append("\n");
                builder.append("请求地址: ").append(jsonObject.optString("url")).append("\n");
                builder.append("响应数据: ").append(jsonObject.optString("data")).append("\n");
                builder.append("错误码: ").append(jsonObject.optInt("error"));
                mTvResult.setText(builder.toString());
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mTvResult.setText("请求失败" + exception.getMessage());
        }
    };

    private HttpListener<JSONArray> arrayListener = new HttpListener<JSONArray>() {
        @Override
        public void onSucceed(int what, Response<JSONArray> response) {
            JSONArray jsonArray = response.get();
            StringBuilder builder = new StringBuilder(jsonArray.toString());

            builder.append("\n\n解析数据: \n\n");
            for (int i = 0; i < jsonArray.length(); i++) {
                String string = jsonArray.optString(i);
                builder.append(string).append("\n");
            }

            mTvResult.setText(builder.toString());
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        }
    };

}
