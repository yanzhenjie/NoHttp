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
package com.yolanda.nohttp.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import com.yolanda.nohttp.BasicAnalyzeRequest;
import com.yolanda.nohttp.Logger;

import android.annotation.SuppressLint;

/**
 * Created in Jul 28, 2015 7:31:45 PM
 * 
 * @author YOLANDA
 */
public final class SecureVerifier {

	/**
	 * The sigle entrance
	 */
	private static SecureVerifier _HttpsVerifier;

	/**
	 * Does all HTTPS requests are allowed
	 */
	private boolean isAllowAll;

	private SecureVerifier() {
		PRNGFixes.apply();
	}

	/**
	 * Get the sigle entrance object
	 */
	public static SecureVerifier getInstance() {
		if (_HttpsVerifier == null) {
			_HttpsVerifier = new SecureVerifier();
		}
		return _HttpsVerifier;
	}

	/**
	 * Does all HTTPS requests are allowed
	 * 
	 * @param isAll true represent allow, false represent disallow
	 */
	public void setAllowAllHttps(boolean isAll) {
		isAllowAll = isAll;
	}

	/**
	 * validate https
	 * 
	 * @param httpsURLConnection HttpsURLConnection
	 * @param certificate The certificate parameter, if set up to allow all Https requests, is ignored.
	 */
	@SuppressLint("TrulyRandom")
	public void doVerifier(HttpsURLConnection httpsURLConnection, BasicAnalyzeRequest request) {
		if (isAllowAll || request.isAllowHttps()) {
			httpsURLConnection.setSSLSocketFactory(DefaultSSLSocketFactory.get());
			httpsURLConnection.setHostnameVerifier(hostnameVerifier);
		} else if (request.getCertificate() != null) {
			try {
				KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
				Certificate certificate = request.getCertificate();

				InputStream inputStream = certificate.getInputStream();
				if (inputStream == null) {
					throw new RuntimeException("Certificate argument error, the " + certificate.getName() + " file not found");
				}
				if (certificate.hasKeyPass()) {
					keystore.load(inputStream, certificate.getKeyPassCharArray());
				} else {
					keystore.load(null);
				}
				keystore.setCertificateEntry("NoHttp_" + certificate.getName(), CertificateFactory.getInstance("X.509").generateCertificate(inputStream));
				inputStream.close();

				TrustManagerFactory managerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				managerFactory.init(keystore);

				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, managerFactory.getTrustManagers(), new SecureRandom());
				httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
			} catch (KeyManagementException e) {
				Logger.e(e);
			} catch (KeyStoreException e) {
				Logger.e(e);
			} catch (NoSuchAlgorithmException e) {
				Logger.e(e);
			} catch (CertificateException e) {
				Logger.e(e);
			} catch (IOException e) {
				Logger.e(e);
			}
		}
	}

	/**
	 * Default allow all address
	 */
	private HostnameVerifier hostnameVerifier = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
}
