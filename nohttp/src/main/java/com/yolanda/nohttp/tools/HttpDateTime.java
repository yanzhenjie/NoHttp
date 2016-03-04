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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Helper for parsing an HTTP date.</p>
 * Created in Jan 5, 2016 2:09:49 PM.
 *
 * @author YOLANDA;
 */
public final class HttpDateTime {

    public static final String FORMAT_HTTP_DATA = "EEE, dd MMM y HH:mm:ss 'GMT'";

    public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

    /**
     * Parsing the TimeZone of time in milliseconds.
     *
     * @param gmtTime GRM Time, Format such as: {@value #FORMAT_HTTP_DATA}.
     * @return The number of milliseconds from 1970.1.1.
     * @throws ParseException if an error occurs during parsing.
     */
    public static long parseGMTToMillis(String gmtTime) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
        formatter.setTimeZone(GMT_TIME_ZONE);
        Date date = formatter.parse(gmtTime);
        return date.getTime();
    }

    /**
     * Parsing the TimeZone of time from milliseconds.
     *
     * @param milliseconds the number of milliseconds from 1970.1.1.
     * @return GRM Time, Format such as: {@value #FORMAT_HTTP_DATA}.
     */
    public static String formatMillisToGMT(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
        simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
        return simpleDateFormat.format(date);
    }

    /**
     * Returned the local number of milliseconds after 100.
     *
     * @return Long format time.
     */
    public static long getMaxExpiryMillis() {
        return System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 365L * 100L;
    }

}
