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

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.app.BaseActivity;
import com.yanzhenjie.nohttp.sample.config.AppConfig;
import com.yanzhenjie.nohttp.sample.config.UrlConfig;
import com.yanzhenjie.nohttp.sample.http.Download;
import com.yanzhenjie.nohttp.sample.http.DownloadCallback;

import java.math.BigDecimal;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public class DownloadPresenter
  extends BaseActivity
  implements Contract.DownloadPresenter {

    private Contract.DownloadView mView;

    private DownloadRequest mRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mView = new DownloadView(this, this);
    }

    @Override
    public void tryDownload() {
        if (mRequest != null) {
            mRequest.cancel();
            // or Download.getInstance().cancelBySign(this);
        } else {
            String dir = AppConfig.get().PATH_APP_DOWNLOAD;
            mRequest = new DownloadRequest(UrlConfig.DOWNLOAD, RequestMethod.GET, dir, "sou.apk", true, true);
            mRequest.setCancelSign(this);
            Download.getInstance().download(0, mRequest, new DownloadCallback(this) {
                @Override
                public void onException(String message) {
                    mView.onError(message);
                    mRequest = null;
                }

                @Override
                public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders,
                                    long allCount) {
                    mView.onStart();
                }

                @Override
                public void onProgress(int what, int progress, long fileCount, long speed) {
                    BigDecimal bg = new BigDecimal(speed / 1024D / 1024D);
                    String speedText = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                    speedText = getString(R.string.download_speed, speedText);
                    mView.setProgress(progress, speedText);
                }

                @Override
                public void onFinish(int what, String filePath) {
                    mView.onFinish();
                    mRequest = null;
                }

                @Override
                public void onCancel(int what) {
                    if (mView != null) {
                        mView.onCancel();
                    }
                    mRequest = null;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        mView = null;
        Download.getInstance().cancelBySign(this);
        super.onDestroy();
    }
}