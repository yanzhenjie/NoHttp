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

import com.yolanda.nohttp.base.BasePoster;

/**
 * Created in Jul 30, 2015 12:11:14 PM
 * 
 * @author YOLANDA
 */
class RequestPoster extends BasePoster implements Serializable {

	private static final long serialVersionUID = 104L;
	/**
	 * From the HTTP address request data
	 */
	static final int COMMAND_REQUEST_HTTP = 0;
	/**
	 * From a url file name, including the extension
	 */
	static final int COMMAND_REQUEST_FILENAME = 1;
	/**
	 * Choose to perform the HTTP method
	 */
	private int command;
	/**
	 * The implementation of the Http parameters
	 */
	private Request mRequest;
	/**
	 * The angel of a mission
	 */
	private Messenger mMessenger;

	/**
	 * Build an asynchronous task
	 * 
	 * @param command Used to distinguish NoHttp function,{@link #COMMAND_REQUEST_FILENAME},
	 *        {@link #COMMAND_REQUEST_HTTP}
	 * @param request Http requestor
	 * @param messenger The angel of a mission
	 */
	public RequestPoster(int command, Request request, Messenger messenger) {
		super();
		this.command = command;
		this.mRequest = request;
		this.mMessenger = messenger;
	}

	@Override
	public void run() {
		ResponseBase baseResponse = null;
		switch (command) {
		case COMMAND_REQUEST_HTTP:
			baseResponse = HttpExecutor.getInstance().request(mRequest);
			break;
		case COMMAND_REQUEST_FILENAME:
			baseResponse = HttpExecutor.getInstance().requestFilename(mRequest);
			break;
		default:
			break;
		}
		if (mMessenger != null) {
			mMessenger.setBaseResponse(baseResponse);
			postMessenger(mMessenger);
		}
	}
}
