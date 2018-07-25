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
package com.yanzhenjie.nohttp;

import com.yanzhenjie.nohttp.able.Cancelable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by YanZhenjie on 2018/2/27.
 */
public class CancelerManager {

    private final Map<BasicRequest<?>, Cancelable> mCancelMap;

    public CancelerManager() {
        this.mCancelMap = new ConcurrentHashMap<>();
    }

    /**
     * Add a task to cancel.
     *
     * @param request target request.
     * @param cancelable canceller.
     */
    public void addCancel(BasicRequest<?> request, Cancelable cancelable) {
        mCancelMap.put(request, cancelable);
    }

    /**
     * Remove a task.
     *
     * @param request target request.
     */
    public void removeCancel(BasicRequest<?> request) {
        mCancelMap.remove(request);
    }

    /**
     * According to the sign to cancel a task.
     *
     * @param sign sign.
     */
    public void cancel(Object sign) {
        for (Map.Entry<BasicRequest<?>, Cancelable> entry : mCancelMap.entrySet()) {
            BasicRequest<?> request = entry.getKey();
            Object olgSign = request.getCancelSign();
            if ((sign == olgSign) || (sign != null && sign.equals(olgSign))) {
                entry.getValue().cancel();
            }
        }
    }

    /**
     * Cancel all tasks.
     */
    public void cancelAll() {
        for (Map.Entry<BasicRequest<?>, Cancelable> entry : mCancelMap.entrySet()) {
            entry.getValue().cancel();
        }
    }

    public int size() {
        return mCancelMap.size();
    }
}