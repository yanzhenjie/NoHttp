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

import java.io.File;
import java.io.Serializable;

import com.yolanda.nohttp.base.BasePoster;

import android.content.Context;

/**
 * Created in Jul 31, 2015 10:39:43 AM
 * 
 * @author YOLANDA
 */
class DownloadPoster extends BasePoster implements DownloadListener, Serializable {

	private static final long serialVersionUID = 200L;
	/**
	 * From the HTTP address request data
	 */
	static final int COMMAND_DOWNLOAD_STATIC = 0;
	/**
	 * From a url file name, including the extension
	 */
	static final int COMMAND_DOWNLOAD_DYNAMIC = 1;
	/**
	 * Used to access the network
	 */
	private Context mContext;
	/**
	 * Need to monitor or don't need to listen
	 */
	private int command;
	/**
	 * Download parameters
	 */
	private DownloadRequest mDownloadRequest;
	/**
	 * messenger
	 */
	private Messenger messenger;
	/**
	 * Download process response
	 */
	private DownloadResponse downloadResponse;

	public DownloadPoster(Context context, int command, DownloadRequest downloadRequest, Messenger messenger) {
		this.mContext = context;
		this.command = command;
		this.mDownloadRequest = downloadRequest;
		this.messenger = messenger;
	}

	@Override
	public void run() {
		if (command == COMMAND_DOWNLOAD_STATIC) {
			String url = mDownloadRequest.getUrl();
			String path = mDownloadRequest.getFileDir() + File.separator + mDownloadRequest.getFileName();
			Downloader.getInstance(mContext).download(url, path);
		} else
			Downloader.getInstance(mContext).download(mDownloadRequest, this);
	}

	private DownloadResponse createResponse(int command) {
		if (downloadResponse == null) {
			downloadResponse = new DownloadResponse();
		}
		downloadResponse.setCommand(command);
		return downloadResponse;
	}

	@Override
	public void onDownloadError(int what, StatusCode statusCode) {
		createResponse(DownloadResponse.ERROR);
		downloadResponse.setStatusCode(statusCode);
		messenger.setDownloadResponse(downloadResponse);
		postMessenger(messenger);
	}

	@Override
	public void onStart(int what) {
		createResponse(DownloadResponse.START);
		messenger.setDownloadResponse(downloadResponse);
		postMessenger(messenger);
	}

	@Override
	public void onProgress(int what, int progress) {
		createResponse(DownloadResponse.PROGRESS);
		downloadResponse.setProgress(progress);
		messenger.setDownloadResponse(downloadResponse);
		postMessenger(messenger);
	}

	@Override
	public void onFinish(int what, String filePath) {
		createResponse(DownloadResponse.FINISH);
		downloadResponse.setFilepath(filePath);
		messenger.setDownloadResponse(downloadResponse);
		postMessenger(messenger);
	}
}
