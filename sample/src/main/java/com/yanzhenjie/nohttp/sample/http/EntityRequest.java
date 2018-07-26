/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.nohttp.RequestMethod;

/**
 * <p>请求实体。</p>
 * Created by Yan Zhenjie on 2016/11/16.
 */
public class EntityRequest<Entity extends Parcelable> extends AbstractRequest<Entity> {

    private Class<Entity> clazz;

    public EntityRequest(String url, RequestMethod requestMethod, Class<Entity> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }

    @Override
    protected Entity parseEntity(String responseBody) throws Throwable {
        if (TextUtils.isEmpty(responseBody)) return null;
        else return JSON.parseObject(responseBody, clazz);
    }
}
