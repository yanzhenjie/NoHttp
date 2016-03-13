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

import com.yolanda.nohttp.able.FinishAble;
import com.yolanda.nohttp.able.QueueAble;
import com.yolanda.nohttp.able.SignCancelAble;
import com.yolanda.nohttp.able.StartAble;
import com.yolanda.nohttp.cache.CacheMode;

import java.net.Proxy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>Developers provide data interface.</p>
 * Created in Dec 21, 2015 4:17:25 PM.
 *
 * @author YOLANDA;
 */
public interface ImplClientRequest extends QueueAble, StartAble, SignCancelAble, FinishAble {

    /**
     * Set proxy server.
     *
     * @param proxy Can use {@code Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("64.233.162.83", 80));}.
     */
    void setProxy(Proxy proxy);

    /**
     * If the server and {@link ImplServerRequest#needCache()} allow cache, will use this key as the only key to the.
     * cache request return data.
     *
     * @param key unique key.
     */
    void setCacheKey(String key);

    /**
     * Set the cache mode.
     *
     * @param cacheMode The value from {@link CacheMode}.
     */
    void setCacheMode(CacheMode cacheMode);

    /**
     * Sets the {@link SSLSocketFactory} for this request.
     *
     * @param socketFactory {@link SSLSocketFactory}.
     */
    void setSSLSocketFactory(SSLSocketFactory socketFactory);

    /**
     * Set the {@link HostnameVerifier}.
     *
     * @param hostnameVerifier {@link HostnameVerifier}.
     */
    void setHostnameVerifier(HostnameVerifier hostnameVerifier);

    /**
     * Sets the connection timeout time.
     *
     * @param connectTimeout timeout number, Unit is a millisecond.
     */
    void setConnectTimeout(int connectTimeout);

    /**
     * Sets the read timeout time.
     *
     * @param readTimeout timeout number, Unit is a millisecond.
     */
    void setReadTimeout(int readTimeout);

    /**
     * Sets redirect interface.
     *
     * @param redirectHandler {@link RedirectHandler}.
     */
    void setRedirectHandler(RedirectHandler redirectHandler);

    /**
     * If there is a key to delete, and then add a new key-value header.
     *
     * @param key   key.
     * @param value value.
     */
    void setHeader(String key, String value);

    /**
     * Add a new key-value header.
     *
     * @param key   key.
     * @param value value.
     */
    void addHeader(String key, String value);

    /**
     * Remove the key from the information.
     *
     * @param key key.
     */
    void removeHeader(String key);

    /**
     * Remove all header.
     */
    void removeAllHeader();

    /**
     * Settings you want to post data, if the requestBody isn't null, then other data
     * will not be sent.
     *
     * @param requestBody byte array.
     */
    void setRequestBody(byte[] requestBody);

    /**
     * @param requestBody byte array.
     * @see #setRequestBody(byte[])
     */
    void setRequestBody(String requestBody);

    /**
     * Set tag of task, At the end of the task is returned to you.
     *
     * @param tag {@link Object}.
     */
    void setTag(Object tag);

}
