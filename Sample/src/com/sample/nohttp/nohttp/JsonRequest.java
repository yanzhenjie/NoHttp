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

import org.json.JSONException;
import org.json.JSONObject;

import com.yolanda.nohttp.RestRequestor;

/**
 * Created in Oct 23, 2015 8:07:41 PM
 * 
 * @author YOLANDA
 */
public class JsonRequest extends RestRequestor<JSONObject> {

	public JsonRequest(String url, int requestMethod) {
		super(url, requestMethod);
	}

	public JsonRequest(String url) {
		super(url);
	}

	@Override
	public JSONObject parseResponse(String url, String contentType, byte[] byteArray) {
		String jsonString = "{\"name\":\"yolanda\",\"pass\":\"yolanda.pass\"}";
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
