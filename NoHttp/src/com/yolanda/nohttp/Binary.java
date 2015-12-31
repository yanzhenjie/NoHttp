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

import java.io.IOException;
import java.io.OutputStream;

import com.yolanda.nohttp.able.Cancelable;
import com.yolanda.nohttp.tools.CounterOutputStream;

/**
 * File interface</br>
 * All the methods are called in Son thread.</br>
 * </br>
 * Created in Oct 12, 2015 4:44:07 PM
 * 
 * @author YOLANDA
 */
public abstract class Binary implements Cancelable {

	/**
	 * Return the byteArray of file
	 */
	public final void onWriteBinary(CommonRequest request, OutputStream outputStream) throws IOException {
		if (outputStream instanceof CounterOutputStream) {
			((CounterOutputStream) outputStream).write(onMeasureLength());
		} else {
			onWriteByteArray(request, outputStream);
		}
	}

	/**
	 * Length of measurement
	 */
	protected abstract long onMeasureLength();

	/**
	 * Return the byteArray of file
	 */
	protected abstract void onWriteByteArray(CommonRequest request, OutputStream outputStream);

	/**
	 * Return the fileName
	 */
	public abstract String getFileName();

	/**
	 * Return mimeType of stream
	 */
	public abstract String getMimeType();

	/**
	 * Get file charset
	 */
	public abstract String getCharset();
}
