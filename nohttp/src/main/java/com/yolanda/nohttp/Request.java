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
 * <p>Achieve {@link ImplClientRequest} and {@link ImplServerRequest}, and can add parameters of the interface.</p>
 * Created in Oct 16, 2015 8:22:06 PM.
 *
 * @author YOLANDA;
 */
public interface Request<T> extends ImplClientRequest, ImplServerRequest {

    /**
     * Add {@link CharSequence} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, String value);

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, int value);

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, long value);

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, boolean value);

    /**
     * Add {@link char} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, char value);

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, double value);

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, float value);

    /**
     * Add {@link Short} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, short value);

    /**
     * Add {@link Byte} param.
     *
     * @param key   param name.
     * @param value param value 0 x01, for example, the result is 1.
     */
    void add(String key, byte value);

    /**
     * Add {@link java.io.File} param; NoHttp already has a default implementation: {@link FileBinary}.
     *
     * @param key    param name.
     * @param binary param value.
     */
    void add(String key, Binary binary);

    /**
     * add all param.
     *
     * @param params params map.
     */
    void add(Map<String, String> params);

    /**
     * set all param.
     *
     * @param params params map.
     */
    void set(Map<String, String> params);

    /**
     * Remove a request param by key.
     *
     * @param key key
     * @return The object is removed, if there are no returns null.
     */
    Object remove(String key);

    /**
     * Remove all request param.
     */
    void removeAll();

    /**
     * Parse response.
     *
     * @param url             url.
     * @param responseHeaders response {@link Headers} of server.
     * @param responseBody    response data of server.
     * @return {@link T}: your response result.
     */
    T parseResponse(String url, Headers responseHeaders, byte[] responseBody);
}
