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

/**
 * Download Network Interface</br>
 * Created in Oct 20, 2015 4:13:04 PM
 * 
 * @author YOLANDA
 */
public abstract interface Downloader {

	/**
	 * Execute a download task
	 * 
	 * @param downloadRequest Download request parameter
	 * @param what what of task
	 * @param downloadListener The download process monitor
	 */
	public abstract void download(DownloadRequest downloadRequest, int what, DownloadListener downloadListener);

}
