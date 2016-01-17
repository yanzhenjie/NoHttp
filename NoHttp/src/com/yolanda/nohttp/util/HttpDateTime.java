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
package com.yolanda.nohttp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Helper for parsing an HTTP date.
 * </br>
 * Created in Jan 5, 2016 2:09:49 PM
 * 
 * @author YOLANDA;
 */
public final class HttpDateTime {

	public static final String FORMAT_HTTP_DATA = "EEE, dd MMM y HH:mm:ss 'GMT'";

	public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

	public static long parseToMillis(String gmtTime) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
		formatter.setTimeZone(GMT_TIME_ZONE);
		Date date = formatter.parse(gmtTime);
		return date.getTime();
	}

	public static String formatToGTM(long milliseconds) {
		Date date = new Date(milliseconds);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_HTTP_DATA, Locale.US);
		simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
		return simpleDateFormat.format(date);
	}

	public static long getMaxExpiryMillis() {
		return System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 365L * 100L;
	}

}
