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

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.nohttp.sample.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public class BodyView
  extends Contract.BodyView {

    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.btn_copy)
    Button mBtnCopy;

    public BodyView(Activity activity, Contract.BodyPresenter presenter) {
        super(activity, presenter);
    }

    @OnClick({R.id.btn_select, R.id.btn_upload, R.id.btn_copy})
    void onViewClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_select: {
                getPresenter().selectFile();
                break;
            }
            case R.id.btn_upload: {
                getPresenter().updateFile();
                break;
            }
            case R.id.btn_copy: {
                getPresenter().copyPath();
                break;
            }
        }
    }

    @Override
    public void setLocalFile(String filepath) {
        Album.getAlbumConfig().getAlbumLoader().load(mIvImage, filepath);

        mTvStatus.setText(R.string.body_status_un_upload);
        mBtnCopy.setVisibility(View.GONE);
    }

    @Override
    public void setRemoteFile(String filepath) {
        Album.getAlbumConfig().getAlbumLoader().load(mIvImage, filepath);

        mTvStatus.setText(R.string.body_status_upload_succeed);
        mBtnCopy.setVisibility(View.VISIBLE);
    }
}