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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>The realization method of the parameters.</p>
 * Created in Oct 20, 2015 4:24:27 PM.
 *
 * @author YOLANDA;
 */
public abstract class RestRequest<T> extends BasicRequest<T> {

    /**
     * Param collection.
     */
    protected Map<String, Object> mParamMap = null;

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request adress, like: http://www.google.com.
     */
    public RestRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a request
     *
     * @param url           request adress, like: http://www.google.com.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public RestRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
        this.mParamMap = new LinkedHashMap<String, Object>();
    }

    @Override
    public void add(String key, String value) {
        mParamMap.put(key, value == null ? "" : value);
    }

    @Override
    public void add(String key, int value) {
        mParamMap.put(key, Integer.toString(value));
    }

    @Override
    public void add(String key, long value) {
        mParamMap.put(key, Long.toString(value));
    }

    @Override
    public void add(String key, boolean value) {
        mParamMap.put(key, String.valueOf(value));
    }

    @Override
    public void add(String key, char value) {
        mParamMap.put(key, String.valueOf(value));
    }

    @Override
    public void add(String key, double value) {
        mParamMap.put(key, Double.toString(value));
    }

    @Override
    public void add(String key, float value) {
        mParamMap.put(key, Float.toString(value));
    }

    @Override
    public void add(String key, short value) {
        mParamMap.put(key, Integer.toString(value));
    }

    @Override
    public void add(String key, byte value) {
        mParamMap.put(key, Integer.toString(value));
    }

    @Override
    public void add(String key, Binary binary) {
        mParamMap.put(key, binary);
    }

    @Override
    public void add(Map<String, String> params) {
        if (params != null)
            this.mParamMap.putAll(params);
    }

    @Override
    public void set(Map<String, String> params) {
        if (params != null) {
            this.mParamMap.clear();
            this.mParamMap.putAll(params);
        }
    }

    @Override
    public Object remove(String key) {
        return mParamMap.remove(key);
    }

    @Override
    public void removeAll() {
        mParamMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return mParamMap.keySet();
    }

    @Override
    public Object value(String key) {
        return mParamMap.get(key);
    }
}
