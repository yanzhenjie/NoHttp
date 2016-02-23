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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import com.yolanda.nohttp.tools.HeaderParser;
import com.yolanda.nohttp.tools.NetUtil;

import android.webkit.URLUtil;

/**
 * Network operating interface, The implementation of the network layer
 * </br>
 * Created in Jul 28, 2015 7:33:22 PM
 *
 * @author YOLANDA
 */
public final class HttpRestConnection extends BasicConnection implements ImplRestConnection {

    private static HttpRestConnection instance;

    public static HttpRestConnection getInstance() {
        if (instance == null)
            instance = new HttpRestConnection();
        return instance;
    }

    private HttpRestConnection() {
    }

    @Override
    public HttpResponse requestNetwork(ImplServerRequest request) {
        if (request == null)
            throw new IllegalArgumentException("reqeust == null");

        Logger.d("--------------Reuqest start--------------");

        int responseCode = 0;
        boolean isSucceed = false;
        Headers responseHeaders = new HttpHeaders();
        byte[] responseBody = null;

        String url = request.url();
        if (!URLUtil.isValidUrl(url))
            responseBody = new StringBuffer("URL error: ").append(url).toString().getBytes();
        else if (!NetUtil.isNetworkAvailable(NoHttp.getContext()))
            responseBody = "Network error".getBytes();
        else {
            HttpURLConnection httpConnection = null;
            try {
                httpConnection = getHttpConnection(request);
                Logger.d("-------Response start-------");
                responseCode = httpConnection.getResponseCode();
                responseHeaders = parseResponseHeaders(new URI(url), responseCode, httpConnection.getResponseMessage(), httpConnection.getHeaderFields());

                // handle body
                if (hasResponseBody(request.getRequestMethod(), responseCode)) {
                    InputStream inputStream;
                    try {
                        inputStream = httpConnection.getInputStream();
                    } catch (IOException e) {
                        inputStream = httpConnection.getErrorStream();
                    }
                    if (HeaderParser.isGzipContent(responseHeaders.getContentEncoding()))
                        inputStream = new GZIPInputStream(inputStream);
                    responseBody = readResponseBody(inputStream);
                    inputStream.close();
                }

                isSucceed = true;// Deal successfully with all
            } catch (Exception e) {
                String exceptionInfo = getExceptionMessage(e);
                responseBody = exceptionInfo.getBytes();
                Logger.e(e);
            } finally {
                if (httpConnection != null)
                    httpConnection.disconnect();
                Logger.d("-------Response end-------");
            }
        }
        Logger.d("--------------Reqeust finish--------------");
        return new HttpResponse(isSucceed, responseHeaders, responseBody);
    }
}
