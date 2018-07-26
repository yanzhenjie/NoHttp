/*
 * Copyright 2018 Yan Zhenjie.
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

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by YanZhenjie on 2018/3/28.
 */
public class NewsWrapper
  implements Parcelable {

    @JSONField(name = "dataList")
    private List<News> mDataList;

    @JSONField(name = "page")
    private Page mPage;

    public NewsWrapper() {
    }

    protected NewsWrapper(Parcel in) {
        mDataList = in.createTypedArrayList(News.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mDataList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewsWrapper> CREATOR = new Creator<NewsWrapper>() {
        @Override
        public NewsWrapper createFromParcel(Parcel in) {
            return new NewsWrapper(in);
        }

        @Override
        public NewsWrapper[] newArray(int size) {
            return new NewsWrapper[size];
        }
    };

    public List<News> getDataList() {
        return mDataList;
    }

    public void setDataList(List<News> dataList) {
        mDataList = dataList;
    }

    public Page getPage() {
        return mPage;
    }

    public void setPage(Page page) {
        mPage = page;
    }
}