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

import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.yolanda.nohttp.tools.MultiValueMap;

/**
 * <p>
 * Http header.
 * </p>
 * Created in Jan 10, 2016 2:29:42 PM.
 *
 * @author Yan Zhenjie.
 */
public interface Headers extends MultiValueMap<String, String> {

    String HEAD_KEY_RESPONSE_CODE = "ResponseCode";

    String HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage";

    String HEAD_KEY_ACCEPT = "Accept";

    String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";

    String HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate";// no sdch

    String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";

    String HEAD_KEY_CONTENT_TYPE = "Content-Type";

    String HEAD_KEY_CONTENT_LENGTH = "Content-Length";

    String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";

    String HEAD_KEY_CONTENT_RANGE = "Content-Range";

    String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

    String HEAD_KEY_CONNECTION = "Connection";

    String HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive";

    String HEAD_VALUE_CONNECTION_CLOSE = "close";

    String HEAD_KEY_DATE = "Date";

    String HEAD_KEY_EXPIRES = "Expires";

    String HEAD_KEY_E_TAG = "ETag";

    String HEAD_KEY_PRAGMA = "Pragma";

    String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";

    String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";

    String HEAD_KEY_LAST_MODIFIED = "Last-Modified";

    String HEAD_KEY_LOCATION = "Location";

    String HEAD_KEY_USER_AGENT = "User-Agent";

    String HEAD_KEY_COOKIE = "Cookie";

    String HEAD_KEY_COOKIE2 = "Cookie2";

    String HEAD_KEY_SET_COOKIE = "Set-Cookie";

    String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";

    /**
     * Copy all head to Headers.
     *
     * @param headers headers.
     */
    void addAll(Headers headers);

    /**
     * Remove all of the head now, add a new head in.
     *
     * @param headers headers.
     */
    void setAll(Headers headers);

    /**
     * Conform to the URI of the Cookie is added to the head.
     *
     * @param uri url.
     * @param cookieHandler cookieHandler.
     */
    void addCookie(URI uri, CookieHandler cookieHandler);

    /**
     * From the json format String parsing out the {@code Map<String, List<String>>} data.
     *
     * @param jsonString json string.
     * @throws JSONException thrown it when format error.
     */
    void setJSONString(String jsonString) throws JSONException;

    /**
     * Into a json format string.
     *
     * @return Json format data.
     */
    String toJSONString();

    /**
     * Into a single key-value map.
     *
     * @return Map.
     */
    Map<String, String> toRequestHeaders();

    /**
     * To multiple key - the value of the map.
     *
     * @return Map format data.
     */
    Map<String, List<String>> toResponseHeaders();

    /**
     * All the cookies in header information.
     *
     * @return {@code List<HttpCookie>}.
     */
    List<HttpCookie> getCookies();

    /**
     * {@value #HEAD_KEY_CACHE_CONTROL}.
     *
     * @return CacheControl.
     */
    String getCacheControl();

    /**
     * {@value #HEAD_KEY_CONTENT_ENCODING}.
     *
     * @return ContentEncoding.
     */
    String getContentEncoding();

    /**
     * {@value #HEAD_KEY_CONTENT_LENGTH}.
     *
     * @return ContentLength.
     */
    int getContentLength();

    /**
     * {@value #HEAD_KEY_CONTENT_TYPE}.
     *
     * @return ContentType.
     */
    String getContentType();

    /**
     * {@value #HEAD_KEY_DATE}.
     *
     * @return Date.
     */
    long getDate();

    /**
     * {@value #HEAD_KEY_E_TAG}.
     *
     * @return ETag.
     */
    String getETag();

    /**
     * {@value #HEAD_KEY_EXPIRES}.
     *
     * @return Expiration.
     */
    long getExpiration();

    /**
     * {@value #HEAD_KEY_LAST_MODIFIED}.
     *
     * @return LastModified.
     */
    long getLastModified();

    /**
     * {@value #HEAD_KEY_LOCATION}.
     *
     * @return Location.
     */
    String getLocation();

    /**
     * {@value #HEAD_KEY_RESPONSE_CODE}.
     *
     * @return ResponseCode.
     */
    int getResponseCode();

    /**
     * {@value #HEAD_KEY_RESPONSE_MESSAGE}.
     *
     * @return ResponseMessage.
     */
    String getResponseMessage();
}
