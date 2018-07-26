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

import com.yanzhenjie.album.AlbumFile;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public class FileItem
  implements Parcelable {

    private AlbumFile mAlbumFile;
    private int mProgress;

    public FileItem() {
    }

    protected FileItem(Parcel in) {
        mAlbumFile = in.readParcelable(AlbumFile.class.getClassLoader());
        mProgress = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAlbumFile, flags);
        dest.writeInt(mProgress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileItem> CREATOR = new Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel in) {
            return new FileItem(in);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };

    public AlbumFile getAlbumFile() {
        return mAlbumFile;
    }

    public void setAlbumFile(AlbumFile albumFile) {
        mAlbumFile = albumFile;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }
}