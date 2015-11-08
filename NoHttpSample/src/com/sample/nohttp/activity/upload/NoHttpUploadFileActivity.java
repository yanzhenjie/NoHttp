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
package com.sample.nohttp.activity.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpCallback;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

/**
 * 上传文件demo </br>
 * Created in Oct 23, 2015 8:40:52 AM
 * 
 * @author YOLANDA
 */
public class NoHttpUploadFileActivity extends Activity implements View.OnClickListener, HttpCallback<String> {

	/**
	 * 显示状态
	 */
	private TextView mTvStatus;
	/**
	 * 文件路径
	 */
	private String filePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "head.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("NoHttp演示文件上传");

		setContentView(R.layout.activity_nohttp_uploadfile);
		mTvStatus = (TextView) findViewById(R.id.tv_status);
		findViewById(R.id.btn_upload_file).setOnClickListener(this);

		// 这里写把一张图片写到SD卡
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream inputStream = getResources().openRawResource(R.drawable.head);
					File file = new File(filePath);
					if (!file.exists()) {
						file.createNewFile();
					}
					OutputStream outputStream = new FileOutputStream(file);
					int len = -1;
					byte[] buffer = new byte[1024];
					while ((len = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, len);
					}
					outputStream.flush();
					outputStream.close();
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_upload_file) {
			uploadFileNoHttp();
		}
	}

	private String url = "http://www.baidu.com";

	/**
	 * 用NoHtt默认实现上传文件
	 */
	private void uploadFileNoHttp() {
		Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);// 或者用PUT，看服务器支持什么方法上传
		// 添加一个普通参数
		request.add("user", "yolanda");

		// 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了一个上传File的，传入File和fileName就可以了
		// 因为这个接口是在子线程中回调，所以接口的方法可以做IO操作
		request.add("head", new FileBinary(new File(filePath), "head.png"));
		CallServer.getRequestInstance().add(this, 0, request, this);
	}

	/**
	 * @param what
	 * @param response
	 */
	@Override
	public void onSucceed(int what, Response<String> response) {
		mTvStatus.setText(response.get());
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message) {
		mTvStatus.setText("发生错误了，这里更换成你的http接口就好了：" + message);
	}
}
