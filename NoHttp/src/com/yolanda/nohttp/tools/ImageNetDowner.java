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
package com.yolanda.nohttp.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.yolanda.nohttp.Logger;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

/**
 * Created in Nov 4, 2015 3:10:58 PM
 * 
 * @author YOLANDA;
 */
public class ImageNetDowner {

	private static ImageNetDowner instance;
	private Poster mPoster;
	private ThreadPoolExecutor mPoolExecutor;
	/**
	 * Cache path
	 */
	private String mCachePath;

	private ImageNetDowner(Context context) {
		setCachePath(context.getCacheDir().toString());
		mPoster = new Poster();
		mPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * Singleton mode to create download object
	 */
	public synchronized static ImageNetDowner getInstance(Context context) {
		if (instance == null) {
			instance = new ImageNetDowner(context);
		}
		return instance;
	}

	/**
	 * Set cache path
	 */
	public void setCachePath(String cachePath) {
		if (TextUtils.isEmpty(cachePath))
			throw new NullPointerException("cachePath cann't null");
		this.mCachePath = cachePath;
		File file = new File(cachePath);
		if (file.exists() && file.isFile()) {
			file.delete();
		} else if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * download image
	 */
	public void downloadImage(String imageUrl, OnImageDownListener downListener, Object tag) {
		if (TextUtils.isEmpty(mCachePath) || mCachePath.trim().isEmpty())
			throw new NullPointerException("cache path cann't is null");
		StringBuffer buffer = new StringBuffer(mCachePath);
		buffer.append(File.separator);
		buffer.append(getMa5ForString(imageUrl));
		buffer.append(".png");
		downImage(imageUrl, downListener, buffer.toString(), tag);
	}

	/**
	 * Download the image to the specified path
	 */
	public void downImage(String imageUrl, OnImageDownListener downListener, String path, Object tag) {
		Logger.d("ImageDownload url: " + imageUrl);
		File file = new File(path);
		if (file.exists()) {
			ImageHolder holder = new ImageHolder();
			holder.imageUrl = imageUrl;
			holder.isSucceed = true;
			holder.imagePath = path;
			holder.downListener = downListener;
			holder.tag = tag;
			mPoster.obtainMessage(0, holder).sendToTarget();
		} else {
			mPoolExecutor.execute(new DownImageThread(imageUrl, path, downListener, tag));
		}
	}

	private class DownImageThread implements Runnable {

		private String mImageUrl;

		private String mImagePath;

		private Object tag;

		private OnImageDownListener mDownListener;

		public DownImageThread(String imageUrl, String imagePath, OnImageDownListener downListener, Object tag) {
			super();
			this.mImageUrl = imageUrl;
			this.mImagePath = imagePath;
			this.mDownListener = downListener;
			this.tag = tag;
		}

		@Override
		public void run() {
			ImageHolder holder = new ImageHolder();
			holder.imageUrl = mImageUrl;
			holder.downListener = mDownListener;
			holder.imagePath = mImagePath;
			holder.tag = tag;
			try {
				URL url = new URL(mImageUrl);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setConnectTimeout(5 * 1000);
				urlConnection.setReadTimeout(5 * 1000);
				urlConnection.connect();
				if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
					OutputStream outputStream = new FileOutputStream(mImagePath);
					InputStream inputStream = urlConnection.getInputStream();
					int len = -1;
					byte[] buffer = new byte[1024];
					while ((len = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, len);
					}
					inputStream.close();
					outputStream.flush();
					outputStream.close();
					holder.isSucceed = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mPoster.obtainMessage(0, holder).sendToTarget();
		}
	}

	private static class Poster extends Handler {
		Poster() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			ImageHolder holder = (ImageHolder) msg.obj;
			holder.poster();
		}
	}

	private String getMa5ForString(String content) {
		StringBuffer md5str = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] tempBytes = digest.digest(content.getBytes());
			int digital;
			for (int i = 0; i < tempBytes.length; i++) {
				digital = tempBytes[i];
				if (digital < 0) {
					digital += 256;
				}
				if (digital < 16) {
					md5str.append("0");
				}
				md5str.append(Integer.toHexString(digital));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5str.toString();
	}

	private class ImageHolder {
		String imageUrl;
		String imagePath;
		OnImageDownListener downListener;
		boolean isSucceed;
		Object tag;

		void poster() {
			downListener.onDownFinish(imageUrl, imagePath, isSucceed, tag);
		}
	}

	public interface OnImageDownListener {
		/**
		 * @param imageUrl Picture address
		 * @param path Picture save address
		 * @param isSucceed success ask for download
		 * @param tag Tag that are passed to download
		 */
		void onDownFinish(String imageUrl, String path, boolean isSucceed, Object tag);
	}
}
