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

import android.content.Context;

/**
 * Created in Jul 31, 2015 2:04:28 PM
 * 
 * @author YOLANDA
 */
public class DownloadManager {

	private static DownloadManager _DownloadManager;
	private Context mContext;

	private DownloadManager(Context context) {
		this.mContext = context;
	}

	public static DownloadManager getInstance(Context context) {
		if (context == null) {
			throw new NullPointerException("Context isn't null");
		}
		if (_DownloadManager == null) {
			_DownloadManager = new DownloadManager(context);
		}
		return _DownloadManager;
	}

	/**
	 * Download a file,If can choose breakpoint continuingly
	 * 
	 * @param downloadRequest Download parameters
	 * @param what Used to mark download task, when the callback
	 * @param downloadListener Download status callback
	 */
	public void download(DownloadRequest downloadRequest, int what, DownloadListener downloadListener) {
		DownloadPoster downloadPoster = new DownloadPoster(mContext, what, DownloadPoster.COMMAND_DOWNLOAD_DYNAMIC,
				downloadListener);
		downloadPoster.execute(downloadRequest);
	}

	/**
	 * 1. Call this method, only the URL, file path is used, there is no effect of other parameters
	 * 2. And how to download in existing files will be directly deleted, before download again
	 * 
	 * @param downloadRequest Download parameters
	 */
	public void download(DownloadRequest downloadRequest) {
		DownloadPoster downloadPoster = new DownloadPoster(mContext, 0, DownloadPoster.COMMAND_DOWNLOAD_STATIC, null);
		downloadPoster.execute(downloadRequest);
	}
}
