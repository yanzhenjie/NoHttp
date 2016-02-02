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

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.tools.HeaderParser;
import com.yolanda.nohttp.tools.HttpDateTime;

import android.text.TextUtils;

/**
 * The request executor, Interact with the network layer
 * </br>
 * Created in Jan 6, 2016 5:45:19 PM
 *
 * @author YOLANDA;
 */
public class HttpRestExecutor implements ImplRestExecutor {

    private static HttpRestExecutor _INSTANCE;

    private ImplRestConnection mConnection;

    private Cache<CacheEntity> mCache;

    private HttpRestExecutor(Cache<CacheEntity> cache, ImplRestConnection connection) {
        this.mCache = cache;
        this.mConnection = connection;
    }

    public static HttpRestExecutor getInstance(Cache<CacheEntity> cache, ImplRestConnection connection) {
        if (_INSTANCE == null)
            _INSTANCE = new HttpRestExecutor(cache, connection);
        return _INSTANCE;
    }

    @Override
    public HttpResponse executRequest(Request<?> request) {
        // handle cache header
        CacheEntity cacheEntity = null;
        if (request.needCache())
            cacheEntity = mCache.get(request.getCacheKey());

        // handle response
        HttpResponse httpResponse = null;
        if (cacheEntity == null || cacheEntity.getLocalExpire() < System.currentTimeMillis()) {
            if (cacheEntity != null)
                handleCacheHeader(request, cacheEntity);
            httpResponse = mConnection.requestNetwork(request);
        } else
            httpResponse = new HttpResponse(true, cacheEntity.getResponseHeaders(), cacheEntity.getData());

        boolean isSucceed = httpResponse.isSucceed;
        Headers responseHeaders = httpResponse.responseHeaders;
        byte[] responseBody = httpResponse.responseBody;

        int responseCode = responseHeaders.getResponseCode();

        if (isSucceed) {
            if (responseCode == 304 && cacheEntity != null) {// cache
                cacheEntity.getResponseHeaders().setAll(responseHeaders);
                responseHeaders = cacheEntity.getResponseHeaders();
                responseBody = cacheEntity.getData();
            } else if (responseCode == 302 || responseCode == 303) {// redirect
                // redirect request
                Request<?> redirestRequest = null;
                RedirectHandler redirectHandler = request.getRedirectHandler();
                if (redirectHandler != null)
                    redirestRequest = redirectHandler.onRedirect(responseHeaders);
                else {
                    redirestRequest = NoHttp.createStringRequest(responseHeaders.getLocation(), request.getRequestMethod());
                    redirestRequest.setSSLSocketFactory(request.getSSLSocketFactory());
                    redirestRequest.setProxy(request.getProxy());
                }

                if (redirestRequest == null) {
                    // needn't redirect
                } else {
                    HttpResponse redirectHttpResponse = executRequest(redirestRequest);

                    // response result
                    isSucceed = redirectHttpResponse.isSucceed;
                    Headers redirectHeaders = redirectHttpResponse.responseHeaders;
                    responseBody = redirectHttpResponse.responseBody;

                    // response ContentEncoding
                    String contentEncoding = redirectHeaders.getContentEncoding();
                    if (!TextUtils.isEmpty(contentEncoding))
                        responseHeaders.set(Headers.HEAD_KEY_CONTENT_ENCODING, contentEncoding);

                    // response ContentLength
                    responseHeaders.set(Headers.HEAD_KEY_CONTENT_LENGTH, Integer.toString(redirectHeaders.getContentLength()));

                    // response ContentType
                    String contentType = redirectHeaders.getContentType();
                    if (!TextUtils.isEmpty(contentType))
                        responseHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
                }
            }
            // needn't cache redirect data
            if (request.needCache() && responseBody != null && responseCode != 302 && responseCode != 303) {
                if (cacheEntity == null)
                    cacheEntity = HeaderParser.parseCacheHeaders(responseHeaders, responseBody);
                if (cacheEntity != null)
                    mCache.replace(request.getCacheKey(), cacheEntity);
            }
        }
        return new HttpResponse(isSucceed, responseHeaders, responseBody);
    }

    /**
     * Perform the request before, Handle the cache headers
     *
     * @param request     The request object
     * @param cacheEntity Cached entities
     */
    private void handleCacheHeader(Request<?> request, CacheEntity cacheEntity) {
        if (cacheEntity == null) {
            request.removeHeader(Headers.HEAD_KEY_IF_NONE_MATCH);
            request.removeHeader(Headers.HEAD_KEY_IF_MODIFIED_SINCE);
        } else {
            Headers headers = cacheEntity.getResponseHeaders();
            String eTag = headers.getETag();
            if (eTag != null)
                request.setHeader(Headers.HEAD_KEY_IF_NONE_MATCH, eTag);

            long lastModified = headers.getLastModified();
            if (lastModified > 0)
                request.setHeader(Headers.HEAD_KEY_IF_MODIFIED_SINCE, HttpDateTime.formatMillisToGMT(lastModified));
        }
    }
}
