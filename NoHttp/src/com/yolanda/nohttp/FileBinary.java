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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.text.TextUtils;

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

	public FileBinary(File file, String fileName) {
		if (file == null)
			throw new IllegalArgumentException("file == null");
		if (!file.isFile())
			throw new IllegalArgumentException("file isn't file");
		this.file = file;
		this.fileName = fileName;
		if (TextUtils.isEmpty(fileName))
			this.fileName = file.getName();
	}

	@Override
	public byte[] getByteArray() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			FileInputStream inputStream = new FileInputStream(file);
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			Logger.wtf(e);
		}
		return outputStream.toByteArray();
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMimeType() {
		return NoHttp.MIMETYE_DEFAULT;
	}

	@Override
	public String getCharset() {
		return NoHttp.CHARSET_DEFAULT;
	}

}
