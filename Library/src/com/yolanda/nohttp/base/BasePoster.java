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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created in Jul 31, 2015 10:41:48 AM
 * 
 * @author YOLANDA
 */
public abstract class BasePoster implements Runnable {

	/**
	 * Used for sending the Message
	 */
	private static HanderPoster HANDER_POSTER;
	/**
	 * The thread pool for the Http request queue
	 */
	private static ExecutorService EXECUTOR_SERVICE;

	/**
	 * Build Poster of execution thread and send the results
	 */
	public static void buildPoster(int count) {
		if (HANDER_POSTER == null || EXECUTOR_SERVICE == null) {
			HANDER_POSTER = new HanderPoster(Looper.getMainLooper());
			EXECUTOR_SERVICE = new ThreadPoolExecutor(count, count, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>());
		}
	}

	/**
	 * Executes the given command at some time in the future. The command
	 * may execute in a new thread, in a pooled thread, or in the calling
	 * thread, at the discretion of the {@code Executor} implementation.
	 *
	 * @param runnable the runnable task
	 */
	protected static void excute(Runnable runnable) {
		EXECUTOR_SERVICE.execute(runnable);
	}

	/**
	 * Pushes a message onto the end of the message queue after all pending messages
	 * before the current time.
	 */
	protected static void postMessenger(BaseMessenger messenger) {
		synchronized (HANDER_POSTER) {
			HANDER_POSTER.sendMessageAtTime(messenger.obtain(), SystemClock.uptimeMillis());
		}
	}

	/**
	 * Cancel all of the threads
	 */
	public static void cancelAll() {
		EXECUTOR_SERVICE.shutdown();
	}

	/**
	 * Perform their tasks
	 */
	public void execute() {
		excute(this);
	}

	private static class HanderPoster extends Handler {
		public HanderPoster(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			BaseMessenger baseMessenger = (BaseMessenger) msg.obj;
			baseMessenger.callback();
		}
	}

}
