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
package com.yolanda.nohttp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Proxy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>
 * Analytical {@link BasicClientRequest} NoHttp interface.
 * </p>
 * Created in Dec 21, 2015 3:34:59 PM.
 *
 * @author Yan Zhenjie.
 */
public interface BasicServerRequest {

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
     * The length of the request body.
     *
     * @return Such as: {@code 94949}.
     */
    long getContentLength();

    /**
     * The client wants to accept data language types.
     *
     * @return Such as {@code zh-CN,zh,q=0.8}.
     */
    String getAcceptLanguage();

    /**
     * The type of the request body.
     *
     * @return such as: {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}, {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_XML}, {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_X_WWW_FORM_URLENCODED}.
     */
    String getContentType();

    /**
     * The "UserAgent" of the client.
     *
     * @return such as: {@code Mozilla/5.0 (Android U; Android 5.0) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 Safari/533.1}.
     */
    String getUserAgent();

    /**
     * Get the params encoding.
     *
     * @return such as {@code "utf-8, gbk, bg2312"}.
     */
    String getParamsEncoding();

    /**
     * Call before carry out the request, you can do some preparation work.
     */
    void onPreExecute();

    /**
     * Get the redirect handler.
     *
     * @return {@link RedirectHandler}.
     */
    RedirectHandler getRedirectHandler();

    /**
     * Send request body data.
     *
     * @param writer {@link OutputStream}.
     * @throws IOException write error.
     */
    void onWriteRequestBody(OutputStream writer) throws IOException;

    /**
     * Should to return the tag of the object.
     *
     * @return {@link Object}.
     */
    Object getTag();

}
