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

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.yolanda.nohttp.error.NotFoundFileError;
import com.yolanda.nohttp.tools.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * <p>
 * A default implementation of Binary.
 * All the methods are called in Son thread.
 * </p>
 * Created in Oct 17, 2015 12:40:54 PM.
 *
 * @author YOLANDA;
 */
public class FileBinary implements Binary {

    private static final Object HANDLER_LOCK = new Object();

    private static Handler sProgressHandler;

    private File file;

    private String fileName;

    private String mimeType;

    private boolean isCancel = false;

    private boolean isFinish = false;

    private int handlerWhat;

    private OnUploadListener mUploadListener;

    public FileBinary(File file) {
        this(file, file.getName());
    }

    public FileBinary(File file, String fileName) {
        this(file, fileName, null);
    }

    public FileBinary(File file, String fileName, String mimeType) {
        if (file == null) {
            Logger.w("File == null");
        } else if (!file.exists()) {
            Logger.w("File isn't exists");
        }
        this.file = file;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    /**
     * To monitor file upload progress, more than {@link FileBinary} can use the same {@link OnUploadListener}.
     *
     * @param what             in {@link OnUploadListener} will return to you.
     * @param mProgressHandler {@link OnUploadListener}.
     */
    public void setUploadListener(int what, OnUploadListener mProgressHandler) {
        this.handlerWhat = what;
        this.mUploadListener = mProgressHandler;
    }

    @Override
    public long getLength() {
        if (file == null || !file.exists())
            return 0;
        return this.file.length();
    }

    @Override
    public void onWriteBinary(OutputStream outputStream) {
        if (file == null || !file.exists()) {
            String errorInfo = "File does not exist or the file object is null";
            Logger.e(errorInfo);
            UploadPoster error = new UploadPoster(handlerWhat, mUploadListener);
            error.error(new NotFoundFileError(errorInfo));
            getPosterHandler().post(error);
        } else {
            RandomAccessFile accessFile = null;
            try {
                UploadPoster start = new UploadPoster(handlerWhat, mUploadListener);
                start.start();
                getPosterHandler().post(start);

                int oldProgress = 0;
                long totalLength = getLength();
                long count = 0;
                accessFile = new RandomAccessFile(file, "rw");
                int len;
                byte[] buffer = new byte[1024];
                while (!isCancel && (len = accessFile.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                    count += len;
                    if (totalLength != 0 && mUploadListener != null) {
                        int progress = (int) (count * 100 / totalLength);
                        if ((0 == progress % 2 || 0 == progress % 3 || 0 == progress % 5 || 0 == progress % 7) && oldProgress != progress) {
                            oldProgress = progress;
                            UploadPoster progressPoster = new UploadPoster(handlerWhat, mUploadListener);
                            progressPoster.progress(oldProgress);
                            getPosterHandler().post(progressPoster);
                        }
                    }
                }
            } catch (IOException e) {
                Logger.e(e);
                UploadPoster error = new UploadPoster(handlerWhat, mUploadListener);
                error.error(e);
                getPosterHandler().post(error);
            } finally {
                if (accessFile != null)
                    try {
                        accessFile.close();
                    } catch (IOException e) {
                    }
                UploadPoster finish = new UploadPoster(handlerWhat, mUploadListener);
                finish.finish();
                getPosterHandler().post(finish);
            }
            if (isCancel) {
                UploadPoster cancel = new UploadPoster(handlerWhat, mUploadListener);
                cancel.cancel();
                getPosterHandler().post(cancel);
            }
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

    @Override
    public void cancel(boolean cancel) {
        this.isCancel = cancel;
    }

    @Override
    public boolean isCanceled() {
        return !isCancel;
    }

    @Override
    public void toggleCancel() {
        this.isCancel = !isCancel;
    }

    @Override
    public boolean isFinished() {
        return isFinish;
    }

    @Override
    public void finish(boolean finish) {
        this.isFinish = finish;
    }

    @Override
    public void toggleFinish() {
        this.isFinish = !isFinish;
    }

    private class UploadPoster implements Runnable {

        private final int what;
        private final OnUploadListener mOnUploadListener;

        private int command;

        public static final int ON_START = 0;
        public static final int ON_CANCEL = 1;
        public static final int ON_PROGRESS = 2;
        public static final int ON_FINISH = 3;
        public static final int ON_ERROR = 4;

        private int progress;
        private Exception exception;

        public UploadPoster(int what, OnUploadListener onUploadListener) {
            this.what = what;
            this.mOnUploadListener = onUploadListener;
        }

        public void start() {
            this.command = ON_START;
        }

        public void cancel() {
            this.command = ON_CANCEL;
        }

        public void progress(int progress) {
            this.command = ON_PROGRESS;
            this.progress = progress;
        }

        public void finish() {
            this.command = ON_FINISH;
        }

        public void error(Exception exception) {
            this.command = ON_ERROR;
            this.exception = exception;
        }

        @Override
        public void run() {
            if (mOnUploadListener != null) {
                if (command == ON_START)
                    mOnUploadListener.onStart(what);
                else if (command == ON_FINISH)
                    mOnUploadListener.onFinish(what);
                else if (command == ON_PROGRESS)
                    mOnUploadListener.onProgress(what, progress);
                else if (command == ON_CANCEL)
                    mOnUploadListener.onCancel(what);
                else if (command == ON_ERROR)
                    mOnUploadListener.onError(what, exception);
            }
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
