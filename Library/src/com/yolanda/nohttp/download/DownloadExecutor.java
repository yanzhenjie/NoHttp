/**
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.base.BaseExecutor;
import com.yolanda.nohttp.util.FileUtil;
import com.yolanda.nohttp.util.NetUtil;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.URLUtil;

/**
 * Created in Jul 31, 2015 9:11:55 AM
 * 
 * @author YOLANDA
 */
public class DownloadExecutor extends BaseExecutor {

	/**
	 * sigle model
	 */
	private static DownloadExecutor _Downloader;
	/**
	 * context
	 */
	private Context mContext;

	private DownloadExecutor(Context context) {
		this.mContext = context;
	}

	public static DownloadExecutor getInstance(Context context) {
		if (_Downloader == null) {
			_Downloader = new DownloadExecutor(context);
		}
		return _Downloader;
	}

	/**
	 * @param urlAdress http download address
	 * @param filePath save path, like: sdcard/download/yolanda.png
	 * @return Successful returns true, otherwise return false
	 */
	public boolean download(DownloadRequest request, String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			HttpURLConnection urlConnection = buildHttpAttribute(request);
			if (urlConnection.getResponseCode() == 200) {
				InputStream inputStream = urlConnection.getInputStream();
				String contentEncode = urlConnection.getHeaderField("Content-Encoding");// connection.getContentEncoding();
				if (!TextUtils.isEmpty(contentEncode)
						&& (contentEncode.toLowerCase(Locale.getDefault()).contains("gzip"))) {
					inputStream = new GZIPInputStream(inputStream);
				}
				byte[] buffer = new byte[1024];
				int len;
				OutputStream outputStream = new FileOutputStream(file);
				while ((len = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
				}
				outputStream.close();
				inputStream.close();
				urlConnection.disconnect();
				return true;
			}
		} catch (Throwable e) {
			if (NoHttp.isDebug())
				e.printStackTrace();
		}
		return false;
	}

