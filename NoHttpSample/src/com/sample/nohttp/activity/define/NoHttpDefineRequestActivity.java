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
package com.sample.nohttp.activity.define;

import org.json.JSONObject;

import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpCallback;
import com.sample.nohttp.nohttp.JsonRequest;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 自定义请求对象
 * Created in Oct 23, 2015 8:12:22 PM
 * 
 * @author YOLANDA
 */
public class NoHttpDefineRequestActivity extends Activity implements HttpCallback<JSONObject> {

	/**
	 * 显示请求结果
	 */
	private TextView mTvStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("NoHttp演示自定义请求对象");

		setContentView(R.layout.activity_nohttp_define);
		mTvStatus = (TextView) findViewById(R.id.tv_status);
		findViewById(R.id.btn_reqeust_define).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Request<JSONObject> request = new JsonRequest("http://www.baidu.com");
				CallServer.getRequestInstance().add(NoHttpDefineRequestActivity.this, 0, request, NoHttpDefineRequestActivity.this);
			}
		});
	}

	@Override
	public void onSucceed(int what, Response<JSONObject> response) {
		mTvStatus.setText("成功：" + response.get().toString());
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
		mTvStatus.setText("失败：" + message);
	}
}
