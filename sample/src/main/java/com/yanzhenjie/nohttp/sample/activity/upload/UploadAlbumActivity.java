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
package com.yanzhenjie.nohttp.sample.activity.upload;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.task.LocalImageLoader;
import com.yanzhenjie.durban.Durban;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.config.AppConfig;
import com.yanzhenjie.nohttp.sample.nohttp.HttpListener;
import com.yanzhenjie.nohttp.sample.util.Constants;

import java.io.File;

/**
 * <p>从相册选择图片上传。</p>
 * Created on 2016/6/3.
 *
 * @author Yan Zhenjie;
 */
public class UploadAlbumActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 相册选择回调。
     */
    private static final int RESULT_BACK_ALBUM = 0x01;

    /**
     * 展示选择的头像。
     */
    ImageView mIvIcon;
    /**
     * 显示状态。
     */
    TextView mTvResult;
    /**
     * 显示进度。
     */
    ProgressBar mProgressBar;

    private String filePath;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_upload_album);
        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);

        findViewById(R.id.btn_album).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
    }

    /**
     * 按钮点击。
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_album) {
            // 去相册选择图片。
            Album.album(this)
                    .columnCount(2)
                    .selectCount(1)
                    .requestCode(RESULT_BACK_ALBUM)
                    .start();
            // 相册开源项目：https://github.com/yanzhenjie/Album
        } else if (v.getId() == R.id.btn_start) {
            executeUpload();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case 1: { // 接受到相册图片选择结果。
                String tempFilePath = Album.parseResult(data).get(0);

                // 裁剪图片：开源项目：https://github.com/yanzhenjie/Durban
                Durban.with(this)
                        .requestCode(2)
                        .outputDirectory(AppConfig.getInstance().APP_PATH_ROOT)
                        .inputImagePaths(tempFilePath)
                        .aspectRatio(1, 1)
                        .start();
                break;
            }
            case 2: { // 接受图片裁剪结果。
                // 记录路径，并加载到页面。
                this.filePath = Durban.parseResult(data).get(0);
                LocalImageLoader.getInstance().loadImage(mIvIcon, filePath);
                break;
            }
        }
    }

    /**
     * 执行上传任务。
     */
    private void executeUpload() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_UPLOAD, RequestMethod.POST);

        // 添加普通参数。
        request.add("user", "yolanda");

        // 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了FileBinary、InputStreamBinary、ByteArrayBitnary、BitmapBinary。
        FileBinary fileBinary0 = new FileBinary(new File(filePath));
        /**
         * 监听上传过程，如果不需要监听就不用设置。
         * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
         * 第二个参数： 监听器。
         */
        fileBinary0.setUploadListener(0, mOnUploadListener);

        request.add("userHead", fileBinary0);// 添加1个文件

        request(0, request, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                showMessageDialog(R.string.request_succeed, response.get());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showMessageDialog(R.string.request_failed, response.getException().getMessage());
            }
        }, false, true);
    }

    /**
     * 文件上传监听。
     */
    private OnUploadListener mOnUploadListener = new OnUploadListener() {

        @Override
        public void onStart(int what) {// 这个文件开始上传。
            mTvResult.setText(R.string.upload_start);
        }

        @Override
        public void onCancel(int what) {// 这个文件的上传被取消时。
            mTvResult.setText(R.string.upload_cancel);
        }

        @Override
        public void onProgress(int what, int progress) {// 这个文件的上传进度发生边耍
            mProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish(int what) {// 文件上传完成
            mTvResult.setText(R.string.upload_succeed);
        }

        @Override
        public void onError(int what, Exception exception) {// 文件上传发生错误。
            mTvResult.setText(R.string.upload_error);
        }
    };
}
