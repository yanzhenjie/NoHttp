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

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.cache.DiskCacheStore;
import com.yolanda.nohttp.cookie.DiskCookieStore;
import com.yolanda.nohttp.download.DownloadConnection;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.download.Downloader;
import com.yolanda.nohttp.download.RestDownloadRequest;
import com.yolanda.nohttp.tools.PRNGFixes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * <p>NoHttp.</p>
 * Created in Jul 28, 2015 7:32:22 PM.
 *
 * @author YOLANDA;
 */
public class NoHttp {
    /**
     * Default charset of request body, value is {@value}.
     */
    public static final String CHARSET_UTF8 = "utf-8";
    /**
     * Default mimeType of upload file, value is {@value}.
     */
    public static final String MIME_TYPE_FILE = "application/octet-stream";
    /**
     * Default timeout, value is {@value} ms.
     */
    public static final int TIMEOUT_8S = 8 * 1000;
    /**
     * RequestQueue default thread size, value is {@value}.
     */
    public static final int DEFAULT_THREAD_SIZE = 3;

    /**
     * Context.
     */
    private static Application sApplication;

    /**
     * Cookie.
     */
    private static CookieHandler sCookieHandler;

    /**
     * Initialization NoHttp, Should invoke on {@link Application#onCreate()}.
     *
     * @param application {@link Application}.
     */
    public static void init(Application application) {
        if (sApplication == null) {
            sApplication = application;
            PRNGFixes.apply();
            sCookieHandler = new CookieManager(DiskCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL);
        }
    }

    /**
     * Get application of app.
     *
     * @return {@link Application}.
     */
    public static Application getContext() {
        if (sApplication == null)
            throw new ExceptionInInitializerError("Please invoke NoHttp.init(Application) on Application#onCreate()");
        return sApplication;
    }

    /**
     * Create a new request queue, using NoHttp default configuration. And number of concurrent requests is {@value #DEFAULT_THREAD_SIZE}.
     *
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(Cache, ImplRestConnection, int)
     * @see #newRequestQueue(ImplRestExecutor, int)
     * @see #newRequestQueue(ImplRestParser, int)
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(DEFAULT_THREAD_SIZE);
    }

    /**
     * Create a new request queue, using NoHttp default configuration.
     *
     * @param threadPoolSize request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(Cache, ImplRestConnection, int)
     * @see #newRequestQueue(ImplRestExecutor, int)
     * @see #newRequestQueue(ImplRestParser, int)
     */
    public static RequestQueue newRequestQueue(int threadPoolSize) {
        return newRequestQueue(DiskCacheStore.INSTANCE, HttpRestConnection.getInstance(), threadPoolSize);
    }

    /**
     * Create a new request queue, using NoHttp default request executor {@link HttpRestExecutor} and default response parser {@link HttpRestParser}.
     *
     * @param cache              cache interface, which is used to cache the request results.
     * @param implRestConnection network operating interface, The implementation of the network layer.
     * @param threadPoolSize     request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(ImplRestExecutor, int)
     * @see #newRequestQueue(ImplRestParser, int)
     */
    public static RequestQueue newRequestQueue(Cache<CacheEntity> cache, ImplRestConnection implRestConnection, int threadPoolSize) {
        return newRequestQueue(HttpRestExecutor.getInstance(cache, implRestConnection), threadPoolSize);
    }

    /**
     * Create a new request queue, using NoHttp default response parser {@link HttpRestParser}.
     *
     * @param implRestExecutor the executor, Interact with the network layer.
     * @param threadPoolSize   request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(Cache, ImplRestConnection, int)
     * @see #newRequestQueue(ImplRestParser, int)
     */
    public static RequestQueue newRequestQueue(ImplRestExecutor implRestExecutor, int threadPoolSize) {
        return newRequestQueue(HttpRestParser.getInstance(implRestExecutor), threadPoolSize);
    }

