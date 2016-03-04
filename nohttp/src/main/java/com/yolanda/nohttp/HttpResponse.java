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

/**
 * Created in Jan 6, 2016 5:19:13 PM.
 *
 * @author YOLANDA;
 */
public class HttpResponse {

    public final boolean isFromCache;
    public final byte[] responseBody;
    public final Headers responseHeaders;
    public final Exception exception;

    public HttpResponse(boolean isFromCache, Headers responseHeaders, byte[] responseBody, Exception exception) {
        this.isFromCache = isFromCache;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        this.exception = exception;
    }
}
