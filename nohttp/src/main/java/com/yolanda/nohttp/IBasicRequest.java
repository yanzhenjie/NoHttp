/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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

import com.yolanda.nohttp.able.Cancelable;
import com.yolanda.nohttp.able.Finishable;
import com.yolanda.nohttp.able.Queueable;
import com.yolanda.nohttp.able.SignCancelable;
import com.yolanda.nohttp.able.Startable;
import com.yolanda.nohttp.tools.MultiValueMap;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Yan Zhenjie on 2016/8/20.
 */
public interface IBasicRequest extends IPriority, Queueable, Startable, Cancelable, SignCancelable, Finishable, Comparable<IBasicRequest> {

    /*
     * =====================================================
     * ||                     Client                      ||
     * =====================================================
     */

    /**
     * Mandatory set to form pattern to transmit data.
     * <p>MultipartFormEnable is request method is the premise of the POST/PUT/PATCH/DELETE, but the Android system under API level 19 does not support the DELETE.</p>
     *
     * @param enable true enable, other wise false.
     */
    void setMultipartFormEnable(boolean enable);

    /**
     * Set proxy server.
     *
     * @param proxy Can use {@code Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("64.233.162.83", 80));}.
     */
    void setProxy(Proxy proxy);

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
     * Add a new key-value header.
     *
     * @param key   key.
     * @param value value.
     */
    void addHeader(String key, String value);

    /**
     * If there is a key to delete, and then add a new key-value header.
     *
     * @param key   key.
     * @param value value.
     */
    void setHeader(String key, String value);

    /**
     * <p>Add a {@link HttpCookie}.</p>
     * Just like the:
     * <pre>
     *     HttpCookie httpCookie = getHttpCookie();
     *     if(httpCookie != null)
     *          request.addHeader("Cookie", cookie.getName() + "=" + cookie.getValue());
     *     ...
     * </pre>
     *
     * @param cookie {@link HttpCookie}.
     */
    void addHeader(HttpCookie cookie);

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
     * Set the accept for head.
     *
     * @param accept such as: "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}", "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_XML}.
     */
    void setAccept(String accept);

    /**
     * Set the acceptLanguage for head.
     *
     * @param acceptLanguage such as "zh-CN,zh", "en-US,us".
     */
    void setAcceptLanguage(String acceptLanguage);

    /**
     * Set the contentType for head.
     *
     * @param contentType such as: "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}", "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_XML}" or "{@value Headers#HEAD_VALUE_ACCEPT_MULTIPART_FORM_DATA}". Note, does not need to include quotation marks.
     */
    void setContentType(String contentType);

    /**
     * Set the userAgent for head.
     *
     * @param userAgent such as: {@code Mozilla/5.0 (Android U; Android 5.0) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 Safari/533.1}.
     */
    void setUserAgent(String userAgent);

    /**
     * Set the request fails retry count.The default value is 0, that is to say, after the failure will not go to this to initiate the request again.
     *
     * @param count the retry count, The default value is 0.
     */
    void setRetryCount(int count);

