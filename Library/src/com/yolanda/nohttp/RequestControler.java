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
package com.yolanda.nohttp;

import android.os.AsyncTask;

/**
 * Created in Jul 28, 2015 7:30:58 PM
 * 
 * @author YOLANDA
 */
class RequestControler extends AsyncTask<Request, Void, BaseResponse> {

	/**
	 * Every time the command executed and callback
	 */
	private Executor mExecutor;

	/**
	 * Build asynchronous actuators
	 * 
	 * @param executor Actuator global parameters
	 */
	public RequestControler(Executor executor) {
		this.mExecutor = executor;
	}

	@Override
	protected BaseResponse doInBackground(Request... params) {
		if (params != null) {
			return execute(params[0]);
		} else {
			throw new NullPointerException("The Http request parameters cannot be null");
		}
	}

	@Override
	protected void onPostExecute(BaseResponse response) {
		if (mExecutor != null && mExecutor.responseListener != null) {
			if (response.isSuccessful()) {
				Logger.i("Http excute successful");
				mExecutor.responseListener.onNoHttpResponse(mExecutor.what, (Response) response);
			} else {
				Logger.e("Http excute failure");
				mExecutor.responseListener.onNoHttpError(mExecutor.what, (ResponseError) response);
			}
		}
	}

	/**
	 * According to the command execution request
	 * 
	 * @param requestParam The packaging of the HTTP request parameter
	 * @return The response
	 */
	private BaseResponse execute(Request request) {
		switch (mExecutor.command) {
		case Command.REQUEST_HTTP:
			return HttpExecutor.getInstance().request(request);
		case Command.REQUEST_FILENAME:
			return HttpExecutor.getInstance().requestFilename(request);
		default:
			return null;
		}
	}
}
