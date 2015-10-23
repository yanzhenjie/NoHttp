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

/**
 * Requet Queue</br>
 * Created in Oct 19, 2015 8:36:22 AM
 * 
 * @author YOLANDA
 */
public class RequestQueue {
	/**
	 * Save reuest task
	 */
	private final LinkedBlockingQueue<NetworkRequestor<?>> mRequestQueue = new LinkedBlockingQueue<>();
	/**
	 * HTTP request actuator interface
	 */
	private final BasicConnectionRest mConnectionRest;

	/**
	 * Request queue polling thread array
	 */
	private RequestDispatcher[] mDispatchers;

	/**
	 * Create request queue manager
	 * 
	 * @param httpStack Download the network task execution interface, where you need to implement the download tasks that have been implemented.
	 * @param threadPoolSize Number of thread pool
	 */
	public RequestQueue(BasicConnectionRest httpStack, int threadPoolSize) {
		mConnectionRest = httpStack;
		mDispatchers = new RequestDispatcher[threadPoolSize];
	}

	/**
	 * Start polling the request queue, a one of the implementation of the download task, if you have started to poll the download queue, then it will stop all the threads, to re create thread
	 * execution
	 */
	public void start() {
		stop();
		for (int i = 0; i < mDispatchers.length; i++) {
			RequestDispatcher networkDispatcher = new RequestDispatcher(mRequestQueue, mConnectionRest);
			mDispatchers[i] = networkDispatcher;
			networkDispatcher.start();
		}
	}

	/**
	 * Add a request task to download queue, waiting for execution, if there is no task in the queue or the number of tasks is less than the number of thread pool, will be executed immediately
	 */
	public <T> void add(int what, Request<T> request, OnResponseListener<T> responseListener) {
		mRequestQueue.add(new NetworkRequestor<T>(what, request, responseListener));
	}

	/**
	 * Polling the queue will not be executed, and this will not be canceled.
	 */
	public void stop() {
		for (int i = 0; i < mDispatchers.length; i++) {
			if (mDispatchers[i] != null)
				mDispatchers[i].quit();
		}
	}

	/**
	 * All requests for the sign specified in the queue, if you are executing, will interrupt the download task
	 * 
	 * @param sign This sign will be the same as sign's DownloadRequest, and if it is the same, then cancel the task.
	 */
	public void cancelAll(Object sign) {
		synchronized (mRequestQueue) {
			for (NetworkRequestor<?> request : mRequestQueue)
				request.request.cancelBySign(sign);
		}
	}
}
