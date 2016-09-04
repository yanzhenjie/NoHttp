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

import com.yolanda.nohttp.Connection;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.IRestConnection;
import com.yolanda.nohttp.RestConnection;
import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.error.NotFoundCacheError;
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
public class RestProtocol implements IRestProtocol {

    private static RestProtocol instance;

    private Cache<CacheEntity> mCache;

    private IRestConnection mIRestConnection;

    public static IRestProtocol getInstance(Cache<CacheEntity> cache, IRestConnection iRestConnection) {
        synchronized (RestProtocol.class) {
            if (instance == null) {
                instance = new RestProtocol(cache, iRestConnection);
            }
            return instance;
        }
    }

    private RestProtocol(Cache<CacheEntity> cache, IRestConnection iRestConnection) {
        mCache = cache;
        mIRestConnection = iRestConnection;
    }

    @Override
    public ProtocolResult requestNetwork(IProtocolRequest request) {
        // Handle cache header.
        CacheMode cacheMode = request.getCacheMode();
        String cacheKey = request.getCacheKey();
        CacheEntity cacheEntity = mCache.get(cacheKey);

        ProtocolResult httpResponse;
        switch (cacheMode) {
            case ONLY_READ_CACHE:// Only read cache.
                if (cacheEntity == null) {
                    return new ProtocolResult(null, null, true, new NotFoundCacheError("The cache mode is ONLY_READ_CACHE, but Did not find the cache."));
                } else {
                    return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                }
//                break;
            case ONLY_REQUEST_NETWORK:// Only request network.
                httpResponse = getHttpResponse(request);
                break;
            case NONE_CACHE_REQUEST_NETWORK:// Cache none request network.
                if (cacheEntity == null) {
                    httpResponse = getHttpResponse(request);
                } else {
                    return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                }
                break;
            case REQUEST_NETWORK_FAILED_READ_CACHE:// Request network failed read cache.
                if (cacheEntity != null)
                    setRequestCacheHeader(request, cacheEntity);
                httpResponse = getHttpResponse(request);
                break;
            default:// Default, Comply with the RFC2616.
                if (cacheEntity != null) {
                    if (cacheEntity.getLocalExpire() > System.currentTimeMillis())// Cache valid.
                        return new ProtocolResult(cacheEntity.getResponseHeaders(), cacheEntity.getData(), true, null);
                    setRequestCacheHeader(request, cacheEntity);
                }
                httpResponse = getHttpResponse(request);
                break;
        }
        return handleResponseCache(request, cacheEntity, httpResponse);
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
            if (eTag != null) {
                request.headers().set(Headers.HEAD_KEY_IF_NONE_MATCH, eTag);
            }

            long lastModified = headers.getLastModified();
            if (lastModified > 0) {
                request.headers().set(Headers.HEAD_KEY_IF_MODIFIED_SINCE, HeaderUtil.formatMillisToGMT(lastModified));
            }
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
        Connection connection = mIRestConnection.getConnection(request);
        Headers responseHeaders = connection.responseHeaders();
        Exception exception = connection.exception();
        if (exception == null) {
            if (RestConnection.hasResponseBody(request.getRequestMethod(), responseHeaders.getResponseCode()))
                try {
                    responseBody = IOUtils.toByteArray(connection.serverStream());
                } catch (IOException e) {// IOException.
                    exception = e;
                }
        }
        IOUtils.closeQuietly(connection);
        return new ProtocolResult(responseHeaders, responseBody, exception != null, exception);
    }

    /**
     * Process the response cache.
     *
     * @param request          {@link IProtocolRequest}, The original request object.
     * @param localCacheEntity {@link CacheEntity}, This request the corresponding local cached entities, which can be null.
     * @param httpResponse     {@link ProtocolResult}, Request the server to generate the response entity.
     * @return {@link RestProtocol}, According to the response headers and local server cache to regenerate the response entity, you should use this response entity.
     */
    private ProtocolResult handleResponseCache(IProtocolRequest request, CacheEntity localCacheEntity, ProtocolResult httpResponse) {
        boolean isFromCache = false;
        Headers responseHeaders = httpResponse.responseHeaders();
        byte[] responseBody = httpResponse.responseBody();
        Exception exception = httpResponse.exception();

        CacheMode cacheMode = request.getCacheMode();

        int responseCode = responseHeaders.getResponseCode();
        if (exception == null) {// 请求成功
            if (responseCode == 304) {
                isFromCache = true;

                if (localCacheEntity == null) { // Fix server error for 304.
                    responseBody = new byte[0];
                } else {
                    // Update response header.
                    localCacheEntity.getResponseHeaders().setAll(responseHeaders);
                    responseHeaders = localCacheEntity.getResponseHeaders();

                    // Update localExpires.
                    localCacheEntity.setLocalExpire(HeaderUtil.getLocalExpires(responseHeaders));

                    responseBody = localCacheEntity.getData();
                }
            } else if (responseBody != null) {// Redirect data need cache ?
                if (localCacheEntity == null) {
                    localCacheEntity = HeaderUtil.parseCacheHeaders(responseHeaders, responseBody, !cacheMode.isStandardHttpProtocol());// Standard protocol not force.
                    // Maybe null: Http CacheControl: (no-cache || no-store) && !cacheMode.isStandardHttpProtocol().
                } else {
                    localCacheEntity.getResponseHeaders().setAll(responseHeaders);

                    // Update localExpires.
                    localCacheEntity.setLocalExpire(HeaderUtil.getLocalExpires(responseHeaders));

                    localCacheEntity.setData(responseBody);
                }
            }
            if (localCacheEntity != null)
                mCache.replace(request.getCacheKey(), localCacheEntity);

        } else if (cacheMode == CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE && localCacheEntity != null) {
            exception = null;
            isFromCache = true;
            responseHeaders = localCacheEntity.getResponseHeaders();
            responseBody = localCacheEntity.getData();
        }
        return new ProtocolResult(responseHeaders, responseBody, isFromCache, exception);
    }
}