	/**
	 * The download file
	 * 
	 * @param request Download reqeust
	 * @param downloadListener Download listener
	 */
	public void download(DownloadRequest request, DownloadListener downloadListener) {
		if (!NetUtil.isNetworkAvailable(mContext)) {
			downloadListener.onDownloadError(0, StatusCode.ERROR_NETWORK);
			return;
		}
		// 地址验证
		if (!URLUtil.isValidUrl(request.getUrl())) {
			downloadListener.onDownloadError(0, StatusCode.ERROR_URL);
			return;
		}
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		File tempFile = null;
		try {
			// 目录验证
			File savePathDir = new File(request.getFileDir());
			if (!savePathDir.exists()) {
				savePathDir.mkdirs();
			}
			// 文件验证
			File lastFile = new File(savePathDir, request.getFileName());
			Logger.i("Download file save path：" + lastFile.getAbsolutePath());
			if (lastFile.exists()) {// 已存在，删除
				if (request.isRange())
					downloadListener.onFinish(0, lastFile.getAbsolutePath());
				else
					lastFile.delete();
			}

			tempFile = new File(request.getFileDir(), request.getFileName() + ".temp");
			// 临时文件判断，断点续
			long tempFileLength = 0L;// 临时文件大小记录,文件已经下载的大小，开始处
			if (tempFile.exists())
				if (request.isRange())
					tempFileLength = tempFile.length();
				else {
					tempFile.delete();
					tempFile.createNewFile();
				}
			else
				tempFile.createNewFile();

			httpURLConnection = buildHttpAttribute(request);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			if (request.isRange()) {
				httpURLConnection.setRequestProperty("RANGE", "bytes=" + tempFileLength + "-");// 从断点开始下载，事例：Range:bytes=0-801
			}

			int statusCode = httpURLConnection.getResponseCode();

			long totalLength = 0L;// 本次下载总大小，服务器传过来的

			/* ==更新文件开始下载处的大小和总大小== */
			if (statusCode == 206 && request.isRange()) {// 206处理了部分get请求；如果服务器支持断点续下
				String range = httpURLConnection.getHeaderField("Content-Range");// 事例：Content-Range:
																					// bytes
																					// 0-800/801
				if (!TextUtils.isEmpty(range)) {
					// 截取'/'之后的
					totalLength = Long.parseLong(range.substring(range.indexOf('/') + 1));
				}
			} else if (statusCode == 200) {// 直接下载
				String contentLenght = httpURLConnection.getHeaderField("Content-Length");
				Logger.i("contentLenght：" + contentLenght);
				if (!TextUtils.isEmpty(contentLenght)) {
					totalLength = Integer.valueOf(contentLenght).intValue();
				}
			} else {
				if (!NetUtil.isNetworkAvailable(mContext)) {
					downloadListener.onDownloadError(0, StatusCode.ERROR_NETWORK);
				} else {
					downloadListener.onDownloadError(0, StatusCode.ERROR_OTHER);
				}
				return;
			}

			// 保存空间判断
			if (FileUtil.getDirSize(request.getFileDir()) < totalLength) {
				downloadListener.onDownloadError(0, StatusCode.ERROR_SDCARD_NOSPACE);
				return;
			}
			// 通知开始下载了
			downloadListener.onStart(0);
			// 开始下载
			inputStream = httpURLConnection.getInputStream();
			String contentEncode = httpURLConnection.getHeaderField("Content-Encoding");
			// 压缩过的
			if (!TextUtils.isEmpty(contentEncode)
					&& (contentEncode.toLowerCase(Locale.getDefault()).contains("gzip"))) {
				inputStream = new GZIPInputStream(inputStream);
			}

			/*
			 * ==是用来访问那些保存数据记录的文件的，这样你就可以用seek()方法来访问记录，
			 * 并进行读写了。这些记录的大小不必相同；但是其大小和位置必须是可知的，最后记得close==
			 */
			RandomAccessFile content = new RandomAccessFile(tempFile, "rw");
			content.seek(tempFileLength);

			byte[] buffer = new byte[1024];
			int readBytes = 0;

			/* ==进度通知== */
			int oldProgress = 0;// 旧的进度记录，防止重复通知
			long count = tempFileLength;// 追加目前已经下载的进度
			/* == **** == */

			while (((readBytes = inputStream.read(buffer)) != -1)) {
				content.write(buffer, 0, readBytes);
				/* ==进度通知== */
				count += readBytes;
				if (totalLength != 0) {
					int progress = (int) (count * 100 / totalLength);
					if ((0 == progress % 2 || 0 == progress % 3) && oldProgress != progress) {
						oldProgress = progress;
						downloadListener.onProgress(0, progress);// 进度通知
					}
				}
				/* == **** == */
			}
			content.close();

			/* ==验证下载文件== */
			if (tempFile.length() < totalLength) {
				tempFile.delete();
				downloadListener.onDownloadError(0, StatusCode.ERROR_FILE_DAMAGE);
			} else {
				tempFile.renameTo(lastFile);
				downloadListener.onFinish(0, lastFile.getAbsolutePath());
			}
		} catch (SecurityException e) {
			downloadListener.onDownloadError(0, StatusCode.ERROR_PERMISSION);
			if (NoHttp.isDebug())
				e.printStackTrace();
		} catch (SocketTimeoutException e) {
			downloadListener.onDownloadError(0, StatusCode.ERROR_TIMEOUT);
			if (NoHttp.isDebug())
				e.printStackTrace();
		} catch (UnknownHostException e) {
			downloadListener.onDownloadError(0, StatusCode.ERROR_NOSERVER);
			if (NoHttp.isDebug())
				e.printStackTrace();
		} catch (Throwable e) {
			downloadListener.onDownloadError(0, StatusCode.ERROR_OTHER);
			if (NoHttp.isDebug())
				e.printStackTrace();
		} finally {
			if (!request.isRange() && tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e1) {
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}
}
