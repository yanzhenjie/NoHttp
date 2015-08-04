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

import com.yolanda.multiasynctask.MultiAsynctask;

import android.content.Context;

/**
 * Created in Jul 31, 2015 10:39:43 AM
 * 
 * @author YOLANDA
 */
class DownloadPoster extends MultiAsynctask<DownloadRequest, DownloadResponse, Void>implements DownloadListener {

	/**
	 * From the HTTP address request data
	 */
	static final int COMMAND_DOWNLOAD_STATIC = 0;
	/**
	 * From a url file name, including the extension
	 */
	static final int COMMAND_DOWNLOAD_DYNAMIC = 1;
	/**
	 * error code
	 */
	static final int ERROR = 0;
	/**
	 * download start
	 */
	static final int START = 1;
	/**
	 * download progress change
	 */
	static final int PROGRESS = 2;
	/**
	 * download finish
	 */
	static final int FINISH = 3;
	/**
	 * Used to access the network
	 */
	private Context mContext;
	/**
	 * Make download task
	 */
	private int what;
	/**
	 * Need to monitor or don't need to listen
	 */
	private int command;
	/**
	 * Download listener
	 */
	private DownloadListener mDownloadListener;
	/**
	 * Maintain response object
	 */
	private DownloadResponse mDownloadResponse;

	public DownloadPoster(Context context, int what, int command, DownloadListener downloadListener) {
		this.mContext = context.getApplicationContext();
		this.what = what;
		this.command = command;
		this.mDownloadListener = downloadListener;
	}

	@Override
	public Void onTask(DownloadRequest... params) {
		DownloadRequest request = params[0];
		switch (command) {
		case COMMAND_DOWNLOAD_STATIC:
			String path = request.getFileDir() + File.separator + request.getFileName();
			DownloadExecutor.getInstance(mContext).download(request, path);
			break;
		default:
			DownloadExecutor.getInstance(mContext).download(request, this);
			break;
		}
		return null;
	}

	@Override
	public void onUpdate(DownloadResponse update) {
		if (this.mDownloadListener != null) {
			int command = update.getCommand();
			switch (command) {
			case ERROR:
				mDownloadListener.onDownloadError(what, update.getStatusCode());
				break;
			case START:
				mDownloadListener.onStart(what);
				break;
			case PROGRESS:
				mDownloadListener.onProgress(what, update.getProgress());
				break;
			default:
				mDownloadListener.onFinish(what, update.getFilepath());
				break;
			}
		}
	}

	/**
	 * create response object
	 * 
	 * @param command status command
	 */
	private DownloadResponse createResponse(int command) {
		if (mDownloadResponse == null) {
			mDownloadResponse = new DownloadResponse();
		}
		mDownloadResponse.setCommand(command);
		return mDownloadResponse;
	}

	@Override
	public void onDownloadError(int what, StatusCode statusCode) {
		createResponse(ERROR);
		mDownloadResponse.setStatusCode(statusCode);
		postUpdate(mDownloadResponse);
	}

	@Override
	public void onStart(int what) {
		createResponse(START);
		postUpdate(mDownloadResponse);
	}

	@Override
	public void onProgress(int what, int progress) {
		createResponse(PROGRESS);
		mDownloadResponse.setProgress(progress);
		postUpdate(mDownloadResponse);
	}

	@Override
	public void onFinish(int what, String filePath) {
		createResponse(FINISH);
		mDownloadResponse.setFilepath(filePath);
		postUpdate(mDownloadResponse);
	}
}
