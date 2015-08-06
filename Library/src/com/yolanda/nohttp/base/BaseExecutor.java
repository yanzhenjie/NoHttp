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

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import com.yolanda.nohttp.Logger;

import android.content.Context;

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
		if (!request.isOutPut() && request.hasParam()) {
			if (urlStr.contains("?") && urlStr.contains("="))
				urlStr += ("&" + request.buildParam());
			else
				urlStr += ("?" + request.buildParam());
		}
		Logger.d("Reuqest adress:" + urlStr);
		if (android.os.Build.VERSION.SDK_INT <= 9)
			System.setProperty("http.keepAlive", "false");
		URL url = new URL(urlStr);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		if (urlStr.startsWith("https"))
			HttpsVerifier.verify((HttpsURLConnection) httpURLConnection);
		String requestMethod = request.getRequestMethod().toString();
		Logger.d("Request method:" + requestMethod);
		httpURLConnection.setRequestMethod(requestMethod);
		httpURLConnection.setDoInput(true);
		boolean isCache = request.isCache();
		httpURLConnection.setUseCaches(isCache);
		httpURLConnection.setConnectTimeout(request.getConnectTimeout());
		httpURLConnection.setReadTimeout(request.getReadTimeout());
		httpURLConnection.setRequestProperty("Charset", request.getCharset());
		httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// default Keep-Alive
		httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");// default gzip
		// httpURLConnection.setRequestProperty("Cache-Control", "no-cache");// no-cache、no-store

		Set<String> headKeys = request.getHeadKeys();
		for (String headKey : headKeys) {
			httpURLConnection.setRequestProperty(headKey, request.getHead(headKey));
		}
		return httpURLConnection;
	}

	/**
	 * Open Http cache
	 */
	public void enableHttpResponseCache(Context context) {
		try {
			long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
			File httpCacheDir = new File(context.getCacheDir(), "http");
			Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, httpCacheSize);
		} catch (Exception e) {// httpResponseCacheNotAvailable
		}
	}
}
