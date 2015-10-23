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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 开始界面</br>
 * Created in Oct 21, 2015 2:19:16 PM
 * 
 * @author YOLANDA
 */
public class StartActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		NoHttp.setDebug(true);

		findViewById(R.id.btn_method_activity).setOnClickListener(this);
		findViewById(R.id.btn_download_activity).setOnClickListener(this);
		findViewById(R.id.btn_upload_activity).setOnClickListener(this);
		findViewById(R.id.btn_iamge_activity).setOnClickListener(this);
		findViewById(R.id.btn_define_activity).setOnClickListener(this);
		findViewById(R.id.btn_cancel_activity).setOnClickListener(this);
		findViewById(R.id.btn_sync_activity).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_method_activity) {// 演示请求方法
			Intent intent = new Intent(this, NoHttpMethodActivity.class);
			startActivity(intent);
		}

		if (v.getId() == R.id.btn_download_activity) {// 演示下载
			Intent intent = new Intent(this, NoHttpDownloadActivity.class);
			startActivity(intent);
		}

		if (v.getId() == R.id.btn_upload_activity) {// 演示上传
			Intent intent = new Intent(this, NoHttpUploadFileActivity.class);
			startActivity(intent);
		}

		if (v.getId() == R.id.btn_iamge_activity) {// 演示请求图片
			Intent intent = new Intent(this, NoHttpImageActivity.class);
			startActivity(intent);
		}

		if (v.getId() == R.id.btn_define_activity) {// 演示自定义请求对象
			Intent intent = new Intent(this, NoHttpDefineRequestActivity.class);
			startActivity(intent);
		}

		if (v.getId() == R.id.btn_cancel_activity) {// 演示取消请求，取消请求队列，取消所有请求
			Intent intent = new Intent(this, NoHttpCancelActivity.class);
			startActivity(intent);
		}

		if (v.getId() == R.id.btn_sync_activity) {// 演示同步请求
			Intent intent = new Intent(this, NoHttpSyncActivity.class);
			startActivity(intent);
		}
	}

}
