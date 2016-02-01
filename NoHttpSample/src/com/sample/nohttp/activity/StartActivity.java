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

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.adapter.StringAbsListAdapter;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.util.OnItemClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

/**
 * 开始界面
 * </br>
 * Created in Oct 21, 2015 2:19:16 PM
 * 
 * @author YOLANDA
 */
public class StartActivity extends BaseActivity {

	@Override
	protected void onActivityCreate(Bundle savedInstanceState) {
		setTitle(R.string.activity_start);
		setContentView(R.layout.activity_start);
		hideBackBar();// 隐藏返回键

		StringAbsListAdapter listAdapter = new StringAbsListAdapter(this, R.layout.item_abs_list_text, Application.getInstance().nohttpTitleList, mItemClickListener);
		((AbsListView) findView(R.id.lv)).setAdapter(listAdapter);
	}

	/**
	 * list item单击
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View v, int position) {
			geOtherPager(position);
		}
	};

	private void geOtherPager(int position) {
		Intent intent = null;
		switch (position) {
		case 0:// 最原始使用方法
			intent = new Intent(this, OriginalActivity.class);
			break;
		case 1:// 自定义请求FastJson
			intent = new Intent(this, FastJsonActvity.class);
		case 2:// 各种请求方法演示(GET, POST, HEAD, PUT等等)
			intent = new Intent(this, MethodActivity.class);
			break;
		case 3:// 请求图片
			intent = new Intent(this, ImageActivity.class);
			break;
		case 4:// JsonObject, JsonArray
			intent = new Intent(this, JsonActivity.class);
			break;
		case 5:// 响应码304缓存演示
			intent = new Intent(this, CacheActivity.class);
			break;
		case 6:// 响应码302/303重定向演示
			intent = new Intent(this, RedirectActivity.class);
			break;
		case 7:// 文件上传
			intent = new Intent(this, UploadFileActivity.class);
			break;
		case 8: // 文件下载
			intent = new Intent(this, DownloadActivity.class);
			break;
		case 9:// 如何取消请求
			intent = new Intent(this, CancelActivity.class);
			break;
		case 10:// 同步请求
			intent = new Intent(this, SyncActivity.class);
			break;
		case 11:// 通过代理服务器请求
			intent = new Intent(this, ProXYActivity.class);
			break;
		case 12:// https请求
			intent = new Intent(this, HttpsActivity.class);
			break;
		default:
			break;
		}
		if (intent != null)
			startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 程序退出时，停止所有请求
		CallServer.getRequestInstance().stopAll();
	}

}
