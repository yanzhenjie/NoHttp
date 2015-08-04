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
	 * status change
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
	int getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	void setCommand(int command) {
		this.command = command;
	}

	/**
	 * @return the statusCode
	 */
	StatusCode getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the progress
	 */
	int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the filepath
	 */
	String getFilepath() {
		return filepath;
	}

	/**
	 * @param filepath the filepath to set
	 */
	void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
