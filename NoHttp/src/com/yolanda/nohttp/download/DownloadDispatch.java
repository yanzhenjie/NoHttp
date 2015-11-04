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
package com.yolanda.nohttp.download;

import java.util.concurrent.BlockingQueue;

import com.yolanda.nohttp.Headers;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

/**
 * Download queue polling thread</br>
 * Created in Oct 21, 2015 2:46:23 PM
 * 
 * @author YOLANDA
 */
public class DownloadDispatch extends Thread {

	/**
	 * Get handler lock
	 */
	private static final Object HANDLER_LOCK = new Object();

	/**
	 * Send download status
	 */
	private static Handler sDownloadHandler;
	/**
	 * Download task queue
	 */
	private final BlockingQueue<NetworkDownloadRequest> mDownloadQueue;
	/**
	 * Perform network request interface
	 */
	private final Downloader mDownloader;
	/**
	 * Are you out of this thread
	 */
	private volatile boolean mQuit = false;

	/**
	 * Create a thread that executes the download queue
	 * 
	 * @param downloadQueue Download queue to be polled
	 * @param downloader Perform network request interface
	 */
	public DownloadDispatch(BlockingQueue<NetworkDownloadRequest> downloadQueue, Downloader downloader) {
		mDownloadQueue = downloadQueue;
		mDownloader = downloader;
	}

	/**
	 * Quit this thread
	 */
	public void quit() {
		mQuit = true;
		interrupt();
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		while (true) {
			final NetworkDownloadRequest request;
			try {
				request = mDownloadQueue.take();
			} catch (InterruptedException e) {
				if (mQuit) {
					return;
				}
				continue;
			}

			if (request.downloadRequest.isCanceled())
				continue;

			final ThreadPoster threadPoster = new ThreadPoster(request.what, request.downloadListener);

			mDownloader.download(request.what, request.downloadRequest, new DownloadListener() {

				@Override
				public void onStart(int what, Headers headers, int allCount) {
					threadPoster.onStart(headers, allCount);
					getPosterHandler().post(threadPoster);
				}

				@Override
				public void onDownloadError(int what, StatusCode statusCode, CharSequence errorMessage) {
					threadPoster.onError(statusCode, errorMessage);
					getPosterHandler().post(threadPoster);
				}

				@Override
				public void onProgress(int what, int progress) {
					threadPoster.onProgress(progress);
					getPosterHandler().post(threadPoster);
				}

				@Override
				public void onFinish(int what, String filePath) {
					threadPoster.onFinish(filePath);
					getPosterHandler().post(threadPoster);
				}

				@Override
				public void onCancel(int what) {
					threadPoster.onCancel();
					getPosterHandler().post(threadPoster);
				}
			});
		}
	}

	private Handler getPosterHandler() {
		synchronized (HANDLER_LOCK) {
			if (sDownloadHandler == null)
				sDownloadHandler = new Handler(Looper.getMainLooper());
		}
		return sDownloadHandler;
	}

	public class ThreadPoster implements Runnable {

		public static final int COMMAND_START = 0;
		public static final int COMMAND_PROGRESS = 1;
		public static final int COMMAND_ERROR = 2;
		public static final int COMMAND_FINISH = 3;
		public static final int COMMAND_CANCEL = 4;

		private final int what;
		private final DownloadListener downloadListener;

		// command
		private int command;

		// start
		private Headers responseHeaders;
		private int allCount;

		// progress
		private int progress;

		// error
		private StatusCode statusCode;
		private CharSequence errorMessage;

		// finish
		private String filePath;

		public ThreadPoster(int what, DownloadListener downloadListener) {
			this.what = what;
			this.downloadListener = downloadListener;
		}

		public void onStart(Headers responseHeaders, int allCount) {
			this.command = COMMAND_START;
			this.responseHeaders = responseHeaders;
		}

		public void onProgress(int progress) {
			this.command = COMMAND_PROGRESS;
			this.progress = progress;
		}

		public void onError(StatusCode statusCode, CharSequence errorMessage) {
			this.command = COMMAND_ERROR;
			this.statusCode = statusCode;
			this.errorMessage = errorMessage;
		}

		public void onCancel() {
			this.command = COMMAND_CANCEL;
		}

		public void onFinish(String filePath) {
			this.command = COMMAND_FINISH;
			this.filePath = filePath;
		}

		@Override
		public synchronized void run() {
			switch (command) {
			case COMMAND_START:
				downloadListener.onStart(what, responseHeaders, allCount);
				break;
			case COMMAND_PROGRESS:
				downloadListener.onProgress(what, progress);
				break;
			case COMMAND_ERROR:
				downloadListener.onDownloadError(what, statusCode, errorMessage);
				break;
			case COMMAND_FINISH:
				downloadListener.onFinish(what, filePath);
				break;
			case COMMAND_CANCEL:
				downloadListener.onCancel(what);
				break;
			default:
				break;
			}
		}
	}
}
