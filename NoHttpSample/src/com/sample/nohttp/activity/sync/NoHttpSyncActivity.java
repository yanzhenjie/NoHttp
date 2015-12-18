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
package com.sample.nohttp.activity.sync;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 同步请求代码</br>
 * Created in Oct 23, 2015 1:13:06 PM
 * 
 * @author YOLANDA
 */
public class NoHttpSyncActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("NoHttp演示同步请求");
		
		TextView textView = new TextView(this);
		textView.setText("请看本Activity的代码");
		setContentView(textView);

		new Thread() {
			public void run() {
				// 这里直接发起一个同步请求，建议在子线程这么使用
				Request<String> request = NoHttp.createStringRequest("http://www.baidu.com", RequestMethod.POST);
				Response<String> response = NoHttp.startRequestSync(request);
				if (response.isSucceed()) {
					Logger.i("响应消息： " + response.get());
				} else {
					Logger.i("错误信息： " + response.getErrorMessage());
				}
			}
		}.start();
	}
}