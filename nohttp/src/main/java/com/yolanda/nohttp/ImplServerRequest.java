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

import com.yolanda.nohttp.cache.CacheMode;
import com.yolanda.nohttp.tools.Writer;

import java.net.Proxy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>Analytical {@link ImplClientRequest} NoHttp interface.</p>
 * Created in Dec 21, 2015 3:34:59 PM.
 *
 * @author YOLANDA;
 */
public interface ImplServerRequest {

    /**
     * Return url of request.
     *
     * @return Url.
     */
    String url();

    /**
     * return method of request.
     *
     * @return {@link RequestMethod}.
     */
    RequestMethod getRequestMethod();

    /**
     * NoHttp need to cache.
     *
     * @return True: need cache, false: needn't cache.
     */
    boolean needCache();

    /**
     * Get key of cache data.
     *
     * @return Cache key.
     */
    String getCacheKey();

    /**
     * He got the request cache mode.
     *
     * @return Value from {@link CacheMode}.
     */
    CacheMode getCacheMode();

    /**
     * Get proxy server.
     *
     * @return Can use {@code Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("64.233.162.83", 80));}.
     */
    Proxy getProxy();

    /**
     * Get SSLSocketFactory.
     *
     * @return {@link SSLSocketFactory}.
     */
    SSLSocketFactory getSSLSocketFactory();

    /**
     * Get the HostnameVerifier.
     *
     * @return {@link HostnameVerifier}.
     */
    HostnameVerifier getHostnameVerifier();

    /**
     * If the request is POST, PUT, PATCH, the true should be returned.
     *
     * @return True: output = true, false: output = false.
     */
    boolean doOutPut();

    /**
     * Get the connection timeout time, Unit is a millisecond.
     *
     * @return Connection timeout.
     */
    int getConnectTimeout();

    /**
     * Get the read timeout time, Unit is a millisecond.
     *
     * @return Read timeout.
     */
    int getReadTimeout();

    /**
     * Get the redirect handler.
     *
     * @return {@link RedirectHandler}.
     */
    RedirectHandler getRedirectHandler();

    /**
     * Get all Heads.
     *
     * @return {@code Headers}.
     */
    Headers headers();

    /**
     * The client wants to accept data types.
     *
     * @return Such as: {@code application/json}.
     */
    String getAccept();

    /**
     * The client wants to accept data language types.
     *
     * @return Such as {@code zh-CN,zh,q=0.8}.
     */
    String getAcceptLanguage();

    /**
     * The length of the request body.
     *
     * @return Such as: {@code 94949}.
     */
    long getContentLength();

    /**
     * The type of the request body.
     *
     * @return Such as: {@code application/x-www-form-urlencoded; charset=UTF-8}, or: {@code application/json}.
     */
    String getContentType();

    /**
     * The "UserAgent" of the client.
     *
     * @return Such as: {@code Mozilla/5.0 (Android U; Android 5.0) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 Safari/533.1}.
     */
    String getUserAgent();

    /**
     * Call before perform request, here you can rebuild the request object.
     */
    void onPreExecute();

    /**
     * Send request body data.
     *
     * @param writer {@link Writer}.
     */
    void onWriteRequestBody(Writer writer);

    /**
     * Should to return the tag of the object.
     *
     * @return {@link Object}.
     */
    Object getTag();

}
