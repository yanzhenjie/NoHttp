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
package com.yolanda.nohttp;

import android.os.Build;

import com.yolanda.nohttp.tools.AndroidVersion;

/**
 * <p>
 * HTTP request method.
 * </p>
 * Created in Oct 10, 2015 8:00:48 PM.
 *
 * @author Yan Zhenjie.
 */
public enum RequestMethod {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    MOVE("MOVE"),

    COPY("COPY"),

    DELETE("DELETE"),

    HEAD("HEAD"),

    PATCH("PATCH"),

    OPTIONS("OPTIONS"),

    TRACE("TRACE"),

    CONNECT("CONNECT");

    private final String value;

    RequestMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public boolean allowRequestBody() {
        boolean allowRequestBody = this == POST || this == PUT || this == PATCH || this == DELETE;
        if (Build.VERSION.SDK_INT < AndroidVersion.LOLLIPOP)
            allowRequestBody = allowRequestBody && this != DELETE;
        return allowRequestBody;
    }

}
