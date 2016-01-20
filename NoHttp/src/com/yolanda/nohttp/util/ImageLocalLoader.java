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
package com.yolanda.nohttp.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.yolanda.nohttp.Logger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created in Nov 4, 2015 3:07:29 PM
 * 
 * @author YOLANDA;
 */
public class ImageLocalLoader {

	/**
	 * Deposit in the province read images, width is high, the greater the picture clearer, but also the memory
	 * 
	 * @param imagePath Pictures in the path of the memory card
	 * @param maxWidth The highest limit value target width
	 * @param maxHeight The highest limit value target height
	 * @return
	 */
	public Bitmap readImage(String imagePath, int maxWidth, int maxHeight) {
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			try {
				BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(imageFile));
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(inputStream, null, options);
				inputStream.close();
				int i = 0;
				while (true) {
					if ((options.outWidth >> i <= maxWidth) && (options.outHeight >> i <= maxHeight)) {
						inputStream = new BufferedInputStream(new FileInputStream(new File(imagePath)));
						options.inSampleSize = (int) Math.pow(2.0, i);
						options.inJustDecodeBounds = false;
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
						inputStream.close();
						return bitmap;
					}
					i += 1;
				}
			} catch (IOException e) {
				Logger.e(e, imagePath);
			}
		}
		return null;
	}
}
