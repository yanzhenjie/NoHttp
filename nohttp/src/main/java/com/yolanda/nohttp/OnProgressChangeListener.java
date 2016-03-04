/*
 * Copyright © ${user}. All Rights Reserved
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
package com.yolanda.nohttp;

/**
 * Created in 2016/3/3 14:41.
 *
 * @author YOLANDA;
 */
public abstract class OnProgressChangeListener implements OnUploadListener {

    @Override
    public void onStart(int what) {
    }

    @Override
    public void onCancel(int what) {
    }

    @Override
    public void onFinish(int what) {
    }

    @Override
    public void onError(int what, Exception exception) {
    }

}
