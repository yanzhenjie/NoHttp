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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yolanda.nohttp.cache.Cache;
import com.yolanda.nohttp.cache.CacheEntity;
import com.yolanda.nohttp.cache.DiskCacheStore;
import com.yolanda.nohttp.cookie.DiskCookieStore;
import com.yolanda.nohttp.download.DownloadConnection;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.download.RestDownloadRequestor;
import com.yolanda.nohttp.tools.PRNGFixes;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * NoHttp
 * </br>
 * Created in Jul 28, 2015 7:32:22 PM
 *
 * @author YOLANDA
 */
public class NoHttp {
    /**
     * Default charset of request body, value is {@value}
     */
    public static final String CHARSET_UTF8 = "utf-8";
    /**
     * Default mimeType of upload file, value is {@value}
     */
    public static final String MIME_TYPE_FILE = "application/octet-stream";
    /**
     * Default timeout, value is {@value} ms
     */
    public static final int TIMEOUT_8S = 8 * 1000;
    /**
     * RequestQueue default thread size, value is {@value}
     */
    public static final int DEFAULT_THREAD_SIZE = 3;

    /**
     * Context
     */
    private static Application sApplication;

    /**
     * Cookie
     */
    private static CookieHandler sCookieHandler;

    /**
     * Initialization NoHttp, Should invoke on {@link Application#onCreate()}
     */
    public static void init(Application application) {
        if (sApplication == null) {
            sApplication = application;
            PRNGFixes.apply();
            sCookieHandler = new CookieManager(DiskCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL);
        }
    }

    /**
     * Get application of app
     */
    public static Application getContext() {
        if (sApplication == null)
            throw new ExceptionInInitializerError("Please invoke NoHttp.init(Application) on Application#onCreate()");
        return sApplication;
    }

    /**
     * Create a new request queue
     *
     * @param implRestParser The response parser, The result of parsing the network layer
     * @param threadPoolSize Request the number of concurrent
     * @return Returns the request queue, the queue is used to control the entry of the request
     */
    public static RequestQueue newRequestQueue(ImplRestParser implRestParser, int threadPoolSize) {
        RequestQueue requestQueue = new RequestQueue(implRestParser, threadPoolSize);
        requestQueue.start();
        return requestQueue;
    }

    /**
     * Create a new request queue, Using NoHttp default response parser {@link HttpRestParser}
     *
     * @param implRestExecutor The executor, Interact with the network layer
     * @param threadPoolSize   Request the number of concurrent
     * @return Returns the request queue, the queue is used to control the entry of the request
     */
    public static RequestQueue newRequestQueue(ImplRestExecutor implRestExecutor, int threadPoolSize) {
        return newRequestQueue(HttpRestParser.getInstance(implRestExecutor), threadPoolSize);
    }

    /**
     * Create a new request queue, Using NoHttp default request executor {@link HttpRestExecutor} and default response parser {@link HttpRestParser}
     *
     * @param cache              Cache interface, which is used to cache the request results
     * @param implRestConnection Network operating interface, The implementation of the network layer
     * @param threadPoolSize     Request the number of concurrent
     * @return Returns the request queue, the queue is used to control the entry of the request
     */
    public static RequestQueue newRequestQueue(Cache<CacheEntity> cache, ImplRestConnection implRestConnection, int threadPoolSize) {
        return newRequestQueue(HttpRestExecutor.getInstance(cache, implRestConnection), threadPoolSize);
    }

    /**
     * Create a new request queue, Using NoHttp default configuration
     *
     * @param threadPoolSize Request the number of concurrent
     * @return Returns the request queue, the queue is used to control the entry of the request
     */
    public static RequestQueue newRequestQueue(int threadPoolSize) {
        return newRequestQueue(DiskCacheStore.INSTANCE, HttpRestConnection.getInstance(), threadPoolSize);
    }

    /**
     * Create a new request queue, Using NoHttp default configuration. And number of concurrent requests is {@value #DEFAULT_THREAD_SIZE}
     *
     * @return Returns the request queue, the queue is used to control the entry of the request
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(DEFAULT_THREAD_SIZE);
    }

    /**
     * Create a String type request, The request method is {@code GET}
     */
    public static Request<String> createStringRequest(String url) {
        return new StringRequest(url);
    }

    /**
     * Create a String type request, custom request method, method from {@link RequestMethod}
     */
    public static Request<String> createStringRequest(String url, RequestMethod requestMethod) {
        return new StringRequest(url, requestMethod);
    }

    /**
     * Create a JSONObject type request, The request method is {@code GET}
     */
    public static Request<JSONObject> createJsonObjectRequest(String url) {
        return new JsonObjectRequest(url);
    }

