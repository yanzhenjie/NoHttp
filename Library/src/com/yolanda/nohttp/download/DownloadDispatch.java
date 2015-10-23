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
	private final BlockingQueue<DownloadRequest> mDownloadQueue;
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
	public DownloadDispatch(BlockingQueue<DownloadRequest> downloadQueue, Downloader downloader) {
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
			final DownloadRequest request;
			try {
				request = mDownloadQueue.take();
			} catch (InterruptedException e) {
				if (mQuit) {
					return;
				}
				continue;
			}

			if (request.isCanceled())
				continue;

			mDownloader.download(request, request.what(), new DownloadListener() {
				@Override
				public void onStart(int what) {
					getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_START, request, 0, null, null, null));
				}

				@Override
				public void onProgress(int what, int progress) {
					getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_PROGRESST, request, progress, null, null, null));
				}

				@Override
				public void onFinish(int what, String filePath) {
					getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_FINISH, request, 0, null, null, filePath));
				}

				@Override
				public void onDownloadError(int what, StatusCode statusCode, CharSequence errorMessage) {
					getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_ERROR, request, 0, statusCode, errorMessage, null));
				}

				@Override
				public void onCancel(int what) {
					getPosterHandler().post(new ThreadPoster(ThreadPoster.COMMAND_CANCEL, request, 0, null, null, null));
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
		public static final int COMMAND_PROGRESST = 1;
		public static final int COMMAND_ERROR = 2;
		public static final int COMMAND_FINISH = 3;
		public static final int COMMAND_CANCEL = 4;

		// start
		private final int command;
		private final DownloadRequest request;

		// progress
		private final int progress;

		// error
		private final StatusCode statusCode;
		private final CharSequence errorMessage;

		private final String filePath;

		public ThreadPoster(int command, DownloadRequest downloadRequest, int progress, StatusCode statusCode, CharSequence errorMessage, String filePath) {
			this.command = command;
			this.request = downloadRequest;
			this.progress = progress;
			this.statusCode = statusCode;
			this.errorMessage = errorMessage;
			this.filePath = filePath;
		}

		@Override
		public void run() {
			switch (command) {
			case COMMAND_START:
				request.onStart();
				break;
			case COMMAND_PROGRESST:
				request.onProgress(progress);
				break;
			case COMMAND_ERROR:
				request.onDownloadError(statusCode, errorMessage);
				break;
			case COMMAND_FINISH:
				request.onFinish(filePath);
				break;
			case COMMAND_CANCEL:
				request.onCancel();
				break;
			default:
				break;
			}
		}
	}
}
