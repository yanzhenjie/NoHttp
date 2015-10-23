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

import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * Image request parameter</br>
 * Created in Oct 17, 2015 12:17:57 PM
 * 
 * @author YOLANDA
 */
public class ImageRequest extends RestRequestor<Bitmap> {

	private final int mMaxWidth;
	private final int mMaxHeight;
	private final Config mDecodeConfig;
	private ScaleType mScaleType;

	/** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
	private static final Object DECODE_LOCK = new Object();

	public ImageRequest(String url, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, ImageView.ScaleType scaleType) {
		super(url, RequestMethod.GET);
		this.mMaxWidth = maxWidth;
		this.mMaxHeight = maxHeight;
		this.mDecodeConfig = decodeConfig;
		this.mScaleType = scaleType;
	}

	@Override
	public Bitmap parseResponse(String url, String contentType, byte[] byteArray) {
		synchronized (DECODE_LOCK) {
			Bitmap bitmap = null;
			if (byteArray != null) {
				try {
					bitmap = doResponse(byteArray);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format(Locale.getDefault(), "Caught OOM for %d byte image, url=%s", byteArray.length, url);
					Logger.wtf(e, errorMessage);
				}
			}
			return bitmap;
		}
	}

	/**
	 * The real guts of AnalyzeResponse. Broken out for readability.
	 */
	private Bitmap doResponse(byte[] byteArray) throws OutOfMemoryError {
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		Bitmap bitmap = null;
		if (mMaxWidth == 0 && mMaxHeight == 0) {
			decodeOptions.inPreferredConfig = mDecodeConfig;
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, decodeOptions);
		} else {
			// If we have to resize this image, first get the natural bounds.
			decodeOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, decodeOptions);
			int actualWidth = decodeOptions.outWidth;
			int actualHeight = decodeOptions.outHeight;

			// Then compute the dimensions we would ideally like to decode to.
			int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight, actualWidth, actualHeight, mScaleType);
			int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth, actualHeight, actualWidth, mScaleType);

			// Decode to the nearest power of two scaling factor.
			decodeOptions.inJustDecodeBounds = false;
			// TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
			// decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
			decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
			Bitmap tempBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, decodeOptions);

			// If necessary, scale down to the maximal acceptable size.
			if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
				bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
				tempBitmap.recycle();
			} else {
				bitmap = tempBitmap;
			}
		}
		return bitmap;
	}

	/**
	 * Scales one side of a rectangle to fit aspect ratio.
	 *
	 * @param maxPrimary Maximum size of the primary dimension (i.e. width for
	 *        max width), or zero to maintain aspect ratio with secondary
	 *        dimension
	 * @param maxSecondary Maximum size of the secondary dimension, or zero to
	 *        maintain aspect ratio with primary dimension
	 * @param actualPrimary Actual size of the primary dimension
	 * @param actualSecondary Actual size of the secondary dimension
	 * @param scaleType The ScaleType used to calculate the needed image size.
	 */
	private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary, ScaleType scaleType) {

		// If no dominant value at all, just return the actual.
		if ((maxPrimary == 0) && (maxSecondary == 0)) {
			return actualPrimary;
		}

		// If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
		if (scaleType == ScaleType.FIT_XY) {
			if (maxPrimary == 0) {
				return actualPrimary;
			}
			return maxPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;

		// If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
		if (scaleType == ImageView.ScaleType.CENTER_CROP) {
			if ((resized * ratio) < maxSecondary) {
				resized = (int) (maxSecondary / ratio);
			}
			return resized;
		}

		if ((resized * ratio) > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	/**
	 * Returns the largest power-of-two divisor for use in downscaling a bitmap
	 * that will not result in the scaling past the desired dimensions.
	 *
	 * @param actualWidth Actual width of the bitmap
	 * @param actualHeight Actual height of the bitmap
	 * @param desiredWidth Desired width of the bitmap
	 * @param desiredHeight Desired height of the bitmap
	 */
	// Visible for testing.
	public static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}
		return (int) n;
	}

}
