/**
 * Copyright © Yan Zhenjie. All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.nohttp.sample.util;

import android.content.Context;

/**
 * Created in Jan 31, 2016 4:15:36 PM.
 *
 * @author Yan Zhenjie.
 */
public class Toast {

    public static void show(Context context, CharSequence msg) {
        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, int stringId) {
        android.widget.Toast.makeText(context, stringId, android.widget.Toast.LENGTH_LONG).show();
    }

}
