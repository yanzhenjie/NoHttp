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
package com.sample.nohttp.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

/**
 * Created in Oct 23, 2015 1:19:04 PM
 * 
 * @author YOLANDA
 */
public class WaitDialog extends ProgressDialog {

	public WaitDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setProgressStyle(STYLE_SPINNER);
		setMessage("正在请求，请稍候…");
	}

}
