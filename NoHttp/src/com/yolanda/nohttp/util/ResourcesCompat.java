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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
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
		if (VERSION.SDK_INT > 20)
			return resources.getDrawable(resId, theme);// heigh than leve21
		else
			return resources.getDrawable(resId);// small than leve21
	}

	public static void setLeftDrawable(TextView textView, Drawable leftDrawable) {
		setDrawableBounds(leftDrawable);
		textView.setCompoundDrawables(leftDrawable, null, null, null);
	}

	public static void setLeftDrawable(TextView textView, int resId) {
		setLeftDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	public static void setTopDrawable(TextView textView, Drawable topDrawable) {
		setDrawableBounds(topDrawable);
		textView.setCompoundDrawables(null, topDrawable, null, null);
	}

	public static void setTopDrawable(TextView textView, int resId) {
		setTopDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	public static void setRightDrawable(TextView textView, Drawable rightDrawable) {
		setDrawableBounds(rightDrawable);
		textView.setCompoundDrawables(null, null, rightDrawable, null);
	}

	public static void setRightDrawable(TextView textView, int resId) {
		setRightDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	public static void setBottomDrawable(TextView textView, Drawable bottomDrawable) {
		setDrawableBounds(bottomDrawable);
		textView.setCompoundDrawables(null, null, bottomDrawable, null);
	}

	public static void setBottomDrawable(TextView textView, int resId) {
		setBottomDrawable(textView, getDrawable(textView.getContext(), resId));
	}

	public static void setCompoundDrawables(TextView textView, Drawable leftmDrawable, Drawable topDrawable, Drawable rightDrawable, Drawable bottomDrawable) {
		setDrawableBounds(leftmDrawable);
		setDrawableBounds(topDrawable);
		setDrawableBounds(rightDrawable);
		setDrawableBounds(bottomDrawable);
		textView.setCompoundDrawables(leftmDrawable, topDrawable, rightDrawable, bottomDrawable);
	}

	public static void setCompoundDrawables(TextView textView, int resLeftId, int resRightId, int resTopId, int resBottomId) {
		Context context = textView.getContext();
		setCompoundDrawables(textView, getDrawable(context, resLeftId), getDrawable(context, resRightId), getDrawable(context, resTopId), getDrawable(context, resBottomId));
	}

	public static void setDrawableBounds(Drawable drawable) {
		if (drawable != null)
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	}

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
		if (VERSION.SDK_INT > 22)
			return resources.getColor(resId, theme);// heigh than leve21
		else
			return resources.getColor(resId);// small than leve21
	}

	public static ColorStateList getColorStateList(Context context, int resId) {
		return getColorStateList(context.getResources(), resId);
	}

	public static ColorStateList getColorStateList(Context context, int resId, Theme theme) {
		return getColorStateList(context.getResources(), resId, theme);
	}

	public static ColorStateList getColorStateList(Resources resources, int resId) {
		return getColorStateList(resources, resId, null);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static ColorStateList getColorStateList(Resources resources, int resId, Theme theme) {
		if (VERSION.SDK_INT > 22)
			return resources.getColorStateList(resId, theme);// heigh than leve21
		else
			return resources.getColorStateList(resId);// small than leve21

	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setBackground(View view, Drawable background) {
		if (VERSION.SDK_INT > 15)
			view.setBackground(background);
		else
			view.setBackgroundDrawable(background);
	}

	public static SpannableString getScaleText(String content, int start, int end, int px) {
		SpannableString stringSpan = new SpannableString(content);
		stringSpan.setSpan(new AbsoluteSizeSpan(px), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return stringSpan;
	}

	public static SpannableString getColotText(String content, String colorText, int color) {
		SpannableString stringSpan = new SpannableString(content);
		int index = content.indexOf(colorText);
		if (index != -1)
			stringSpan.setSpan(new ForegroundColorSpan(color), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return stringSpan;
	}

	public static SpannableString getColotText(String content, int start, int end, int color) {
		SpannableString stringSpan = new SpannableString(content);
		stringSpan.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return stringSpan;
	}

	public static SpannableString getDeleteText(String content) {
		return getDeleteText(content, 0, content.length());
	}

	public static SpannableString getDeleteText(String content, int start, int end) {
		SpannableString stringSpan = new SpannableString(content);
		stringSpan.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return stringSpan;
	}

	public static SpannableString getImageSpanText(String content, Drawable drawable, int start, int end) {
		SpannableString stringSpan = new SpannableString(content);
		setDrawableBounds(drawable);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		stringSpan.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return stringSpan;
	}
}
