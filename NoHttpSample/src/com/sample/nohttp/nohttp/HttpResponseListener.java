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
package com.sample.nohttp.nohttp;

import com.sample.nohttp.dialog.WaitDialog;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Response;

import android.content.Context;

/**
 * Created in Nov 4, 2015 12:02:55 PM
 * 
 * @author YOLANDA
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

	private WaitDialog mWaitDialog;

	private HttpCallback<T> callback;

	public HttpResponseListener(Context context, HttpCallback<T> httpCallback) {
		if (context != null)
			mWaitDialog = new WaitDialog(context);
		this.callback = httpCallback;
	}

	@Override
	public void onStart(int what) {
		if (mWaitDialog != null && !mWaitDialog.isShowing())
			mWaitDialog.show();
	}

	@Override
	public void onSucceed(int what, Response<T> response) {
		if (callback != null)
			callback.onSucceed(what, response);
	}

	@Override
	public void onFailed(int what, String url, Object tag, CharSequence message) {
		if (callback != null)
			callback.onFailed(what, url, tag, message);
	}

	@Override
	public void onFinish(int what) {
		if (mWaitDialog != null && mWaitDialog.isShowing())
			mWaitDialog.dismiss();
	}

}
