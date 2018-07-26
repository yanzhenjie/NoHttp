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
package com.yanzhenjie.nohttp.sample.app.form;

import android.app.Activity;

import com.yanzhenjie.nohttp.sample.app.form.entity.FileItem;
import com.yanzhenjie.nohttp.sample.mvp.BasePresenter;
import com.yanzhenjie.nohttp.sample.mvp.BaseView;

import java.util.List;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public final class Contract {

    public interface FormPresenter
      extends BasePresenter {

        /**
         * Add file.
         */
        void addFile();

        /**
         * Start upload.
         */
        void uploadFile();
    }

    public static abstract class FormView
      extends BaseView<FormPresenter> {

        public FormView(Activity activity, FormPresenter presenter) {
            super(activity, presenter);
        }

        /**
         * Set file list.
         */
        public abstract void setFileList(List<FileItem> fileList);

        /**
         * Refresh item.
         */
        public abstract void notifyItem(int position);

        /**
         * Upload succeed.
         */
        public abstract void setStatusText(String text);
    }

}