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

import com.yolanda.multiasynctask.MultiAsynctask;
import com.yolanda.nohttp.base.BaseResponse;

/**
 * 多Http请求并发|Multiple Http requests
 * Created in Aug 4, 2015 8:42:31 AM
 * 
 * @author YOLANDA
 */
class RequestPoster extends MultiAsynctask<Request, Void, BaseResponse> {

	static final int COMMAND_REQUEST_HTTP = 0;
	static final int COMMAND_REQUEST_FILENAME = 1;

	private int what;
	private int command;
	private OnResponseListener mResponseListener;

	public RequestPoster(int what, int command, OnResponseListener responseListener) {
		this.what = what;
		this.command = command;
		this.mResponseListener = responseListener;
	}

	@Override
	public BaseResponse onTask(Request... params) {
		BaseResponse response = null;
		switch (command) {
		case COMMAND_REQUEST_HTTP:
			response = HttpExecutor.getInstance().request(params[0]);
			break;
		default:
			response = HttpExecutor.getInstance().requestFilename(params[0]);
			break;
		}
		return response;
	}

	@Override
	public void onResult(BaseResponse result) {
		if (mResponseListener != null)
			if (result.isSuccessful())
				mResponseListener.onNoHttpResponse(what, (Response) result);
			else
				mResponseListener.onNoHttpError(what, (ResponseError) result);
	}
}
