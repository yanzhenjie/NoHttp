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
public class UserInfo {

    @JSONField(name = "website")
    private String website;

    @JSONField(name = "blog")
    private String blog;

    public UserInfo() {
    }

    public UserInfo(String website, String blog) {
        this.website = website;
        this.blog = blog;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }
}
