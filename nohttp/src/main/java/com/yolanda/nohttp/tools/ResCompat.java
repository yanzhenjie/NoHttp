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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;

import com.yolanda.nohttp.NoHttp;

/**
 * Created in Nov 27, 2015 6:20:48 PM.
 *
 * @author YOLANDA;
 */
public class ResCompat {

    public static Drawable getDrawable(int resId) {
        return getDrawable(resId, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(int resId, Theme theme) {
        Resources resources = NoHttp.getContext().getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return resources.getDrawable(resId, theme);
        else
            return resources.getDrawable(resId);
    }

    public static void setLeftDrawable(TextView textView, Drawable leftDrawable) {
        setDrawableBounds(leftDrawable);
        Drawable top = textView.getCompoundDrawables()[1];
        Drawable right = textView.getCompoundDrawables()[2];
        Drawable bottom = textView.getCompoundDrawables()[3];
        textView.setCompoundDrawables(leftDrawable, top, right, bottom);
    }

    public static void setLeftDrawable(TextView textView, int resId) {
        setLeftDrawable(textView, getDrawable(resId));
    }

    public static void setTopDrawable(TextView textView, Drawable topDrawable) {
        setDrawableBounds(topDrawable);
        Drawable left = textView.getCompoundDrawables()[0];
        Drawable right = textView.getCompoundDrawables()[2];
        Drawable bottom = textView.getCompoundDrawables()[3];
        textView.setCompoundDrawables(left, topDrawable, right, bottom);
    }

    public static void setTopDrawable(TextView textView, int resId) {
        setTopDrawable(textView, getDrawable(resId));
    }

    public static void setRightDrawable(TextView textView, Drawable rightDrawable) {
        setDrawableBounds(rightDrawable);
        Drawable left = textView.getCompoundDrawables()[0];
        Drawable top = textView.getCompoundDrawables()[1];
        Drawable bottom = textView.getCompoundDrawables()[3];
        textView.setCompoundDrawables(left, top, rightDrawable, bottom);
    }

    public static void setRightDrawable(TextView textView, int resId) {
        setRightDrawable(textView, getDrawable(resId));
    }

    public static void setBottomDrawable(TextView textView, Drawable bottomDrawable) {
        setDrawableBounds(bottomDrawable);
        Drawable left = textView.getCompoundDrawables()[0];
        Drawable top = textView.getCompoundDrawables()[1];
        Drawable bottom = textView.getCompoundDrawables()[2];
        textView.setCompoundDrawables(left, top, bottom, bottomDrawable);
    }

    public static void setBottomDrawable(TextView textView, int resId) {
        setBottomDrawable(textView, getDrawable(resId));
    }

    public static void setCompoundDrawables(TextView textView, Drawable leftDrawable, Drawable topDrawable, Drawable rightDrawable, Drawable bottoDrawable) {
        setDrawableBounds(leftDrawable);
        setDrawableBounds(topDrawable);
        setDrawableBounds(rightDrawable);
        setDrawableBounds(bottoDrawable);
        textView.setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottoDrawable);
    }

    public static void setCompoundDrawables(TextView textView, int resLeftId, int resRightId, int resTopId, int resBottomId) {
        setCompoundDrawables(textView, getDrawable(resLeftId), getDrawable(resRightId), getDrawable(resTopId), getDrawable(resBottomId));
    }

    public static void setDrawableBounds(Drawable drawable) {
        if (drawable != null)
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }

    public static int getColor(int resId) {
        return getColor(resId, null);
    }

    public static int getColor(int resId, Theme theme) {
        return getColor(NoHttp.getContext().getResources(), resId, theme);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    public static int getColor(Resources resources, int resId, Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return resources.getColor(resId, theme);
        else
            return resources.getColor(resId);
    }

    public static ColorStateList getColorStateList(int resId) {
        return getColorStateList(resId, null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    public static ColorStateList getColorStateList(int resId, Theme theme) {
        Resources resources = NoHttp.getContext().getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return resources.getColorStateList(resId, theme);
        else
            return resources.getColorStateList(resId);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(background);
        else
            view.setBackgroundDrawable(background);
    }

    public static SpannableString getScaleText(String content, int start, int end, int px) {
        SpannableString stringSpan = new SpannableString(content);
        stringSpan.setSpan(new AbsoluteSizeSpan(px), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringSpan;
    }

    public static SpannableString getColorText(String content, int start, int end, int color) {
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
