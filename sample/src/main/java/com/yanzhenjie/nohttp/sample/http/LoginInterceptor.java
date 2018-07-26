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
package com.yanzhenjie.nohttp.sample.http;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Interceptor;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestHandler;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.sample.config.UrlConfig;

/**
 * Created by YanZhenjie on 2018/7/25.
 */
public class LoginInterceptor
  implements Interceptor {

    @Override
    public <T> Response<T> intercept(RequestHandler requestHandler, Request<T> request) {
        Response<T> tResponse = requestHandler.handle(request);
        if (tResponse.isSucceed()) {
            Headers headers = tResponse.getHeaders();
            if (headers.getResponseCode() == 401) {

                StringRequest loginRequest = new StringRequest(UrlConfig.LOGIN, RequestMethod.POST);
                loginRequest.add("name", 123).add("password", 456);
                Response<Result<String>> loginResponse = requestHandler.handle(loginRequest);
                Result<String> result = loginResponse.get();
                if (result.isSucceed()) {
                    return requestHandler.handle(request);
                }
            }
        }
        return tResponse;
    }
}