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
import java.net.CookieManager;

import com.yolanda.nohttp.download.DownloadConnection;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.download.RestDownloadRequestor;
import com.yolanda.nohttp.security.SecureVerifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created in Jul 28, 2015 7:32:22 PM
 * 
 * @author YOLANDA
 */
public class NoHttp {

	public static final String CHARSET_UTF8 = "UTF-8";

	public static final String MIMETYE_FILE = "application/octet-stream";

	public static final int TIMEOUT_8S = 8 * 1000;

	/**
	 * Cookie
	 */
	private static CookieManager sCookieManager;

	/**
	 * Create a new request queue
	 * 
	 * @param context ApplicationContext
	 * @param threadPoolSize Thread pool number, here is the number of concurrent tasks
	 * @return
	 */
	public static RequestQueue newRequestQueue(Context context, int threadPoolSize) {
		RequestQueue requestQueue = new RequestQueue(HttpRestConnection.getInstance(context), threadPoolSize);
		requestQueue.start();
		return requestQueue;
	}

	/**
	 * Create a request queue, the default thread pool number is 5
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, 5);
	}

	/**
	 * To create a String type request, the request method is GET
	 */
	public static Request<String> createStringRequestGet(String url) {
		return createStringRequest(url, RequestMethod.GET);
	}

	/**
	 * To create a String type request, the request method is POST
	 */
	public static Request<String> createStringRequestPost(String url) {
		return createStringRequest(url, RequestMethod.POST);
	}

	/**
	 * Create a String type request, custom request method, method from {@link #RequestMethod}
	 */
	public static Request<String> createStringRequest(String url, int requestMethod) {
		return new StringRequest(url, requestMethod);
	}

	/**
	 * Create a Image type request
	 */
	public static Request<Bitmap> createImageRequest(String url) {
		return createImageRequest(url, 1000, 1000, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_INSIDE);
	}

	/**
	 * Create a Image type request
	 */
	public static Request<Bitmap> createImageRequest(String url, int maxWidth, int maxHeight, Bitmap.Config config, ImageView.ScaleType scaleType) {
		return new ImageRequest(url, maxWidth, maxHeight, config, scaleType);
	}

	/**
	 * To start a synchronization request, the request task will be triggered at the current thread, and the thread can
	 * be used.
	 * 
	 * @param what Http request sign, If multiple requests the Listener is the same, so that I can be used to mark which
	 *        one is the request
	 * @param request The packaging of the HTTP request parameter
	 */
	public static <T> Response<T> startRequestSync(Context context, Request<T> request) {
		Response<T> response = null;
		if (request != null)
			response = HttpRestConnection.getInstance(context).request(request);
		return response;
	}

	/**
	 * Create a new download queue, the default thread pool number is 1
	 * 
	 * @param context ApplicationContext
	 * @return
	 */
	public static DownloadQueue newDownloadQueue(Context context) {
		return newDownloadQueue(context, 2);
	}

	/**
	 * Create a new download queue
	 * 
	 * @param context ApplicationContext
	 * @param threadPoolSize Thread pool number, here is the number of concurrent tasks
	 * @return
	 */
	public static DownloadQueue newDownloadQueue(Context context, int threadPoolSize) {
		DownloadQueue downloadQueue = new DownloadQueue(DownloadConnection.getInstance(context), threadPoolSize);
		downloadQueue.start();
		return downloadQueue;
	}

	/**
	 * Create a download requestor
	 * 
	 * @param url Download address
	 * @param fileFloder Folder to save files
	 * @param filename filename
	 * @param isRange Whether power resume Download
	 * @param isDeleteOld If there is a old files, whether to delete the old files
	 */
	public static DownloadRequest createDownloadRequest(String url, String fileFloder, String filename, boolean isRange, boolean isDeleteOld) {
		return new RestDownloadRequestor(url, fileFloder, filename, isRange, isDeleteOld);
	}

	/**
	 * Start a sync Download
	 */
	public static void downloadSync(Context context, int what, DownloadRequest downloadRequest, DownloadListener downloadListener) {
		DownloadConnection.getInstance(context).download(what, downloadRequest, downloadListener);
	}

	/**
	 * Set is a debug mode, if it is a debug mode, you can see NoHttp Log information
	 * 
	 * @param debug Set to debug mode is introduced into true, introduced to false otherwise
	 */
	public static void setDebug(boolean debug) {
		Logger.isDebug = debug;
	}

	/**
	 * Set the log of the tag
	 * 
	 * @param tag The incoming string will be NoHttp logtag, also is in development tools logcat tag bar to see
	 */
	public static void setLogTag(String logTag) {
		Logger.sLogTag = logTag;
	}

	/**
	 * Sets up if all HTTPS certificates are allowed, if you set the true, the certificate parameter will be ignored
	 * 
	 * @param isAll True is allowed, false is not disallowed, false need to verify the certificate
	 */
	public static void setAllowAllHttps(boolean isAll) {
		SecureVerifier.getInstance().setAllowAllHttps(isAll);
	}

	/**
	 * Returns the system-wide cookie handler or {@code null} if not set.
	 */
	public static CookieManager getDefaultCookieManager() {
		if (sCookieManager == null)
			sCookieManager = new CookieManager();
		return sCookieManager;
	}

	/**
	 * Sets the system-wide cookie manager
	 */
	public static void setDefaultCookieManager(CookieManager cookieManager) {
		if (cookieManager == null)
			throw new IllegalArgumentException("cookieManager == null");
		sCookieManager = cookieManager;
	}

	/**
	 * Open Http cache
	 */
	public static void enableHttpResponseCache(Context context) {
		try {
			long httpCacheSize = 40 * 1024 * 1024;
			File httpCacheDir = new File(context.getCacheDir(), "NoHttp");
			Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, httpCacheDir, httpCacheSize);
		} catch (Exception e) {// httpResponseCacheNotAvailable
			Logger.throwable(e);
		}
	}

	private NoHttp() {
	}
}