    /**
     * Create a new request queue.
     *
     * @param implRestParser the response parser, The result of parsing the network layer.
     * @param threadPoolSize request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(Cache, ImplRestConnection, int)
     * @see #newRequestQueue(ImplRestExecutor, int)
     */
    public static RequestQueue newRequestQueue(ImplRestParser implRestParser, int threadPoolSize) {
        RequestQueue requestQueue = new RequestQueue(implRestParser, threadPoolSize);
        requestQueue.start();
        return requestQueue;
    }

    /**
     * Create a String type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<String>}.
     * @see #createStringRequest(String, RequestMethod)
     */
    public static Request<String> createStringRequest(String url) {
        return new StringRequest(url);
    }

    /**
     * Create a String type request, custom request method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<String>}.
     * @see #createStringRequest(String)
     */
    public static Request<String> createStringRequest(String url, RequestMethod requestMethod) {
        return new StringRequest(url, requestMethod);
    }

    /**
     * Create a JSONObject type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<JSONObject>}.
     * @see #createJsonObjectRequest(String, RequestMethod)
     */
    public static Request<JSONObject> createJsonObjectRequest(String url) {
        return new JsonObjectRequest(url);
    }

    /**
     * Create a JSONObject type request, custom request method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<JSONObject>}.
     * @see #createJsonObjectRequest(String)
     */
    public static Request<JSONObject> createJsonObjectRequest(String url, RequestMethod requestMethod) {
        return new JsonObjectRequest(url, requestMethod);
    }

    /**
     * Create a JSONArray type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<JSONArray>}.
     * @see #createJsonArrayRequest(String, RequestMethod)
     */
    public static Request<JSONArray> createJsonArrayRequest(String url) {
        return new JsonArrayRequest(url);
    }

    /**
     * Create a JSONArray type request, custom request method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<JSONArray>}.
     * @see #createJsonArrayRequest(String)
     */
    public static Request<JSONArray> createJsonArrayRequest(String url, RequestMethod requestMethod) {
        return new JsonArrayRequest(url, requestMethod);
    }

    /**
     * Create a Image type request, the request method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.google.com}.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String, RequestMethod)
     * @see #createImageRequest(String, RequestMethod, int, int, Bitmap.Config, ImageView.ScaleType)
     */
    public static Request<Bitmap> createImageRequest(String url) {
        return createImageRequest(url, RequestMethod.GET);
    }

