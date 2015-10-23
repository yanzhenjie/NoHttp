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
package com.yolanda.nohttp.download;

import com.yolanda.nohttp.BasicAnalyzeRequest;

/**
 * Download task request interface</br>
 * Created in Oct 21, 2015 11:09:04 AM
 * 
 * @author YOLANDA
 */
public abstract interface DownloadRequest extends BasicAnalyzeRequest {

	/**
	 * Returns the what flag for the current task
	 */
	public abstract int what();

	/**
	 * Return the mFileDir
	 */
	public abstract String getFileDir();

	/**
	 * Return the mFileName
	 */
	public abstract String getFileName();

	/**
	 * Return the isRange
	 */
	public abstract boolean isRange();

	/**
	 * Set sign of the download
	 */
	public abstract void setCancelSign(Object sign);

	/**
	 * Cancel a request according to sign
	 */
	public abstract void cancelBySign(Object sign);

	/**
	 * Cancel the download
	 */
	public abstract void cancel();

	/**
	 * Return Download is canceled
	 */
	public abstract boolean isCanceled();

	/**
	 * Objects that can be identified by the network implementation.
	 */
	public abstract BasicAnalyzeRequest getAnalyzeReqeust();

	/**
	 * The current task download error
	 * 
	 * @param statusCode Error code, used to distinguish what kind of mistake
	 * @param errorMessage error message
	 */
	public abstract void onDownloadError(StatusCode statusCode, CharSequence errorMessage);

	/**
	 * Notification of the current download task at the start
	 */
	public abstract void onStart();

	/**
	 * When the download process change
	 */
	public abstract void onProgress(int progress);

	/**
	 * Download is complete.
	 * 
	 * @param filePath After download, file save path
	 */
	public abstract void onFinish(String filePath);

	/**
	 * Download request is canceled
	 * 
	 * @param what Which is used to mark the download tasks
	 */
	public abstract void onCancel();
}
