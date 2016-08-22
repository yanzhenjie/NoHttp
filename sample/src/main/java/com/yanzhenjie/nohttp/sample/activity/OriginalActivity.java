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
package com.yanzhenjie.nohttp.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.dialog.WaitDialog;
import com.yanzhenjie.nohttp.sample.util.Constants;
import com.yanzhenjie.nohttp.sample.util.Snackbar;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.net.ProtocolException;
import java.util.Locale;

/**
 * <p>最原始的使用方法。这里没有任何的对NoHttp的封装，就是new队列，然后把new请求，把请求添加到队列，完成请求。</p>
 * Created in Nov 4, 2015 1:38:02 PM.
 *
 * @author Yan Zhenjie.
 */
public class OriginalActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 用来标志请求的what, 类似handler的what一样，这里用来区分请求。
     */
    private static final int NOHTTP_WHAT_TEST = 0x001;

    /**
     * 请求的时候等待框。
     */
    private WaitDialog mWaitDialog;

    /**
     * 请求队列。
     */
    private RequestQueue requestQueue;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_original);
        findView(R.id.btn_start).setOnClickListener(this);

        mWaitDialog = new WaitDialog(this);

        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1)。
        // 不过正式的项目中不要每次请求时都创建队列，应该把队列封装成单例模式，这样才能把队列的优点发挥出来。
        requestQueue = NoHttp.newRequestQueue();
    }

    @Override
    public void onClick(View v) {
        // 创建请求对象。
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_TEST, RequestMethod.POST);

        // 添加请求参数。
        request.add("userName", "yolanda"); // String型。
        request.add("userPass", 1); // int型。
        request.add("userAge", 1.25); // double型。
        request.add("nooxxx", 1.2F); // flocat型。

        request.setConnectTimeout(10 * 1000); // 设置连接超时。
        request.setReadTimeout(20 * 1000); // 设置读取超时时间，也就是服务器的响应超时。

        /**
         * 上传文件；上传文件支持File、Bitmap、ByteArrayBinary、InputStream四种，这里推荐File、InputStream。
         */
        // request.add("userHead", new FileBinary());
        // request.add("userHead", new BitmapBinary());
        // request.add("userHead", new ByteArrayBinary());
        // request.add("", new InputStreamBinary());

        // 请求头，是否要添加头，添加什么头，要看开发者服务器端的要求。
        request.addHeader("Author", "sample");

        // 设置一个tag, 在请求完(失败/成功)时原封不动返回; 多数情况下不需要。
        request.setTag(this);

        // 设置取消标志。
        request.setCancelSign(this);

		/*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样。
		 * request: 请求对象。
		 * onResponseListener 回调对象，接受请求结果。
		 */
        requestQueue.add(NOHTTP_WHAT_TEST, request, onResponseListener);
    }

    /**
     * 回调对象，接受请求结果.
     */
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == NOHTTP_WHAT_TEST) {// 根据what判断是哪个请求的返回，这样就可以用一个OnResponseListener来接受多个请求的结果。
                int responseCode = response.getHeaders().getResponseCode();// 服务器响应码。

                if (responseCode == 200) {// 如果是是用NoHttp的默认的请求或者自己没有对NoHttp做封装，这里最好判断下Http状态码。
                    String result = response.get();// 响应结果。

                    ((TextView) findView(R.id.tv_result)).setText(result);

                    Object tag = response.getTag();// 拿到请求时设置的tag。

                    // 响应头
                    Headers headers = response.getHeaders();
                    String headResult = getString(R.string.request_original_result);
                    headResult = String.format(Locale.getDefault(), headResult, headers.getResponseCode(), response.getNetworkMillis());
                    ((TextView) findView(R.id.tv_head)).setText(headResult);
                }
            }
        }

        @Override
        public void onStart(int what) {
            // 请求开始，这里可以显示一个dialog
            if (mWaitDialog != null && !mWaitDialog.isShowing())
                mWaitDialog.show();
        }

        @Override
        public void onFinish(int what) {
            // 请求结束，这里关闭dialog
            if (mWaitDialog != null && mWaitDialog.isShowing())
                mWaitDialog.dismiss();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            //TODO 特别注意：这里可能有人会想到是不是每个地方都要这么判断，其实不用，请参考HttpResponseListener类的封装，你也可以这么封装。

            // 请求失败
            Exception exception = response.getException();
            if (exception instanceof NetworkError) {// 网络不好
                Snackbar.show(OriginalActivity.this, R.string.error_please_check_network);
            } else if (exception instanceof TimeoutError) {// 请求超时
                Snackbar.show(OriginalActivity.this, R.string.error_timeout);
            } else if (exception instanceof UnKnownHostError) {// 找不到服务器
                Snackbar.show(OriginalActivity.this, R.string.error_not_found_server);
            } else if (exception instanceof URLError) {// URL是错的
                Snackbar.show(OriginalActivity.this, R.string.error_url_error);
            } else if (exception instanceof NotFoundCacheError) {
                // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
                Snackbar.show(OriginalActivity.this, R.string.error_not_found_cache);
            } else if (exception instanceof ProtocolException) {
                Snackbar.show(OriginalActivity.this, R.string.error_system_unsupport_method);
            } else if (exception instanceof ParseError) {
                Snackbar.show(OriginalActivity.this, R.string.error_parse_data_error);
            } else {
                Snackbar.show(OriginalActivity.this, R.string.error_unknow);
            }
            Logger.e("错误：" + exception.getMessage());
        }
    };

    @Override
    protected void onDestroy() {
        if (requestQueue != null)
            // 根据取消标志取消队列中的请求。
            requestQueue.cancelBySign(this);
        super.onDestroy();
    }
}
