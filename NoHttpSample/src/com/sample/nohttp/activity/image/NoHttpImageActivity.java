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
package com.sample.nohttp.activity.image;

import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpCallback;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 请求图片
 * Created in Oct 23, 2015 7:46:17 PM
 * 
 * @author YOLANDA
 */
public class NoHttpImageActivity extends Activity implements HttpCallback<Bitmap> {

	/**
	 * 显示图片
	 */
	private ImageView mImageView;
	/**
	 * 显示状态
	 */
	private TextView mStatus;

	private String url = "http://192.168.1.136/HttpServer/ImageDownload";

	// private String url = "http://a1.qpic.cn/psb?/V13FzpwG3JdUAq/.y.rmni00WJydktHAv9kFaRHIJ.xZDZYyjwV72Hnu.Q!/b/dG8AAAAAAAAA&bo=LAEsAQAAAAAFByQ!&rf=viewer_4";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("NoHttp演示请求Bitmap");

		setContentView(R.layout.activity_nohttp_image);
		mImageView = (ImageView) findViewById(R.id.iv_status);
		mStatus = (TextView) findViewById(R.id.tv_status);

		findViewById(R.id.btn_reqeust_iamge).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Request<Bitmap> request = NoHttp.createImageRequest(url);
				CallServer.getRequestInstance().add(NoHttpImageActivity.this, 0, request, NoHttpImageActivity.this);
			}
		});

	}

	@Override
	public void onSucceed(int what, Response<Bitmap> response) {
		Bitmap bitmap = response.get();
		if (bitmap == null)
			Logger.i("图片非空");
		mImageView.setImageBitmap(bitmap);
		mStatus.setText("成功");
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
		mStatus.setText("失败：" + message);
	}

}
