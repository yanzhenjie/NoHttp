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

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.dialog.WaitDialog;
import com.sample.nohttp.nohttp.HttpResponseListener;
import com.sample.nohttp.util.Constants;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * <p>最原始的使用方法</p>
 * Created in Nov 4, 2015 1:38:02 PM
 *
 * @author YOLANDA
 */
public class OriginalActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 用来标志请求的what, 类似handler的what一样，这里用来区分请求
     */
    private static final int NOHTTP_WHAT_TEST = 0x001;

    /**
     * 请求的时候等待框
     */
    private WaitDialog mWaitDialog;

    /**
     * 请求队列
     */
    private RequestQueue requestQueue;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[0]);
        setContentView(R.layout.activity_original);

        findView(R.id.btn_start).setOnClickListener(this);

        mWaitDialog = new WaitDialog(this);

        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
    }

    @Override
    public void onClick(View v) {
        // 创建请求对象
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_TEST, RequestMethod.POST);

        // 添加请求参数
        request.add("userName", "yolanda");
        request.add("userPass", 1);
        request.add("userAge", 1.25);

        // 上传文件
        // request.add("userHead", new FileBinary(new File(AppConfig.APP_UPLOAD_FILE_PATH)));

        // 添加请求头
        request.addHeader("Author", "nohttp_sample");

        // 设置一个tag, 在请求完(失败/成功)时原封不动返回; 多数情况下不需要
        request.setTag(new Object());

		/*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(NOHTTP_WHAT_TEST, request, onResponseListener);
    }

    /**
     * 回调对象，接受请求结果
     */
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == NOHTTP_WHAT_TEST) {
                // 请求成功
                String result = response.get();// 响应结果

                ((TextView) findView(R.id.tv_status)).setText(result);
                Object tag = response.getTag();// 拿到请求时设置的tag
                byte[] responseBody = response.getByteArray();// 如果需要byteArray

                // 响应头
                Headers headers = response.getHeaders();

                StringBuilder headBuild = new StringBuilder("响应码: ");
                headBuild.append(headers.getResponseCode());// 响应码
                headBuild.append("\n请求花费时间: ");
                headBuild.append(response.getNetworkMillis()).append("毫秒"); // 请求花费的时间
                ((TextView) findView(R.id.tv_head)).setText(headBuild.toString());
            }
        }

        @Override
        public void onStart(int what) {
            // 请求开始，这里可以显示一个dialog
            mWaitDialog.show();
        }

        @Override
        public void onFinish(int what) {
            // 请求结束，这里关闭dialog
            mWaitDialog.dismiss();
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            new HttpResponseListener<String>(null, null, null, false, false).onFailed(0, null, null, exception, 0, 0);
            // 请求失败
            ((TextView) findView(R.id.tv_status)).setText("请求失败: " + exception.getMessage());
        }
    };

}
