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
package com.yolanda.multiasynctask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 
 * Created in Aug 3, 2015 11:06:14 AM
 * 
 * @author YOLANDA
 * @param <Param> Execution parameters
 * @param <Update> Update parameters
 * @param <Result> Result parameter
 */
@SuppressWarnings("unchecked")
public abstract class MultiAsynctask<Param, Update, Result> {
	/**
	 * Mark update implementation process
	 */
	static final int MULTI_ASYNCTASK_UDPATE = 0x001;
	/**
	 * Mark sent execution results
	 */
	static final int MULTI_ASYNCTASK_RESULT = 0x002;
	/**
	 * To deal with the main thread and message between the child thread
	 */
	private static HandlerPoster sHandlerPoster;
	/**
	 * Used to perform asynchronous tasks, if you want to modify the number of concurrent tasks, update the count
	 */
	private static ThreadPoolExecutor MAIN_THREAD_POOL_EXECUTOR;

	public MultiAsynctask() {
		this(5);
	}

	public MultiAsynctask(int count) {
		MAIN_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(count, count, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * Use the single model, produce a handler, to forward the message
	 * 
	 * @return
	 */
	private final HandlerPoster getPoster() {
		synchronized (MultiAsynctask.class) {
			if (sHandlerPoster == null) {
				sHandlerPoster = new HandlerPoster();
			}
			return sHandlerPoster;
		}
	}

	/**
	 * Call this method will trigger the {@link #onTask(Object...)} method, you either from the son of the thread or the
	 * main thread to call this method, The {@link #onTask(Object...)} method is the son thread execution
	 *
	 * 
	 * @param params To pass the parameters of the thread of the son, you will receive it in the
	 *        {@link #onTask(Object...)} method.
	 */
	public final void execute(Param... params) {
		onPrepare();
		TaskExecutor taskExecutor = new TaskExecutor(this, params);
		MAIN_THREAD_POOL_EXECUTOR.execute(taskExecutor);
	}

	/**
	 * Convenience version of {@link #execute(Object...)} for use with
	 * a simple Runnable object. See {@link #execute(Object[])} for more
	 * information on the order of execution.
	 *
	 * @see #execute(Object[])
	 */
	public final void execute(Runnable runnable) {
		MAIN_THREAD_POOL_EXECUTOR.execute(runnable);
	}

	/**
	 * Before executing the child thread is called, you can now some <code>Dialog</code>
	 */
	public void onPrepare() {
	}

	/**
	 * When you call the {@link #execute(Object...)} method to trigger the method.
	 * This method is executed in the son thread
	 * 
	 * @param params You call the {@link #execute(Object...)} method when the parameters are introduced.
	 * @return
	 */
	public abstract Result onTask(Param... params);

	/**
	 * Pearson my class to override this method, you can receive a value of {@link #postUpdate(Object)} update.<br/>
	 * This method is executed in the main thread
	 * 
	 * @param update You send out the update values from the {@link #postReult(Object)} method.
	 */
	public void onUpdate(Update update) {
	}

	/**
	 * Pearson my class to override this method, you can receive a value of {@link #onTask(Object...)} return.</br>
	 * This method is executed in the main thread
	 * 
	 * @param result You return the value from the {@link #onTask(Object...)} method.
	 */
	public void onResult(Result result) {
	}

	/**
	 * To submit your updated values, you can receive it from {@link #onUpdate(Object)} method
	 * 
	 * @param update Specific update value
	 */
	public final synchronized void postUpdate(Update update) {
		Message message = getPoster().obtainMessage(MULTI_ASYNCTASK_UDPATE,
				new Messenger<Param, Update, Result>(this, update, null));
		message.sendToTarget();
	}

	/**
	 * To deal with the main thread and message between the child thread
	 * Created in Aug 3, 2015 2:13:01 PM
	 * 
	 * @author YOLANDA
	 */
	private static class HandlerPoster extends Handler {
		public HandlerPoster() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			Messenger<?, ?, ?> result = (Messenger<?, ?, ?>) msg.obj;
			switch (msg.what) {
			case MultiAsynctask.MULTI_ASYNCTASK_UDPATE:
				result.onUpdate();
				break;
			case MultiAsynctask.MULTI_ASYNCTASK_RESULT:
				result.onResult();
				break;
			}
		}
	}

	/**
	 * This bean Java can easily callback the main thread, used to send update messages and Zi Xiancheng implementation
	 * results
	 * 
	 * @author YOLANDA
	 * @param <Param> Execution parameters
	 * @param <Update> Update parameters
	 * @param <Result> Result parameter
	 */
	private static class Messenger<Param, Update, Result> {

		private MultiAsynctask<Param, Update, Result> mAsynctask;

		private Update mUpdate;

		private Result mResult;

		public Messenger(MultiAsynctask<Param, Update, Result> asynctask, Update update, Result result) {
			this.mAsynctask = asynctask;
			this.mUpdate = update;
			this.mResult = result;
		}

		public void onUpdate() {
			this.mAsynctask.onUpdate(mUpdate);
		}

		public void onResult() {
			this.mAsynctask.onResult(mResult);
		}

	}

	/**
	 * Task thread class
	 * Created in Aug 3, 2015 2:00:01 PM
	 * 
	 * @author YOLANDA
	 */
	private class TaskExecutor implements Runnable {

		/**
		 * This thread to run the parameters
		 */
		private Param[] mParams;

		/**
		 * Used to perform tasks
		 */
		private MultiAsynctask<Param, Update, Result> mTask;

		/**
		 * Create a new thread to perform tasks, if the task of the line is more than the number of settings, the thread
		 * will wait until the thread pool thread number is less than the number of settings, this task is arranged in
		 * the thread pool
		 * 
		 * @param mTask Asynchronous task entity, which is used to allow the child thread callback to execute the method
		 * @param params The parameters through the {@link MultiAsynctask#execute(Object...)} method to send,
		 *        will be from the thread, and the callback method to {@link MultiAsynctask#onTask(Object...)}
		 */
		public TaskExecutor(MultiAsynctask<Param, Update, Result> mTask, Param... params) {
			super();
			this.mTask = mTask;
			this.mParams = params;
		}

		@Override
		public void run() {
			postReult(mTask.onTask(mParams));
		}

		/**
		 * Send child thread execution results
		 */
		private void postReult(Result result) {
			Message message = getPoster().obtainMessage(MULTI_ASYNCTASK_RESULT,
					new Messenger<Param, Update, Result>(mTask, null, result));
			message.sendToTarget();
		}
	}

}