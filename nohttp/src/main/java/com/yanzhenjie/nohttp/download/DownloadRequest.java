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
package com.yanzhenjie.nohttp.download;

import com.yanzhenjie.nohttp.BasicRequest;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.able.Queueable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;

/**
 * <p>
 * File download request based on BasicRequest.
 * </p>
 * Created by YanZhenjie on Jul 31, 2015 10:38:10 AM.
 */
public class DownloadRequest extends BasicRequest<DownloadRequest> implements Queueable {

    /**
     * Also didn't download to start download again.
     */
    public static final int STATUS_RESTART = 0;
    /**
     * Part has been downloaded, continue to download last time.
     */
    public static final int STATUS_RESUME = 1;
    /**
     * Has the download is complete, not to download operation.
     */
    public static final int STATUS_FINISH = 2;

    /**
     * The callback mark.
     */
    private int what;
    /**
     * The request of the listener.
     */
    private WeakReference<DownloadListener> downloadListener;
    /**
     * File the target folder.
     */
    private final String mFileDir;
    /**
     * The file target name.
     */
    private final String mFileName;
    /**
     * If is to download a file, whether the breakpoint continuing.
     */
    private final boolean isRange;
    /**
     * If there is a old files, whether to delete the old files.
     */
    private final boolean isDeleteOld;

    /**
     * Request queue
     */
    private BlockingQueue<?> blockingQueue;

    /**
     * Create download request.
     *
     * @param url           url.
     * @param requestMethod {@link RequestMethod}.
     * @param fileFolder    file save folder.
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is
     *                      complete, not to request the network.
     * @see #DownloadRequest(String, RequestMethod, String, String, boolean, boolean)
     */
    public DownloadRequest(String url, RequestMethod requestMethod, String fileFolder, boolean isRange, boolean isDeleteOld) {
        this(url, requestMethod, fileFolder, null, isRange, isDeleteOld);
    }

    /**
     * Create a download object.
     *
     * @param url           download address.
     * @param requestMethod {@link RequestMethod}.
     * @param fileFolder    folder to save file.
     * @param filename      filename.
     * @param isRange       whether the breakpoint continuing.
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is
     *                      complete, not to request the network.
     * @see #DownloadRequest(String, RequestMethod, String, boolean, boolean)
     */
    public DownloadRequest(String url, RequestMethod requestMethod, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        super(url, requestMethod);
        this.mFileDir = fileFolder;
        this.mFileName = filename;
        this.isRange = isRange;
        this.isDeleteOld = isDeleteOld;
    }

    /**
     * Return the mFileDir.
     *
     * @return it won't be empty.
     */
    public String getFileDir() {
        return this.mFileDir;
    }

    /**
     * Return the mFileName.
     *
     * @return it won't be empty.
     */
    public String getFileName() {
        return this.mFileName;
    }

    /**
     * Return the isRange.
     *
     * @return true: breakpoint continuing, false: don't need a breakpoint continuing.
     */
    public boolean isRange() {
        return this.isRange;
    }

    /**
     * If there is a old files, whether to delete the old files.
     *
     * @return true: deleted, false: don't delete.
     */
    public boolean isDeleteOld() {
        return this.isDeleteOld;
    }

    /**
     * <p>
     * Query before download status {@link #STATUS_RESTART} representative no download do to download again; Download
     * {@link #STATUS_RESUME} represents a part of, to continue to download; {@link #STATUS_FINISH} representatives
     * have finished downloading.
     * </p>
     *
     * @return int value, compared with the {@value #STATUS_RESTART}, {@value #STATUS_RESUME}, {@value #STATUS_FINISH}.
     * @see #STATUS_RESTART
     * @see #STATUS_RESUME
     * @see #STATUS_FINISH
     */
    public int checkBeforeStatus() {
        if (this.isRange) {
            try {
                File lastFile = new File(mFileDir, mFileName);
                if (lastFile.exists() && !isDeleteOld)
                    return STATUS_FINISH;
                File tempFile = new File(mFileDir, mFileName + ".nohttp");
                if (tempFile.exists())
                    return STATUS_RESUME;
            } catch (Exception ignored) {
            }
        }
        return STATUS_RESTART;
    }

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param downloadListener {@link DownloadListener}.
     */
    public void onPreResponse(int what, DownloadListener downloadListener) {
        this.what = what;
        this.downloadListener = new WeakReference<>(downloadListener);
    }

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, DownloadListener)} incoming credit.
     * @see #onPreResponse(int, DownloadListener)
     */
    public int what() {
        return what;
    }

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, DownloadListener)} incoming credit.
     * @see #onPreResponse(int, DownloadListener)
     */
    public DownloadListener downloadListener() {
        if (downloadListener != null)
            return downloadListener.get();
        return null;
    }

    @Override
    public void setQueue(BlockingQueue<?> queue) {
        blockingQueue = queue;
    }

    @Override
    public boolean inQueue() {
        return blockingQueue != null && blockingQueue.contains(this);
    }
}
