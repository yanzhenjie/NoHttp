/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.nohttp.sample.rxjava;

import android.content.Context;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.dialog.WaitDialog;
import com.yanzhenjie.nohttp.sample.util.Toast;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.IParserRequest;
import com.yolanda.nohttp.rest.Response;

import java.net.ProtocolException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Yan Zhenjie on 2016/10/16.
 */
public class RxNoHttp {

    public static <T> void request(final Context context, final IParserRequest<T> request, final Subscriber<Response<T>> subscriber) {
        final WaitDialog waitDialog = new WaitDialog(context);
        waitDialog.show();

        Observable.create((Observable.OnSubscribe<Response<T>>) subscriberOut -> {
            // 最关键的就是用NoHttp的同步请求请求到response了，其它的都是rxjava做的，跟nohttp无关的。
            Response<T> response = NoHttp.startRequestSync(request);
            if (response.isSucceed()) subscriberOut.onNext(response);
            else subscriberOut.onError(response.getException());
            subscriberOut.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<T>>() {
                    @Override
                    public void onCompleted() {
                        if (waitDialog.isShowing()) waitDialog.dismiss();
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 关闭dialog.
                        if (waitDialog.isShowing()) waitDialog.dismiss();

                        // 提示异常信息。
                        if (e instanceof NetworkError) {// 网络不好
                            Toast.show(context, R.string.error_please_check_network);
                        } else if (e instanceof TimeoutError) {// 请求超时
                            Toast.show(context, R.string.error_timeout);
                        } else if (e instanceof UnKnownHostError) {// 找不到服务器
                            Toast.show(context, R.string.error_not_found_server);
                        } else if (e instanceof URLError) {// URL是错的
                            Toast.show(context, R.string.error_url_error);
                        } else if (e instanceof NotFoundCacheError) {
                            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
                            Toast.show(context, R.string.error_not_found_cache);
                        } else if (e instanceof ProtocolException) {
                            Toast.show(context, R.string.error_system_unsupport_method);
                        } else if (e instanceof ParseError) {
                            Toast.show(context, R.string.error_parse_data_error);
                        } else {
                            Toast.show(context, R.string.error_unknow);
                        }
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(Response<T> tResponse) {
                        subscriber.onNext(tResponse);
                    }
                });
    }

}
