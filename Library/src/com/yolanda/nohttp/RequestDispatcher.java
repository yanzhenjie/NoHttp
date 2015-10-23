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

import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

/**
 * Request queue polling thread</br>
 * Created in Oct 19, 2015 8:35:35 AM
 * 
 * @author YOLANDA
 */
public class RequestDispatcher extends Thread {
	/**
	 * Gets the lock for Handler to prevent the request result from confusing
	 */
	private static final Object HANDLER_LOCK = new Object();
	/**
	 * Poster of send request result
	 */
	private static Handler sRequestHandler;
	/**
	 * Request queue
	 */
	private final BlockingQueue<NetworkRequestor<?>> mRequestQueue;
	/**
	 * HTTP request actuator interface
	 */
	private final BasicConnectionRest mConnectionRest;
	/**
	 * Whether the current request queue polling thread is out of
	 */
	private volatile boolean mQuit = false;

	/**
	 * Create a request queue polling thread
	 * 
	 * @param reqeustQueue Request queue
	 * @param connectionRest Network request task actuator
	 */
	public RequestDispatcher(BlockingQueue<NetworkRequestor<?>> reqeustQueue, BasicConnectionRest connectionRest) {
		mRequestQueue = reqeustQueue;
		mConnectionRest = connectionRest;
	}

	/**
	 * Exit polling thread
	 */
	public void quit() {
		mQuit = true;
		interrupt();
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		while (true) {
			NetworkRequestor<?> request;
			try {
				request = mRequestQueue.take();
			} catch (InterruptedException e) {
				if (mQuit) {
					return;
				}
				continue;
			}

			if (request.request.isCanceled())
				continue;
			getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_START, request.what, request.responseListener, null));
			Response<?> response = mConnectionRest.request(request.request);
			getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_RESPONSE, request.what, request.responseListener, response));
		}
	}

	private Handler getPosterHandler() {
		synchronized (HANDLER_LOCK) {
			if (sRequestHandler == null)
				sRequestHandler = new Handler(Looper.getMainLooper());
		}
		return sRequestHandler;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public class ThreadPoster implements Runnable {

		public final static int COMMAND_START = 0;
		public final static int COMMAND_RESPONSE = 3;

		private final int command;
		private final int what;
		private final OnResponseListener responseListener;
		private final Response response;

		public ThreadPoster(int command, int what, OnResponseListener<?> responseListener, Response<?> response) {
			this.command = command;
			this.what = what;
			this.responseListener = responseListener;
			this.response = response;
		}

		@Override
		public void run() {
			if (responseListener != null) {
				if (command == COMMAND_START) {
					responseListener.onStart(what);
				} else if (command == COMMAND_RESPONSE && response != null) {
					responseListener.onFinish(what);
					if (response.isSucceed())
						responseListener.onSucceed(what, response);
					else
						responseListener.onFailed(what, response.url(), response.getTag(), response.getErrorMessage());
				}
			}
		}
	}
}