    /**
     * Create a JSONObject type request, custom request method, method from {@linkplain RequestMethod}
     */
    public static Request<JSONObject> createJsonObjectRequest(String url, RequestMethod requestMethod) {
        return new JsonObjectRequest(url, requestMethod);
    }

    /**
     * Create a JSONArray type request, The request method is {@code GET}
     */
    public static Request<JSONArray> createJsonArrayRequest(String url) {
        return new JsonArrayRequest(url);
    }

    /**
     * Create a JSONArray type request, custom request method, method from {@link RequestMethod}
     */
    public static Request<JSONArray> createJsonArrayRequest(String url, RequestMethod requestMethod) {
        return new JsonArrayRequest(url, requestMethod);
    }

    /**
     * Create a Image type request
     */
    public static Request<Bitmap> createImageRequest(String url) {
        return createImageRequest(url, RequestMethod.GET);
    }

    /**
     * Create a Image type request
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod) {
        return createImageRequest(url, requestMethod, 1000, 1000, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * Create a Image type request
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod, int maxWidth, int maxHeight, Bitmap.Config config, ImageView.ScaleType scaleType) {
        return new ImageRequest(url, requestMethod, maxWidth, maxHeight, config, scaleType);
    }

    /**
     * Initiate a synchronization request
     *
     * @param cache              Cache interface, which is used to cache the request results
     * @param implRestConnection Network operating interface, The implementation of the network layer
     * @param request            Request object
     * @return Response result
     */
    public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, ImplRestConnection implRestConnection, Request<T> request) {
        Response<T> response = null;
        if (cache != null && implRestConnection != null && request != null)
            response = HttpRestParser.getInstance(HttpRestExecutor.getInstance(DiskCacheStore.INSTANCE, HttpRestConnection.getInstance())).parserRequest(request);
        return response;
    }

    /**
     * Initiate a synchronization request
     *
     * @param cache   Cache interface, which is used to cache the request results
     * @param request Request object
     * @return Response result
     */
    public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, Request<T> request) {
        return startRequestSync(cache, HttpRestConnection.getInstance(), request);
    }

    /**
     * Initiate a synchronization request
     *
     * @param request Request object
     * @return Response result
     */
    public static <T> Response<T> startRequestSync(ImplRestConnection implRestConnection, Request<T> request) {
        return startRequestSync(DiskCacheStore.INSTANCE, implRestConnection, request);
    }

    /**
     * Initiate a synchronization request
     *
     * @param request Request object
     * @return Response result
     */
    public static <T> Response<T> startRequestSync(Request<T> request) {
        return startRequestSync(DiskCacheStore.INSTANCE, HttpRestConnection.getInstance(), request);
    }

    /**
     * Create a new download queue, the default thread pool number is {@value NoHttp#DEFAULT_THREAD_SIZE}
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
        DownloadQueue downloadQueue = new DownloadQueue(new DownloadConnection(), threadPoolSize);
        downloadQueue.start();
        return downloadQueue;
    }

    /**
     * Create a download object
     *
     * @param url         Download address
     * @param fileFolder  Folder to save file
     * @param filename    Filename
     * @param isRange     Whether the breakpoint continuingly
     * @param isDeleteOld Find the same when the file is deleted after download, or on behalf of the download is complete, not to request the network
     * @return {@link DownloadRequest}
     */
    public static DownloadRequest createDownloadRequest(String url, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        return createDownloadRequest(url, RequestMethod.GET, fileFolder, filename, isRange, isDeleteOld);
    }

    /**
     * Create a download object
     *
     * @param url           Download address
     * @param requestMethod {@link RequestMethod}
     * @param fileFolder    Folder to save file
     * @param filename      Filename
     * @param isRange       Whether the breakpoint continuingly
     * @param isDeleteOld   Find the same when the file is deleted after download, or on behalf of the download is complete, not to request the network
     * @return {@link DownloadRequest}
     */
    public static DownloadRequest createDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        return new RestDownloadRequestor(url, requestMethod, fileFolder, filename, isRange, isDeleteOld);
    }

    /**
     * Get NoHttp Cookie manager by default
     */
    public static CookieHandler getDefaultCookieHandler() {
        return sCookieHandler;
    }

    /**
     * Sets the system-wide cookie handler
     */
    public static void setDefaultCookieHandler(CookieHandler cookieHandler) {
        if (cookieHandler == null)
            throw new IllegalArgumentException("CookieHandler == null");
        sCookieHandler = cookieHandler;
    }

    private NoHttp() {
    }
}
