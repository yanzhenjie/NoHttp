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

import java.net.CookieManager;
import java.net.CookiePolicy;

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.cache.DiskCacheStore;
import com.yolanda.nohttp.cookie.DiskCookieStore;
import com.yolanda.nohttp.download.DownloadConnection;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.download.RestDownloadRequestor;
import com.yolanda.nohttp.security.SecureVerifier;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created in Jul 28, 2015 7:32:22 PM
 * 
 * @author YOLANDA
 */
public class NoHttp {
	/**
	 * Default charset of request body, value is {@value}
	 */
	public static final String CHARSET_UTF8 = "UTF-8";
	/**
	 * Default mimetype of upload file, value is {@value}
	 */
	public static final String MIMETYE_FILE = "application/octet-stream";
	/**
	 * Default timeout, value is {@value}s
	 */
	public static final int TIMEOUT_8S = 8 * 1000;
	/**
	 * RequestQueue default thread size, value is {@value}
	 */
	public static final int DEFAULT_THREAD_SIZE = 1;

	/**
	 * Context
	 */
	private static Application sApplication;

	/**
	 * Cookie
	 */
	private static CookieManager sCookieManager;

	/**
	 * Sync Connection manager
	 */
	private static BasicConnectionManager mBasicConnectionManager;

	/**
	 * Initialization NoHttp, Should invoke on {@link Application#onCreate()}
	 */
	public static void init(Application application) {
		if (sApplication == null) {
			sApplication = application;
			sCookieManager = new CookieManager(DiskCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL);
		}
	}

	/**
	 * Get application of app
	 */
	public static Application getContext() {
		if (sApplication == null)
			throw new ExceptionInInitializerError("please invoke NoHttp.init(Application) on Application#onCreate()");
		return sApplication;
	}

	public static RequestQueue newRequestQueue(BasicConnectionManager connectionManager, int threadPoolSize) {
		RequestQueue requestQueue = new RequestQueue(connectionManager, threadPoolSize);
		requestQueue.start();
		return requestQueue;
	}

	public static RequestQueue newRequestQueue(Cache<CacheEntity> cache, BasicConnectionRest connectionRest, int threadPoolSize) {
		return newRequestQueue(new ConnectionManager(cache, connectionRest), threadPoolSize);
	}

	public static RequestQueue newRequestQueue(int threadPoolSize) {
		return newRequestQueue(DiskCacheStore.INSTANCE, new HttpRestConnection(getContext()), threadPoolSize);
	}

	public static RequestQueue newRequestQueue() {
		return newRequestQueue(DEFAULT_THREAD_SIZE);
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
	public static Request<String> createStringRequest(String url, RequestMethod requestMethod) {
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

	private synchronized static BasicConnectionManager createSyncConnectionManager() {
		if (mBasicConnectionManager == null) {
			BasicConnectionRest connectionRest = new HttpRestConnection(getContext());
			mBasicConnectionManager = new ConnectionManager(DiskCacheStore.INSTANCE, connectionRest);
		}
		return mBasicConnectionManager;
	}

	public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, BasicConnectionRest connectionRest, Request<T> request) {
		Response<T> response = null;
		if (cache != null && connectionRest != null && request != null)
			response = createSyncConnectionManager().handleRequest(request);
		return response;
	}

	public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, Request<T> request) {
		return startRequestSync(cache, new HttpRestConnection(getContext()), request);
	}

	public static <T> Response<T> startRequestSync(BasicConnectionRest connectionRest, Request<T> request) {
		return startRequestSync(DiskCacheStore.INSTANCE, connectionRest, request);
	}

	public static <T> Response<T> startRequestSync(Request<T> request) {
		return startRequestSync(DiskCacheStore.INSTANCE, new HttpRestConnection(getContext()), request);
	}

	/**
	 * Create a new download queue, the default thread pool number is {@link NoHttp#DEFAULT_DOWNLOAD_THREAD_SIZE}
	 */
	public static DownloadQueue newDownloadQueue() {
		return newDownloadQueue(DEFAULT_THREAD_SIZE);
	}

	/**
	 * Create a new download queue
	 * 
	 * @param threadPoolSize Thread pool number, here is the number of concurrent tasks
	 */
	public static DownloadQueue newDownloadQueue(int threadPoolSize) {
		DownloadQueue downloadQueue = new DownloadQueue(DownloadConnection.getInstance(getContext()), threadPoolSize);
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
	public static void downloadSync(int what, DownloadRequest downloadRequest, DownloadListener downloadListener) {
		DownloadConnection.getInstance(getContext()).download(what, downloadRequest, downloadListener);
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

	private NoHttp() {
	}
}
