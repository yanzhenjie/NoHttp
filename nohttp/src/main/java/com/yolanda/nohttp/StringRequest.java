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

import java.io.UnsupportedEncodingException;

import com.yolanda.nohttp.tools.HeaderParser;

/**
 * Created in Jul 28, 2015 7:33:52 PM.
 *
 * @author YOLANDA;
 */
public class StringRequest extends RestRequest<String> {

    public StringRequest(String url) {
        this(url, RequestMethod.GET);
    }

    public StringRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public String getAccept() {
        return "text/html,application/xhtml+xml,application/xml;*/*;q=0.9";
    }

    @Override
    public String parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        return parseResponseString(url, responseHeaders, responseBody);
    }

    public static final String parseResponseString(String url, Headers responseHeaders, byte[] responseBody) {
        String result = null;
        if (responseBody != null && responseBody.length > 0) {
            try {
                String charset = HeaderParser.parseHeadValue(responseHeaders.getContentType(), "charset", "");
                result = new String(responseBody, charset);
            } catch (UnsupportedEncodingException e) {
                Logger.w("Charset error in ContentType returned by the server：" + responseHeaders.getValue(Headers.HEAD_KEY_CONTENT_TYPE, 0));
                result = new String(responseBody);
            }
        }
        return result;
    }
}
