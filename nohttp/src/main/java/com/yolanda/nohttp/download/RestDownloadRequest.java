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
package com.yolanda.nohttp.download;

import java.io.File;

import com.yolanda.nohttp.BasicRequest;
import com.yolanda.nohttp.RedirectHandler;
import com.yolanda.nohttp.RequestMethod;

/**
 * <p>
 * Download the implementation class of the parameter request, and convert it to the object of the network download.
 * </p>
 * Created in Jul 31, 2015 10:38:10 AM.
 *
 * @author Yan Zhenjie..
 */
public class RestDownloadRequest extends BasicRequest implements DownloadRequest {

    /**
     * The callback mark.
     */
    private int what;
    /**
     * The request of the listener.
     */
    private DownloadListener downloadListener;
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

    public RestDownloadRequest(String url, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        this(url, RequestMethod.GET, fileFolder, filename, isRange, isDeleteOld);
    }

    public RestDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        super(url, requestMethod);
        this.mFileDir = fileFolder;
        this.mFileName = filename;
        this.isRange = isRange;
        this.isDeleteOld = isDeleteOld;
    }

    @Override
    public String getAccept() {
        return "*/*";
    }

    @Override
    public String getFileDir() {
        return this.mFileDir;
    }

    @Override
    public String getFileName() {
        return this.mFileName;
    }

    @Override
    public boolean isRange() {
        return this.isRange;
    }

    @Override
    public boolean isDeleteOld() {
        return this.isDeleteOld;
    }

    @Override
    public int checkBeforeStatus() {
        if (this.isRange) {
            try {
                File lastFile = new File(mFileDir, mFileName);
                if (lastFile.exists() && !isDeleteOld)
                    return STATUS_FINISH;
                File tempFile = new File(mFileDir, mFileName + ".nohttp");
                if (tempFile.exists())
                    return STATUS_RESUME;
            } catch (Exception e) {
            }
        }
        return STATUS_RESTART;
    }

    @Override
    public void onPreResponse(int what, DownloadListener downloadListener) {
        this.what = what;
        this.downloadListener = downloadListener;
    }

    @Override
    public int what() {
        return what;
    }

    @Override
    public DownloadListener downloadListener() {
        return downloadListener;
    }

    @Override
    public void setRedirectHandler(RedirectHandler redirectHandler) {
    }
}
