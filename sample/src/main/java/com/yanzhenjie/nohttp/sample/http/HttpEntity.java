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
package com.yanzhenjie.nohttp.sample.http;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by YanZhenjie on 2018/3/26.
 */
public class HttpEntity
  implements Parcelable {

    @JSONField(name = "succeed")
    private boolean mSucceed;

    @JSONField(name = "message")
    private String mMessage;

    @JSONField(name = "data")
    private String mData;

    public HttpEntity() {
    }

    protected HttpEntity(Parcel in) {
        mSucceed = in.readByte() != 0;
        mMessage = in.readString();
        mData = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mSucceed ? 1 : 0));
        dest.writeString(mMessage);
        dest.writeString(mData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HttpEntity> CREATOR = new Creator<HttpEntity>() {
        @Override
        public HttpEntity createFromParcel(Parcel in) {
            return new HttpEntity(in);
        }

        @Override
        public HttpEntity[] newArray(int size) {
            return new HttpEntity[size];
        }
    };

    public boolean isSucceed() {
        return mSucceed;
    }

    public void setSucceed(boolean succeed) {
        mSucceed = succeed;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }
}