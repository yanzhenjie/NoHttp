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

import com.yolanda.nohttp.cookie.CookieManager;

/**
 * Created in Oct 23, 2015 2:16:17 PM
 * 
 * @author YOLANDA
 */
public class MyCookieManager extends CookieManager {

	/**
	 * NoHttp会替你维护Cookie，这里可以用自己的CookieManger，添加一些必要的Cookie
	 */
	public MyCookieManager() {
//		String cookieString = "sessionid=f564fsaf3asd4f6as35";
//		Map<String, List<String>> map = new HashMap<>();
//		List<String> lists = new ArrayList<>();
//		lists.add(cookieString);
//		map.put("Set-Cookie", lists);
//		try {
//			put(new URI("http://www.baidu.com"), map);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
	}

}
