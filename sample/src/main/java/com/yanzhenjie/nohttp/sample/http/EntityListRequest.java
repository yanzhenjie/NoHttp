/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.nohttp.RequestMethod;

import java.util.Collections;
import java.util.List;

/**
 * <p>请求JsonArray的实体list。</p>
 * Created by Yan Zhenjie on 2016/11/21.
 */
public class EntityListRequest<Entity> extends AbstractRequest<List<Entity>> {

    private Class<Entity> clazz;

    public EntityListRequest(String url, RequestMethod requestMethod, Class<Entity> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }

    @Override
    protected List<Entity> parseEntity(String responseBody) throws Throwable {
        if (!TextUtils.isEmpty(responseBody)) {
            List<Entity> entities = JSON.parseArray(responseBody, clazz);
            return entities == null ? Collections.<Entity>emptyList() : entities;
        }
        return Collections.emptyList();
    }

}
