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

import java.io.File;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.yolanda.nohttp.BasicAnalyzeRequest;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.security.Certificate;

/**
 * Download the implementation class of the parameter request, and convert it to the object of the network download</br>
 * Created in Jul 31, 2015 10:38:10 AM
 * 
 * @author YOLANDA
 */
public class RestDownloadRequestor implements DownloadRequest, BasicAnalyzeRequest {
	/**
	 * Default timeout
	 */
	public static final int TIMEOUT_DEFAULT = 8 * 1000;
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
	 * sign of the request
	 */
	private Object cancelSign;
	/**
	 * Download is canceled
	 */
	private boolean isCancel;
	/**
	 * Connect http timeout
	 */
	private int mConnectTimeout = TIMEOUT_DEFAULT;
	/**
	 * Read data timeout
	 */
	private int mReadTimeout = TIMEOUT_DEFAULT;
	/**
	 * Whether this request is allowed to be directly passed through Https, not a certificate validation
	 */
	private boolean isAllowHttps = true;
	/**
	 * Request heads
	 */
	private Headers mheaders;
	/**
	 * Https certificate
	 */
	private Certificate mCertificate;

	/**
	 * Create a download requestor
	 * 
	 * @param url Download address
	 * @param fileFloder Folder to save files
	 * @param filename filename
	 * @param isRange Whether power resume Download
	 */
	public RestDownloadRequestor(String url, String fileFloder, String filename, boolean isRange) {
		this.url = url;
		this.mFileDir = fileFloder;
		this.mFileName = filename;
		this.isRange = isRange;
		this.mheaders = new Headers();
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
		return mCertificate;
	}

	@Override
	public boolean isAllowHttps() {
		return isAllowHttps;
	}

	@Override
	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	@Override
	public int getReadTimeout() {
		return mReadTimeout;
	}

	@Override
	public Headers getHeaders() {
		return mheaders;
	}

	@Override
	public String getParamsEncoding() {
		return NoHttp.CHARSET_UTF8;
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
	public int checkBeforeStatus() {
		if (isRange) {
			try {
				File lastFile = new File(mFileDir, mFileName);
				if (lastFile.exists())
					return STATUS_FINISH;
				File tempFile = new File(mFileDir, mFileName + ".temp");
				if (tempFile.exists())
					return STATUS_RESUME;
			} catch (Exception e) {
			}
		}
		return STATUS_RESTART;
	}

	@Override
	public BasicAnalyzeRequest getAnalyzeReqeust() {
		return this;
	}

	@Override
	public void setCertificate(Certificate mCertificate) {
		this.mCertificate = mCertificate;
	}

	@Override
	public void setAllowHttps(boolean isAllowHttps) {
		this.isAllowHttps = isAllowHttps;
	}

	@Override
	public void setConnectTimeout(int connectTimeout) {
		this.mConnectTimeout = connectTimeout;
	}

	@Override
	public void setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
	}

	@Override
	public void setHeader(String name, String value) {
		mheaders.set(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		mheaders.add(name, value);
	}

	@Override
	public void addCookie(HttpCookie cookie) {
		try {
			URI uri = new URI(url);
			if (HttpCookie.domainMatches(cookie.getDomain(), uri.getHost())) {
				mheaders.add(Headers.HEAD_KEY_COOKIE, cookie.getName() + "=" + cookie.getValue());
			}
		} catch (URISyntaxException e) {
			Logger.throwable(e);
		}
	}

	@Override
	public void addCookie(CookieStore cookieStore) {
		try {
			URI uri = new URI(url);
			List<HttpCookie> httpCookies = cookieStore.get(uri);
			for (HttpCookie cookie : httpCookies) {
				addCookie(cookie);
			}
		} catch (URISyntaxException e) {
			Logger.throwable(e);
		}
	}

	@Override
	public void removeHeader(String name) {
		mheaders.removeAll(name);
	}

	@Override
	public void removeAllHeaders() {
		mheaders.clear();
	}
}
