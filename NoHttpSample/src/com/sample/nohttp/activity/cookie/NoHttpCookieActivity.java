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
package com.sample.nohttp.activity.cookie;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * 演示各种请求方法Demo</br>
 * Created in Oct 23, 2015 1:13:06 PM
 * 
 * @author YOLANDA
 */
public class NoHttpCookieActivity extends Activity implements View.OnClickListener, HttpCallback<String> {
	/**
	 * 请求地址，你运行demo时，这里换成你的地址
	 */
	private String url = "http://www.baidu.com";
	/**
	 * 请求对象
	 */
	private Request<String> mRequest = null;
	/**
	 * 显示请求结果
	 */
	private TextView mTvStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("NoHttp使用Cookie演示");

		setContentView(R.layout.activity_nohttp_cookie);
		findViewById(R.id.btn_cookie_reqeust).setOnClickListener(this);
		mTvStatus = (TextView) findViewById(R.id.tv_status);

		// NoHttp会自动维持Cookie，如果开发者自己管理Cookie，设置CookieManager即可
		CookieManager cookieManager = new CookieManager();
		NoHttp.setDefaultCookieManager(cookieManager);

		// 并且这里你可以初始化一些Cookie到CookieManger，例如：
		Map<String, List<String>> heads = new HashMap<>();
		List<String> cookies = new ArrayList<>();
		cookies.add("sessionId=dsjl2345");
		cookies.add("userInfo=yolanda");
		heads.put("Set-Cookie", cookies);
		try {
			// 添加Cookie到管理器
			cookieManager.put(new URI(url), heads);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// 在某个请求也可以添加Cookie，请看下文
		// 在响应中也可以接受到服务器下发的Cookie
	}

	@Override
	public void onClick(View v) {
		// 初始化url和method
		Request<String> mRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
		// 添加请求参数
		mRequest.add("userName", "yolanda");
		mRequest.add("userSex", "男");
		mRequest.add("userAge", 20);

		// 添加Cookie
		HttpCookie cookie = new HttpCookie("sessionId", "164adsf465dsfs");
		cookie.setDomain(".baidu.com");
		mRequest.addCookie(cookie);

		// 添加Cookie第二种方法
		// mRequest.addCookie(cookieStore);

		CallServer.getInstance().add(this, 0, mRequest, this);
	}

	@Override
	public void onSucceed(int what, Response<String> response) {
		String result = "请求成功，接收到Cookie: ";
		List<HttpCookie> cookies = response.getCookies();
		if (cookies != null && cookies.size() > 0) {
			for (HttpCookie httpCookie : cookies) {
				result = result + "\n\n" + httpCookie.getName() + "=" + httpCookie.getValue();
			}
		}
		mTvStatus.setText(result + "\n\n\n响应结果：\n" + response.get());
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message) {
		mTvStatus.setText("请求失败: " + message);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时取消请求
		if (mRequest != null) {
			mRequest.cancel();
		}
	}

}