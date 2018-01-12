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
package com.yanzhenjie.nohttp;

import android.text.TextUtils;

import com.yanzhenjie.nohttp.tools.BasicMultiValueMap;
import com.yanzhenjie.nohttp.tools.HeaderUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * <p>
 * Http header.
 * </p>
 * Created in Jan 10, 2016 2:29:42 PM.
 *
 * @author Yan Zhenjie.
 */
public class Headers extends BasicMultiValueMap<String, String> {

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_RESPONSE_CODE = "ResponseCode";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_ACCEPT = "Accept";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_ACCEPT_ALL = "application/json,application/xml,application/xhtml+xml,text/html;q=0.9,image/webp,*/*;q=0.8";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_ACCEPT_ENCODING_GZIP_DEFLATE = "gzip, deflate";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_ACCEPT_RANGE = "Accept-Range";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CONTENT_RANGE = "Content-Range";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CONTENT_TYPE = "Content-Type";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONTENT_TYPE_FORM_DATA = "multipart/form-data";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONTENT_TYPE_JSON = "application/json";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONTENT_TYPE_XML = "application/xml";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_CONNECTION = "Connection";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_VALUE_CONNECTION_CLOSE = "close";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_DATE = "Date";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_EXPIRES = "Expires";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_E_TAG = "ETag";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_PRAGMA = "Pragma";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_LOCATION = "Location";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_USER_AGENT = "User-Agent";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_COOKIE = "Cookie";

    /**
     * The value is {@value}.
     */
    public static final String HEAD_KEY_SET_COOKIE = "Set-Cookie";

