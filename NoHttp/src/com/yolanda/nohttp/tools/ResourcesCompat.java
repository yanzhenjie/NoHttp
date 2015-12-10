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
import android.widget.TextView;

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

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param leftDrawable target drawable
	 */
	public static void setLeftDrawable(TextView textView, Drawable leftDrawable) {
		setDrawableBounds(leftDrawable);
		textView.setCompoundDrawables(leftDrawable, null, null, null);
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param resId drawable in res
	 */
	public static void setLeftDrawable(TextView textView, int resId) {
		setLeftDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param topDrawable target drawable
	 */
	public static void setTopDrawable(TextView textView, Drawable topDrawable) {
		setDrawableBounds(topDrawable);
		textView.setCompoundDrawables(null, topDrawable, null, null);
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param resId drawable in res
	 */
	public static void setTopDrawable(TextView textView, int resId) {
		setTopDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param rightDrawable target drawable
	 */
	public static void setRightDrawable(TextView textView, Drawable rightDrawable) {
		setDrawableBounds(rightDrawable);
		textView.setCompoundDrawables(null, null, rightDrawable, null);
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param resId drawable in res
	 */
	public static void setRightDrawable(TextView textView, int resId) {
		setRightDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param bottomDrawable target drawable
	 */
	public static void setBottomDrawable(TextView textView, Drawable bottomDrawable) {
		setDrawableBounds(bottomDrawable);
		textView.setCompoundDrawables(null, null, bottomDrawable, null);
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param resId drawable in res
	 */
	public static void setBottomDrawable(TextView textView, int resId) {
		setBottomDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param leftmDrawable left target drawable
	 * @param topDrawable top target drawable
	 * @param rightDrawable right target drawable
	 * @param bottomDrawable bottom target drawable
	 */
	public static void setCompoundDrawables(TextView textView, Drawable leftmDrawable, Drawable topDrawable, Drawable rightDrawable, Drawable bottomDrawable) {
		setDrawableBounds(leftmDrawable);
		setDrawableBounds(topDrawable);
		setDrawableBounds(rightDrawable);
		setDrawableBounds(bottomDrawable);
		textView.setCompoundDrawables(leftmDrawable, topDrawable, rightDrawable, bottomDrawable);
	}

	/**
	 * set drawable for textview、button、checkbox
	 * 
	 * @param textView target textview
	 * @param resLeftId left target drawable
	 * @param resRightId top target drawable
	 * @param resTopId right target drawable
	 * @param resBottomId bottom target drawable
	 */
	public static void setCompoundDrawables(TextView textView, int resLeftId, int resRightId, int resTopId, int resBottomId) {
		Context context = textView.getContext();
		setCompoundDrawables(textView, getDrawable(context, resLeftId), getDrawable(context, resRightId), getDrawable(context, resTopId), getDrawable(context, resBottomId));
	}

	/**
	 * init drawable
	 */
	private static void setDrawableBounds(Drawable drawable) {
		if (drawable != null) {
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
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
