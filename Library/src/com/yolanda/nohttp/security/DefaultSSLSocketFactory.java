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
package com.yolanda.nohttp.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;

/**
 * Created in Sep 28, 2015 6:26:57 PM
 * 
 * @author YOLANDA
 */
public class DefaultSSLSocketFactory {

	private static SSLSocketFactory mDefaultSocketFactory;

	@SuppressLint("TrulyRandom")
	public static SSLSocketFactory get() {
		if (mDefaultSocketFactory == null)
			synchronized (DefaultSSLSocketFactory.class) {
				if (mDefaultSocketFactory == null)
					try {
						TrustManager[] managers = { trustManager };
						SSLContext sslContext = SSLContext.getInstance("TLS");
						sslContext.init(null, managers, new SecureRandom());
						mDefaultSocketFactory = sslContext.getSocketFactory();
					} catch (KeyManagementException e) {
					} catch (NoSuchAlgorithmException e) {
					}
			}
		return mDefaultSocketFactory;
	}

	private static TrustManager trustManager = new X509TrustManager() {
		private X509Certificate[] certificates;

		@Override
		public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = certificates;
			}
		}

		@Override
		public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = certificates;
			}
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return certificates;
		}
	};
}
