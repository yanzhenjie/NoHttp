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
package com.sample.nohttp.activity;

import android.os.Bundle;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.StringRequest;

/**
 * 演示怎么取消一个请求
 * </br>
 * Created in Oct 23, 2015 1:13:06 PM
 *
 * @author YOLANDA
 */
public class CancelActivity extends BaseActivity implements HttpListener<String> {

    private static final int REQUEST_1 = 0;
    private static final int REQUEST_2 = 1;
    private static final int REQUEST_3 = 2;

    /**
     * 请求对象
     */
    private Request<String> mRequest;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[9]);
        setContentView(R.layout.activity_cacel);

        // 实例化取消标志
        // 请求对象的取消标志
        Object cancelSign = new Object();

        // 请求1
        mRequest = new StringRequest(Constants.URL_NOHTTP_TEST, RequestMethod.GET);
        mRequest.setCancelSign(cancelSign);

        // 请求2
        Request<String> request1 = new StringRequest(Constants.URL_NOHTTP_TEST, RequestMethod.GET);
        request1.setCancelSign(cancelSign);

        // 请求3
        Request<String> request2 = new StringRequest(Constants.URL_NOHTTP_TEST, RequestMethod.GET);
        request2.setCancelSign(cancelSign);

		/*
         * 说明一下第五个参数, 用户按下back键是否能关闭dialog, dialog关闭时, 也会取消相应的请求
		 */

        // 添加到请求队列
        CallServer.getRequestInstance().add(this, REQUEST_1, mRequest, this, true, true);
        CallServer.getRequestInstance().add(this, REQUEST_2, request1, this, true, false);
        CallServer.getRequestInstance().add(this, REQUEST_3, request2, this, true, false);

        // 以下三种方式选择合适自己的使用, 这里只是例举

        // 取消一个请求
        mRequest.cancel(true);

        // 取消用sign标志的请求，这样就能取消上面三个用cancelSign标志的请求了
        CallServer.getRequestInstance().cancelBySign(cancelSign);

        // 直接取消所有请求，这样会取消队列中所有的请求
        CallServer.getRequestInstance().cancelAll();

        // 停止请求整个请求队列，队列会被停止，再添加请求对象也不会被执行了
        // CallServer.getRequestInstance().stopAll();
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        Logger.i("请求成功: " + response.get());
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        Logger.i("请求失败: " + exception.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时取消请求
        if (mRequest != null)
            mRequest.cancel(true);
    }
}