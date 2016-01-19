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
package com.sample.nohttp.activity.https;

import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpCallback;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created in Nov 3, 2015 1:48:34 PM
 * 
 * @author YOLANDA
 */
public class NoHttpsActivity extends Activity implements View.OnClickListener, HttpCallback<String> {

	/**
	 * 显示请求结果
	 */
	private TextView mTvStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Https自定义证书 请求实例");

		setContentView(R.layout.activity_nohttp_https);
		findViewById(R.id.btn_https_reqeust).setOnClickListener(this);
		findViewById(R.id.btn_https_nocer_reqeust).setOnClickListener(this);
		mTvStatus = (TextView) findViewById(R.id.tv_status);

		// 终极大招，允许所有Https请求，如果设置了这个为true，那么允许所有https请求
		// NoHttp.setAllowAllHttps(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_https_reqeust) { // 需要https证书的请求
			needCertificate();
		} else if (v.getId() == R.id.btn_https_nocer_reqeust) { // 不需要https证书的请求
			needNoCertificate();
		}
	}

	/**
	 * 发起需要证书的请求
	 */
	private void needCertificate() {
		String url = "https://kyfw.12306.cn/otn";
		// 初始化url和method
		Request<String> httpsRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
		CallServer.getRequestInstance().add(this, 0, httpsRequest, this);
	}

	/**
	 * 发起需要证书的请求
	 */
	private void needNoCertificate() {
		String url = "https://kyfw.12306.cn/otn";
		// 初始化url和method
		Request<String> httpsRequest = NoHttp.createStringRequest(url, RequestMethod.POST);

		CallServer.getRequestInstance().add(this, 0, httpsRequest, this);
	}

	@Override
	public void onSucceed(int what, Response<String> response) {
		mTvStatus.setText("成功：\n" + response.get());
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
		mTvStatus.setText("失败：\n" + message);
	}
}
