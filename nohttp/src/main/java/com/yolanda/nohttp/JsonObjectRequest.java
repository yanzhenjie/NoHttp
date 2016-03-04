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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>JsonObject is returned by the server data, using the request object.</p>
 * Created in Jan 19, 2016 3:27:35 PM.
 *
 * @author YOLANDA;
 */
public class JsonObjectRequest extends RestRequest<JSONObject> {

    public static final String ACCEPT = "application/json;q=1";

    public JsonObjectRequest(String url) {
        super(url);
    }

    public JsonObjectRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public String getAccept() {
        return ACCEPT;
    }

    @Override
    public JSONObject parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        String jsonStr = StringRequest.parseResponseString(url, responseHeaders, responseBody);
        try {
            return new JSONObject(jsonStr);
        } catch (Exception e) {
            try {
                return new JSONObject("{}");
            } catch (JSONException exception) {
            }
        }
        return null;
    }

}
