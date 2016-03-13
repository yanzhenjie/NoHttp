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
package com.sample.nohttp.util;

import com.sample.nohttp.config.AppConfig;

/**
 * Created in Jan 29, 2016 9:25:18 AM.
 *
 * @author YOLANDA;
 */
public class Constants {

    /**
     * 服务器地址.
     */
    public static final String SERVER;

    /**
     * 正式时使用新浪sae托管; 测试使用本地, 节省SAE的消费.
     */
    static {
        if (AppConfig.DEBUG) {
            SERVER = "http://192.168.1.116/HttpServer/";
        } else {
            SERVER = "http://1.nohttp.applinzi.com/";
        }
    }

    /**
     * 测试接口.
     */
    public static final String URL_NOHTTP_TEST = SERVER + "test";
    /**
     * 各种方法测试.
     */
    public static final String URL_NOHTTP_METHOD = SERVER + "method";

    /**
     * 支持304缓存的接口--返回text.
     */
    public static final String URL_NOHTTP_CACHE_STRING = SERVER + "cache";

    /**
     * 支持304缓存的接口--返回image.
     */
    public static final String URL_NOHTTP_CACHE_IMAGE = SERVER + "imageCache";

    /**
     * 请求图片的接口，支持各种方法.
     */
    public static final String URL_NOHTTP_IMAGE = SERVER + "image";

    /**
     * 请求jsonObject接口, 支持各种方法.
     */
    public static final String URL_NOHTTP_JSONOBJECT = SERVER + "jsonObject";

    /**
     * 请求jsonArray接口, 支持各种方法.
     */
    public static final String URL_NOHTTP_JSONARRAY = SERVER + "jsonArray";

    /**
     * 重定向接口.
     * <p/>
     * <pre>
     * Nohttp支持多重重定向，实现了浏览器的功能.
     * </pre>
     */
    public static final String URL_NOHTTP_REDIRECT = SERVER + "redirect";

    /**
     * 上传文件接口.
     */
    public static final String URL_NOHTTP_UPLOAD = SERVER + "upload";

}