    /**
     * Create a Image type request.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String)
     * @see #createImageRequest(String, RequestMethod, int, int, Bitmap.Config, ImageView.ScaleType)
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod) {
        return createImageRequest(url, requestMethod, 1000, 1000, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * Create a Image type request.
     *
     * @param url           such as: {@code http://www.google.com}.
     * @param requestMethod {@link RequestMethod}.
     * @param maxWidth      width.
     * @param maxHeight     height.
     * @param config        config.
     * @param scaleType     scaleType.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String)
     * @see #createImageRequest(String, RequestMethod)
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod, int maxWidth, int maxHeight, Bitmap.Config config, ImageView.ScaleType scaleType) {
        return new ImageRequest(url, requestMethod, maxWidth, maxHeight, config, scaleType);
    }

    /**
     * Initiate a synchronization request.
     *
     * @param cache              cache interface, which is used to cache the request results.
     * @param implRestConnection network operating interface, The implementation of the network layer.
     * @param request            tequest object.
     * @param <T>                {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(Request)
     * @see #startRequestSync(ImplRestConnection, Request)
     * @see #startRequestSync(Cache, Request)
     */
    public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, ImplRestConnection implRestConnection, Request<T> request) {
        Response<T> response = null;
        if (cache != null && implRestConnection != null && request != null)
            response = HttpRestParser.getInstance(HttpRestExecutor.getInstance(DiskCacheStore.INSTANCE, HttpRestConnection.getInstance())).parserRequest(request);
        return response;
    }

    /**
     * Initiate a synchronization request.
     *
     * @param cache   cache interface, which is used to cache the request results.
     * @param request request object.
     * @param <T>     {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(Request)
     * @see #startRequestSync(ImplRestConnection, Request)
     * @see #startRequestSync(Cache, ImplRestConnection, Request)
     */
    public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, Request<T> request) {
        return startRequestSync(cache, HttpRestConnection.getInstance(), request);
    }

    /**
     * Initiate a synchronization request.
     *
     * @param implRestConnection complete implementation of the {@link ImplRestConnection}.
     * @param request            request object.
     * @param <T>                {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(Request)
     * @see #startRequestSync(Cache, Request)
     * @see #startRequestSync(Cache, ImplRestConnection, Request)
     */
    public static <T> Response<T> startRequestSync(ImplRestConnection implRestConnection, Request<T> request) {
        return startRequestSync(DiskCacheStore.INSTANCE, implRestConnection, request);
    }

    /**
     * Initiate a synchronization request.
     *
     * @param request request object.
     * @param <T>     {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(ImplRestConnection, Request)
     * @see #startRequestSync(Cache, Request)
     * @see #startRequestSync(Cache, ImplRestConnection, Request)
     */
    public static <T> Response<T> startRequestSync(Request<T> request) {
        return startRequestSync(HttpRestConnection.getInstance(), request);
    }

    /**
     * Create a new download queue, the default thread pool number is {@value NoHttp#DEFAULT_THREAD_SIZE}.
     *
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue(int)
     * @see #newDownloadQueue(Downloader, int)
     */
    public static DownloadQueue newDownloadQueue() {
        return newDownloadQueue(DEFAULT_THREAD_SIZE);
    }

    /**
     * Create a new download queue.
     *
     * @param threadPoolSize thread pool number, here is the number of concurrent tasks.
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue()
     * @see #newDownloadQueue(Downloader, int)
     */
    public static DownloadQueue newDownloadQueue(int threadPoolSize) {
        return newDownloadQueue(new DownloadConnection(), threadPoolSize);
    }

    /**
     * Create a new download queue.
     *
     * @param downloader     {@link Downloader}.
     * @param threadPoolSize number of concurrent.
     * @return {@link DownloadQueue}
     * @see #newDownloadQueue()
     * @see #newDownloadQueue(int)
     */
    public static DownloadQueue newDownloadQueue(Downloader downloader, int threadPoolSize) {
        DownloadQueue downloadQueue = new DownloadQueue(downloader, threadPoolSize);
        downloadQueue.start();
        return downloadQueue;
    }

    /**
     * Create a download object. The request method is {@link RequestMethod#GET}.
     *
     * @param url         download address.
     * @param fileFolder  folder to save file.
     * @param filename    filename.
     * @param isRange     whether the breakpoint continuing.
     * @param isDeleteOld find the same when the file is deleted after download, or on behalf of the download is complete, not to request the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, RequestMethod, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        return createDownloadRequest(url, RequestMethod.GET, fileFolder, filename, isRange, isDeleteOld);
    }

    /**
     * Create a download object.
     *
     * @param url           download address.
     * @param requestMethod {@link RequestMethod}.
     * @param fileFolder    folder to save file.
     * @param filename      filename.
     * @param isRange       whether the breakpoint continuing.
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is complete, not to request the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        return new RestDownloadRequest(url, requestMethod, fileFolder, filename, isRange, isDeleteOld);
    }

    /**
     * Get NoHttp Cookie manager by default.
     *
     * @return {@link CookieHandler}.
     * @see #setDefaultCookieHandler(CookieHandler)
     */
    public static CookieHandler getDefaultCookieHandler() {
        return sCookieHandler;
    }

    /**
     * Sets the system-wide cookie handler.
     *
     * @param cookieHandler {@link CookieHandler}.
     * @see #getDefaultCookieHandler()
     */
    public static void setDefaultCookieHandler(CookieHandler cookieHandler) {
        if (cookieHandler == null)
            throw new IllegalArgumentException("CookieHandler == null");
        sCookieHandler = cookieHandler;
    }

    private NoHttp() {
    }
}
