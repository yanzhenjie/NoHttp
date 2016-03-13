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
package com.yolanda.nohttp.rest;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.tools.HeaderParser;
import com.yolanda.nohttp.tools.IOUtils;

/**
 * Created in Jul 28, 2015 7:33:52 PM.
 *
 * @author Yan Zhenjie.
 */
public class StringRequest extends com.yolanda.nohttp.rest.RestRequest<String> {

    public StringRequest(String url) {
        this(url, RequestMethod.GET);
    }

    public StringRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public String getAccept() {
        return "application/json,application/xml,text/html,application/xhtml+xml";
    }

    @Override
    public String parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        return parseResponseString(url, responseHeaders, responseBody);
    }

    public static String parseResponseString(String url, Headers responseHeaders, byte[] responseBody) {
        if (responseBody == null)
            return "";
        return IOUtils.toString(responseBody, HeaderParser.parseHeadValue(responseHeaders.getContentType(), Headers.HEAD_KEY_CONTENT_TYPE, ""));
    }
}