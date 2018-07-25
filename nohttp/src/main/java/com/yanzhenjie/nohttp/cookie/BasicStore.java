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
package com.yanzhenjie.nohttp.cookie;

import java.net.CookieStore;

/**
 * Created by YanZhenjie on 2018/7/25.
 */
public abstract class BasicStore<T extends BasicStore<T>>
  implements CookieStore {

    private boolean mEnable = true;

    public T setEnable(boolean enable) {
        this.mEnable = enable;
        return (T)this;
    }

    public boolean isEnable() {
        return mEnable;
    }
}