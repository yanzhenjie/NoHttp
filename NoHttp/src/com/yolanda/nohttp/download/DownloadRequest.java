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

import com.yolanda.nohttp.CommonRequest;
import com.yolanda.nohttp.RequestMethod;

/**
 * Download task request interface</br>
 * Created in Oct 21, 2015 11:09:04 AM
 * 
 * @author YOLANDA
 */
public abstract class DownloadRequest extends CommonRequest<Void> {

	/**
	 * Also didn't download to start download again
	 */
	public static final int STATUS_RESTART = 0;
	/**
	 * Part has been downloaded, continue to download last time
	 */
	public static final int STATUS_RESUME = 1;
	/**
	 * Has the download is complete, not to download operation
	 */
	public static final int STATUS_FINISH = 2;

	/**
	 * @param url
	 * @param requestMethod
	 */
	public DownloadRequest(String url, RequestMethod requestMethod) {
		super(url, RequestMethod.GET);
	}

	/**
	 * @param url
	 */
	public DownloadRequest(String url) {
		super(url);
	}

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
	 * If there is a old files, whether to delete the old files
	 */
	public abstract boolean isDeleteOld();

	/**
	 * Query before download status
	 * STATUS_RESTART representative no download do to download again; Download STATUS_RESUME represents a part of, to
	 * continue to download; STATUS_FINISH representatives have finished downloading.
	 * 
	 * @return
	 * @see #STATUS_RESTART
	 * @see #STATUS_RESUME
	 * @see #STATUS_FINISH
	 */
	public abstract int checkBeforeStatus();
}
