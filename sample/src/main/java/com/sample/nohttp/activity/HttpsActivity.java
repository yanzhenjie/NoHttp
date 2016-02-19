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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.SSLContextUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created in Nov 3, 2015 1:48:34 PM
 *
 * @author YOLANDA
 */
public class HttpsActivity extends BaseActivity implements View.OnClickListener, HttpListener<String> {

    /**
     * 显示请求结果
     */
    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[12]);
        setContentView(R.layout.activity_https);

        findView(R.id.btn_https_reqeust).setOnClickListener(this);
        mTvResult = findView(R.id.tv_result);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_https_reqeust) {
            Request<String> httpsRequest = NoHttp.createStringRequest("https://kyfw.12306.cn/otn", RequestMethod.POST);
            SSLContext sslContext = SSLContextUtil.getSSLContext();
            if (sslContext != null) {
                SSLSocketFactory socketFactory = sslContext.getSocketFactory();
                httpsRequest.setSSLSocketFactory(socketFactory);
            }
            CallServer.getRequestInstance().add(this, 0, httpsRequest, this, false, true);
        }
    }

    @Override
    public void onSucceed(int what, Response<String> response) {
        mTvResult.setText("成功：\n" + response.get());
    }

    @Override
    public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
        mTvResult.setText("失败：\n" + message);
    }
}
