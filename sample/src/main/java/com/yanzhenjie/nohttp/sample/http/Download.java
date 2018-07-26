/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

/**
 * Created by Yan Zhenjie on 2016/12/20.
 */
public class Download {

    private static Download instance;

    public static Download getInstance() {
        if (instance == null) synchronized (Download.class) {
            if (instance == null) instance = new Download();
        }
        return instance;
    }

    private DownloadQueue mDownloadQueue;

    private Download() {
        mDownloadQueue = NoHttp.newDownloadQueue();
    }

    public void download(int what, DownloadRequest mRequest, DownloadListener mListener) {
        mDownloadQueue.add(what, mRequest, mListener);
    }

    public void cancelBySign(Object sign) {
        mDownloadQueue.cancelBySign(sign);
    }

    public void cancelAll() {
        mDownloadQueue.cancelAll();
    }

}
