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
package com.yolanda.nohttp.base;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.yolanda.nohttp.Logger;

/**
 * Created in Aug 4, 2015 10:12:38 AM
 * 
 * @author YOLANDA
 */
public abstract class BaseExecutor {

	/**
	 * Build request before written request attributes, such as url, head
	 * 
	 * @param request Request parameters, which is used to set the request header information
	 * @return
	 */
	protected HttpURLConnection buildHttpAttribute(BaseRequest request) throws Throwable {
		String urlStr = request.getUrl();
		if (request.isOutPut() && request.hasParam()) {
			urlStr += ("?" + request.buildParam());
		}
		Logger.d("Reuqest adress:" + urlStr);
		URL url = new URL(urlStr);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		if (urlStr.startsWith("https"))
			HttpsVerifier.verify((HttpsURLConnection) httpURLConnection);
		String requestMethod = request.getRequestMethod().toString();
		Logger.d("Request method:" + requestMethod);
		httpURLConnection.setRequestMethod(requestMethod);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setUseCaches(false);// 不许有缓存
		httpURLConnection.setConnectTimeout(request.getConnectTimeout());
		httpURLConnection.setReadTimeout(request.getReadTimeout());
		/* =====请求头===== */
		httpURLConnection.setRequestProperty("Charset", request.getCharset());
		if (request.isKeepAlive())
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
		httpURLConnection.setRequestProperty("Cache-Control", "no-cache");

		Set<String> headKeys = request.getHeadKeys();
		for (String headKey : headKeys) {
			httpURLConnection.setRequestProperty(headKey, request.getHead(headKey));
		}
		return httpURLConnection;
	}

}
