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
package com.yolanda.nohttp.download;

import com.yolanda.nohttp.base.BaseListener;
import com.yolanda.nohttp.base.BaseMessenger;

import android.os.Message;

/**
 * Created in Jul 31, 2015 2:06:36 PM
 * 
 * @author YOLANDA
 */
class Messenger extends BaseMessenger {

	private DownloadResponse downloadResponse;

	/**
	 * @param what
	 * @param baseListener
	 */
	public Messenger(int what, BaseListener baseListener) {
		super(what, baseListener);
	}

	@Override
	public Message obtain() {
		Message message = Message.obtain();
		message.obj = this;
		return message;
	}

	/**
	 * @param downloadResponse the downloadResponse to set
	 */
	public void setDownloadResponse(DownloadResponse downloadResponse) {
		this.downloadResponse = downloadResponse;
	}

	@Override
	public void callback() {
		if (downloadResponse != null && getResponseListener() != null) {
			DownloadListener downloadListener = (DownloadListener) getResponseListener();
			switch (downloadResponse.getCommand()) {
			case DownloadResponse.START:
				downloadListener.onStart(getWhat());
				break;
			case DownloadResponse.ERROR:
				downloadListener.onDownloadError(getWhat(), downloadResponse.getStatusCode());
				break;
			case DownloadResponse.FINISH:
				downloadListener.onFinish(getWhat(), downloadResponse.getFilepath());
				break;
			case DownloadResponse.PROGRESS:
				downloadListener.onProgress(getWhat(), downloadResponse.getProgress());
				break;
			default:
				break;
			}
		}
	}

}
