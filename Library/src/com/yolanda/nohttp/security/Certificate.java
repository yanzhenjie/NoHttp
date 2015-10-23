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

import java.io.IOException;
import java.io.InputStream;

import com.yolanda.nohttp.Logger;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created in Sep 28, 2015 5:07:17 PM
 * 
 * @author YOLANDA
 */
public class Certificate {

	private Context mContext;
	/**
	 * Certificate path in assets
	 */
	private String assetsCerName;
	/**
	 * Certificate resources in ID res
	 */
	private int resCerId = -1;
	/**
	 * key pass
	 */
	private String keyPass;

	public Certificate() {
		super();
	}

	/**
	 * @param context context
	 * @param assetsCerName File name in assets, such as: {@code assets/static/srca.cer}, should write {@code static/srca.cer}
	 * @param keyPass Key password, no password can be null or""
	 */
	public Certificate(Context context, String assetsCerName, String keyPass) {
		super();
		this.mContext = context;
		this.assetsCerName = assetsCerName;
		this.keyPass = keyPass;
	}

	/**
	 * @param context context
	 * @param resCerId Certificate resources in ID res
	 * @param keyPass Key password, no password can be null or""
	 */
	public Certificate(Context context, int resCerId, String keyPass) {
		super();
		this.mContext = context;
		this.resCerId = resCerId;
		this.keyPass = keyPass;
	}

	/**
	 * wether has key pass
	 * 
	 * @return Have to return true, otherwise return false
	 */
	public boolean hasKeyPass() {
		return !TextUtils.isEmpty(keyPass);
	}

	/**
	 * get the cerificate
	 * 
	 * @return string
	 */
	public String getName() {
		if (!TextUtils.isEmpty(assetsCerName)) {
			return assetsCerName.replace(".", "");
		} else if (resCerId > 0) {
			return Integer.toString(resCerId);
		}
		return null;
	}

	/**
	 * Get a certificate of the inputStream
	 * 
	 * @return The inputStream of the certificate, There may be null
	 */
	public InputStream getInputStream() {
		InputStream inputStream = null;
		try {
			if (!TextUtils.isEmpty(assetsCerName)) {
				inputStream = mContext.getAssets().open(assetsCerName);
			}
		} catch (IOException e) {
			Logger.throwable(e);
		}
		if (inputStream == null && resCerId > 0) {
			inputStream = mContext.getResources().openRawResource(resCerId);
		}
		return inputStream;
	}

	/**
	 * key pass char[]
	 * 
	 * @return char[]
	 */
	public char[] getKeyPassCharArray() {
		if (!TextUtils.isEmpty(keyPass)) {
			return keyPass.toCharArray();
		}
		return null;
	}
}
