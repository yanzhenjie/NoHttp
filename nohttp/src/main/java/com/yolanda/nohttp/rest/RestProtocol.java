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

import com.yolanda.nohttp.ConnectionResult;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.HttpConnection;
import com.yolanda.nohttp.NetworkExecutor;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.tools.CacheStore;
import com.yolanda.nohttp.tools.HeaderUtil;
import com.yolanda.nohttp.tools.IOUtils;

import java.io.IOException;

/**
 * <p>
 * Parsing the Http protocol related attributes, complete and the interaction of the network.
 * </p>
 * Created in Jul 28, 2015 7:33:22 PM.
 *
 * @author Yan Zhenjie.
 */
public class RestProtocol {

    private CacheStore<CacheEntity> mCache;

    private HttpConnection mHttpConnection;

    public RestProtocol(CacheStore<CacheEntity> cache, NetworkExecutor executor) {
        mCache = cache;
        mHttpConnection = new HttpConnection(executor);
    }

    public ProtocolResult requestNetwork(IProtocolRequest request) {
        // Handle cache header.
        CacheMode cacheMode = request.getCacheMode();
        String cacheKey = request.getCacheKey();
        CacheEntity cacheEntity = mCache.get(cacheKey);

        ProtocolResult httpResponse = null;
        switch (cacheMode) {
            case ONLY_READ_CACHE:// Only read cache.
                if (cacheEntity == null) {
                    return new ProtocolResult(null, null, true, new NotFoundCacheError("The cache mode is ONLY_READ_CACHE, but Did not find the cache."));
                } else {
                    return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                }
            case ONLY_REQUEST_NETWORK:// Only request network.
                httpResponse = getHttpResponse(request);
                break;
            case NONE_CACHE_REQUEST_NETWORK:// CacheStore none request network.
                if (cacheEntity != null)
                    return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                else
                    httpResponse = getHttpResponse(request);
                break;
            case REQUEST_NETWORK_FAILED_READ_CACHE:// Request network failed read cache.
                setRequestCacheHeader(request, cacheEntity);
                httpResponse = getHttpResponse(request);
                if (httpResponse.exception() != null && cacheEntity != null)
                    return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                break;
            case DEFAULT:// Default, Comply with the RFC2616.
                if (cacheEntity != null && cacheEntity.getLocalExpire() > System.currentTimeMillis())// CacheStore validate.
                    return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                setRequestCacheHeader(request, cacheEntity);
                httpResponse = getHttpResponse(request);
                break;
        }
        return handleResponseCache(cacheKey, cacheEntity, httpResponse);
    }

    /**
     * Perform the request before, Handle the cache headers.
     *
     * @param request     the request object.
     * @param cacheEntity cached entities.
     */
    private void setRequestCacheHeader(IProtocolRequest request, CacheEntity cacheEntity) {
        if (cacheEntity == null) {
            request.headers().remove(Headers.HEAD_KEY_IF_NONE_MATCH);
            request.headers().remove(Headers.HEAD_KEY_IF_MODIFIED_SINCE);
        } else {
            Headers headers = cacheEntity.getResponseHeaders();
            String eTag = headers.getETag();
            if (eTag != null)
                request.headers().set(Headers.HEAD_KEY_IF_NONE_MATCH, eTag);

            long lastModified = headers.getLastModified();
            if (lastModified > 0)
                request.headers().set(Headers.HEAD_KEY_IF_MODIFIED_SINCE, HeaderUtil.formatMillisToGMT(lastModified));
        }
    }

    /**
     * Handle retries, and complete the request network here.
     *
     * @param request request object.
     * @return {@link ProtocolResult}.
     */
    private ProtocolResult getHttpResponse(IProtocolRequest request) {
        byte[] responseBody = null;
        ConnectionResult connection = mHttpConnection.getConnection(request);
        Exception exception = connection.exception();
        if (exception == null && connection.serverStream() != null) {
            try {
                responseBody = IOUtils.toByteArray(connection.serverStream());
            } catch (IOException e) {
                exception = e;
            }
        }
        IOUtils.closeQuietly(connection);
        return new ProtocolResult(connection.responseHeaders(), responseBody, exception != null, exception);
    }

    /**
     * Process the response cache.
     *
     * @param cacheKey         cache key.
     * @param localCacheEntity {@link CacheEntity}, This request the corresponding local cached entities, which can be null.
     * @param protocolResult   {@link ProtocolResult}, Request the server to generate the response entity.
     * @return {@link RestProtocol}, According to the response headers and local server cache to regenerate the response entity, you should use this response entity.
     */
    private ProtocolResult handleResponseCache(String cacheKey, CacheEntity localCacheEntity, ProtocolResult protocolResult) {
        if (protocolResult.exception() == null) {// Successfully.
            Headers responseHeaders = protocolResult.responseHeaders();
            byte[] responseBody = protocolResult.responseBody();
            int responseCode = responseHeaders.getResponseCode();

            if (responseCode == 304) {
                if (localCacheEntity == null) { // Fix server error for 304.
                    responseBody = new byte[0];
                } else {
                    protocolResult.setFromCache(true);
                    responseHeaders = localCacheEntity.getResponseHeaders();
                    responseHeaders.set(Headers.HEAD_KEY_RESPONSE_CODE, Integer.toString(304));
                    responseBody = localCacheEntity.getData();
                }
            } else if (responseBody != null) {
                if (localCacheEntity == null) {
                    localCacheEntity = HeaderUtil.parseCacheHeaders(responseHeaders, responseBody);
                } else {
                    localCacheEntity.setLocalExpire(HeaderUtil.getLocalExpires(responseHeaders));
                    localCacheEntity.getResponseHeaders().setAll(responseHeaders);
                    localCacheEntity.setData(responseBody);
                }
            }
            if (localCacheEntity != null)
                mCache.replace(cacheKey, localCacheEntity);

            protocolResult.setResponseBody(responseBody);
            protocolResult.setResponseHeaders(responseHeaders);
        }
        return protocolResult;
    }
}