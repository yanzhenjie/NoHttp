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
package com.yanzhenjie.nohttp.sample.app.form;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.SimpleUploadListener;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.app.BaseActivity;
import com.yanzhenjie.nohttp.sample.app.form.entity.FileInfo;
import com.yanzhenjie.nohttp.sample.app.form.entity.FileItem;
import com.yanzhenjie.nohttp.sample.config.UrlConfig;
import com.yanzhenjie.nohttp.sample.http.EntityRequest;
import com.yanzhenjie.nohttp.sample.http.HttpCallback;
import com.yanzhenjie.nohttp.sample.http.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/3/22.
 */
public class FormPresenter
  extends BaseActivity
  implements Contract.FormPresenter {

    private Contract.FormView mView;

    private ArrayList<AlbumFile> mAlbumList;
    private List<FileItem> mFileItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        mView = new FormView(this, this);
    }

    @Override
    public void addFile() {
        Album.image(this)
          .multipleChoice()
          .selectCount(3)
          .camera(true)
          .checkedList(mAlbumList)
          .onResult(new Action<ArrayList<AlbumFile>>() {
              @Override
              public void onAction(@NonNull ArrayList<AlbumFile> albumFiles) {
                  mAlbumList = albumFiles;

                  mFileItems = new ArrayList<>();
                  for (AlbumFile albumFile : mAlbumList) {
                      FileItem fileItem = new FileItem();
                      fileItem.setAlbumFile(albumFile);
                      mFileItems.add(fileItem);
                  }
                  mView.setFileList(mFileItems);

                  mView.setStatusText(getString(R.string.form_upload_wait));
              }
          })
          .start();
    }

    @Override
    public void uploadFile() {
        if (mAlbumList != null) {
            executeUpload();
        } else {
            mView.toast(R.string.form_upload_select_error);
        }
    }

    private void executeUpload() {
        FileBinary binary1 = new FileBinary(new File(mAlbumList.get(0).getPath()));
        binary1.setUploadListener(0, new SimpleUploadListener() {
            @Override
            public void onProgress(int what, int progress) {
                mFileItems.get(0).setProgress(progress);
                mView.notifyItem(0);
            }
        });
        FileBinary binary2 = null;
        if (mAlbumList.size() > 1) {
            binary2 = new FileBinary(new File(mAlbumList.get(1).getPath()));
            binary2.setUploadListener(0, new SimpleUploadListener() {
                @Override
                public void onProgress(int what, int progress) {
                    mFileItems.get(1).setProgress(progress);
                    mView.notifyItem(1);
                }
            });
        }
        FileBinary binary3 = null;
        if (mAlbumList.size() > 2) {
            binary3 = new FileBinary(new File(mAlbumList.get(2).getPath()));
            binary3.setUploadListener(0, new SimpleUploadListener() {
                @Override
                public void onProgress(int what, int progress) {
                    mFileItems.get(2).setProgress(progress);
                    mView.notifyItem(2);
                }
            });
        }

        EntityRequest<FileInfo> request =
          new EntityRequest<>(UrlConfig.UPLOAD_FORM, RequestMethod.POST, FileInfo.class);
        request.add("name", "nohttp")
          .add("age", 18)
          .add("file1", binary1)
          .add("file2", binary2)
          .add("file3", binary3);
        request(request, false, new HttpCallback<FileInfo>() {
            @Override
            public void onResponse(Result<FileInfo> response) {
                if (response.isSucceed()) {
                    mAlbumList = null;

                    mView.setStatusText(getString(R.string.form_upload_result));
                } else {
                    mView.toast(response.error());
                }
            }
        });
    }

}