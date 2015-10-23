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
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RestRequestor;
import com.yolanda.nohttp.security.Certificate;

/**
 * Download the implementation class of the parameter request, and convert it to the object of the network download</br>
 * Created in Jul 31, 2015 10:38:10 AM
 * 
 * @author YOLANDA
 */
public class DownloadRequestor implements DownloadRequest, BasicAnalyzeRequest {
	/**
	 * Record the what of the current download request
	 */
	private final int what;
	/**
	 * url of download target
	 */
	private final String url;
	/**
	 * File the target folder
	 */
	private final String mFileDir;
	/**
	 * The file target name
	 */
	private final String mFileName;
	/**
	 * If is to download a file, whether the breakpoint continuingly
	 */
	private final boolean isRange;
	/**
	 * download listener
	 */
	private final DownloadListener downloadListener;
	/**
	 * sign of the request
	 */
	private Object cancelSign;
	/**
	 * Download is canceled
	 */
	private boolean isCancel;

	/**
	 * Create a download requestor
	 * 
	 * @param what Used to flag this request, equivalent to the what in handler, will be back in the downloadListener, as is returned, when multiple requests using the same DownloadListener, you can
	 *        use what to distinguish which request
	 * @param url Download address
	 * @param fileFloder Folder to save files
	 * @param filename filename
	 * @param isRange Whether power resume Download
	 * @param downloadListener Download process monitoring, the download status changes when the callback, such as progress changes, the occurrence of errors, download and complete
	 */
	public DownloadRequestor(int what, String url, String fileFloder, String filename, boolean isRange, DownloadListener downloadListener) {
		this.what = what;
		this.url = url;
		this.mFileDir = fileFloder;
		this.mFileName = filename;
		this.isRange = isRange;
		this.downloadListener = downloadListener;
	}

	@Override
	public int what() {
		return what;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public String getFileDir() {
		return mFileDir;
	}

	@Override
	public String getFileName() {
		return mFileName;
	}

	@Override
	public boolean isRange() {
		return isRange;
	}

	@Override
	public int getRequestMethod() {
		return RequestMethod.GET;
	}

	@Override
	public Certificate getCertificate() {
		return null;
	}

	@Override
	public boolean isAllowHttps() {
		return true;
	}

	@Override
	public int getConnectTimeout() {
		return RestRequestor.TIMEOUT_DEFAULT;
	}

	@Override
	public int getReadTimeout() {
		return RestRequestor.TIMEOUT_DEFAULT;
	}

	@Override
	public Headers getHeaders() {
		return null;
	}

	@Override
	public String getParamsEncoding() {
		return null;
	}

	@Override
	public boolean isOutPut() {
		return false;
	}

	@Override
	public boolean hasBinary() {
		return false;
	}

	@Override
	public void setCancelSign(Object sign) {
		this.cancelSign = sign;
	}

	@Override
	public void cancelBySign(Object sign) {
		if (this.cancelSign == sign)
			cancel();
	}

	@Override
	public boolean isCanceled() {
		return isCancel;
	}

	@Override
	public void cancel() {
		this.isCancel = true;
	}

	@Override
	public void onDownloadError(StatusCode statusCode, CharSequence errorMessage) {
		this.downloadListener.onDownloadError(what, statusCode, errorMessage);
	}

	@Override
	public void onStart() {
		this.downloadListener.onStart(what);
	}

	@Override
	public void onProgress(int progress) {
		this.downloadListener.onProgress(what, progress);
	}

	@Override
	public void onFinish(String filePath) {
		this.downloadListener.onFinish(what, filePath);
	}

	@Override
	public void onCancel() {
		this.downloadListener.onCancel(what);
	}

	@Override
	public BasicAnalyzeRequest getAnalyzeReqeust() {
		return this;
	}
}
