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
package com.yolanda.nohttp;

import java.util.Map;

/**
 * <p>Achieve {@link ImplClientRequest} and {@link ImplServerRequest}, and can add parameters of the interface</p>
 * Created in Oct 16, 2015 8:22:06 PM
 *
 * @author YOLANDA
 */
public interface Request<T> extends ImplClientRequest, ImplServerRequest {

    /**
     * Add {@code CharSequence} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, String value);

    /**
     * Add {@code Integer} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, int value);

    /**
     * Add {@code Long} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, long value);

    /**
     * Add {@code Boolean} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, boolean value);

    /**
     * Add {@code char} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, char value);

    /**
     * Add {@code Double} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, double value);

    /**
     * Add {@code Float} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, float value);

    /**
     * Add {@code Short} param
     *
     * @param key   Param name
     * @param value Param value
     */
    void add(String key, short value);

    /**
     * Add {@code Byte} param
     *
     * @param key   Param name
     * @param value Param value 0 x01, for example, the result is 1
     */
    void add(String key, byte value);

    /**
     * Add {@code File} param; NoHttp already has a default implementation: {@link FileBinary}
     *
     * @param key    Param name
     * @param binary Param value
     */
    void add(String key, Binary binary);

    /**
     * add all param
     *
     * @param params params map
     */
    void add(Map<String, String> params);

    /**
     * set all param
     *
     * @param params
     */
    void set(Map<String, String> params);

    /**
     * Remove a request param by key
     */
    Object remove(String key);

    /**
     * Remove all request param
     */
    void removeAll();

    /**
     * Parse response
     */
    T parseResponse(String url, Headers responseHeaders, byte[] responseBody);
}
