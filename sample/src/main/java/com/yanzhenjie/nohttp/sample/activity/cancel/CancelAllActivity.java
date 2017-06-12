/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.nohttp.sample.activity.cancel;

import android.os.Bundle;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.util.Constants;

/**
 * <p>取消所有请求。</p>
 * Created on 2016/5/31.
 *
 * @author Yan Zhenjie;
 */
public class CancelAllActivity extends BaseActivity {

    private RequestQueue mRequestQueue;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cacel_demo);

        mRequestQueue = NoHttp.newRequestQueue(5);

        // 请求1。
        Request<String> request1 = NoHttp.createStringRequest(Constants.URL_NOHTTP_JSONOBJECT, RequestMethod.GET);

        // 请求2。
        Request<String> request2 = NoHttp.createStringRequest(Constants.URL_NOHTTP_JSONOBJECT, RequestMethod.GET);

        // 请求3。
        Request<String> request3 = NoHttp.createStringRequest(Constants.URL_NOHTTP_JSONOBJECT, RequestMethod.GET);

        // 这里添加很多request进去。
//        mRequestQueue.add(0, request1, null);
//        mRequestQueue.add(1, request2, null);
//        mRequestQueue.add(2, request3, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在一个合适的时机，调用队列的cancelAll就会取消所有的请求，包括正在执行的。
        mRequestQueue.cancelAll();

        // 如果每一个页面都新建了RequestQueue，那么退出页面时需要停止这个Queue。如果是全局一个Queue，那么在退出App时，停止Queue即可。
        mRequestQueue.stop();
    }
}
