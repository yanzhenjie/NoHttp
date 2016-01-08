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
import java.util.Collections;
import java.util.Set;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;

/**
 * Download the implementation class of the parameter request, and convert it to the object of the network download</br>
 * Created in Jul 31, 2015 10:38:10 AM
 * 
 * @author YOLANDA
 */
public class RestDownloadRequestor extends DownloadRequest {
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
	 * If there is a old files, whether to delete the old files
	 */
	private final boolean isDeleteOld;

	public RestDownloadRequestor(String url, String fileFloder, String filename, boolean isRange, boolean isDeleteOld) {
		super(url, RequestMethod.GET);
		this.mFileDir = fileFloder;
		this.mFileName = filename;
		this.isRange = isRange;
		this.isDeleteOld = isDeleteOld;
	}

	@Override
	public String getFileDir() {
		return this.mFileDir;
	}

	@Override
	public String getFileName() {
		return this.mFileName;
	}

	@Override
	public boolean isRange() {
		return this.isRange;
	}

	@Override
	public boolean isDeleteOld() {
		return this.isDeleteOld;
	}

	@Override
	public boolean isOutPutMethod() {
		return false;
	}

	@Override
	public boolean hasBinary() {
		return false;
	}

	@Override
	public int checkBeforeStatus() {
		if (this.isRange) {
			try {
				File lastFile = new File(mFileDir, mFileName);
				if (lastFile.exists() && !isDeleteOld)
					return STATUS_FINISH;
				File tempFile = new File(mFileDir, mFileName + ".nohttp");
				if (tempFile.exists())
					return STATUS_RESUME;
			} catch (Exception e) {
			}
		}
		return STATUS_RESTART;
	}

	@Override
	protected Set<String> keySet() {
		return Collections.emptySet();
	}

	@Override
	protected Object value(String key) {
		return null;
	}

	@Override
	public Void parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
		return null;
	}
}
