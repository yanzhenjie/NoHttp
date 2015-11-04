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
package com.sample.nohttp.activity.download;

import com.sample.nohttp.R;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.download.DownloadRequestor;
import com.yolanda.nohttp.download.StatusCode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 下载件demo</br>
 * Created in Oct 10, 2015 12:58:25 PM
 * 
 * @author YOLANDA
 */
public class NoHttpDownloadActivity extends Activity implements View.OnClickListener, DownloadListener {
	/**
	 * 下载队列
	 */
	private DownloadQueue mDownloadQueue;
	/**
	 * 下载按钮、暂停、开始等
	 */
	private TextView mBtnStart;
	/**
	 * 下载进度条
	 */
	private ProgressBar mProgressBar;
	/***
	 * 下载地址
	 */
	private String url = "http://m.apk.67mo.com/apk/999129_21769077_1443483983292.apk";
	/**
	 * 下载请求
	 */
	private DownloadRequest downloadRequest;
	/**
	 * 是否正在下载
	 */
	private boolean isStarted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("NoHttp演示文件下载");

		setContentView(R.layout.activity_nohttp_download);

		mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
		mBtnStart = (TextView) findViewById(R.id.btn_start_download);
		mBtnStart.setOnClickListener(this);

		mDownloadQueue = NoHttp.newDownloadQueue(getApplicationContext());
	}

	@Override
	public void onClick(View v) {
		if (isStarted) {
			// 取消下载
			downloadRequest.cancel();
		} else {
			String fileFloder = Environment.getExternalStorageDirectory().getAbsolutePath();
			String filename = "123.apk";
			// url 下载地址
			// fileFloader 保存的文件夹
			// fileName 文件名
			// isRange 是否断点续传下载
			downloadRequest = new DownloadRequestor(url, fileFloder, filename, true);
			// what 区分下载
			// downloadRequest 下载请求对象
			// downloadListener 下载监听
			mDownloadQueue.add(0, downloadRequest, this);
		}
	}

	@Override
	public void onStart(int what, Headers headers, int allCount) {
		isStarted = true;
		mBtnStart.setText("暂停下载");
	}

	@Override
	public void onDownloadError(int what, StatusCode statusCode, CharSequence errorMessage) {
		isStarted = false;
		mBtnStart.setText("出错了，重新下载");
		Logger.e("下载出错");
	}

	@Override
	public void onProgress(int what, int progress) {
		mBtnStart.setText("进度：" + progress);
		mProgressBar.setProgress(progress);
		Logger.e("进度: " + progress);
	}

	@Override
	public void onFinish(int what, String filePath) {
		isStarted = false;
		mBtnStart.setText("完成：" + filePath);
		Logger.i("下载完成: " + filePath);
	}

	@Override
	public void onCancel(int what) {
		isStarted = false;
		mBtnStart.setText("暂停了，继续下载");
		Logger.i("暂停下载");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (downloadRequest != null)
			downloadRequest.cancel();
	}
}