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

import android.text.TextUtils;

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.cache.CacheMode;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.tools.HeaderParser;
import com.yolanda.nohttp.tools.HttpDateTime;

/**
 * <p>The request executor, Interact with the network layer.</p>
 * Created in Jan 6, 2016 5:45:19 PM.
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
    public HttpResponse executeRequest(Request<?> request) {
        // handle cache header
        CacheMode cacheMode = request.getCacheMode();
        CacheEntity cacheEntity = mCache.get(request.getCacheKey());

        if (cacheMode == CacheMode.ONLY_READ_CACHE) {// Only read cache data.
            if (cacheEntity == null)
                return new HttpResponse(false, null, null, new NotFoundCacheError("Could not find the cache."));
            else
                return new HttpResponse(true, cacheEntity.getResponseHeaders(), cacheEntity.getData(), null);
        } else if (cacheMode == CacheMode.IF_NONE_CACHE_REQUEST) {// If none cache to request.
            if (cacheEntity != null)
                return new HttpResponse(true, cacheEntity.getResponseHeaders(), cacheEntity.getData(), null);
        }

        // According to the standard HTTP protocol operation response.
        HttpResponse httpResponse;
        if (cacheEntity == null || cacheEntity.getLocalExpire() < System.currentTimeMillis()) {
            if (cacheEntity != null)
                handleCacheHeader(request, cacheEntity);
            httpResponse = mConnection.requestNetwork(request);
        } else
            httpResponse = new HttpResponse(true, cacheEntity.getResponseHeaders(), cacheEntity.getData(), null);

        boolean isFromCache = httpResponse.isFromCache;
        Headers responseHeaders = httpResponse.responseHeaders;
        byte[] responseBody = httpResponse.responseBody;
        Exception exception = httpResponse.exception;

        int responseCode = responseHeaders.getResponseCode();

        if (exception == null) {
            if (responseCode == 304) {
                if (cacheEntity == null)
                    exception = new ServerError("The server responseCode of 304, but not the client cache.");
                else {
                    isFromCache = true;
                    cacheEntity.getResponseHeaders().setAll(responseHeaders);
                    responseHeaders = cacheEntity.getResponseHeaders();
                    responseBody = cacheEntity.getData();
                }
            } else if (responseCode == 302 || responseCode == 303) {// redirect
                // redirect request
                Request<?> redirectRequest;
                RedirectHandler redirectHandler = request.getRedirectHandler();
                if (redirectHandler != null)
                    redirectRequest = redirectHandler.onRedirect(responseHeaders);
                else {
                    redirectRequest = NoHttp.createStringRequest(responseHeaders.getLocation(), request.getRequestMethod());
                    redirectRequest.setSSLSocketFactory(request.getSSLSocketFactory());
                    redirectRequest.setProxy(request.getProxy());
                }

                if (redirectRequest != null) {
                    HttpResponse redirectHttpResponse = executeRequest(redirectRequest);

                    // response result
                    Headers redirectHeaders = redirectHttpResponse.responseHeaders;
                    responseBody = redirectHttpResponse.responseBody;
                    exception = redirectHttpResponse.exception;

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
                    cacheEntity = HeaderParser.parseCacheHeaders(responseHeaders, responseBody, true);
                if (cacheEntity != null)
                    mCache.replace(request.getCacheKey(), cacheEntity);
            }
        } else if (cacheEntity != null && cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
            exception = null;
            isFromCache = true;
            responseHeaders = cacheEntity.getResponseHeaders();
            responseBody = cacheEntity.getData();
        }
        return new HttpResponse(isFromCache, responseHeaders, responseBody, exception);
    }

    /**
     * Perform the request before, Handle the cache headers.
     *
     * @param request     the request object.
     * @param cacheEntity cached entities.
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
