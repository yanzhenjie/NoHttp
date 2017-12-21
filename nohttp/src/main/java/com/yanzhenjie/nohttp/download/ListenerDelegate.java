package com.yanzhenjie.nohttp.download;

import com.yanzhenjie.nohttp.Headers;

import java.util.Map;

/**
 * Created by YanZhenjie on 2017/12/21.
 */
public class ListenerDelegate implements DownloadListener {

    private final DownloadRequest mRequest;
    private final Map<DownloadRequest, Messenger> mMessengerMap;

    public ListenerDelegate(DownloadRequest request, Map<DownloadRequest, Messenger> messengerMap) {
        mRequest = request;
        mMessengerMap = messengerMap;
    }

    @Override
    public void onDownloadError(int what, Exception exception) {
        mMessengerMap.get(mRequest).error(exception);
    }

    @Override
    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
        mMessengerMap.get(mRequest).start(isResume, rangeSize, responseHeaders, allCount);
    }

    @Override
    public void onProgress(int what, int progress, long fileCount, long speed) {
        mMessengerMap.get(mRequest).progress(progress, fileCount, speed);
    }

    @Override
    public void onFinish(int what, String filePath) {
        mMessengerMap.get(mRequest).finish(filePath);
    }

    @Override
    public void onCancel(int what) {
        mMessengerMap.get(mRequest).cancel();
    }
}