    /**
     * Format to Hump-shaped words.
     */
    public static String formatKey(String key) {
        if (TextUtils.isEmpty(key))
            return "";

        key = key.toLowerCase(Locale.ENGLISH);
        String[] words = key.split("-");

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            String first = word.substring(0, 1);
            String end = word.substring(1, word.length());
            builder.append(first.toUpperCase(Locale.ENGLISH)).append(end).append("-");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.lastIndexOf("-"));
        }
        return builder.toString();
    }

    public Headers() {
        super(new TreeMap<String, List<String>>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        }) {
            @Override
            public List<String> put(String key, List<String> value) {
                return super.put(formatKey(key), value);
            }

            @Override
            public List<String> get(Object key) {
                if (key != null) {
                    key = formatKey(key.toString());
                }
                return super.get(key);
            }

            @Override
            public List<String> remove(Object key) {
                if (key != null) {
                    key = formatKey(key.toString());
                }
                return super.remove(key);
            }

            @Override
            public boolean containsKey(Object key) {
                if (key != null) {
                    key = formatKey(key.toString());
                }
                return super.containsKey(key);
            }
        });
    }

    /**
     * Remove all of the head now, add a new head in.
     *
     * @param headers headers.
     */
    public void setAll(Headers headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Conform to the URI of the Cookie is added to the head.
     *
     * @param uri           url.
     * @param cookieHandler cookieHandler.
     */
    public void addCookie(URI uri, CookieHandler cookieHandler) {
        try {
            Map<String, List<String>> diskCookies = cookieHandler.get(uri, new HashMap<String, List<String>>());
            for (Map.Entry<String, List<String>> entry : diskCookies.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                if (HEAD_KEY_COOKIE.equalsIgnoreCase(key)) {
                    add(key, TextUtils.join("; ", value));
                }
            }
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    /**
     * From the json format String parsing out the {@code Map<String, List<String>>} data.
     *
     * @param jsonString json string.
     * @throws JSONException thrown it when format error.
     */
    public void setJSONString(String jsonString) throws JSONException {
        clear();
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<String> keySet = jsonObject.keys();
        while (keySet.hasNext()) {
            String key = keySet.next();
            String value = jsonObject.optString(key);
            JSONArray values = new JSONArray(value);
            for (int i = 0; i < values.length(); i++)
                add(key, values.optString(i));
        }
    }

    /**
     * Into a json format string.
     *
     * @return Json format data.
     */
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        Set<Map.Entry<String, List<String>>> entrySet = entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            JSONArray value = new JSONArray(values);
            try {
                jsonObject.put(TextUtils.isEmpty(key) ? "" : key, value);
            } catch (JSONException e) {
                Logger.w(e);
            }
        }

        return jsonObject.toString();
    }

    /**
     * Into a single key-value map.
     *
     * @return Map.
     */
    public Map<String, String> toRequestHeaders() {
        Map<String, String> singleMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            String trueValue = TextUtils.join("; ", value);
            singleMap.put(key, trueValue);
        }
        return singleMap;
    }

    /**
     * To multiple key - the value of the map.
     *
     * @return Map format data.
     */
    public Map<String, List<String>> toResponseHeaders() {
        return getSource();
    }

    /**
     * All the cookies in header information.
     *
     * @return {@code List<HttpCookie>}.
     */
    public List<HttpCookie> getCookies() {
        List<HttpCookie> cookies = new ArrayList<>();
        for (String key : keySet()) {
            if (key.equalsIgnoreCase(HEAD_KEY_SET_COOKIE)) {
                List<String> cookieValues = getValues(key);
                for (String cookieStr : cookieValues) {
                    for (HttpCookie cookie : HttpCookie.parse(cookieStr))
                        cookies.add(cookie);
                }
            }
        }
        return cookies;
    }

    /**
     * {@value #HEAD_KEY_CACHE_CONTROL}.
     *
     * @return CacheControl.
     */
    public String getCacheControl() {
        // first http1.1, second http1.0
        List<String> cacheControls = getValues(HEAD_KEY_CACHE_CONTROL);
        if (cacheControls == null)
            cacheControls = getValues(HEAD_KEY_PRAGMA);
        if (cacheControls == null)
            cacheControls = new ArrayList<>();
        return TextUtils.join(",", cacheControls);
    }

    /**
     * {@value HEAD_KEY_CONTENT_DISPOSITION}.
     *
     * @return {@value HEAD_KEY_CONTENT_DISPOSITION}.
     */
    public String getContentDisposition() {
        return getFirstValue(HEAD_KEY_CONTENT_DISPOSITION);
    }

    /**
     * {@value #HEAD_KEY_CONTENT_ENCODING}.
     *
     * @return ContentEncoding.
     */
    public String getContentEncoding() {
        return getFirstValue(HEAD_KEY_CONTENT_ENCODING);
    }

    /**
     * {@value #HEAD_KEY_CONTENT_LENGTH}.
     *
     * @return ContentLength.
     */
    public int getContentLength() {
        String contentLength = getFirstValue(HEAD_KEY_CONTENT_LENGTH);
        try {
            return Integer.parseInt(contentLength);
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * {@value #HEAD_KEY_CONTENT_TYPE}.
     *
     * @return ContentType.
     */
    public String getContentType() {
        return getFirstValue(HEAD_KEY_CONTENT_TYPE);
    }

    /**
     * {@value #HEAD_KEY_CONTENT_RANGE} or {@value #HEAD_KEY_ACCEPT_RANGE}.
     *
     * @return {@value #HEAD_KEY_CONTENT_RANGE} or {@value #HEAD_KEY_ACCEPT_RANGE}.
     */
    public String getContentRange() {
        String contentRange = getFirstValue(HEAD_KEY_CONTENT_RANGE);
        if (contentRange == null)
            contentRange = getFirstValue(HEAD_KEY_ACCEPT_RANGE);
        return contentRange;
    }

    /**
     * {@value #HEAD_KEY_DATE}.
     *
     * @return Date.
     */
    public long getDate() {
        return getDateField(HEAD_KEY_DATE);
    }

    /**
     * {@value #HEAD_KEY_E_TAG}.
     *
     * @return ETag.
     */
    public String getETag() {
        return getFirstValue(HEAD_KEY_E_TAG);
    }

    /**
     * {@value #HEAD_KEY_EXPIRES}.
     *
     * @return Expiration.
     */
    public long getExpiration() {
        return getDateField(HEAD_KEY_EXPIRES);
    }

    /**
     * {@value #HEAD_KEY_LAST_MODIFIED}.
     *
     * @return LastModified.
     */
    public long getLastModified() {
        return getDateField(HEAD_KEY_LAST_MODIFIED);
    }

    /**
     * {@value #HEAD_KEY_LOCATION}.
     *
     * @return Location.
     */
    public String getLocation() {
        return getFirstValue(HEAD_KEY_LOCATION);
    }

    /**
     * {@value #HEAD_KEY_RESPONSE_CODE}.
     *
     * @return ResponseCode.
     */
    public int getResponseCode() {
        String responseCode = getFirstValue(HEAD_KEY_RESPONSE_CODE);
        try {
            return Integer.parseInt(responseCode);
        } catch (Exception ignored) {
            return 0;
        }
    }

    /**
     * <p>
     * Returns the date value in milliseconds since 1970.1.1, 00:00h corresponding to the header field field. The
     * defaultValue will be returned if no such field can be found in the response header.
     * </p>
     *
     * @param key the header field name.
     * @return the header field represented in milliseconds since January 1, 1970 GMT.
     */
    private long getDateField(String key) {
        String value = getFirstValue(key);
        if (!TextUtils.isEmpty(value))
            try {
                return HeaderUtils.parseGMTToMillis(value);
            } catch (ParseException ignored) {
            }
        return 0;
    }
}
