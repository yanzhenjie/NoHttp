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
package com.yanzhenjie.nohttp.sample.app.body;

import android.app.Activity;

import com.yanzhenjie.nohttp.sample.mvp.BasePresenter;
import com.yanzhenjie.nohttp.sample.mvp.BaseView;

/**
 * Created by YanZhenjie on 2018/3/28.
 */
public final class Contract {

    public interface BodyPresenter
      extends BasePresenter {

        /**
         * Select file.
         */
        void selectFile();

        /**
         * Upload file.
         */
        void updateFile();

        /**
         * Copy path.
         */
        void copyPath();
    }

    public static abstract class BodyView
      extends BaseView<BodyPresenter> {

        public BodyView(Activity activity, BodyPresenter presenter) {
            super(activity, presenter);
        }

        /**
         * Select successful, set file.
         */
        public abstract void setLocalFile(String filepath);

        /**
         * Upload successful, set file.
         */
        public abstract void setRemoteFile(String filepath);
    }
}