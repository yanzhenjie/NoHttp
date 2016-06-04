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
package com.yanzhenjie.nohttp.sample;

import com.yanzhenjie.nohttp.sample.config.AppConfig;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

/**
 * Created in Oct 23, 2015 12:59:13 PM.
 *
 * @author Yan Zhenjie.
 */
public class Application extends android.app.Application {

    private static Application _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        NoHttp.initialize(this);

        Logger.setTag("NoHttpSample");
        Logger.setDebug(true);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志

        AppConfig.getInstance();
    }

    public static Application getInstance() {
        return _instance;
    }

}
