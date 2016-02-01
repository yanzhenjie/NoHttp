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

import com.alibaba.fastjson.JSONObject;
import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.FastJsonRequest;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * </br>
 * Created in Feb 1, 2016 9:14:37 AM
 * 
 * @author YOLANDA;
 */
public class FastJsonActvity extends BaseActivity implements View.OnClickListener, HttpListener<JSONObject> {

	private TextView mTvResult;

	@Override
	protected void onActivityCreate(Bundle savedInstanceState) {
		setTitle(Application.getInstance().nohttpTitleList[1]);
		setContentView(R.layout.activity_proxy);

		findView(R.id.btn_start).setOnClickListener(this);
		mTvResult = findView(R.id.tv_result);
	}

	@Override
	public void onClick(View v) {
		Request<JSONObject> request = new FastJsonRequest(Constants.URL_NOHTTP_JSONOBJECT);
		CallServer.getRequestInstance().add(this, 0, request, this, false, true);
	}

	@Override
	public void onSucceed(int what, Response<JSONObject> response) {
		JSONObject jsonObject = response.get();
		if (0 == jsonObject.getIntValue("error")) {
			StringBuilder builder = new StringBuilder(jsonObject.toString());
			builder.append("\n\n解析数据: \n\n请求方法: ").append(jsonObject.getString("method")).append("\n");
			builder.append("请求地址: ").append(jsonObject.getString("url")).append("\n");
			builder.append("响应数据: ").append(jsonObject.getString("data")).append("\n");
			builder.append("错误码: ").append(jsonObject.getIntValue("error"));
			mTvResult.setText(builder.toString());
		}
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
		mTvResult.setText("请求失败\n" + message);
	}

}
