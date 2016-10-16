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
package com.yanzhenjie.nohttp.sample.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Yan Zhenjie on 2016/10/16.
 */
public class YanZhenjie extends BaseEntity {

    @JSONField(name = "data")
    private UserInfo data;

    public YanZhenjie() {
    }

    public YanZhenjie(UserInfo data) {
        this.data = data;
    }

    public YanZhenjie(String method, int error, String url, UserInfo data) {
        super(method, error, url);
        this.data = data;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringBuilder("Url: ")
                .append(getUrl())
                .append("\r\nMethod: ")
                .append(getMethod())
                .append("\r\nErrorCode: ")
                .append(getError())
                .append("\r\nYanZhenjie: ")
                .append("\n    WebSite: ")
                .append(getData().getWebsite())
                .append("\n    Blog: ")
                .append(getData().getBlog())
                .toString();
    }
}
