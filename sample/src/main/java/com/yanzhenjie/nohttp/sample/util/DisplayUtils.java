/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.util;

import android.app.Activity;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by Yan Zhenjie on 2016/7/7.
 */
public class DisplayUtils {

    private static boolean isInitialize = false;
    public static int screenWidth;
    public static int screenHeight;
    public static int screenDpi;
    public static float density = 1;
    public static float scaledDensity;

    public static void initScreen(Activity activity) {
        if (isInitialize) return;
        isInitialize = true;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metric = new DisplayMetrics();
        if (VERSION.SDK_INT >= 17) {
            display.getRealMetrics(metric);
        } else {
            display.getMetrics(metric);
        }

        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
        screenDpi = metric.densityDpi;
        density = metric.density;
        scaledDensity = metric.scaledDensity;
    }

    public static int px2dip(float inParam) {
        return (int)(inParam / density + 0.5F);
    }

    public static int dip2px(float inParam) {
        return (int)(inParam * density + 0.5F);
    }

    public static int px2sp(float inParam) {
        return (int)(inParam / scaledDensity + 0.5F);
    }

    public static int sp2px(float inParam) {
        return (int)(inParam * scaledDensity + 0.5F);
    }
}