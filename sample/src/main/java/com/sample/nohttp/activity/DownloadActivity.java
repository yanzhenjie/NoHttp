/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.nohttp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.config.AppConfig;
import com.sample.nohttp.nohttp.CallServer;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.error.ArgumentError;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ReadWriteError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageCantWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

/**
 * 下载件demo</br>
 * Created in Oct 10, 2015 12:58:25 PM
 *
 * @author YOLANDA
 */
public class DownloadActivity extends BaseActivity implements View.OnClickListener, DownloadListener {

    private final static String PROGRESS_KEY = "download_progress";
    /**
     * 下载按钮、暂停、开始等
     */
    private TextView mBtnStart;
    /**
     * 下载状态
     */
    private TextView mTvStatus;
    /**
     * 下载进度条
     */
    private ProgressBar mProgressBar;
    /***
     * 下载地址
     */
//    private String url = "http://m.apk.67mo.com/apk/999129_21769077_1443483983292.apk";
    private String url = "http://113.105.125.2:8083/fUpload/FastDownloadServlet?fileId=group1/M00/03/9E/AAGGoVbOz1KEeR7tAAAAAIpqEeM711.mp4";
    /**
     * 下载请求
     */
    private DownloadRequest downloadRequest;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[8]);
        setContentView(R.layout.activity_download);

        mProgressBar = findView(R.id.pb_progress);
        mBtnStart = findView(R.id.btn_start_download);
        mTvStatus = findView(R.id.tv_status);
        mBtnStart.setOnClickListener(this);

        // url 下载地址
        // fileFloader 保存的文件夹
        // fileName 文件名
        // isRange 是否断点续传下载
        // isDeleteOld 如果发现文件已经存在是否删除后重新下载
        downloadRequest = NoHttp.createDownloadRequest(url, AppConfig.getInstance().APP_PATH_ROOT, "nohttp1.mp4", true, false);

        // 检查之前的下载状态
        int beforeStatus = downloadRequest.checkBeforeStatus();
        switch (beforeStatus) {
            case DownloadRequest.STATUS_RESTART:
                mProgressBar.setProgress(0);
                mBtnStart.setText("开始下载");
                break;
            case DownloadRequest.STATUS_RESUME:
                int progress = AppConfig.getInstance().getInt(PROGRESS_KEY, 0);
                mProgressBar.setProgress(progress);
                mBtnStart.setText("已下载: " + progress + "%; 继续下载");
                break;
            case DownloadRequest.STATUS_FINISH:
                mProgressBar.setProgress(100);
                mBtnStart.setText("已下载完成");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (downloadRequest.isStarted()) {
            // 暂停下载
            downloadRequest.cancel(true);
        } else {
            // what 区分下载
            // downloadRequest 下载请求对象
            // downloadListener 下载监听
            CallServer.getDownloadInstance().add(0, downloadRequest, this);
        }
    }

    @Override
    public void onStart(int what, boolean isResume, long beforeLenght, Headers headers, long allCount) {
        int progress = 0;
        if (allCount != 0) {
            progress = (int) (beforeLenght * 100 / allCount);
            mProgressBar.setProgress(progress);
        }
        mTvStatus.setText("已下载: " + progress + "%");
        mBtnStart.setText("暂停");
    }

    @Override
    public void onDownloadError(int what, Exception exception) {
        mBtnStart.setText("再次尝试");

        String message = "下载出错了：";
        if (exception instanceof ClientError) {
            message += "客户端错误";
        } else if (exception instanceof ServerError) {
            message += "服务器发生内部错误";
        } else if (exception instanceof NetworkError) {
            message += "网络不可用，请检查网络";
        } else if (exception instanceof StorageCantWriteError) {
            message += "存储位置不可写入";
        } else if (exception instanceof StorageSpaceNotEnoughError) {
            message += "存储位置空间不足";
        } else if (exception instanceof TimeoutError) {
            message += "下载超时";
        } else if (exception instanceof UnKnownHostError) {
            message += "服务器找不到";
        } else if (exception instanceof URLError) {
            message += "url地址错误";
        } else if (exception instanceof ArgumentError) {
            message += "下载参数错误";
        } else if(exception instanceof ReadWriteError) {
            message += "SD卡错误";
        } else {
            message += "未知错误";
        }
        mTvStatus.setText(message);
    }

    @Override
    public void onProgress(int what, int progress, long fileCount) {
        mTvStatus.setText("已下载: " + progress + "%");
        mProgressBar.setProgress(progress);
        AppConfig.getInstance().putInt(PROGRESS_KEY, progress);
    }

    @Override
    public void onFinish(int what, String filePath) {
        mTvStatus.setText("下载完成, 文件保存在: \n" + filePath);
        mBtnStart.setText("重新下载");
    }

    @Override
    public void onCancel(int what) {
        mTvStatus.setText("下载被取消");
        mBtnStart.setText("继续下载");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadRequest != null)
            downloadRequest.cancel(true);
    }
}