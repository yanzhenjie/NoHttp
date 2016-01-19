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

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.util.HeaderParser;
import com.yolanda.nohttp.util.HttpDateTime;

import android.os.SystemClock;

/**
 * </br>
 * Created in Jan 6, 2016 5:45:19 PM
 * 
 * @author YOLANDA;
 */
public class HttpRestExecutor implements ImplRestExecutor {

	private static HttpRestExecutor _INSTANCE;

	public static HttpRestExecutor getInstance(Cache<CacheEntity> cache, ImplRestConnection connection) {
		if (_INSTANCE == null)
			_INSTANCE = new HttpRestExecutor(cache, connection);
		return _INSTANCE;
	}

	private ImplRestConnection mConnection;

	private Cache<CacheEntity> mCache;

	public HttpRestExecutor(Cache<CacheEntity> cache, ImplRestConnection connection) {
		this.mCache = cache;
		this.mConnection = connection;
	}

	@Override
	public <T> Response<T> handleRequest(Request<T> request) {
		long requestStart = SystemClock.elapsedRealtime();
		T result = null;
		String url = request.url();

		// handle cache header
		CacheEntity cacheEntity = null;
		if (request.needCache())
			cacheEntity = mCache.get(request.getCacheKey());

		// handle response
		HttpResponse httpResponse = null;
		if (cacheEntity == null || cacheEntity.getLocalExpire() < System.currentTimeMillis()) {
			if (cacheEntity != null)
				handleCacheHeader(request, cacheEntity);
			httpResponse = mConnection.request(request);
		} else
			httpResponse = new HttpResponse(true, cacheEntity.getResponseHeaders(), cacheEntity.getData());

		Headers responseHeaders = httpResponse.responseHeaders;
		byte[] responseBody = httpResponse.responseBody;

		Response<T> returnResponse = null;

		if (httpResponse.isSucceed) {
			if (responseHeaders.getResponseCode() == 304) {
				// maybe server error responseCode
				if (cacheEntity == null)
					returnResponse = new RestResponser<T>(url, true, responseHeaders, responseBody, request.getTag(), null, SystemClock.elapsedRealtime() - requestStart);
				else {
					cacheEntity.getResponseHeaders().setAll(responseHeaders);
					Headers headers = cacheEntity.getResponseHeaders();
					byte[] body = cacheEntity.getData();
					result = request.parseResponse(url, headers, body);
					returnResponse = new RestResponser<T>(url, true, headers, body, request.getTag(), result, SystemClock.elapsedRealtime() - requestStart);
				}
			} else {
				if (responseBody == null) /* such as responseCode is 204 */
					responseBody = new byte[0];

				result = request.parseResponse(url, responseHeaders, responseBody);
				returnResponse = new RestResponser<T>(url, true, responseHeaders, responseBody, request.getTag(), result, SystemClock.elapsedRealtime() - requestStart);
			}
		} else
			returnResponse = new RestResponser<T>(url, false, null, null, request.getTag(), null, SystemClock.elapsedRealtime() - requestStart);
		if (request.needCache()) {
			if (cacheEntity == null)
				cacheEntity = HeaderParser.parseCacheHeaders(responseHeaders, responseBody);
			mCache.replace(request.getCacheKey(), cacheEntity);
		}
		return returnResponse;
	}

	private void handleCacheHeader(Request<?> request, CacheEntity cacheEntity) {
		if (cacheEntity == null) {
			request.removeHeader(Headers.HEAD_KEY_IF_NONE_MATCH);
			request.removeHeader(Headers.HEAD_KEY_IF_MODIFIED_SINCE);
		} else {
			Headers headers = cacheEntity.getResponseHeaders();
			String eTag = headers.getETag();
			if (eTag != null)
				request.setHeader(Headers.HEAD_KEY_IF_NONE_MATCH, eTag);

			long lastModified = headers.getLastModified();
			if (lastModified > 0)
				request.setHeader(Headers.HEAD_KEY_IF_MODIFIED_SINCE, HttpDateTime.formatToGTM(lastModified));
		}
	}
}
