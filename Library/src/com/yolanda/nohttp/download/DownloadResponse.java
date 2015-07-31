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

/**
 * Created in Jul 31, 2015 1:14:37 PM
 * 
 * @author YOLANDA
 */
class DownloadResponse implements Serializable {

	private static final long serialVersionUID = 202L;
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
	 * status change
	 * {@link #ERROR}
	 * {@link #START}
	 * {@link #PROGRESS}
	 * {@link #FINISH}
	 */
	private int command;
	/**
	 * Download error code
	 */
	private StatusCode statusCode;
	/**
	 * Download progress
	 */
	private int progress;
	/**
	 * Download finish, file save path
	 */
	private String filepath;

	DownloadResponse() {
		super();
	}

	/**
	 * @return the command
	 */
	public int getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(int command) {
		this.command = command;
	}

	/**
	 * @return the statusCode
	 */
	public StatusCode getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the filepath
	 */
	public String getFilepath() {
		return filepath;
	}

	/**
	 * @param filepath the filepath to set
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
