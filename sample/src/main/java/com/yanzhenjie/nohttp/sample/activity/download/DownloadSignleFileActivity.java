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

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.config.AppConfig;
import com.yanzhenjie.nohttp.sample.nohttp.CallServer;
import com.yanzhenjie.nohttp.sample.util.Constants;
import com.yanzhenjie.nohttp.sample.util.Snackbar;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.error.ArgumentError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageReadWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.tools.IOUtils;

import java.util.Locale;

/**
 * <p>下载单个文件。</p>
 * Created on 2016/5/30.
 *
 * @author Yan Zhenjie;
 */
public class DownloadSignleFileActivity extends BaseActivity implements View.OnClickListener {

    private final static String PROGRESS_KEY = "download_single_progress";
    /**
     * 下载按钮、暂停、开始等.
     */
    private TextView mBtnStart;
    /**
     * 下载状态.
     */
    private TextView mTvResult;
    /**
     * 下载进度条.
     */
    private ProgressBar mProgressBar;
    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_download_single);

        mProgressBar = findView(R.id.pb_progress);
        mBtnStart = findView(R.id.btn_start_download);
        mTvResult = findView(R.id.tv_result);
        mBtnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_download) {
            download();
        }
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

            /**
             * 这里不传文件名称、不断点续传，则会从响应头中读取文件名自动命名，如果响应头中没有则会从url中截取。
             */
            // url 下载地址。
            // fileFolder 文件保存的文件夹。
            // isDeleteOld 发现文件已经存在是否要删除重新下载。
//            mDownloadRequest = NoHttp.createDownloadRequest(Constants.URL_DOWNLOADS[0], AppConfig.getInstance().APP_PATH_ROOT, true);

            /**
             * 如果使用断点续传的话，一定要指定文件名喔。
             */
            // url 下载地址。
            // fileFolder 保存的文件夹。
            // fileName 文件名。
            // isRange 是否断点续传下载。
            // isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功。
            mDownloadRequest = NoHttp.createDownloadRequest(Constants.URL_DOWNLOADS[0], AppConfig.getInstance().APP_PATH_ROOT, "nohttp.apk", true, true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getDownloadInstance().add(0, mDownloadRequest, downloadListener);

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
            updateProgress(progress);

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
            } else if (exception instanceof ArgumentError) {
                messageContent = getString(R.string.download_error_argument);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
            mTvResult.setText(message);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {
            updateProgress(progress);
            mProgressBar.setProgress(progress);
            AppConfig.getInstance().putInt(PROGRESS_KEY, progress);
        }

        @Override
        public void onFinish(int what, String filePath) {
            Logger.d("Download finish, file path: " + filePath);
            Snackbar.show(DownloadSignleFileActivity.this, getText(R.string.download_status_finish));// 提示下载完成
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

        private void updateProgress(int progress) {
            String sProgress = getString(R.string.download_progress);
            sProgress = String.format(Locale.getDefault(), sProgress, progress);
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
