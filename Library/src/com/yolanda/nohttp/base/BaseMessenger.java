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

import android.os.Message;

/**
 * Created in Jul 31, 2015 10:43:32 AM
 * 
 * @author YOLANDA
 */
public abstract class BaseMessenger {

	private int what;

	private BaseListener mResponseListener;

	public BaseMessenger(int what, BaseListener baseListener) {
		super();
		this.what = what;
		this.mResponseListener = baseListener;
	}

	public int getWhat() {
		return what;
	}

	public BaseListener getResponseListener() {
		return mResponseListener;
	}

	public abstract Message obtain();

	public abstract void callback();

}
