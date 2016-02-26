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
package com.yolanda.nohttp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import com.yolanda.nohttp.tools.FileUtil;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

/**
 * A default implementation of Binary</br>
 * All the methods are called in Son thread.</br>
 * </br>
 * Created in Oct 17, 2015 12:40:54 PM
 *
 * @author YOLANDA
 */
public class FileBinary implements Binary {

    private static final Object HANDLER_LOCK = new Object();

    private static Handler sProgressHandler;

    private File file;

    private String fileName;

    private String mimeType;

    private boolean isRun = true;

    private int handlerWhat;

    private ProgressHandler mProgressHandler;

    public FileBinary(File file) {
        this(file, file.getName());
    }

    public FileBinary(File file, String fileName) {
        this(file, fileName, null);
    }

    public FileBinary(File file, String fileName, String mimeType) {
        if (file == null) {
            throw new IllegalArgumentException("File is null");
        } else if (!file.exists()) {
            Logger.w("File isn't exists");
        }
        this.file = file;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public void setProgressHandler(int what, ProgressHandler mProgressHandler) {
        this.handlerWhat = what;
        this.mProgressHandler = mProgressHandler;
    }

    @Override
    public long getLength() {
        return this.file.length();
    }

    @Override
    public void onWriteBinary(OutputStream outputStream) {
        try {
            int oldProgress = 0;
            long totalLength = getLength();
            long count = 0;
            RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            int len;
            byte[] buffer = new byte[1024];
            while (isRun && (len = accessFile.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                count += len;
                if (totalLength != 0 && mProgressHandler != null) {
                    int progress = (int) (count * 100 / totalLength);
                    if ((0 == progress % 2 || 0 == progress % 3 || 0 == progress % 5 || 0 == progress % 7) && oldProgress != progress) {
                        oldProgress = progress;
                        ThreadPoster poster = new ThreadPoster(oldProgress);
                        getPosterHandler().post(poster);
                    }
                }
            }
            accessFile.close();
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMimeType() {
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = FileUtil.getMimeTypeByUrl(file.getAbsolutePath());
            if (TextUtils.isEmpty(mimeType))
                mimeType = NoHttp.MIME_TYPE_FILE;
        }
        return mimeType;
    }

    /**
     * @deprecated @deprecated Please use {@link #cancel(boolean)} instead
     */
    @Override
    public void cancel() {
        this.isRun = false;
    }

    @Override
    public void cancel(boolean cancel) {
        this.isRun = cancel;
    }

    @Override
    public boolean isCanceled() {
        return !isRun;
    }

    @Override
    public void toggleCancel() {
        this.isRun = true;
    }

    private class ThreadPoster implements Runnable {

        private int progress;

        public ThreadPoster(int progress) {
            this.progress = progress;
        }

        @Override
        public void run() {
            mProgressHandler.onProgress(handlerWhat, progress);
        }

    }

    private Handler getPosterHandler() {
        synchronized (HANDLER_LOCK) {
            if (sProgressHandler == null)
                sProgressHandler = new Handler(Looper.getMainLooper());
        }
        return sProgressHandler;
    }

}
