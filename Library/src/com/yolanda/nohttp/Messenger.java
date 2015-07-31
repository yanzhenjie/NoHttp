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

import java.io.Serializable;

import com.yolanda.nohttp.base.BaseListener;
import com.yolanda.nohttp.base.BaseMessenger;

/**
 * Created in Jul 30, 2015 12:12:11 PM
 * 
 * @author YOLANDA
 */
class Messenger extends BaseMessenger implements Serializable {

	private static final long serialVersionUID = 103L;
	/**
	 * Accept the results of the response
	 */
	private ResponseBase mBaseResponse;

	/**
	 * @param what Used to mark the request, and return when correction results
	 * @param responseListener Callback the caller
	 * @param response Give null
	 */
	public Messenger(int what, OnResponseListener responseListener) {
		super(what, responseListener);
	}

	/**
	 * @param response the mBaseResponse to set
	 */
	void setBaseResponse(ResponseBase response) {
		this.mBaseResponse = response;
	}

	/**
	 * Get forwarded the main thread of the Message
	 */
	@Override
	public android.os.Message obtain() {
		android.os.Message message = android.os.Message.obtain();
		message.obj = this;
		return message;
	}

	@Override
	public void callback() {
		BaseListener baseListener = getResponseListener();
		if (baseListener != null && mBaseResponse != null) {
			OnResponseListener listener = (OnResponseListener) baseListener;
			if (mBaseResponse.isSuccessful())
				listener.onNoHttpResponse(getWhat(), (Response) mBaseResponse);
			else
				listener.onNoHttpError(getWhat(), (ResponseError) mBaseResponse);
		}
	}

}
