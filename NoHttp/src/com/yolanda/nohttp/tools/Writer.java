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
package com.yolanda.nohttp.tools;

import java.io.IOException;
import java.io.OutputStream;

import com.yolanda.nohttp.Binary;

/**
 * NoHttp sends data to the network as a Writer
 * </br>
 * Created in Jan 16, 2016 12:00:57 PM
 * 
 * @author YOLANDA
 */
public class Writer {

	private OutputStream mOutputStream;

	private boolean isPrint;

	public Writer(OutputStream outputStream) {
		this.mOutputStream = outputStream;
	}

	public Writer(OutputStream outputStream, boolean isPrint) {
		this.mOutputStream = outputStream;
		this.isPrint = isPrint;
	}

	public void write(int b) throws IOException {
		mOutputStream.write(b);
	}

	public void write(byte[] b) throws IOException {
		mOutputStream.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		mOutputStream.write(b, off, len);
	}

	public void write(Binary binary) {
		if (mOutputStream instanceof CounterOutputStream) {
			((CounterOutputStream) mOutputStream).write(binary.getLength());
		} else {
			binary.onWriteBinary(mOutputStream);
		}
	}

	public boolean isPrint() {
		return isPrint;
	}
}
