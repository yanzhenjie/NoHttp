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
package com.yolanda.nohttp.tools;

import java.util.Locale;
import java.util.StringTokenizer;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.HttpResponse;
import com.yolanda.nohttp.cache.CacheEntity;

import android.text.TextUtils;

/**
 * <p>Http header information analysis class.</p>
 * Created in Oct 10, 2015 4:58:30 PM.
 *
 * @author YOLANDA;
 */
public class HeaderParser {


    /**
     * A value of the header information.
     *
     * @param content      like {@code text/html;charset=utf-8}.
     * @param key          like {@code charset}.
     * @param defaultValue list {@code utf-8}.
     * @return If you have a value key, you will return the parsed value if you don't return the default value.
     */
    public static String parseHeadValue(String content, String key, String defaultValue) {
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(key)) {
            StringTokenizer stringTokenizer = new StringTokenizer(content, ";");
            while (stringTokenizer.hasMoreElements()) {
                String valuePair = stringTokenizer.nextToken();
                int index = valuePair.indexOf('=');
                if (index > 0) {
                    String name = valuePair.substring(0, index).trim();
                    if (key.equalsIgnoreCase(name)) {
                        defaultValue = valuePair.substring(index + 1).trim();
                        break;
                    }
                }
            }
        }
        return defaultValue;
    }

    /**
     * Whether the content has been compressed.
     *
     * @param contentEncoding read the data from the server's head.
     * @return True: yes, false: no inclusion.
     */
    public static boolean isGzipContent(String contentEncoding) {
        return contentEncoding != null && contentEncoding.toLowerCase(Locale.getDefault()).contains("gzip");
    }

    /**
     * Extracts a {@link CacheEntity} from a {@link HttpResponse}.
     *
     * @param responseHeaders response headers.
     * @param responseBody    response data.
     * @param forceCache      Whether mandatory cache
     * @return Cache entity.
     */
    public static CacheEntity parseCacheHeaders(Headers responseHeaders, byte[] responseBody, boolean forceCache) {
        long now = System.currentTimeMillis();

        long date = responseHeaders.getDate();
        long expires = responseHeaders.getExpiration();

        long maxAge = 0;
        long staleWhileRevalidate = 0;
        boolean mustRevalidate = false;

        String cacheControl = responseHeaders.getCacheControl();
        if (!TextUtils.isEmpty(cacheControl)) {
            StringTokenizer tokens = new StringTokenizer(cacheControl, ",");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken().trim().toLowerCase(Locale.getDefault());
                if ((token.equals("no-cache") || token.equals("no-store")) && !forceCache) {
                    return null;
                } else if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                    }
                } else if (token.startsWith("stale-while-revalidate=")) {
                    try {
                        staleWhileRevalidate = Long.parseLong(token.substring(23));
                    } catch (Exception e) {
                    }
                } else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
                    mustRevalidate = true;
                }
            }
        }

        CacheEntity cacheEntity = new CacheEntity();
        long localExpire = 0;// 缓存相对于本地的到期时间

        // If must-revalidate, 当缓存过期时, 强制从服务器验证
        // Http1.1
        if (!TextUtils.isEmpty(cacheControl)) {
            localExpire = now + maxAge * 1000;
            if (mustRevalidate)
                localExpire += staleWhileRevalidate * 1000;
        }
        // Http1.0
        else if (date > 0 && expires >= date) {
            localExpire = now + (expires - date);
        }

        cacheEntity.setData(responseBody);
        cacheEntity.setLocalExpire(localExpire);
        cacheEntity.setResponseHeaders(responseHeaders);

        return cacheEntity;
    }
}
