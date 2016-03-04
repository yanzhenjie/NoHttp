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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.config.AppConfig;
import com.sample.nohttp.dialog.WaitDialog;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.sample.nohttp.util.FileUtil;
import com.sample.nohttp.util.Toast;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnUploadListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.error.NotFoundFileError;

import java.io.File;
import java.io.InputStream;

/**
 * <p>
 * 上传文件demo.
 * </p>
 * Created in Oct 23, 2015 8:40:52 AM.
 *
 * @author YOLANDA;
 */
public class UploadFileActivity extends BaseActivity implements View.OnClickListener, HttpListener<String> {

    /**
     * 显示状态.
     */
    private TextView mTvStatus;

    /**
     * 显示结果.
     */
    private TextView mTvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[7]);
        setContentView(R.layout.activity_upload);

        mTvStatus = findView(R.id.tv_status);
        mTvResult = findView(R.id.tv_result);
        findView(R.id.btn_upload_file).setOnClickListener(this);

        saveFile();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_upload_file) {
            uploadFileNoHttp();
        }
    }

    /**
     * 用NoHtt默认实现上传文件.
     */
    private void uploadFileNoHttp() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_UPLOAD, RequestMethod.POST);
        request.add("user", "yolanda");

        // 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了一个FileBinary

        // 这里预先写了一个人间到SD卡
        String filePath = AppConfig.getInstance().APP_PATH_ROOT + File.separator;
        FileBinary fileBinary0 = new FileBinary(new File(filePath + "image1.jpg"));
        FileBinary fileBinary1 = new FileBinary(new File(filePath + "image2.jpg"));

        // 文件上传进度
        fileBinary0.setUploadListener(0, mOnUploadListener);
        fileBinary1.setUploadListener(1, mOnUploadListener);

        request.add("image0", fileBinary0);// 添加头像
        request.add("image1", fileBinary1);// 添加头像

        CallServer.getRequestInstance().add(this, 0, request, this, false, false);
        findView(R.id.btn_upload_file).setEnabled(false);
        mTvResult.setText(null);
    }

    private OnUploadListener mOnUploadListener = new OnUploadListener() {

        /**
         * 文件的上传状态记录.
         */
        private String[] uploadStatus = new String[2];

        @Override
        public void onProgress(int what, int progress) {
            uploadStatus[what] = "第" + what + "个文件已上传" + progress + "%";
            String status = TextUtils.join(";\r\n", uploadStatus);
            mTvStatus.setText(status);
        }

        @Override
        public void onStart(int what) {
            uploadStatus[what] = "第" + what + "文件开始上传";
            String status = TextUtils.join(";\r\n", uploadStatus);
            mTvStatus.setText(status);
        }

        @Override
        public void onCancel(int what) {
            uploadStatus[what] = "第" + what + "文件被取消";
            String status = TextUtils.join(";\r\n", uploadStatus);
            mTvStatus.setText(status);
        }

        @Override
        public void onFinish(int what) {
            uploadStatus[what] = "第" + what + "文件上传完成";
            String status = TextUtils.join(";\r\n", uploadStatus);
            mTvStatus.setText(status);
        }

        @Override
        public void onError(int what, Exception exception) {
            uploadStatus[what] = "第" + what + "文件上传发生异常：" + exception.getClass().getName();
            String status = TextUtils.join(";\r\n", uploadStatus);
            mTvStatus.setText(status);
            if (exception instanceof NotFoundFileError) {
                Toast.show("第" + what + "个文件不存在");
            }
        }
    };

    @Override
    public void onSucceed(int what, Response<String> response) {
        mTvResult.setText(response.get());
        findView(R.id.btn_upload_file).setEnabled(true);
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        mTvResult.setText("上传错误：" + exception.getClass().getName() + "; " + exception.getMessage());
        findView(R.id.btn_upload_file).setEnabled(true);
    }

	/* ====================先保存文件到SD卡==================== */

    private WaitDialog mDialog;

    private void saveFile() {
        mDialog = new WaitDialog(this);
        mDialog.show();
        new Thread(saveFileThread).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mDialog.dismiss();
        }
    };

    private Runnable saveFileThread = new Runnable() {
        @Override
        public void run() {
            try {
                InputStream inputStream = getAssets().open("123.jpg");
                FileUtil.saveFile(inputStream, AppConfig.getInstance().APP_PATH_ROOT + File.separator + "image1.jpg");
                inputStream.close();
                inputStream = getAssets().open("234.jpg");
                FileUtil.saveFile(inputStream, AppConfig.getInstance().APP_PATH_ROOT + File.separator + "image2.jpg");
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.obtainMessage().sendToTarget();
        }
    };
}
