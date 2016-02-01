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
package com.sample.nohttp.util;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.sample.nohttp.Application;

import android.annotation.SuppressLint;

/**
 * </br>
 * Created in Jan 31, 2016 8:03:59 PM
 * 
 * @author YOLANDA
 */
public class SSLContextUtil {

	/**
	 * 拿到SSLContext, NoHttp已经修补了系统的SecureRandom的bug
	 */
	@SuppressLint("TrulyRandom")
	public static SSLContext getSSLContext() {
		SSLContext sslContext = null;
		try {
			InputStream inputStream = Application.getInstance().getAssets().open("srca.cer");
			
	        CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");  
	        Certificate cer = cerFactory.generateCertificate(inputStream);
	        
	        KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        keyStore.load(null, null);
	        keyStore.setCertificateEntry("trust", cer);

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, null);

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);

			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sslContext;
	}

}
