/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yolanda.nohttp;

import android.text.TextUtils;

import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created on 2016/6/1.
 *
 * @author Yan Zhenjie;
 * @deprecated use {com.yolanda.nohttp.rest.JsonArrayRequest} instead.
 */
@Deprecated
public class JsonArrayRequest extends com.yolanda.nohttp.rest.RestRequest<JSONArray> {

    public JsonArrayRequest(String url) {
        this(url, RequestMethod.POST);
    }

    public JsonArrayRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public String getAccept() {
        return JsonObjectRequest.ACCEPT;
    }

    @Override
    public JSONArray parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        JSONArray jsonArray = null;
        String jsonStr = StringRequest.parseResponseString(url, responseHeaders, responseBody);

        if (!TextUtils.isEmpty(jsonStr))
            try {
                jsonArray = new JSONArray(jsonStr);
            } catch (JSONException e) {
                Logger.e(e);
            }
        if (jsonArray == null)
            try {
                jsonArray = new JSONArray("[]");
            } catch (JSONException e) {
            }
        return jsonArray;
    }

}
