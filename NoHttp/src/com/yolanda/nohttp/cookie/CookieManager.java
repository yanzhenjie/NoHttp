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
package com.yolanda.nohttp.cookie;

import java.net.CookiePolicy;
import java.net.CookieStore;

import android.content.Context;

/**
 * </br>
 * Created in Dec 17, 2015 7:56:27 PM
 * 
 * @author YOLANDA;
 */
public class CookieManager extends java.net.CookieManager {

	public CookieManager(Context context) {
		super(new NoHttpCookieStore(context.getApplicationContext()), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
	}

	public CookieManager(CookieStore store) {
		super(store, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
	}

}
