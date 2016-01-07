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

import java.util.concurrent.LinkedBlockingQueue;

import com.yolanda.nohttp.cache.Cache;

/**
 * </br>
 * Created in Jan 5, 2016 4:47:35 PM
 * 
 * @author YOLANDA;
 */
public class ReadDiskDispather extends Thread {

	/**
	 * Used for telling us to die.
	 */
	private volatile boolean mQuit = false;

	/**
	 * Read disk cache queue
	 */
	private LinkedBlockingQueue<HttpRequest<?>> mReadDiskQueue = null;
	/**
	 * Save reuest task
	 */
	private LinkedBlockingQueue<HttpRequest<?>> mRequestQueue = null;

	private Cache mCache;

	public ReadDiskDispather(LinkedBlockingQueue<HttpRequest<?>> readDiskQueue, LinkedBlockingQueue<HttpRequest<?>> requestQueue, Cache cache) {
		this.mReadDiskQueue = readDiskQueue;
		this.mRequestQueue = requestQueue;
		this.mCache = cache;
	}

	public void quit() {
		mQuit = true;
		interrupt();
	}

	@Override
	public void run() {
		mCache.initialize();
		while (true) {
			try {
				final HttpRequest<?> request = mRequestQueue.take();

				if (request.request.isCanceled()) {
					continue;
				}

				Cache.Entrance entrance = mCache.get(request.request.getCacheKey());
				if (entrance == null) {
					mRequestQueue.put(request);
					continue;
				}

				// If it is completely expired, just send it to the network.
				if (entrance.isExpired()) {
					request.request.setCacheEntrance(entrance);
					mRequestQueue.put(request);
					continue;
				}

				Response<?> response = new RestResponser<T>(url, isSucceed, responseCode, headers, byteArray, tag, result, millis);

				if (!entrance.refreshNeeded()) {
					// Completely unexpired cache hit. Just deliver the response.
					mDelivery.postResponse(request, response);
				} else {
					// Soft-expired cache hit. We can deliver the cached response,
					// but we need to also send the request to the network for
					// refreshing.
					request.addMarker("cache-hit-refresh-needed");
					request.setCacheEntry(entrance);

					// Mark the response as intermediate.
					response.intermediate = true;

					// Post the intermediate response back to the user and have
					// the delivery then forward the request along to the network.
					mDelivery.postResponse(request, response, new Runnable() {
						@Override
						public void run() {
							try {
								mNetworkQueue.put(request);
							} catch (InterruptedException e) {
								// Not much we can do about this.
							}
						}
					});
				}

			} catch (InterruptedException e) {
				// We may have been interrupted because it was time to quit.
				if (mQuit) {
					return;
				}
				continue;
			}
		}
	}

}
