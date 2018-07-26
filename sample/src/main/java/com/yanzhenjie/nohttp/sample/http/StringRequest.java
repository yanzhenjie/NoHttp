/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

import com.yanzhenjie.nohttp.RequestMethod;

/**
 * <p>请求String。</p>
 * Created by Yan Zhenjie on 2016/11/16.
 */
public class StringRequest extends AbstractRequest<String> {

    public StringRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    protected String parseEntity(String responseBody) throws Throwable {
        return responseBody;
    }
}
