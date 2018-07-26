/*
 * Copyright © 2018 Yan Zhenjie.
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
package com.yanzhenjie.nohttp.sample.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by YanZhenjie on 2018/3/18.
 */
public class Delivery {

    private static Delivery sInstance;

    public static Delivery getInstance() {
        if (sInstance == null) {
            synchronized (Delivery.class) {
                if (sInstance == null) {
                    sInstance = new Delivery();
                }
            }
        }
        return sInstance;
    }

    private Handler mHandler;

    public Delivery() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void post(Runnable post) {
        mHandler.post(post);
    }

    public void postDelayed(Runnable post, long delay) {
        mHandler.postDelayed(post, delay);
    }
}