    /**
     * Set the params encoding.
     *
     * @param encoding such as {@code utf-8, gbk, gb2312}.
     */
    void setParamsEncoding(String encoding);

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, int value);

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, long value);

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, boolean value);

    /**
     * Add {@code char} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, char value);

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, double value);

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, float value);

    /**
     * Add {@link Short} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, short value);

    /**
     * Add {@link Byte} param.
     *
     * @param key   param name.
     * @param value param value, for example, the result is {@code 1} of {@code 0x01}.
     */
    void add(String key, byte value);

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void add(String key, String value);

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    void set(String key, String value);

    /**
     * Add {@link Binary} param.
     *
     * @param key    param name.
     * @param binary param value.
     */
    void add(String key, Binary binary);

    /**
     * Set {@link Binary} param.
     *
     * @param key    param name.
     * @param binary param value.
     */
    void set(String key, Binary binary);

    /**
     * Add {@link File} param.
     *
     * @param key  param name.
     * @param file param value.
     */
    void add(String key, File file);

    /**
     * Set {@link File} param.
     *
     * @param key  param name.
     * @param file param value.
     */
    void set(String key, File file);

    /**
     * Add {@link Binary} param;
     *
     * @param key      param name.
     * @param binaries param value.
     */
    void add(String key, List<Binary> binaries);

    /**
     * Set {@link Binary} param.
     *
     * @param key      param name.
     * @param binaries param value.
     */
    void set(String key, List<Binary> binaries);

    /**
     * Add all param.
     *
     * @param params params {@link Map}.
     */
    void add(Map<String, String> params);

    /**
     * Set all param.
     *
     * @param params params {@link Map}.
     */
    void set(Map<String, String> params);

    /**
     * Remove a request param by key.
     *
     * @param key key
     * @return The object is removed, if there are no returns null.
     */
    List<Object> remove(String key);

    /**
     * Remove all request param.
     */
    void removeAll();

    /**
     * Get the parameters of key-value pairs.
     *
     * @return Not empty Map.
     */
    MultiValueMap<String, Object> getParamKeyValues();

    /**
     * Settings you want to push data and contentType. Can only accept {@link java.io.ByteArrayInputStream} and {@link java.io.FileInputStream} type.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT}, {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     *
     * @param requestBody There can be a file, pictures, any other data flow.You don't need to close it, NoHttp when complete request will be automatically closed.
     * @param contentType such as: "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_XML}{@code ; charset=}{@value NoHttp#CHARSET_UTF8}", "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}{@code ; charset=}{@value NoHttp#CHARSET_UTF8}" or "{@value Headers#HEAD_VALUE_ACCEPT_MULTIPART_FORM_DATA}". Note, does not need to include quotation marks.
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(JSONObject)
     * @see #setDefineRequestBodyForJson(String)
     * @see #setDefineRequestBodyForXML(String)
     */
    void setDefineRequestBody(InputStream requestBody, String contentType);


    /**
     * Sets the request body and content type.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT}, {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     *
     * @param requestBody string body.
     * @param contentType such as: "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}" or "{@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_XML}". Note, does not need to include quotation marks.
     *                    <p>If ContentType parameter into "" or null, the default for the {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}.</p>
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBodyForJson(JSONObject)
     * @see #setDefineRequestBodyForJson(String)
     * @see #setDefineRequestBodyForXML(String)
     */
    void setDefineRequestBody(String requestBody, String contentType);

    /**
     * Set the request json body.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT}, {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     * <p>The content type is {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}</p>
     *
     * @param jsonBody json body.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(JSONObject)
     * @see #setDefineRequestBodyForXML(String)
     */
    void setDefineRequestBodyForJson(String jsonBody);

    /**
     * Set the request json body.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT}, {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     * <p>The content type is {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}</p>
     *
     * @param jsonBody json body.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(String)
     * @see #setDefineRequestBodyForXML(String)
     */
    void setDefineRequestBodyForJson(JSONObject jsonBody);

    /**
     * Set the request XML body.
     * <p>It is important to note that the request method must be {@link RequestMethod#PUT}, {@link RequestMethod#POST}, {@link RequestMethod#PATCH} in one of them.</p>
     * <p>The content type is {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_XML}</p>
     *
     * @param xmlBody xml body.
     * @see #setDefineRequestBody(InputStream, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBody(String, String)
     * @see #setDefineRequestBodyForJson(String)
     */
    void setDefineRequestBodyForXML(String xmlBody);

    /**
     * Sets redirect interface.
     *
     * @param redirectHandler {@link RedirectHandler}.
     */
    void setRedirectHandler(RedirectHandler redirectHandler);

    /**
     * Set tag of task, At the end of the task is returned to you.
     *
     * @param tag {@link Object}.
     */
    void setTag(Object tag);

     /*
     * =====================================================
     * ||                     Server                      ||
     * =====================================================
     */

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
     * Is MultipartFormEnable ?
     * <p>MultipartFormEnable is request method is the premise of the POST/PUT/PATCH/DELETE, but the Android system under API level 19 does not support the DELETE.</p>
     *
     * @return true enable, other wise false.
     */
    boolean isMultipartFormEnable();

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
     * To get the failure after retries.
     *
     * @return The default value is 0.
     */
    int getRetryCount();

    /**
     * The length of the request body.
     *
     * @return Such as: {@code 2048}.
     */
    long getContentLength();

    /**
     * Get {@value Headers#HEAD_KEY_CONTENT_TYPE}.
     *
     * @return string, such as: {@value Headers#HEAD_VALUE_ACCEPT_APPLICATION_JSON}.
     */
    String getContentType();

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
