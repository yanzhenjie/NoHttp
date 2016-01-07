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

import java.util.Date;
import java.util.Map;

import com.yolanda.nohttp.cache.Cache;

import android.os.SystemClock;
import android.text.format.DateUtils;

/**
 * </br>
 * Created in Jan 6, 2016 5:45:19 PM
 * 
 * @author YOLANDA;
 */
public class ConnectionManager<T> implements BasicConnectionManager<T> {

	private BasicConnectionRest mConnectionRest;

	public ConnectionManager(BasicConnectionRest basicConnectionRest) {
		this.mConnectionRest = basicConnectionRest;
	}

	@Override
	public Response<T> handleRequest(CommonRequest<T> request) {
		long requestStart = SystemClock.elapsedRealtime();
		T result = null;
		String url = request.url();

		// request network
		final HttpResponse httpResponse = mConnectionRest.request(request);

		if (httpResponse.isSucceed) {
			if (httpResponse.responseCode == 304) {
				Cache.Entrance entrance = request.getCacheEntrance();
				if (entrance == null) {
					return new RestResponser<T>(url, true, 304, httpResponse.responseHeaders, null, request.getTag(), null, SystemClock.elapsedRealtime() - requestStart);
				} else {
					entrance.responseHeaders.setAll(httpResponse.responseHeaders);
					result = request.parseResponse(url, entrance.responseHeaders, entrance.data);
					return new RestResponser<T>(url, true, 304, entrance.responseHeaders, entrance.data, request.getTag(), result, SystemClock.elapsedRealtime() - requestStart);
				}
			}

			byte[] responseBody = httpResponse.responseBody;
			Headers responseHeaders = httpResponse.responseHeaders;

			if (responseBody == null) /* such as responseCode is 204 */
				responseBody = new byte[0];

			result = request.parseResponse(url, responseHeaders, responseBody);
			return new RestResponser<T>(url, true, httpResponse.responseCode, responseHeaders, responseBody, request.getTag(), result, SystemClock.elapsedRealtime() - requestStart);
		}
		return null;
	}

	private void addCacheHeaders(Map<String, String> headers, Cache.Entrance entry) {
		// If there's no cache entry, we're done.
		if (entry == null) {
			return;
		}

		if (entry.etag != null)
			headers.put(Headers.HEAD_KEY_IF_NONE_MATCH, entry.etag);

		if (entry.lastModified > 0) {
			Date refTime = new Date(entry.lastModified);
			headers.put(Headers.HEAD_KEY_IF_MODIFIED_SINCE, DateUtils.formatDate(refTime));
		}
	}

}
