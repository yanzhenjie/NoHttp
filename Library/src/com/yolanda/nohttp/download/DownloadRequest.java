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

import java.io.Serializable;

import com.yolanda.nohttp.base.BaseRequest;
import com.yolanda.nohttp.base.RequestMethod;

/**
 * Created in Jul 31, 2015 10:38:10 AM
 * 
 * @author YOLANDA
 */
public class DownloadRequest extends BaseRequest implements Serializable {

	private static final long serialVersionUID = 201L;
	/**
	 * File the target folder
	 */
	private String mFileDir;
	/**
	 * The file target name
	 */
	private String mFileName;
	/**
	 * If is to download a file, whether the breakpoint continuingly
	 */
	private boolean isRange = false;

	public DownloadRequest(String url, RequestMethod requestMethod) {
		super(url, requestMethod);
	}

	/**
	 * Set properties to download
	 * 
	 * @param fileFloder The file save folder
	 * @param filename The filename
	 * @param isRange whether breakpoint continuingly
	 */
	public void setDownloadAttribute(String fileFloder, String filename, boolean isRange) {
		this.mFileDir = fileFloder;
		this.mFileName = filename;
		this.isRange = isRange;
	}

	/**
	 * @return the mFileDir
	 */
	public String getFileDir() {
		return mFileDir;
	}

	/**
	 * @param mFileDir the mFileDir to set
	 */
	public void setFileDir(String mFileDir) {
		this.mFileDir = mFileDir;
	}

	/**
	 * @return the mFileName
	 */
	public String getFileName() {
		return mFileName;
	}

	/**
	 * @param mFileName the mFileName to set
	 */
	public void setFileName(String mFileName) {
		this.mFileName = mFileName;
	}

	/**
	 * @return the isRange
	 */
	public boolean isRange() {
		return isRange;
	}

	/**
	 * @param isRange the isRange to set
	 */
	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}
}
