/*
 * Copyright 2015 Yan Zhenjie
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
package com.yanzhenjie.nohttp.sample.activity.download;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.config.AppConfig;
import com.yanzhenjie.nohttp.sample.nohttp.CallServer;
import com.yanzhenjie.nohttp.sample.util.Constants;
import com.yanzhenjie.nohttp.sample.util.Snackbar;
import com.yanzhenjie.nohttp.tools.IOUtils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * <p>下载单个文件。</p>
 * Created on 2016/5/30.
 *
 * @author Yan Zhenjie;
 */
public class DownloadSingleFileActivity extends BaseActivity {

    private final static String PROGRESS_KEY = "download_single_progress";
    /**
     * 下载按钮、暂停、开始等.
     */
    TextView mBtnStart;
    /**
     * 下载状态.
     */
    TextView mTvResult;
    /**
     * 下载进度条.
     */
    ProgressBar mProgressBar;
    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_download_single);
        mBtnStart = (TextView) findViewById(R.id.btn_start_download);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
    }

    /**
     * 开始下载。
     */
    private void download() {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。

            mDownloadRequest = new DownloadRequest(Constants.URL_DOWNLOADS[0], RequestMethod.GET,
                    AppConfig.getInstance().APP_PATH_ROOT,
                    true, true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getInstance().download(0, mDownloadRequest, downloadListener);

            // 添加到队列，在没响应的时候让按钮不可用。
            mBtnStart.setEnabled(false);
        }
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            int progress = AppConfig.getInstance().getInt(PROGRESS_KEY, 0);
            if (allCount != 0) {
                progress = (int) (beforeLength * 100 / allCount);
                mProgressBar.setProgress(progress);
            }
            updateProgress(progress, 0);

            mBtnStart.setText(R.string.download_status_pause);
            mBtnStart.setEnabled(true);
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            Logger.e(exception);
            mBtnStart.setText(R.string.download_status_again_download);
            mBtnStart.setEnabled(true);

            String message = getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = getString(R.string.download_error_url);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
            mTvResult.setText(message);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            updateProgress(progress, speed);
            mProgressBar.setProgress(progress);
            AppConfig.getInstance().putInt(PROGRESS_KEY, progress);
        }

        @Override
        public void onFinish(int what, String filePath) {
            Logger.d("Download finish, file path: " + filePath);
            Snackbar.show(DownloadSingleFileActivity.this, getText(R.string.download_status_finish));// 提示下载完成
            mTvResult.setText(R.string.download_status_finish);

            mBtnStart.setText(R.string.download_status_re_download);
            mBtnStart.setEnabled(true);
        }

        @Override
        public void onCancel(int what) {
            mTvResult.setText(R.string.download_status_be_pause);
            mBtnStart.setText(R.string.download_status_resume);
            mBtnStart.setEnabled(true);
        }

        private void updateProgress(int progress, long speed) {
            double newSpeed = speed / 1024D;
            DecimalFormat decimalFormat = new DecimalFormat("###0.00");
            String sProgress = getString(R.string.download_progress);
            sProgress = String.format(Locale.getDefault(), sProgress, progress, decimalFormat.format(newSpeed));
            mTvResult.setText(sProgress);
        }
    };

    @Override
    protected void onDestroy() {
        // 暂停下载
        if (mDownloadRequest != null) {
            mDownloadRequest.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_download_file_single, menu);
        return true;
    }

    @Override
    protected boolean onOptionsItemSelectedCompat(MenuItem item) {
        if (item.getItemId() == R.id.menu_download_file_delete) {
            IOUtils.delFileOrFolder(AppConfig.getInstance().APP_PATH_ROOT + "/nohttp.apk");
            Snackbar.show(this, R.string.delete_succeed);
        }
        return true;
    }

}
