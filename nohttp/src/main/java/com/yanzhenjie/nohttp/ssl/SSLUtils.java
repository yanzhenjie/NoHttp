/*
 * Copyright © Yan Zhenjie
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
package com.yanzhenjie.nohttp.ssl;

import android.os.Build;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by YanZhenjie on 2017/6/13.
 */
public class SSLUtils {

    private static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static SSLSocketFactory defaultSSLSocketFactory() {
        return new CompatSSLSocketFactory();
    }

    public static SSLSocketFactory fixSSLLowerThanLollipop(SSLSocketFactory socketFactory) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && !(socketFactory instanceof CompatSSLSocketFactory))
            socketFactory = new CompatSSLSocketFactory(socketFactory);
        return socketFactory;
    }

    public static HostnameVerifier defaultHostnameVerifier() {
        return HOSTNAME_VERIFIER;
    }

}
