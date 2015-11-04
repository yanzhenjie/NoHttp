/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;


/**
 * Created in Nov 4, 2015 3:07:56 PM
 * @author YOLANDA;
 */
public class ImageLocalReader {

	private static ImageLocalReader instance;

	private ImageLocalReader() {
	}

	public synchronized static ImageLocalReader getInstance() {
		if (instance == null)
			instance = new ImageLocalReader();
		return instance;
	}

	/**
	 * 高压缩率，读取图片，宽高越大，图片越清晰，越占内存
	 */
	public Bitmap readImage(String imagePath, int maxWidth, int maxHeight) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(imagePath)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		while (true) {
			if ((options.outWidth >> i <= maxWidth) && (options.outHeight >> i <= maxHeight)) {
				in = new BufferedInputStream(new FileInputStream(new File(imagePath)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				return BitmapFactory.decodeStream(in, null, options);
			}
			i += 1;
		}
	}

	/**
	 * 按照实际测量ImageView宽高读取图片，如果View还没有添加到布局中，会抛出异常
	 */
	public Bitmap readImage(ImageView imageView, String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		int[] viewSizes = new int[2];
		measureWidth(imageView, viewSizes);
		options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, viewSizes[0], viewSizes[1]);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
		return bitmap;
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 */
	public int calculateInSampleSize(int measureWidth, int measureHeight, int reqWidth, int reqHeight) {
		int inSampleSize = 1;
		if (measureWidth > reqWidth && measureHeight > reqHeight) {
			int widthRatio = Math.round((float) measureWidth / (float) reqWidth);
			int heightRatio = Math.round((float) measureHeight / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 */
	public void measureWidth(ImageView imageView, int[] viewSizes) {
		final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();
		// 测量宽
		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		// 测量高
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		viewSizes[0] = width;
		viewSizes[1] = height;
	}

}

