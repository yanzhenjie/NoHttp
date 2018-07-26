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
package com.yanzhenjie.nohttp.sample.app.form.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public class FileInfo
  implements Parcelable {

    @JSONField(name = "name")
    private String mName;
    @JSONField(name = "password")
    private String mPassword;
    @JSONField(name = "file1")
    private String mFile1;
    @JSONField(name = "file2")
    private String mFile2;
    @JSONField(name = "file3")
    private String mFile3;

    public FileInfo() {
    }

    protected FileInfo(Parcel in) {
        mName = in.readString();
        mPassword = in.readString();
        mFile1 = in.readString();
        mFile2 = in.readString();
        mFile3 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPassword);
        dest.writeString(mFile1);
        dest.writeString(mFile2);
        dest.writeString(mFile3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getFile1() {
        return mFile1;
    }

    public void setFile1(String file1) {
        mFile1 = file1;
    }

    public String getFile2() {
        return mFile2;
    }

    public void setFile2(String file2) {
        mFile2 = file2;
    }

    public String getFile3() {
        return mFile3;
    }

    public void setFile3(String file3) {
        mFile3 = file3;
    }
}