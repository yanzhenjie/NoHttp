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
package com.sample.nohttp.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created in Jan 30, 2016 9:18:18 PM
 * 
 * @author YOLANDA
 */
public class DisplayUtil {

	/** 屏幕宽度 **/
	public static int screenWidth;
	/** 屏幕高度 **/
	public static int screenHeight;
	/** 屏幕密度 **/
	public static int screenDpi;

	public static void initScreen(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);

		screenWidth = metric.widthPixels;
		screenHeight = metric.heightPixels;
		screenDpi = metric.densityDpi;
	}

	public static boolean isHorizontal() {
		return screenWidth > screenHeight;
	}

	public static int px2dip(Resources resources, float inParam) {
		float f = resources.getDisplayMetrics().density;
		return (int) (inParam / f + 0.5F);
	}

	public static int dip2px(Resources resources, float inParam) {
		float f = resources.getDisplayMetrics().density;
		return (int) (inParam * f + 0.5F);
	}

	public static int px2sp(Resources resources, float inParam) {
		float f = resources.getDisplayMetrics().scaledDensity;
		return (int) (inParam / f + 0.5F);
	}

	public static int sp2px(Resources resources, float inParam) {
		float f = resources.getDisplayMetrics().scaledDensity;
		return (int) (inParam * f + 0.5F);
	}

}
