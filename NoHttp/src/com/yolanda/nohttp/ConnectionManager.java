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
import com.yolanda.nohttp.tools.HttpDateTime;

import android.os.SystemClock;

/**
 * </br>
 * Created in Jan 6, 2016 5:45:19 PM
 * 
 * @author YOLANDA;
 */
public class ConnectionManager implements BasicConnectionManager {

	private BasicConnectionRest mConnectionRest;

	private Cache mCache;

	public ConnectionManager(Cache cache, BasicConnectionRest connectionRest) {
		this.mCache = cache;
		this.mConnectionRest = connectionRest;
	}

	@Override
	public <T> Response<T> handleRequest(CommonRequest<T> request) {
		long requestStart = SystemClock.elapsedRealtime();
		T result = null;
		String url = request.url();

		// handle cache header
		Cache.Entrance entrance = mCache.get(request.getCacheKey());
		handleCacheHeader(request, entrance);

		// request network
		final HttpResponse httpResponse = mConnectionRest.request(request);

		Headers responseHeaders = httpResponse.responseHeaders;
		byte[] responseBody = httpResponse.responseBody;

		if (httpResponse.isSucceed) {
			if (httpResponse.responseCode == 304) {
				if (entrance == null) { // maybe server error responseCode
					return new RestResponser<T>(url, true, 304, responseHeaders, responseBody, request.getTag(), null, SystemClock.elapsedRealtime() - requestStart);
				} else {
					entrance.responseHeaders.setAll(responseHeaders);
					result = request.parseResponse(url, entrance.responseHeaders, entrance.data);
					return new RestResponser<T>(url, true, 304, entrance.responseHeaders, entrance.data, request.getTag(), result, SystemClock.elapsedRealtime() - requestStart);
				}
			}

			if (responseBody == null) /* such as responseCode is 204 */
				responseBody = new byte[0];

			result = request.parseResponse(url, responseHeaders, responseBody);
			return new RestResponser<T>(url, true, httpResponse.responseCode, responseHeaders, responseBody, request.getTag(), result, SystemClock.elapsedRealtime() - requestStart);
		} else {
			return new RestResponser<T>(url, false, httpResponse.responseCode, responseHeaders, responseBody, request.getTag(), null, SystemClock.elapsedRealtime() - requestStart);
		}
	}

	private void handleCacheHeader(CommonRequest<?> request, Cache.Entrance entry) {
		if (entry == null) {
			request.removeHeader(Headers.HEAD_KEY_IF_NONE_MATCH);
			request.removeHeader(Headers.HEAD_KEY_IF_MODIFIED_SINCE);
		} else {
			if (entry.etag != null)
				request.setHeader(Headers.HEAD_KEY_IF_NONE_MATCH, entry.etag);

			if (entry.lastModified > 0) {
				request.setHeader(Headers.HEAD_KEY_IF_MODIFIED_SINCE, HttpDateTime.parseToGTM(entry.lastModified));
			}
		}
	}

}
