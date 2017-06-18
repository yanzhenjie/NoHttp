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

/**
 * <p>
 * Download task request interface.
 * </p>
 * Created in Oct 21, 2015 11:09:04 AM.
 *
 * @author Yan Zhenjie.
 */
public abstract class DownloadRequest extends BasicRequest<DownloadRequest> implements Queueable {

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

    public DownloadRequest(String url) {
        super(url);
    }

    public DownloadRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    /**
     * Return the mFileDir.
     *
     * @return it won't be empty.
     */
    public abstract String getFileDir();

    /**
     * Return the mFileName.
     *
     * @return it won't be empty.
     */
    public abstract String getFileName();

    /**
     * According to the Http header named files automatically.
     *
     * @return true need, false not need.
     */
    public abstract boolean autoNameByHead();

    /**
     * Return the isRange.
     *
     * @return true: breakpoint continuing, false: don't need a breakpoint continuing.
     */
    public abstract boolean isRange();

    /**
     * If there is a old files, whether to delete the old files.
     *
     * @return true: deleted, false: don't delete.
     */
    public abstract boolean isDeleteOld();

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
    public abstract int checkBeforeStatus();

    /**
     * Prepare the callback parameter, while waiting for the response callback with thread.
     *
     * @param what             the callback mark.
     * @param downloadListener {@link DownloadListener}.
     */
    abstract void onPreResponse(int what, DownloadListener downloadListener);

    /**
     * The callback mark.
     *
     * @return Return when {@link #onPreResponse(int, DownloadListener)} incoming credit.
     * @see #onPreResponse(int, DownloadListener)
     */
    public abstract int what();

    /**
     * The request of the listener.
     *
     * @return Return when {@link #onPreResponse(int, DownloadListener)} incoming credit.
     * @see #onPreResponse(int, DownloadListener)
     */
    public abstract DownloadListener downloadListener();
}
