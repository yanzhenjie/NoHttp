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

import android.text.TextUtils;
import android.util.Log;

import com.yolanda.nohttp.ConnectionResult;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.HttpConnection;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NetworkExecutor;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageReadWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.tools.HeaderUtil;
import com.yolanda.nohttp.tools.IOUtils;
import com.yolanda.nohttp.tools.NetUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.UnknownHostException;

/**
 * <p>
 * The network layer to download missions.
 * </p>
 * Created in Jul 31, 2015 9:11:55 AM.
 *
 * @author Yan Zhenjie.
 */
public class Downloader {

    private HttpConnection mHttpConnection;

    public Downloader(NetworkExecutor executor) {
        this.mHttpConnection = new HttpConnection(executor);
    }

    public void download(int what, DownloadRequest downloadRequest, DownloadListener downloadListener) {
        ConnectionResult connection = null;
        if (downloadRequest == null)
            throw new IllegalArgumentException("downloadRequest == null.");
        if (downloadListener == null)
            throw new IllegalArgumentException("downloadListener == null.");

        RandomAccessFile randomAccessFile = null;
        String savePathDir = downloadRequest.getFileDir();
        String fileName = downloadRequest.getFileName();
        try {
            if (TextUtils.isEmpty(savePathDir))
                savePathDir = NoHttp.getContext().getCacheDir().getAbsolutePath();

            File file = new File(savePathDir);
            if (file.exists() && file.isFile())
                IOUtils.delFileOrFolder(file);

            if (!IOUtils.createFolder(savePathDir))
                throw new StorageReadWriteError("Failed to create the folder " + savePathDir + ", please check storage devices.");

            if (!NetUtil.isNetworkAvailable())
                throw new NetworkError("Network is not available.");

            if (TextUtils.isEmpty(fileName))// auto named.
                fileName = Long.toString(System.currentTimeMillis());

            File tempFile = new File(savePathDir, fileName + ".nohttp");
            // 根据临时文件处理断点头。
            long rangeSize = 0L;// 断点开始处。
            if (tempFile.exists()) {
                if (tempFile.isDirectory())
                    IOUtils.createNewFile(tempFile);

                if (downloadRequest.isRange()) {
                    rangeSize = tempFile.length();
                    // 例如：从1024开始下载：Range:bytes=1024-。
                    downloadRequest.setHeader("Range", "bytes=" + rangeSize + "-");
                } else {
                    tempFile.delete();
                }
            }

            // 连接服务器。
            connection = mHttpConnection.getConnection(downloadRequest);
            Exception exception = connection.exception();
            if (exception != null)
                throw exception;

            Logger.i("----------Response Start----------");
            Headers responseHeaders = connection.responseHeaders();
            int responseCode = responseHeaders.getResponseCode();

            // getList filename from server.
            if (downloadRequest.autoNameByHead()) {
                String contentDisposition = responseHeaders.getContentDisposition();
                if (!TextUtils.isEmpty(contentDisposition)) {
                    fileName = HeaderUtil.parseHeadValue(contentDisposition, "filename", null);
                    if (!TextUtils.isEmpty(fileName)) {
                        fileName = URLDecoder.decode(fileName, downloadRequest.getParamsEncoding());
                        if (fileName.startsWith("\"") && fileName.endsWith("\"")) {
                            fileName = fileName.substring(1, fileName.length() - 1);
                        }
                    }
                }

                if (TextUtils.isEmpty(fileName)) {
                    // handle redirect url.
                    String tempUrl = downloadRequest.url();
                    String[] slash = tempUrl.split("/");
                    fileName = slash[slash.length - 1];
                    int paramIndex = fileName.indexOf("?");
                    if (paramIndex > 0) {
                        fileName = fileName.substring(0, paramIndex);
                    }
                }
            }

            InputStream serverStream = connection.serverStream();
            if (responseCode >= 400) {
                ServerError error = new ServerError("Download fails, the server response code is " + responseCode + ": " + downloadRequest.url());
                error.setErrorBody(IOUtils.toString(serverStream));
                throw error;
            } else {
                long contentLength = 0;
                // 文件总大小
                if (responseCode == 206) {
                    // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]。
                    String range = responseHeaders.getContentRange(); // 事例：Accept-Range:bytes 1024-2047/2048。
                    try {
                        contentLength = Long.parseLong(range.substring(range.indexOf('/') + 1));// 截取'/'之后的总大小。
                    } catch (Throwable e) {
                        throw new ServerError("ResponseCode is 206, but content-Range error in Server HTTP header information: " + range + ".");
                    }
                } else if (responseCode == 200) {
                    contentLength = responseHeaders.getContentLength();// 直接下载。
                    rangeSize = 0L; // 没有contentLength时断点移动到头部。
                } else if (responseCode == 304) {
                    int httpContentLength = responseHeaders.getContentLength();
                    downloadListener.onStart(what, true, httpContentLength, responseHeaders, httpContentLength);
                    downloadListener.onProgress(what, 100, httpContentLength);
                    Logger.d("-------Download finish-------");
                    downloadListener.onFinish(what, savePathDir + File.separator + fileName);
                    return;
                }

                // 验证文件已经存在。
                File lastFile = new File(savePathDir, fileName);
                if (lastFile.exists()) {
                    if (downloadRequest.isDeleteOld())
                        lastFile.delete();
                    else {
                        downloadListener.onStart(what, true, lastFile.length(), responseHeaders, lastFile.length());
                        downloadListener.onProgress(what, 100, lastFile.length());
                        Logger.d("-------Download finish-------");
                        downloadListener.onFinish(what, lastFile.getAbsolutePath());
                        return;
                    }
                }

                // 需要重新下载，生成临时文件。
                if ((responseCode == 200 || responseCode >= 400) && !IOUtils.createNewFile(tempFile))
                    throw new StorageReadWriteError("Failed to create the file, please check storage devices.");

                if (IOUtils.getDirSize(savePathDir) < contentLength)
                    throw new StorageSpaceNotEnoughError("The folder is not enough space to save the downloaded file: " + savePathDir + ".");

                if (downloadRequest.isCanceled()) {
                    Log.i("NoHttpDownloader", "Download request is canceled.");
                    downloadListener.onCancel(what);
                    return;
                }

                // 通知开始下载了。
                Logger.d("-------Download start-------");
                downloadListener.onStart(what, rangeSize > 0, rangeSize, responseHeaders, contentLength);

                randomAccessFile = new RandomAccessFile(tempFile, "rws");
                randomAccessFile.seek(rangeSize);

                byte[] buffer = new byte[2048];
                int len;

                int oldProgress = 0;// 旧的进度记录，防止重复通知。
                long count = rangeSize;// 追加目前已经下载的进度。

                while (((len = serverStream.read(buffer)) != -1)) {
                    if (downloadRequest.isCanceled()) {
                        Log.i("NoHttpDownloader", "Download request is canceled.");
                        downloadListener.onCancel(what);
                        break;
                    } else {
                        randomAccessFile.write(buffer, 0, len);
                        count += len;
                        if (contentLength != 0) {
                            int progress = (int) (count * 100 / contentLength);
                            if ((0 == progress % 2 || 0 == progress % 3 || 0 == progress % 5 || 0 == progress % 7) && oldProgress != progress) {
                                oldProgress = progress;
                                downloadListener.onProgress(what, oldProgress, count);// 进度通知。
                            }
                        }
                    }
                }
                if (!downloadRequest.isCanceled()) {
                    tempFile.renameTo(lastFile);
                    Logger.d("-------Download finish-------");
                    downloadListener.onFinish(what, lastFile.getAbsolutePath());
                }
            }
        } catch (MalformedURLException e) {
            Logger.e(e);
            downloadListener.onDownloadError(what, new URLError(e.getMessage()));
        } catch (UnknownHostException e) {
            Logger.e(e);
            downloadListener.onDownloadError(what, new UnKnownHostError(e.getMessage()));
        } catch (SocketTimeoutException e) {
            Logger.e(e);
            downloadListener.onDownloadError(what, new TimeoutError(e.getMessage()));
        } catch (IOException e) {
            Exception newException = e;
            if (!IOUtils.canWrite(savePathDir))
                newException = new StorageReadWriteError("This folder cannot be written to the file: " + savePathDir + ".");
            else if (IOUtils.getDirSize(savePathDir) < 1024)
                newException = new StorageSpaceNotEnoughError("The folder is not enough space to save the downloaded file: " + savePathDir + ".");
            Logger.e(newException);
            downloadListener.onDownloadError(what, newException);
        } catch (Exception e) {// NetworkError | ServerError | StorageCantWriteError | StorageSpaceNotEnoughError
            if (!NetUtil.isNetworkAvailable())
                e = new NetworkError("The network is not available.");
            Logger.e(e);
            downloadListener.onDownloadError(what, e);
        } finally {
            Logger.i("----------Response End----------");
            IOUtils.closeQuietly(randomAccessFile);
            IOUtils.closeQuietly(connection);
        }
    }
}
