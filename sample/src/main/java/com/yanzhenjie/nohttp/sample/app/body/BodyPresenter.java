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
package com.yanzhenjie.nohttp.sample.app.body;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.app.BaseActivity;
import com.yanzhenjie.nohttp.sample.app.body.entity.FileInfo;
import com.yanzhenjie.nohttp.sample.config.UrlConfig;
import com.yanzhenjie.nohttp.sample.http.EntityRequest;
import com.yanzhenjie.nohttp.sample.http.HttpCallback;
import com.yanzhenjie.nohttp.sample.http.Result;
import com.yanzhenjie.nohttp.sample.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public class BodyPresenter
  extends BaseActivity
  implements Contract.BodyPresenter {

    private Contract.BodyView mView;

    private AlbumFile mAlbumFile;
    private FileInfo mFileInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        mView = new BodyView(this, this);
    }

    @Override
    public void selectFile() {
        Album.album(this).singleChoice().camera(true).onResult(new Action<ArrayList<AlbumFile>>() {
            @Override
            public void onAction(@NonNull ArrayList<AlbumFile> result) {
                mAlbumFile = result.get(0);
                mView.setLocalFile(mAlbumFile.getPath());
            }
        }).start();
    }

    @Override
    public void updateFile() {
        if (mAlbumFile != null) {
            executeUpload();
        } else {
            mView.toast(R.string.body_upload_select_error);
        }
    }

    @Override
    public void copyPath() {
        if (FileUtils.copyTextToClipboard(this, mFileInfo.getFilepath()))
            mView.toast(R.string.body_copy_successful);
        else mView.toast(R.string.body_copy_failure);
    }

    private void executeUpload() {
        File file = new File(mAlbumFile.getPath());
        InputStream fileStream;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException ignored) {
            throw new AssertionError("omg.");
        }

        EntityRequest<FileInfo> request =
          new EntityRequest<>(UrlConfig.UPLOAD_BODY_FILE, RequestMethod.POST, FileInfo.class);
        request.setDefineRequestBody(fileStream, FileUtils.getMimeType(mAlbumFile.getPath()));
        request(request, new HttpCallback<FileInfo>() {
            @Override
            public void onResponse(Result<FileInfo> response) {
                if (response.isSucceed()) {
                    mAlbumFile = null;

                    mFileInfo = response.get();
                    mView.setRemoteFile(mFileInfo.getFilepath());
                } else {
                    mView.toast(response.error());
                }
            }
        });
    }
}