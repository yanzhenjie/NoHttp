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
package com.yolanda.nohttp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * A default implementation of Binary</br>
 * All the methods are called in Son thread.</br>
 * </br>
 * Created in Oct 17, 2015 12:40:54 PM
 * 
 * @author YOLANDA
 */
public class FileBinary implements Binary {

	private File file;

	private String fileName;

	private String mimeType;

	private String charSet;

	private boolean isRun = true;

	public FileBinary(File file) {
		this(file, file.getName());
	}

	public FileBinary(File file, String fileName) {
		this(file, fileName, NoHttp.MIMETYE_FILE);
	}

	public FileBinary(File file, String fileName, String mimeType) {
		this(file, fileName, mimeType, NoHttp.CHARSET_UTF8);
	}

	public FileBinary(File file, String fileName, String mimeType, String charSet) {
		if (file == null) {
			throw new IllegalArgumentException("File is null");
		} else if (!file.exists()) {
			throw new IllegalArgumentException("File isn't exists");
		}
		this.file = file;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.charSet = charSet;
	}

	@Override
	public long getLength() {
		return this.file.length();
	}

	@Override
	public void onWriteBinary(OutputStream outputStream) {
		if (this.file != null && isRun) {
			try {
				RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
				int len = -1;
				byte[] buffer = new byte[1024];
				while (isRun && (len = accessFile.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
				accessFile.close();
			} catch (IOException e) {
				Logger.e(e);
			}
		}
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public String getCharset() {
		return charSet;
	}

	@Override
	public void cancel() {
		this.isRun = false;
	}

	@Override
	public boolean isCanceled() {
		return !isRun;
	}

	@Override
	public void reverseCancle() {
		this.isRun = true;
	}

}
