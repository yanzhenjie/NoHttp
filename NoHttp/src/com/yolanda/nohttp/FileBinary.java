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

import android.util.Log;

/**
 * A default implementation of Binary</br>
 * All the methods are called in Son thread.</br>
 * </br>
 * Created in Oct 17, 2015 12:40:54 PM
 * 
 * @author YOLANDA
 */
public class FileBinary extends Binary {

	private File file;

	private String fileName;

	private String mimeType;

	private String charSet;

	private boolean isStarted = false;

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
			Log.e("NoHttp", fileName + " is null file");
		} else if (!file.exists()) {
			Log.e("NoHttp", fileName + " is non-existent");
		}
		this.file = file;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.charSet = charSet;
	}
	
	@Override
	protected long onMeasureLength() {
		if(file != null) {
			return this.file.length();
		}
		return 0;
	}

	@Override
	protected void onWriteByteArray(CommonRequest<?> request, OutputStream outputStream) {
		if (this.file != null && isRun) {
			isStarted = true;
			try {
				RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
				int len = -1;
				byte[] buffer = new byte[1024];
				while (!request.isCanceled() && (len = accessFile.read(buffer)) != -1) {
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
		if (isStarted)
			throw new RuntimeException("Upload action has begun, can not be canceled");
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
