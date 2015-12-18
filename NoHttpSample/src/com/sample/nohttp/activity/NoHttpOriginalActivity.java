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

import com.sample.nohttp.R;
import com.sample.nohttp.dialog.WaitDialog;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.security.Certificate;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created in Nov 4, 2015 1:38:02 PM
 * 
 * @author YOLANDA
 */
public class NoHttpOriginalActivity extends Activity {

	/**
	 * 用来标志请求的what，类似handler的what一样，这里用来区分请求
	 */
	private static final int WHAT = 0x001;

	private WaitDialog mWaitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nohttp_original);
		setTitle("NoHttp基本使用方法");

		mWaitDialog = new WaitDialog(this);

		// 创建请求队列
		// 1.第一种方法，默认并发3个请求
		RequestQueue queue = NoHttp.newRequestQueue();
		// 2.第二种方法 第二个参数指同事可并发请求数量
		// queue = NoHttp.newRequestQueue(this, 3);

		// 创建请求对象
		Request<String> request = NoHttp.createStringRequest("https://www.baidu.com/", RequestMethod.POST);

		// 添加参数
		request.add("userName", "yolanda");
		request.add("userPass", 1);
		request.add("userAge", 1.25);

		// 上传文件，需要一个Binary接口，NoHttp提供一个默认实现FileBinary
		// request.add("file", new FileBinary(file, fileName));

		// 如果需要添加头
		request.addHeader("Author", "session=ysd354fas6f13");
		request.addHeader("Dasfs", "ds65fasf1");
		request.addHeader("Htiky", "4fa9f16f1asfwae65s");

		// 如果是https请求
		// 1.直接允许https请求
		request.setAllowHttps(true);
		// 2.添加https证书
		request.setCertificate(new Certificate(this, R.raw.keystore, "yolanda"));

		// 为开发者保持一个tag，在请求完成后原样返回
		request.setTag(new Object());

		// what: 当多个请求同时使用同一个onResponseListener时用来区分请求，类似handler的what一样
		// request: 请求对象
		// responseListener 接受响应的监听对象
		queue.add(WHAT, request, onResponseListener);
	}

	private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
		@Override
		public void onSucceed(int what, Response<String> response) {
			// 请求成功，这里接受结果
			String result = response.get();// 拿到请求结果
			((TextView) findViewById(R.id.tv_status)).setText(result);
			response.getTag();// 拿到保持的tag
			response.getByteArray();// 拿到byte[]数据
			response.getContentLength();
			response.getContentType();
			response.getCookies();// 拿到cookie
			response.getHeaders();// 拿到响应头
			response.getResponseCode();// 拿到响应码
			response.isSucceed();// 是否成功，如果在onResponse中判断，肯定豆都是true
		}

		@Override
		public void onStart(int what) {
			// 请求开始，这里可以显示一个dialog
			mWaitDialog.show();
		}

		@Override
		public void onFinish(int what) {
			// 请求结束，这里关闭dialog
			mWaitDialog.dismiss();
		}

		@Override
		public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
			// 请求失败
			((TextView) findViewById(R.id.tv_status)).setText("请求失败: " + message);
		}
	};

}
