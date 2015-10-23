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
package com.sample.nohttp;

import com.sample.nohttp.nohttp.MyCookieManager;
import com.yolanda.nohttp.NoHttp;

import android.app.Application;

/**
 * Created in Oct 23, 2015 12:59:13 PM
 * 
 * @author YOLANDA
 */
public class SampleApplication extends Application {

	private static SampleApplication _instance;

	public String headPath;

	@Override
	public void onCreate() {
		super.onCreate();
		_instance = this;

		NoHttp.setLogTag("NoHttpSample");
		NoHttp.setDebug(true);//开始NoHttp的调试模式，这样就能看到请求过程和日志
		
		// 设置默认的Cookie管理器，不设置的话NoHttp会自动替你完成Cookie的维护
		// 设置Cookit管理器的好处是APP初始化的时候加载必要的Cookie，但是也可以在每个Request对象中添加Cookie
		NoHttp.setDefaultCookieManager(new MyCookieManager());
	}

	public static SampleApplication getInstance() {
		return _instance;
	}

}
