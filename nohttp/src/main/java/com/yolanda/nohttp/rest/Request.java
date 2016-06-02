/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yolanda.nohttp.rest;

import com.yolanda.nohttp.BasicClientRequest;
import com.yolanda.nohttp.BasicServerRequest;

/**
 * <p>
 * Achieve {@link BasicClientRequest} and {@link BasicServerRequest}, and can add parameters of the interface.
 * </p>
 * Created in Oct 16, 2015 8:22:06 PM.
 *
 * @param <T> a generic, on behalf of you can accept the result type, .It should be with the {@link OnResponseListener}, {@link Response}.
 * @author Yan Zhenjie.
 */
public interface Request<T> extends com.yolanda.nohttp.Request<T> {
}