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
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.sample.nohttp.util.OnItemClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 请求图片
 * </br>
 * Created in Oct 23, 2015 7:46:17 PM
 * 
 * @author YOLANDA
 */
public class ImageActivity extends BaseActivity implements HttpListener<Bitmap> {

	/**
	 * 显示图片
	 */
	private ImageView mImageView;
	/**
	 * 显示状态
	 */
	private TextView mTvResult;

	@Override
	protected void onActivityCreate(Bundle savedInstanceState) {
		setTitle(Application.getInstance().nohttpTitleList[3]);
		setContentView(R.layout.activity_image);

		mImageView = findView(R.id.iv_result);
		mTvResult = findView(R.id.tv_status);
		String[] contentStrings = getResources().getStringArray(R.array.activity_method_item);
		StringAbsListAdapter listAdapter = new StringAbsListAdapter(this, R.layout.item_abs_grid_text, contentStrings, mItemClickListener);
		((AbsListView) findView(R.id.gv)).setAdapter(listAdapter);
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(View v, int position) {
			request(position);
		}
	};

	private void request(int position) {
		Request<Bitmap> request = null;
		switch (position) {
		case 0:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE);
			break;
		case 1:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.POST);
			break;
		case 2:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.PUT);
			break;
		case 3:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.HEAD);
			break;
		case 4:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.DELETE);
			break;
		case 5:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.OPTIONS);
			break;
		case 6:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.TRACE);
			break;
		case 7:
			request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.PATCH);
			break;
		default:
			break;
		}
		if (request != null)
			CallServer.getRequestInstance().add(this, 0, request, this, false, true);
	}

	@Override
	public void onSucceed(int what, Response<Bitmap> response) {
		mImageView.setImageBitmap(response.get());
		if (RequestMethod.HEAD == response.getRequestMethod()) {
			mTvResult.setText("请求成功, 请求方式为HEAD, 没有响应内容");
		} else {
			mTvResult.setText("请求成功");
		}
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
		mTvResult.setText("失败：" + message);
		mImageView.setImageBitmap(null);
	}

}
