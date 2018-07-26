/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.nohttp.sample.app.download;

import android.app.Activity;

import com.yanzhenjie.nohttp.sample.mvp.BasePresenter;
import com.yanzhenjie.nohttp.sample.mvp.BaseView;

/**
 * Created by YanZhenjie on 2018/3/28.
 */
public final class Contract {

    public interface DownloadPresenter
      extends BasePresenter {

        /**
         * Try download.
         */
        void tryDownload();
    }

    public static abstract class DownloadView
      extends BaseView<DownloadPresenter> {

        public DownloadView(Activity activity, DownloadPresenter presenter) {
            super(activity, presenter);
        }

        /**
         * Notify start.
         */
        public abstract void onStart();

        /**
         * Set progress.
         */
        public abstract void setProgress(int progress, String speed);

        /**
         * Notify finish.
         */
        public abstract void onFinish();

        /**
         * Notify error.
         */
        public abstract void onError(String message);

        /**
         * Notify cancel.
         */
        public abstract void onCancel();
    }

}