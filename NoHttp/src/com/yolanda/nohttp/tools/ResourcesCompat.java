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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;

/**
 * </br>
 * Created in Nov 27, 2015 6:20:48 PM
 * 
 * @author YOLANDA;
 */
public class ResourcesCompat {

	public static Drawable getDrawable(Context context, int resId) {
		return getDrawable(context.getResources(), resId);
	}

	public static Drawable getDrawable(Context context, int resId, Theme theme) {
		return getDrawable(context.getResources(), resId, theme);
	}

	public static Drawable getDrawable(Resources resources, int resId) {
		return getDrawable(resources, resId, null);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Drawable getDrawable(Resources resources, int resId, Theme theme) {
		if (VERSION.SDK_INT > 20) {
			return resources.getDrawable(resId, theme);// heigh than leve21
		} else {
			return resources.getDrawable(resId);// small than leve21
		}
	}

	/* =====Color===== */

	public static int getColor(Context context, int resId) {
		return getColor(context.getResources(), resId);
	}

	public static int getColor(Context context, int resId, Theme theme) {
		return getColor(context.getResources(), resId, theme);
	}

	public static int getColor(Resources resources, int resId) {
		return getColor(resources, resId, null);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int getColor(Resources resources, int resId, Theme theme) {
		if (VERSION.SDK_INT > 22) {
			return resources.getColor(resId, theme);// heigh than leve21
		} else {
			return resources.getColor(resId);// small than leve21
		}
	}

}
