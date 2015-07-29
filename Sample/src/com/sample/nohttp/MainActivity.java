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

import com.yolanda.nohttp.BaseResponse;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.ResponseError;

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
	 * Mark request "baidu"
	 */
	private static final int REQUST_BAIDU = 0x001;
	/**
	 * Mark request dynamic file name
	 */
	private static final int REQUEST_DYNAMIC_FILENAME = 0x002;
	/**
	 * Mark request static file name
	 */
	private static final int REQUEST_STATIC_FILENAME = 0x003;
	/**
	 * NoHttp angel
	 */
	private NoHttp noHttp = NoHttp.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btnAsync).setOnClickListener(this);
		findViewById(R.id.btnSync).setOnClickListener(this);
		findViewById(R.id.btnUrlFilename).setOnClickListener(this);
		findViewById(R.id.btnUrlFilename2).setOnClickListener(this);
		NoHttp.setDebug(true);
		NoHttp.setTag(Logger.TAG);
	}

	@Override
	public void onNoHttpResponse(int what, Response response) {
		switch (what) {
		case REQUST_BAIDU:
			Logger.i("Baidu Request Result：\n" + response.string());
			Logger.i("Baidu toString() Result：\n" + response.toString());
			break;
		case REQUEST_DYNAMIC_FILENAME:
			Logger.i("Dynamic Filname Request Result：\n" + response.string());
			break;
		case REQUEST_STATIC_FILENAME:
			Logger.i("Static Filname Request Result：\n" + response.string());
			break;
		default:
			break;
		}
	}

	@Override
	public void onNoHttpError(int what, ResponseError responseError) {
		Logger.e("Request Filed：" + responseError.getErrorInfo());
		Toast.makeText(this, responseError.getErrorInfo(), Toast.LENGTH_LONG).show();
	}

	/**
	 * For other methods to build an HTTP request
	 */
	private Request buildRequest() {
		// 1. The requested address
		// 2. Choose the way to the request
		Request request = new Request("http://www.baidu.com/s", RequestMethod.GET);

		// 3. Add request header,If you don't need to add can remove the following code
		request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		request.addHeader("Author", "name|pass");
		// The two effect is the same, And the default KeepAlive
		request.addHeader("Connection", "Keep-Alive");
		request.setKeppAlive(true);

		// 4. Add the request parameters, If you don't need to add can remove the following code
		request.add("wd", "Android Develop");
		// 5. Upload a file,If you want to upload files can be added directly
		// Bitmap bitmap = null;
		// request.add("picture", bitmap, "head.jpg");
		return request;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnAsync)
			asyncRequest();
		if (v.getId() == R.id.btnSync)
			syncRequest();
		if (v.getId() == R.id.btnUrlFilename)            // dynamic
			dynamicFilename();
		if (v.getId() == R.id.btnUrlFilename2)            // static
			staticFilename();
	}

	/**
	 * 异步请求
	 * ——————————
	 * An asynchronous request
	 */
	private void asyncRequest() {
		Request request = buildRequest();
		noHttp.requestAsync(request, REQUST_BAIDU, this);
	}

	/**
	 * 同步请求
	 * ——————————
	 * An synchronous request
	 */
	private void syncRequest() {
		new Thread() {
			@Override
			public void run() {
				Request request = buildRequest();
				BaseResponse baseResponse = noHttp.requestSync(request);
				if (baseResponse.isSuccessful()) {
					Response response = (Response) baseResponse;
					Logger.i("Request Result:" + response);
				} else {
					ResponseError responseError = (ResponseError) baseResponse;
					Logger.i("Request filed:" + responseError.getErrorInfo());
				}
			};
		}.start();
	}

	/**
	 * From a dynamic URL dynamic
	 */
	private void dynamicFilename() {
		String url = "http://cdn3.ops.baidu.com/new-repackonline/appsearch/AndroidPhone/1.0.31.191/1/1012271a/"
				+ "20150723235831/appsearch_AndroidPhone_1-0-31-191_1012271a.apk?response-content-disposition="
				+ "attachment;filename=appsearch_AndroidPhone_1012271a.apk&amp;response-content-type="
				+ "application/vnd.android.package-archive&amp;request_id=1438133342_2816171802&amp;type=static";
		Request request = new Request(url, RequestMethod.GET);
		noHttp.requestFilenameAsync(request, REQUEST_DYNAMIC_FILENAME, this);
	}

	/**
	 * From a static URL
	 */
	private void staticFilename() {
		String url = "http://ota.53iq.com/static/file/kitchen_14379835129655595.apk";
		Request request = new Request(url, RequestMethod.GET);
		noHttp.requestFilenameAsync(request, REQUEST_STATIC_FILENAME, this);
	}
}
