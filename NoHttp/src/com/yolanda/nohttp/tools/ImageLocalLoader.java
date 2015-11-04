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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

/**
 * Created in Nov 4, 2015 3:07:29 PM
 * 
 * @author YOLANDA;
 */
public class ImageLocalLoader {

	/**
	 * 更新UI
	 */
	private static final int UPDATE_UI = 0x112;
	/**
	 * 默认灰色
	 */
	private final Drawable mDefaultDrawable = new ColorDrawable(Color.GRAY);
	/**
	 * 默认图片
	 */
	private int mDefauleResId = -1;

	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	/**
	 * 更新UI
	 */
	private Handler mPosterHandler;
	/**
	 * 单例
	 */
	private static ImageLocalLoader mInstance;

	/**
	 * 单例获得该实例对象
	 */
	public static ImageLocalLoader getInstance() {
		if (mInstance == null) {
			synchronized (ImageLocalLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLocalLoader(1);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 初始化
	 */
	private ImageLocalLoader(int threadCount) {
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPosterHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == UPDATE_UI) {
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.imagePath;
					if (path.equals(String.valueOf(imageView.getTag()))) {
						imageView.setImageBitmap(bm);
					}
				}
			}
		};

		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 8);
		mLruCache = new LruCache<String, Bitmap>(maxMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			};
		};
	}

	/**
	 * 设置读取图片时的默认图片
	 */
	public void setDefaultImage(int resId) {
		mDefauleResId = resId;
	}

	/**
	 * 加载图片
	 */
	public void loadImage(ImageView imageView, String imagePath) {
		loadImage(imageView, imagePath, 0, 0);
	}

	/**
	 * 根据指定宽高加载图片，宽高越大，图片越清晰，越占内存
	 */
	public void loadImage(ImageView imageView, String imagePath, int width, int height) {
		imageView.setTag(imagePath);
		Bitmap bitmap = getImageFromCache(imagePath + width + height);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (mDefauleResId != -1)
				imageView.setImageResource(mDefauleResId);
			else
				imageView.setImageDrawable(mDefaultDrawable);
			mThreadPool.execute(new TaskThread(imageView, imagePath, width, height));
		}
	}

	/**
	 * 从缓存读取图片
	 */
	private Bitmap getImageFromCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 * 添加图片到缓存
	 */
	private void addImageToCache(String key, Bitmap bitmap) {
		if (getImageFromCache(key) == null && bitmap != null)
			mLruCache.put(key, bitmap);
	}

	private class TaskThread implements Runnable {
		private ImageView mImageView;
		private String mImagePath;
		private int width;
		private int height;

		TaskThread(ImageView imageView, String imagePath, int width, int height) {
			this.mImagePath = imagePath;
			this.mImageView = imageView;
			this.width = width;
			this.height = height;
		}

		@Override
		public void run() {
			Bitmap bitmap = null;
			if (width != 0 && height != 0)
				try {
					bitmap = ImageLocalReader.getInstance().readImage(mImagePath, width, height);
				} catch (IOException e) {
					e.printStackTrace();
				}
			else
				bitmap = ImageLocalReader.getInstance().readImage(mImageView, mImagePath);
			addImageToCache(mImagePath + width + height, bitmap);
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = getImageFromCache(mImagePath + width + height);
			holder.imageView = mImageView;
			holder.imagePath = mImagePath;
			mPosterHandler.obtainMessage(UPDATE_UI, holder).sendToTarget();
		}
	};

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String imagePath;
	}
}
