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
			downloadRequest = new DownloadRequestor(0, url, fileFloder, filename, true, this);
			mDownloadQueue.add(downloadRequest);
		}
	}

	@Override
	public void onStart(int what) {
		isStarted = true;
		mBtnStart.setText("暂停下载");
	}

	@Override
	public void onDownloadError(int what, StatusCode statusCode, CharSequence errorMessage) {
		isStarted = false;
		mBtnStart.setText("出错了，重新下载");
	}

	@Override
	public void onProgress(int what, int progress) {
		mProgressBar.setProgress(progress);
	}

	@Override
	public void onFinish(int what, String filePath) {
		isStarted = false;
		mBtnStart.setText("完成：" + filePath);
	}

	@Override
	public void onCancel(int what) {
		isStarted = false;
		mBtnStart.setText("暂停了，继续下载");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		downloadRequest.cancel();
	}
}