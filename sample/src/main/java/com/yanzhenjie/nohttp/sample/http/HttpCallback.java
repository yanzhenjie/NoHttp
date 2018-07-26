/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

/**
 * Created by Yan Zhenjie on 2016/7/7.
 */
public interface HttpCallback<T> {

    /**
     * 请求回调
     */
    void onResponse(Result<T> response);
}
