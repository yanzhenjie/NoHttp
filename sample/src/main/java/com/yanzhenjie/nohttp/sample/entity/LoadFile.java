/*
 * Copyright 2015 Yan Zhenjie
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
package com.yanzhenjie.nohttp.sample.entity;

import com.yanzhenjie.nohttp.sample.Application;

/**
 * Created on 2016/5/31.
 *
 * @author Yan Zhenjie;
 */
public class LoadFile {

    private String title;
    private int progress;

    public LoadFile() {
    }

    public LoadFile(String title, int progress) {
        this.title = title;
        this.progress = progress;
    }

    public LoadFile(int title, int progress) {
        this.title = Application.getInstance().getString(title);
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitle(int title) {
        this.title = Application.getInstance().getString(title);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
