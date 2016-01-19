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
package com.sample.nohttp.nohttp;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

import com.yolanda.nohttp.cookie.CookieStoreListener;
import com.yolanda.nohttp.cookie.DiskCookieStore;
import com.yolanda.nohttp.util.HttpDateTime;

/**
 * Created in Oct 23, 2015 2:16:17 PM
 * 
 * @author YOLANDA
 */
public class MyCookieManager extends CookieManager implements CookieStoreListener {

	/**
	 * NoHttp会替你维护Cookie，这里可以用自己的CookieManger，添加一些必要的Cookie
	 */
	public MyCookieManager() {
		super(DiskCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL);
		DiskCookieStore.INSTANCE.setCookieStoreListener(this);
	}

	@Override
	public void onSaveCookie(URI uri, HttpCookie cookie) {
		if ("JSESSIONID".equalsIgnoreCase(cookie.getName())) {
			cookie.setMaxAge(HttpDateTime.getMaxExpiryMillis());
		}
	}

	@Override
	public void onRemoveCookie(URI uri, HttpCookie cookie) {
	}

}
