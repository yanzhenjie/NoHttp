/**
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
package com.yolanda.nohttp.base;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.yolanda.nohttp.NoHttp;

import android.content.Context;

/**
 * Created in Jul 28, 2015 7:31:45 PM
 * 
 * @author YOLANDA
 */
public class HttpsVerifier {

	private static SSLContext mContext;

	private HttpsVerifier() {
	}

	/**
	 * Open an asset using ACCESS_STREAMING mode. This provides access to
	 * files that have been bundled with an application as assets -- that is,
	 * files placed in to the "assets" directory.
	 * 
	 * @param assetManager Used to read files in the assets
	 * @param fileName The name of the asset to open. This name can be hierarchical.Like
	 *        file:///android_asset/yolanda.crt
	 * @throws CertificateException .
	 * @throws IOException .
	 * @throws KeyStoreException .
	 * @throws NoSuchAlgorithmException .
	 * @throws KeyManagementException .
	 */
	public static void initVerify(Context context, String fileName) throws CertificateException, IOException,
			KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream in = context.getAssets().open(fileName);
		Certificate ca = cf.generateCertificate(in);

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(null, null);
		keystore.setCertificateEntry("ca", ca);

		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keystore);

		// Create an SSLContext that uses our TrustManager
		mContext = SSLContext.getInstance("TLS");
		mContext.init(null, tmf.getTrustManagers(), null);
	}

	public static void closeVerify() {
		mContext = null;
	}

	/**
	 * By default, all the certification is not recommended
	 * 
	 * @param httpsURLConnection
	 */
	public static void verify(HttpsURLConnection httpsURLConnection) {
		if (mContext != null) {
			httpsURLConnection.setSSLSocketFactory(mContext.getSocketFactory());
		} else {
			dotVerify(httpsURLConnection);
		}
	}

	/**
	 * Don't CRT certificate validation
	 */
	public static void dotVerify(HttpsURLConnection httpsURLConnection) {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			TrustManager[] managers = { trustManager };
			sslContext.init(null, managers, null);
			javax.net.ssl.SSLSocketFactory ssf = sslContext.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			httpsURLConnection.setSSLSocketFactory(ssf);
		} catch (Throwable e) {
			if (NoHttp.isDebug())
				e.printStackTrace();
		}
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
		public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = ax509certificate;
			}
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return certificates;
		}
	};
}
