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
package com.sample.nohttp.nohttp;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.JsonObjectRequest;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RestRequest;
import com.yolanda.nohttp.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>自定义请求对象.</p>
 * Created in Feb 1, 2016 8:53:17 AM.
 *
 * @author YOLANDA;
 */
public class FastJsonRequest extends RestRequest<JSONObject> {

    public FastJsonRequest(String url) {
        super(url);
    }

    public FastJsonRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public JSONObject parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        String result = StringRequest.parseResponseString(url, responseHeaders, responseBody);
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(result)) {
            jsonObject = JSON.parseObject(result);
        } else {
            // 这里默认的错误可以定义为你们自己的协议
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("error", -1);
            map.put("url", url());
            map.put("data", "");
            map.put("method", getRequestMethod().toString());
            jsonObject = (JSONObject) JSON.toJSON(map);
        }
        return jsonObject;
    }

    @Override
    public String getAccept() {
        // 告诉服务器你接受什么类型的数据, 会添加到请求头的Accept中
        return JsonObjectRequest.ACCEPT;
    }

}
