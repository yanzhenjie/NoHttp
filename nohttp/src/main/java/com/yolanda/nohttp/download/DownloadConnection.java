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
package com.yolanda.nohttp.download;

import android.text.TextUtils;
import android.util.Log;

import com.yolanda.nohttp.BasicConnection;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.HttpHeaders;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.error.ArgumentError;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ReadWriteError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageCantWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.tools.FileUtil;
import com.yolanda.nohttp.tools.HeaderParser;
import com.yolanda.nohttp.tools.NetUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

/**
 * The network layer to download missions
 * </br>
 * Created in Jul 31, 2015 9:11:55 AM
 *
 * @author YOLANDA
 */
public class DownloadConnection extends BasicConnection implements Downloader {

    public DownloadConnection() {
    }

    @Override
    public void download(int what, DownloadRequest downloadRequest, DownloadListener downloadListener) {
        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;
        if (downloadRequest == null)
            throw new IllegalArgumentException("downloadRequest == null");
        if (downloadListener == null)
            throw new IllegalArgumentException("downloadListener == null");

        String savePathDir = downloadRequest.getFileDir();
        try {
            if (TextUtils.isEmpty(savePathDir) || TextUtils.isEmpty(downloadRequest.getFileName()))
                throw new ArgumentError("Destination folder creation failed, please check folder parameters and storage devices");

            if (!NetUtil.isNetworkAvailable(NoHttp.getContext()))
                throw new NetworkError("Network is not available");

            if (!FileUtil.createFolder(savePathDir))
                throw new ReadWriteError("Failed to create the folder " + savePathDir + ", please check storage devices");

            // 文件验证
            File lastFile = new File(savePathDir, downloadRequest.getFileName());
            Logger.d("Download file save path：" + lastFile.getAbsolutePath());
            if (lastFile.exists()) {// 已存在，删除或者通知下载完成
                if (downloadRequest.isDeleteOld())
                    lastFile.delete();
                else {
                    downloadListener.onStart(what, true, lastFile.length(), new HttpHeaders(), lastFile.length());
                    downloadListener.onProgress(what, 100, lastFile.length());
                    Logger.d("-------Download finish-------");
                    downloadListener.onFinish(what, lastFile.getAbsolutePath());
                    return;
                }
            }

            File tempFile = new File(savePathDir, downloadRequest.getFileName() + ".nohttp");
            // 临时文件判断，断点续
            long tempFileLength = 0L;// 临时文件大小记录,文件已经下载的大小，开始处
            if (tempFile.exists()) {
                if (downloadRequest.isRange())
                    tempFileLength = tempFile.length();
                else
                    tempFile.delete();
            }

            if (!FileUtil.createFile(tempFile))
                throw new ReadWriteError("Failed to create the file, please check storage devices");

            if (downloadRequest.isRange()) {
                String range = "bytes=" + tempFileLength + "-";
                downloadRequest.setHeader("Range", range);// 从1024开始下载：Range:bytes=1024-
            }

            // 处理连接和cookie等头
            httpConnection = getHttpConnection(downloadRequest);
            Logger.i("----------Response Start----------");
            int responseCode = httpConnection.getResponseCode();
            Headers httpHeaders = parseResponseHeaders(new URI(downloadRequest.url()), responseCode, httpConnection.getResponseMessage(), httpConnection.getHeaderFields());

            if (downloadRequest.isCanceled()) {
                Log.i("NoHttpDownloader", "Download request is canceled");
                downloadListener.onCancel(what);
                return;
            }

            // 文件总大小，不论断点续传下载还是完整下载
            long totalLength = 0;

            // 更新文件总大小
            if (responseCode == 206) {
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                String range = httpHeaders.getValue(Headers.HEAD_KEY_CONTENT_RANGE, 0);// 事例：Content-Range:bytes 1024-2047/2048
                if (!TextUtils.isEmpty(range)) {
                    try {
                        totalLength = Long.parseLong(range.substring(range.indexOf('/') + 1));// 截取'/'之后的总大小
                    } catch (Exception e) {
                        throw new ServerError("Content-Range error in Server HTTP header information");
                    }
                }
            } else if (responseCode == 200) {
                if (!FileUtil.createNewFile(tempFile))
                    throw new ReadWriteError("Failed to create the file, please check storage devices");

                totalLength = httpHeaders.getContentLength();// 直接下载
            }

            // 保存空间判断
            if (!FileUtil.canWrite(savePathDir))
                throw new StorageCantWriteError("This folder cannot be written to the file: " + savePathDir);
            if (FileUtil.getDirSize(savePathDir) < totalLength)
                throw new StorageSpaceNotEnoughError("The folder is not enough space to save the downloaded file: " + savePathDir);

            try {
                inputStream = httpConnection.getInputStream();
            } catch (IOException e) {
                if (responseCode >= 500)
                    throw new ServerError(e.getMessage());
                else if (responseCode <= 400)
                    throw new ClientError(e.getMessage());
            }

            // 通知开始下载了
            Logger.d("-------Download start-------");
            downloadListener.onStart(what, tempFileLength > 0, tempFileLength, httpHeaders, totalLength);

            // 解压文件流
            if (HeaderParser.isGzipContent(httpHeaders.getContentEncoding()))
                inputStream = new GZIPInputStream(inputStream);

            RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rw");
            randomAccessFile.seek(tempFileLength);

            byte[] buffer = new byte[1024];
            int len;

            int oldProgress = 0;// 旧的进度记录，防止重复通知
            long count = tempFileLength;// 追加目前已经下载的进度

            while (((len = inputStream.read(buffer)) != -1)) {
                if (downloadRequest.isCanceled()) {
                    Log.i("NoHttpDownloader", "Download request is canceled");
                    downloadListener.onCancel(what);
                    break;
                } else {
                    randomAccessFile.write(buffer, 0, len);
                    count += len;
                    if (totalLength != 0) {
                        int progress = (int) (count * 100 / totalLength);
                        if ((0 == progress % 2 || 0 == progress % 3 || 0 == progress % 5 || 0 == progress % 7) && oldProgress != progress) {
                            oldProgress = progress;
                            downloadListener.onProgress(what, oldProgress, count);// 进度通知
                        }
                    }
                }
            }
            randomAccessFile.close();
            if (!downloadRequest.isCanceled()) {
                tempFile.renameTo(lastFile);
                Logger.d("-------Download finish-------");
                downloadListener.onFinish(what, lastFile.getAbsolutePath());
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
        } catch (SocketException e) {
            if (NetUtil.isNetworkAvailable(NoHttp.getContext()))
                downloadListener.onDownloadError(what, e);
            else {
                String message = "The network is not available ";
                Logger.e(e, message);
                downloadListener.onDownloadError(what, new NetworkError(message));
            }
        } catch (IOException e) {
            if (!FileUtil.canWrite(savePathDir))
                downloadListener.onDownloadError(what, new StorageCantWriteError("This folder cannot be written to the file: " + savePathDir));
            else if (FileUtil.getDirSize(savePathDir) < 1024)
                downloadListener.onDownloadError(what, new StorageSpaceNotEnoughError("The folder is not enough space to save the downloaded file: " + savePathDir));
            else
                downloadListener.onDownloadError(what, e);
        } catch (Exception e) {// NetworkError | ClientError | ServerError | StorageCantWriteError | StorageSpaceNotEnoughError
            Logger.e(e);
            downloadListener.onDownloadError(what, e);
        } finally {
            Logger.i("----------Response End----------");
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
            }
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }
}
