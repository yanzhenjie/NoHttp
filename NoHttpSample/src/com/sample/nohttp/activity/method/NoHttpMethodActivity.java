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
package com.sample.nohttp.activity.method;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpCallback;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 演示各种请求方法Demo</br>
 * Created in Oct 23, 2015 1:13:06 PM
 * 
 * @author YOLANDA
 */
public class NoHttpMethodActivity extends Activity implements View.OnClickListener, HttpCallback<String> {
	/**
	 * 请求地址，你运行demo时，这里换成你的地址
	 */
	private String mTargetUrl = "http://192.168.1.112/HttpServer/NoHttp";
	/**
	 * 请求对象
	 */
	private Request<String> mRequest;
	/**
	 * 显示请求结果
	 */
	private TextView mTvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("NoHttp演示请求方法");

		setContentView(R.layout.activity_nohttp_method);
		findViewById(R.id.btn_patch).setOnClickListener(this);
		findViewById(R.id.btn_post).setOnClickListener(this);
		findViewById(R.id.btn_put).setOnClickListener(this);
		findViewById(R.id.btn_delete).setOnClickListener(this);
		findViewById(R.id.btn_get).setOnClickListener(this);
		findViewById(R.id.btn_head).setOnClickListener(this);
		findViewById(R.id.btn_options).setOnClickListener(this);
		findViewById(R.id.btn_trace).setOnClickListener(this);
		mTvResult = (TextView) findViewById(R.id.tv_method_result);
	}

	@Override
	public void onClick(View v) {
		RequestMethod method = RequestMethod.GET;// 赋一个默认值
		// 点击不同的按钮，改变请求方法
		switch (v.getId()) {
		case R.id.btn_patch:
			method = RequestMethod.PATCH;
			break;
		case R.id.btn_post:
			method = RequestMethod.POST;
			break;
		case R.id.btn_put:
			method = RequestMethod.PUT;
			break;
		case R.id.btn_delete:
			method = RequestMethod.DELETE;
			break;
		case R.id.btn_get:
			method = RequestMethod.GET;
			break;
		case R.id.btn_head:
			method = RequestMethod.HEAD;
			break;
		case R.id.btn_options:
			method = RequestMethod.OPTIONS;
			break;
		case R.id.btn_trace:
			method = RequestMethod.TRACE;
			break;
		default:
			break;
		}

		// 创建request时传入url和method
		mRequest = NoHttp.createStringRequest(mTargetUrl, method);
		// github是https的请求，这里直接允许，不做证书验证，具体Https的使用请看NoHttpsActivity
		mRequest.setAllowHttps(true);

		// 设置代理
		SocketAddress sa = new InetSocketAddress("192.168.1.112", 8888);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
		mRequest.setProxy(proxy);

		mRequest.add("userName", "yolanda");// String类型
		mRequest.add("userPass", "yolanda.pass");
		mRequest.add("userAge", 20);// int类型
		mRequest.add("userSex", '1');// char类型，还支持其它类型

		// 设置这个请求的tag，NoHttp的请求会为你保持这个tag，在成功或者失败时返回给你
		// mRequest.setTag(object);

		// what: 用来区分请求，当多个请求使用同一个OnResponseListener时，在回调方法中会返回这个what，相当于handler的what一样
		// request: 请求对象，包涵Cookie、Head、请求参数、URL、请求方法
		// responseListener 请求结果监听，回调时把what原样返回
		CallServer.getRequestInstance().add(this, 0, mRequest, this);// 这里的what，先用0代替，正式开发中，多个请求使用同一个Listener时，要传入不同的what，相当于handler的what一样
	}

	@Override
	public void onSucceed(int what, Response<String> response) {
		String result = response.get();
		Logger.i("成功：" + result);
		mTvResult.setText("请求成功：" + result);
	}

	/**
	 * what 用来区分请求
	 * url 请求失败的url
	 * tag 某个请求需要保持的tag
	 * message 错误信息
	 */
	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
		Logger.i("失败：" + message);
		mTvResult.setText("请求失败：" + message);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时可以取消这个请求
		if (mRequest != null)
			mRequest.cancel();
	}

}