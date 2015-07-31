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

import com.yolanda.nohttp.base.RequestMethod;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadManager;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.download.StatusCode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created in Jul 31, 2015 3:28:32 PM
 * 
 * @author YOLANDA
 */
public class DownloadActivity extends Activity implements OnClickListener, DownloadListener {

	private Button mBtnDownload;
	private TextView mTxtProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		mBtnDownload = (Button) findViewById(R.id.btnDownload);
		mTxtProgress = (TextView) findViewById(R.id.txtProgress);
		mBtnDownload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String url = "http://p.gdown.baidu.com/e43c614f00648be2c1250aa4f4faf9719290bcab0dc2180b205be68e7175109e0cf420bf"
				+ "ca4756deff77b42f4609acee22b084f82484670a3eb8bab725ed2b8bcc1796ad7eb9d59019fe08a783d65667d9a1b04f7595"
				+ "5ebfb8420f96f1a4d67c";
		DownloadRequest downloadRequest = new DownloadRequest(url, RequestMethod.GET);
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
		String filename = "yolanda.apk";
		downloadRequest.setDownloadAttribute(dir, filename, true, true);
		DownloadManager.getInstance(this).download(downloadRequest, 1, this);
	}

	@Override
	public void onDownloadError(int what, StatusCode statusCode) {
		Logger.i("下载出错(download error)");
		mTxtProgress.setText("下载出错(download error)");
	}

	@Override
	public void onStart(int what) {
		Logger.i("开始下载(start download)");
		mTxtProgress.setText("开始下载(start download)");
	}

	@Override
	public void onProgress(int what, int progress) {
		Logger.i("已下载(Have downloaded)：" + progress + "%");
		mTxtProgress.setText("已下载(Have downloaded)：" + progress + "%");
	}

	@Override
	public void onFinish(int what, String filePath) {
		Logger.i("下载完成");
		mTxtProgress.setText("下载完成");
	}

}
