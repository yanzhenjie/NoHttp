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
package com.yolanda.nohttp;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created in Jul 28, 2015 7:31:45 PM
 * 
 * @author YOLANDA
 */
class HttpsVerifier {

	private HttpsVerifier() {
	}

	/**
	 * Initialize the HTTPS support
	 * 
	 * @param httpsURLConnection HttpsURLConnection object
	 */
	static void init(HttpsURLConnection httpsURLConnection) {
		HttpsVerifier _HttpsSupport = new HttpsVerifier();
		_HttpsSupport.initSSLALL(httpsURLConnection);
	}

	/**
	 * By default, all the certification is not recommended
	 * 
	 * @param httpsURLConnection
	 */
	private void initSSLALL(HttpsURLConnection httpsURLConnection) {
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
			if (NoHttp.welldebug)
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

	/**
	 * 支持指定load-der.crt证书验证，此种方式Android官方建议
	 * 
	 * @author YOLANDA
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	/*
	 * public void get() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException,
	 * KeyManagementException{
	 * CertificateFactory cf = CertificateFactory.getInstance("X.509");
	 * InputStream in = Application.getInstance().getAssets().open("load-der.crt");
	 * Certificate ca = cf.generateCertificate(in);
	 * 
	 * KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	 * keystore.load(null, null);
	 * keystore.setCertificateEntry("ca", ca);
	 * 
	 * String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
	 * TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
	 * tmf.init(keystore);
	 * 
	 * // Create an SSLContext that uses our TrustManager
	 * SSLContext context = SSLContext.getInstance("TLS");
	 * context.init(null, tmf.getTrustManagers(), null);
	 * URL url = new URL("https://certs.cac.washington.edu/CAtest/");
	 * HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
	 * urlConnection.setSSLSocketFactory(context.getSocketFactory());
	 * }
	 */
}
