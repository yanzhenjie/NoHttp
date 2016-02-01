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
package com.sample.nohttp.util;

import com.sample.nohttp.Application;

/**
 * </br>
 * Created in Jan 31, 2016 4:15:36 PM
 * 
 * @author YOLANDA
 */
public class Toast {

	public static void show(CharSequence msg) {
		android.widget.Toast.makeText(Application.getInstance(), msg, android.widget.Toast.LENGTH_LONG).show();
	}

	public static void show(int stringId) {
		android.widget.Toast.makeText(Application.getInstance(), stringId, android.widget.Toast.LENGTH_LONG).show();
	}

}
