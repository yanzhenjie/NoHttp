/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.perfoct.nohttp.sample.activity.upload;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.perfoct.nohttp.sample.R;
import com.perfoct.nohttp.sample.activity.BaseActivity;
import com.perfoct.nohttp.sample.adapter.LoadFileAdapter;
import com.perfoct.nohttp.sample.config.AppConfig;
import com.perfoct.nohttp.sample.entity.LoadFile;
import com.perfoct.nohttp.sample.nohttp.CallServer;
import com.perfoct.nohttp.sample.nohttp.HttpListener;
import com.perfoct.nohttp.sample.util.Constants;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnUploadListener;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>上传多个文件，需要多个key，如果一个传FileList，请看{@link UploadFileListActivity}。</p>
 * Created on 2016/5/30.
 *
 * @author Yan Zhenjie;
 */
public class UploadMultiFileActivity extends BaseActivity {
    /**
     * 第一个文件的标志。
     */
    private final int WHAT_UPLOAD_FIRST = 0x01;
    /**
     * 第二个文件的标志。
     */
    private final int WHAT_UPLOAD_SECOND = 0x02;
    /**
     * 文件item。
     */
    private List<LoadFile> uploadFiles;
    /**
     * 上传文件list的适配器。
     */
    private LoadFileAdapter mUploadFileAdapter;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_upload_file_list);
        RecyclerView recyclerView = findView(R.id.rv_upload_file_list_activity);

        uploadFiles = new ArrayList<>();
        uploadFiles.add(new LoadFile(R.string.file_status_wait, 0));
        uploadFiles.add(new LoadFile(R.string.file_status_wait, 0));

        mUploadFileAdapter = new LoadFileAdapter(uploadFiles);
        recyclerView.setAdapter(mUploadFileAdapter);
    }

    /**
     * 上传多个文件，用多个key。
     */
    private void uploadMultiFile() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_UPLOAD, RequestMethod.POST);

        // 添加普通参数。
        request.add("user", "yolanda");

        // 上传文件需要实现NoHttp的Binary接口，NoHttp默认实现了FileBinary、InputStreamBinary、ByteArrayBitnary、BitmapBinary。
        FileBinary fileBinary1 = new FileBinary(new File(AppConfig.getInstance().APP_PATH_ROOT + "/image1.jpg"));
        FileBinary fileBinary2 = new FileBinary(new File(AppConfig.getInstance().APP_PATH_ROOT + "/image2.jpg"));
        /**
         * 监听上传过程，如果不需要监听就不用设置。
         * 第一个参数：what，what和handler的what一样，会在回调被调用的回调你开发者，作用是一个Listener可以监听多个文件的上传状态。
         * 第二个参数： 监听器。
         */
        fileBinary1.setUploadListener(WHAT_UPLOAD_FIRST, mOnUploadListener);
        fileBinary2.setUploadListener(WHAT_UPLOAD_SECOND, mOnUploadListener);

        request.add("image1", fileBinary1);// 添加第1个文件
        request.add("image1", fileBinary2);// 添加第2个文件

        CallServer.getRequestInstance().add(this, 0, request, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                showMessageDialog(R.string.request_succeed, response.get());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                showMessageDialog(R.string.request_succeed, exception.getMessage());
            }
        }, false, true);
    }

    /**
     * 文件上传监听。
     */
    private OnUploadListener mOnUploadListener = new OnUploadListener() {

        @Override
        public void onStart(int what) {// 这个文件开始上传。
            if (what == WHAT_UPLOAD_FIRST) {
                uploadFiles.get(0).setTitle(R.string.upload_start);
                mUploadFileAdapter.notifyItemChanged(0);
            } else if (what == WHAT_UPLOAD_SECOND) {
                uploadFiles.get(1).setTitle(R.string.upload_start);
                mUploadFileAdapter.notifyItemChanged(1);
            }
        }

        @Override
        public void onCancel(int what) {// 这个文件的上传被取消时。
            if (what == WHAT_UPLOAD_FIRST) {
                uploadFiles.get(0).setTitle(R.string.upload_cancel);
                mUploadFileAdapter.notifyItemChanged(0);
            } else if (what == WHAT_UPLOAD_SECOND) {
                uploadFiles.get(1).setTitle(R.string.upload_cancel);
                mUploadFileAdapter.notifyItemChanged(1);
            }
        }

        @Override
        public void onProgress(int what, int progress) {// 这个文件的上传进度发生边耍
            if (what == WHAT_UPLOAD_FIRST) {
                uploadFiles.get(0).setProgress(progress);
                mUploadFileAdapter.notifyItemChanged(0);
            } else if (what == WHAT_UPLOAD_SECOND) {
                uploadFiles.get(1).setProgress(progress);
                mUploadFileAdapter.notifyItemChanged(1);
            }
        }

        @Override
        public void onFinish(int what) {// 文件上传完成
            if (what == WHAT_UPLOAD_FIRST) {
                uploadFiles.get(0).setTitle(R.string.upload_succeed);
                mUploadFileAdapter.notifyItemChanged(0);
            } else if (what == WHAT_UPLOAD_SECOND) {
                uploadFiles.get(1).setTitle(R.string.upload_succeed);
                mUploadFileAdapter.notifyItemChanged(1);
            }
        }

        @Override
        public void onError(int what, Exception exception) {// 文件上传发生错误。
            if (what == WHAT_UPLOAD_FIRST) {
                uploadFiles.get(0).setTitle(R.string.upload_error);
                mUploadFileAdapter.notifyItemChanged(0);
            } else if (what == WHAT_UPLOAD_SECOND) {
                uploadFiles.get(1).setTitle(R.string.upload_error);
                mUploadFileAdapter.notifyItemChanged(1);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_file, menu);
        return true;
    }

    @Override
    protected boolean onOptionsItemSelectedCompat(MenuItem item) {
        if (item.getItemId() == R.id.menu_upload_file_request) {
            uploadMultiFile();
        }
        return true;
    }
}
