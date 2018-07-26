/*
 * Copyright © 2018 Yan Zhenjie.
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

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DiskCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.yanzhenjie.nohttp.sample.config.AppConfig;
import com.yanzhenjie.nohttp.sample.http.LoginInterceptor;
import com.yanzhenjie.nohttp.sample.util.MediaLoader;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public class App
  extends Application {

    private static App _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (_instance == null) {
            LeakCanary.install(this);
            _instance = this;

            Logger.setDebug(BuildConfig.DEBUG);
        }
    }

    public void initialize() {
        AppConfig.get().initFileDir();

        NoHttp.initialize(InitializationConfig.newBuilder(this)
                            .networkExecutor(new OkHttpNetworkExecutor())
                            .cacheStore(new DiskCacheStore(this))
                            .cookieStore(new DBCookieStore(this))
                            .interceptor(new LoginInterceptor())
                            .build());

        Album.initialize(AlbumConfig.newBuilder(this).setAlbumLoader(new MediaLoader()).build());
    }

    public static App get() {
        return _instance;
    }

}
