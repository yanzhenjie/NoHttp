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
package com.yolanda.nohttp.able;

/**
 * <p>According to the sign cancel interface.</p>
 * Created in Nov 12, 2015 5:11:56 PM.
 *
 * @author Yan Zhenjie;
 */
public interface SignCancelable {

    /**
     * Cancel operation by contrast the sign.
     *
     * @param sign an object that can be null.
     */
    void cancelBySign(Object sign);

    /**
     * Set cancel sign.
     *
     * @param object a object.
     */
    void setCancelSign(Object object);

}
