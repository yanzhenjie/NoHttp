/**
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
package com.sample.nohttp;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.ResponseError;
import com.yolanda.nohttp.base.BaseResponse;
import com.yolanda.nohttp.base.RequestMethod;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created in Jul 28, 2015 11:07:21 PM
 * 
 * @author YOLANDA
 */
public class MainActivity extends Activity implements View.OnClickListener, OnResponseListener {

	/**
	 * 标志异步请求
	 * Mark the asynchronous request
	 */
	private static final int BTN_ASYNC = 0;
	/**
	 * 标志Https请求
	 * Mark the Https requests
	 */
	private static final int BTN_HTTPS = 1;
	/**
	 * 标志从动态URL获取文件名
	 * Mark from dynamic URL for the file name
	 */
	private static final int BTN_URLFILENAME = 3;
	/**
	 * 标志懂静态URL获取文件名
	 * Mark understand static URL for the file name
	 */
	private static final int BTN_URLFILENAME2 = 4;

	/**
	 * NoHttp angel
	 */
	private NoHttp noHttp = NoHttp.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btnAsync).setOnClickListener(this);
		findViewById(R.id.btnHttps).setOnClickListener(this);
		findViewById(R.id.btnSync).setOnClickListener(this);
		findViewById(R.id.btnUrlFilename).setOnClickListener(this);
		findViewById(R.id.btnUrlFilename2).setOnClickListener(this);

		NoHttp.setDebug(true);
		NoHttp.setTag(Logger.TAG);
		// 1. 打开对Http证书的支持，证书文件(.cer, .crt)在assets文件夹下
		// 2. 打开后，如果是Https的请求，会自动加上证书
		// 3. 如果不需要证书，则不用打开，NoHttp会自动允许所有Http的请求
		// Open support for Https, Certificate file (. CRT,. Cer) in the assets folder
		// After opening, If it is Https requests, automatically add the certificate
		NoHttp.openHttpsVerify(this, "srca.cer");
	}

	@Override
	public void onNoHttpResponse(int what, Response response) {
		// "what" 用来标志是哪个请求
		// "what" which is used to mark the request
		String sign = "";
		switch (what) {
		case BTN_ASYNC:
			sign = "Async request：";
			break;
		case BTN_HTTPS:
			sign = "Https Request：";
			break;
		case BTN_URLFILENAME:
		case BTN_URLFILENAME2:
			sign = "file name：";
			break;
		default:
			break;
		}
		Toast.makeText(this, sign + response.string(), Toast.LENGTH_SHORT).show();
		Logger.e("what：" + what + "；length：" + response.contentLength());
		Logger.i("request：" + response.string());

	}

	@Override
	public void onNoHttpError(int what, ResponseError responseError) {
		Logger.e("Error：" + responseError.getErrorInfo());
		Toast.makeText(this, "Error：" + responseError.getErrorInfo(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAsync:
			asyncRequest();
			break;
		case R.id.btnSync:
			syncRequest();
			break;
		case R.id.btnHttps:
			httpsRequest();
			break;
		case R.id.btnUrlFilename:
			dynamicFilename();
			break;
		case R.id.btnUrlFilename2:
			staticFilename();
			break;
		default:
			break;
		}
	}

	/**
	 * 创建一个请求者
	 * Build an HTTP request
	 */
	private Request buildRequest() {
		// 1. 初始化请求参数，添加请求地址和请求方式
		// 1. Initializes the request parameters, add the address and request
		Request request = new Request("http://www.baidu.com/s", RequestMethod.GET);

		// 2. 添加请求头，一般不需要添加请求头，这一步可以省略
		// 2. Add request header, Generally do not need to add the request header, this step can be omitted
		request.addHeader("Accept-Encoding", "gzip,deflate,sdch");

		// 3.添加请求参数
		// 4. Add the request parameters
		request.add("wd", "钢铁是怎样练成的");

		// 如果有文件上传，也像参数一样传进去即可
		// If there is a file upload, also like parameters just walk
		// Bitmap bitmap = null;
		// request.add("picture", bitmap, "head.jpg");

		return request;
	}

	/**
	 * 异步请求
	 * ——————————
	 * An asynchronous request
	 */
	private void asyncRequest() {
		Request request = buildRequest();
		noHttp.requestAsync(request, BTN_ASYNC, this);
	}

	/**
	 * 同步请求
	 * ——————————
	 * An synchronous request
	 */
	private void syncRequest() {
		Toast.makeText(this, "请看Logcat(See Loacat)", Toast.LENGTH_SHORT).show();
		new Thread() {
			@Override
			public void run() {
				Request request = buildRequest();
				BaseResponse baseResponse = noHttp.requestSync(request);
				if (baseResponse.isSuccessful()) {
					Response response = (Response) baseResponse;
					Logger.i("Sync Request Result:" + response);
				} else {
					ResponseError responseError = (ResponseError) baseResponse;
					Logger.i("Sync Request filed:" + responseError.getErrorInfo());
				}
			};
		}.start();
	}

	/**
	 * 一个Https请求
	 * ———————————
	 * A Https Request
	 */
	private void httpsRequest() {
		Request request1 = new Request("https://kyfw.12306.cn/otn/", RequestMethod.GET);
		noHttp.requestAsync(request1, BTN_HTTPS, this);
	}

	/**
	 * 从动态URL获取文件名
	 * ————————————————————
	 * From a dynamic URL dynamic
	 */
	private void dynamicFilename() {
		String url = "http://cdn3.ops.baidu.com/new-repackonline/appsearch/AndroidPhone/1.0.31.191/1/1012271a/"
				+ "20150723235831/appsearch_AndroidPhone_1-0-31-191_1012271a.apk?response-content-disposition="
				+ "attachment;filename=appsearch_AndroidPhone_1012271a.apk&amp;response-content-type="
				+ "application/vnd.android.package-archive&amp;request_id=1438133342_2816171802&amp;type=static";
		Request request = new Request(url, RequestMethod.GET);
		noHttp.requestFilenameAsync(request, BTN_URLFILENAME, this);
	}

	/**
	 * 从静态URL获取文件名
	 * ————————————————
	 * From a static URL
	 */
	private void staticFilename() {
		String url = "http://ota.53iq.com/static/file/kitchen_14379835129655595.apk";
		Request request = new Request(url, RequestMethod.GET);
		noHttp.requestFilenameAsync(request, BTN_URLFILENAME2, this);
	}
}
