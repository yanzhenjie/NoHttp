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

import android.os.Build;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created in Oct 15, 2015 12:39:06 PM.
 *
 * @author Yan Zhenjie.
 */
public class UserAgent {

    /**
     * UserAgent.
     */
    private static String userAgent;

    /**
     * Get the singleton UA.
     *
     * @return String.
     * @see #newInstance()
     */
    public static String instance() {
        if (TextUtils.isEmpty(userAgent))
            userAgent = newInstance();
        return userAgent;
    }

    /**
     * Get User-Agent of System.
     *
     * @return UA.
     */
    public static String newInstance() {
        String webUserAgent = "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 %sSafari/533.1";

        StringBuffer buffer = new StringBuffer();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append("1.0");
        }
        buffer.append("; ");

        Locale locale = Locale.getDefault();
        final String language = locale.getLanguage();
        if (language != null) {
            buffer.append(language.toLowerCase(locale));
            final String country = locale.getCountry();
            if (!TextUtils.isEmpty(country)) {
                buffer.append("-");
                buffer.append(country.toLowerCase(locale));
            }
        } else {
            // default to "en"
            buffer.append("en");
        }
        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            if (Build.MODEL.length() > 0) {
                buffer.append("; ");
                buffer.append(Build.MODEL);
            }
        }
        if (Build.ID.length() > 0) {
            buffer.append(" Build/");
            buffer.append(Build.ID);
        }
        return String.format(webUserAgent, buffer, "Mobile ");
    }

}
