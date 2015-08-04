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
package com.yolanda.nohttp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yolanda.nohttp.base.BaseResponse;

/**
 * Created in Jul 28, 2015 7:34:35 PM
 * 
 * @author YOLANDA
 */
public class Response extends BaseResponse implements Serializable {

	private static final long serialVersionUID = 101L;
	/**
	 * Response head map
	 */
	private Map<String, List<String>> headers;
	/**
	 * request result:bytes
	 */
	private byte[] bytes = new byte[] {};
	/**
	 * Decoding type
	 */
	private String decoderCharset;
	/**
	 * content length
	 */
	private int contentLength = -1;
	/**
	 * content type
	 */
	private String contentType;

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	/**
	 * Set the data bytes
	 * 
	 * @param bytes http response data
	 */
	void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * Set charset decoding data
	 * 
	 * @param charset like these "utf-8" "gbk" "gb2312"
	 */
	void setCharset(String charset) {
		this.decoderCharset = charset;
	}

	/**
	 * Set the content type
	 * 
	 * @param contentType the contentType to set,like <code>"application/vnd.android.package-archive"</code>
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the content length, according to the byte length calculation
	 * 
	 * @param contentLength the contentLength to set
	 */
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * Returns an unmodifiable map of the response-header fields and values. The
	 * response-header field names are the key values of the map. The map values
	 * are lists of header field values associated with a particular key name.
	 *
	 * <p>
	 * Some implementations (notably {@code HttpURLConnection}) include a mapping for the null key; in HTTP's case, this
	 * maps to the HTTP status line and is treated as being at position 0 when indexing into the header fields.
	 *
	 * @return the response-header representing generic map.
	 */
	public Map<String, List<String>> headers() {
		return headers;
	}

	/**
	 * The results of Stirng type requests
	 * 
	 * @return string result
	 */
	public String string() {
		String result = null;
		try {
			result = new String(bytes, decoderCharset);
		} catch (Throwable e) {
			if (NoHttp.isDebug())
				e.printStackTrace();
		}
		return result;
	}

	/**
	 * The results of bytes type requests
	 * 
	 * @return byte[]
	 */
	public byte[] bytes() {
		return bytes;
	}

	/**
	 * Returns the content length in bytes specified by the response header
	 * field {@code content-length} or {@code -1} if this field is not set or
	 * cannot be represented as an {@code int}.
	 */
	public int contentLength() {
		return contentLength;
	}

	/**
	 * Returns the MIME-type of the content specified by the response header
	 * field {@code content-type} or {@code null} if type is unknown.
	 *
	 * @return the value of the response header field {@code content-type}.
	 */
	public String contentType() {
		return contentType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestCode:");
		builder.append(getResponseCode());
		if (isSuccessful()) {
			builder.append("\nContentLength:");
			builder.append(contentLength);
			builder.append("\nContentType:");
			builder.append(contentType);
			if (headers != null) {
				Set<String> keys = headers.keySet();
				for (String key : keys) {
					List<String> values = headers.get(key);
					for (String value : values) {
						builder.append("\n");
						builder.append(key);
						builder.append(":");
						builder.append(value);
					}
				}
			}
			builder.append("\n");
			builder.append("Content: ");
			builder.append(new String(bytes));
		}
		return builder.toString();
	}
}
