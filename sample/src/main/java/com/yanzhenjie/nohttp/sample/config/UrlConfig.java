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
package com.yanzhenjie.nohttp.sample.config;

/**
 * Created by YanZhenjie on 2018/3/1.
 */
public class UrlConfig {

    private static final String URL_ROOT = "http://kalle.nohttp.net";

    /**
     * Login.
     */
    public static final String LOGIN = URL_ROOT + "/login";

    /**
     * Data list.
     */
    public static final String GET_LIST = URL_ROOT + "/method/get/list";

    /**
     * Form.
     */
    public static final String UPLOAD_FORM = URL_ROOT + "/upload/form";

    /**
     * Body, file.
     */
    public static final String UPLOAD_BODY_FILE = URL_ROOT + "/upload/body/file?filename=abc.jpg";

    /**
     * Download.
     */
    public static final String DOWNLOAD =
      "http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.18_sweb.apk";

}