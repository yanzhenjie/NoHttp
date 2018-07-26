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
package com.yanzhenjie.nohttp.sample.app.download;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanzhenjie.nohttp.sample.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by YanZhenjie on 2018/3/28.
 */
public class DownloadView
  extends Contract.DownloadView {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_speed)
    TextView mTvProgress;
    @BindView(R.id.tv_status)
    TextView mTvStatus;

    @BindView(R.id.btn_download)
    Button mBtnOperation;

    public DownloadView(Activity activity, Contract.DownloadPresenter presenter) {
        super(activity, presenter);
    }

    @Override
    public void onStart() {
        mBtnOperation.setText(R.string.download_stop);
        mTvStatus.setText(R.string.download_status_start);
    }

    @Override
    public void setProgress(int progress, String speed) {
        mProgressBar.setProgress(progress);
        mTvProgress.setText(speed);

        String progressText = getString(R.string.download_progress, progress);
        mTvStatus.setText(progressText);
    }

    @Override
    public void onFinish() {
        mBtnOperation.setText(R.string.download_start);
        mTvStatus.setText(R.string.download_status_finish);
    }

    @Override
    public void onError(String message) {
        mBtnOperation.setText(R.string.download_start);
        mTvStatus.setText(R.string.download_status_error);
    }

    @Override
    public void onCancel() {
        mBtnOperation.setText(R.string.download_start);
        mTvStatus.setText(R.string.download_status_cancel);
    }

    @OnClick(R.id.btn_download)
    void onViewClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_download: {
                getPresenter().tryDownload();
                break;
            }
        }
    }

}