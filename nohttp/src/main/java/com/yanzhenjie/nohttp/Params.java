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
package com.yanzhenjie.nohttp;

import android.text.TextUtils;

import com.yanzhenjie.nohttp.tools.BasicMultiValueMap;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created in Jan 10, 2016 5:03:17 PM.
 *
 * @author Yan Zhenjie.
 */
public class Params extends BasicMultiValueMap<String, Object> {

    public Params() {
        super(new LinkedHashMap<String, List<Object>>() {
            @Override
            public List<Object> put(String key, List<Object> value) {
                return super.put(formatKey(key), value);
            }

            @Override
            public List<Object> get(Object key) {
                if (key != null) {
                    key = formatKey(key.toString());
                }
                return super.get(key);
            }

            @Override
            public List<Object> remove(Object key) {
                if (key != null) {
                    key = formatKey(key.toString());
                }
                return super.remove(key);
            }

            @Override
            public boolean containsKey(Object key) {
                if (key != null) {
                    key = formatKey(key.toString());
                }
                return super.containsKey(key);
            }
        });
    }

    /**
     * Format to Hump-shaped words.
     */
    public static String formatKey(String key) {
        return TextUtils.isEmpty(key) ? "" : key;
    }
}