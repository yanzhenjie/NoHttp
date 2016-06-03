/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.nohttp.sample.nohttp;

import android.content.DialogInterface;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.dialog.WaitDialog;
import com.yanzhenjie.nohttp.sample.util.Toast;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.net.ProtocolException;

/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author Yan Zhenjie.
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private BaseActivity context;
    /**
     * Dialog.
     */
    private WaitDialog mWaitDialog;

    private Request<?> mRequest;

    /**
     * 结果回调.
     */
    private HttpListener<T> callback;

    /**
     * 是否显示dialog.
     */
    private boolean isLoading;

    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(BaseActivity context, Request<?> request, HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        this.context = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new WaitDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback = httpCallback;
        this.isLoading = isLoading;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (isLoading && mWaitDialog != null && !mWaitDialog.isShowing())
            mWaitDialog.show();
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (isLoading && mWaitDialog != null && mWaitDialog.isShowing())
            mWaitDialog.dismiss();
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        int responseCode = response.getHeaders().getResponseCode();
        if (responseCode > 400 && context != null) {
            if (responseCode == 405) {// 405表示服务器不支持这种请求方法，比如GET、POST、TRACE中的TRACE就很少有服务器支持。
                context.showMessageDialog(R.string.request_succeed, R.string.request_method_not_allow);
            } else {// 但是其它400+的响应码服务器一般会有流输出。
                context.showWebDialog(response);
            }
        }
        if (callback != null) {
            callback.onSucceed(what, response);
        }
    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception,
                         int responseCode, long networkMillis) {
        if (exception instanceof NetworkError) {// 网络不好
            Toast.show(context, "请检查网络。");
        } else if (exception instanceof TimeoutError) {// 请求超时
            Toast.show(context, "请求超时，网络不好或者服务器不稳定。");
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            Toast.show(context, "未发现指定服务器。");
        } else if (exception instanceof URLError) {// URL是错的
            Toast.show(context, "URL错误。");
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            Toast.show(context, "没有发现缓存。");
        } else if (exception instanceof ProtocolException) {
            Toast.show(context, "系统不支持的请求方式。");
        } else {
            Toast.show(context, "未知错误。");
        }
        Logger.e("错误：" + exception.getMessage());
        if (callback != null)
            callback.onFailed(what, url, tag, exception, responseCode, networkMillis);
    }

}
