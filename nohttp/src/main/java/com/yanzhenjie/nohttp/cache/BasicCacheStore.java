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
package com.yanzhenjie.nohttp.cache;

import android.content.Context;

import com.yanzhenjie.nohttp.tools.CacheStore;
import com.yanzhenjie.nohttp.tools.Encryption;

/**
 * Created by YanZhenjie on 2017/12/21.
 */
public abstract class BasicCacheStore implements CacheStore<CacheEntity> {

    private Context mContext;

    public BasicCacheStore(Context context) {
        mContext = context;
    }

    protected String uniqueKey(String key) {
        key += mContext.getApplicationInfo().packageName;
        return Encryption.getMD5ForString(key);
    }

}