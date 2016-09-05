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
import com.yolanda.nohttp.rest.IParserRequest;
import com.yolanda.nohttp.rest.IRestParser;
import com.yolanda.nohttp.rest.IRestProtocol;
import com.yolanda.nohttp.rest.ImageRequest;
import com.yolanda.nohttp.rest.JsonArrayRequest;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.RestParser;
import com.yolanda.nohttp.rest.RestProtocol;
import com.yolanda.nohttp.rest.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.PasswordAuthentication;

/**
 * <p>
 * NoHttp.
 * </p>
 * Created in Jul 28, 2015 7:32:22 PM.
 *
 * @author Yan Zhenjie.
 */
public class NoHttp {

    /**
     * The value is {@value}.
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * RequestQueue default thread size, value is {@value}.
     */
    private static int DEFAULT_REQUEST_THREAD_SIZE = 3;

    /**
     * DownloadQueue default thread size, value is {@value}.
     */
    private static int DEFAULT_DOWNLOAD_THREAD_SIZE = 3;

    /**
     * Default connect timeout. The value is {@value}.
     */
    private static int sDefaultConnectTimeout = 10 * 1000;

    /**
     * Default read timeout. The value is {@value}.
     */
    private static int sDefaultReadTimeout = 10 * 1000;

    /**
     * Context.
     */
    private static Application sApplication;

    /**
     * Cookie.
     */
    private static CookieManager sCookieManager;
    /**
     * Is enable cookies.
     */
    private static boolean isEnableCookie = true;

    /**
     * Create a new request queue, using NoHttp default configuration. And number of concurrent requests is 3.
     *
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(Cache, IRestConnection, int)
     * @see #newRequestQueue(IRestProtocol, int)
     * @see #newRequestQueue(IRestParser, int)
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(DEFAULT_REQUEST_THREAD_SIZE);
    }

    /**
     * Create a new request queue, using NoHttp default configuration.
     *
     * @param threadPoolSize request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(Cache, IRestConnection, int)
     * @see #newRequestQueue(IRestProtocol, int)
     * @see #newRequestQueue(IRestParser, int)
     */
    public static RequestQueue newRequestQueue(int threadPoolSize) {
        return newRequestQueue(DiskCacheStore.INSTANCE, RestConnection.getInstance(), threadPoolSize);
    }

    /**
     * Create a new request queue, using NoHttp default request connection {@link RestProtocol} and default response parser {@link RestParser}.
     *
     * @param cache           cache interface, which is used to cache the request results.
     * @param iRestConnection The realization of the network layer.
     * @param threadPoolSize  request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(IRestProtocol, int)
     * @see #newRequestQueue(IRestParser, int)
     */
    public static RequestQueue newRequestQueue(Cache<CacheEntity> cache, IRestConnection iRestConnection, int threadPoolSize) {
        return newRequestQueue(RestProtocol.getInstance(cache, iRestConnection), threadPoolSize);
    }

    /**
     * Create a new request queue, using NoHttp default request executor {@link RestProtocol} and default response parser {@link RestParser}.
     *
     * @param iRestProtocol  network operating interface, The implementation of the network layer.
     * @param threadPoolSize request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(Cache, IRestConnection, int)
     * @see #newRequestQueue(IRestParser, int)
     */
    public static RequestQueue newRequestQueue(IRestProtocol iRestProtocol, int threadPoolSize) {
        return newRequestQueue(RestParser.getInstance(iRestProtocol), threadPoolSize);
    }

    /**
     * Create a new request queue.
     *
     * @param iRestParser the response parser, The result of parsing the network layer.
     * @param threadPoolSize request the number of concurrent.
     * @return Returns the request queue, the queue is used to control the entry of the request.
     * @see #newRequestQueue()
     * @see #newRequestQueue(int)
     * @see #newRequestQueue(Cache, IRestConnection, int)
     * @see #newRequestQueue(IRestProtocol, int)
     */
    public static RequestQueue newRequestQueue(IRestParser iRestParser, int threadPoolSize) {
        RequestQueue requestQueue = new RequestQueue(iRestParser, threadPoolSize);
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
     * @param request request object.
     * @param <T>     {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(Cache, IParserRequest)
     * @see #startRequestSync(IRestProtocol, IParserRequest)
     * @see #startRequestSync(IRestParser, IParserRequest)
     */
    public static <T> Response<T> startRequestSync(IParserRequest<T> request) {
        return startRequestSync(DiskCacheStore.INSTANCE, request);
    }

    /**
     * Initiate a synchronization request.
     *
     * @param cache   cache interface, which is used to cache the request results.
     * @param request tequest object.
     * @param <T>     {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(IParserRequest)
     * @see #startRequestSync(IRestProtocol, IParserRequest)
     * @see #startRequestSync(IRestParser, IParserRequest)
     */
    public static <T> Response<T> startRequestSync(Cache<CacheEntity> cache, IParserRequest<T> request) {
        return startRequestSync(RestProtocol.getInstance(cache, RestConnection.getInstance()), request);
    }

    /**
     * Initiate a synchronization request.
     *
     * @param implRestConnection complete implementation of the {@link IRestProtocol}.
     * @param request            request object.
     * @param <T>                {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(IParserRequest)
     * @see #startRequestSync(Cache, IParserRequest)
     * @see #startRequestSync(IRestParser, IParserRequest)
     */
    public static <T> Response<T> startRequestSync(IRestProtocol implRestConnection, IParserRequest<T> request) {
        return startRequestSync(RestParser.getInstance(implRestConnection), request);
    }

    /**
     * Initiate a synchronization request.
     *
     * @param implRestParser complete implementation of the {@link IRestParser}.
     * @param request        request object.
     * @param <T>            {@link T}.
     * @return {@link Response}.
     * @see #startRequestSync(IParserRequest)
     * @see #startRequestSync(Cache, IParserRequest)
     * @see #startRequestSync(IRestProtocol, IParserRequest)
     */
    public static <T> Response<T> startRequestSync(IRestParser implRestParser, IParserRequest<T> request) {
        return implRestParser.parserRequest(request);
    }

    /**
     * Create a new download queue, the default thread pool number is 3.
     *
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue(int)
     * @see #newDownloadQueue(Downloader, int)
     */
    public static DownloadQueue newDownloadQueue() {
        return newDownloadQueue(DEFAULT_DOWNLOAD_THREAD_SIZE);
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
        return newDownloadQueue(RestConnection.getInstance(), threadPoolSize);
    }

    /**
     * Create a new download queue.
     *
     * @param iRestConnection {@link IRestConnection}.
     * @param threadPoolSize  thread pool number, here is the number of concurrent tasks.
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue()
     * @see #newDownloadQueue(Downloader, int)
     */
    public static DownloadQueue newDownloadQueue(IRestConnection iRestConnection, int threadPoolSize) {
        return newDownloadQueue(DownloadConnection.getInstance(iRestConnection), threadPoolSize);
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
     * Create a download object, auto named file. The request method is {@link RequestMethod#GET}.
     *
     * @param url         download address.
     * @param fileFolder  folder to save file.
     * @param isDeleteOld find the same when the file is deleted after download, or on behalf of the download is complete, not to request the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, RequestMethod, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, String fileFolder, boolean isDeleteOld) {
        return createDownloadRequest(url, RequestMethod.GET, fileFolder, isDeleteOld);
    }

    /**
     * Create a download object, auto named file.
     *
     * @param url           download address.
     * @param requestMethod {@link RequestMethod}.
     * @param fileFolder    folder to save file.
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is complete, not to request the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, RequestMethod, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, boolean isDeleteOld) {
        return new RestDownloadRequest(url, requestMethod, fileFolder, isDeleteOld);
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
     * Get version name of NoHttp.
     *
     * @return {@link String}.
     */
    public static String versionName() {
        return "1.0.6";
    }

    /**
     * Get version code of NoHttp.
     *
     * @return {@link Integer}.
     */
    public static int versionCode() {
        return 106;
    }

    /**
     * Initialization NoHttp, Should invoke on {@link Application#onCreate()}.
     *
     * @param application {@link Application}.
     * @deprecated use {@link #initialize(Application)} instead.
     */
    @Deprecated
    public static void init(Application application) {
        initialize(application);
    }

    /**
     * Initialization NoHttp, Should invoke on {@link Application#onCreate()}.
     *
     * @param application {@link Application}.
     */
    public static void initialize(Application application) {
        if (sApplication == null) {
            sApplication = application;
            sCookieManager = new CookieManager(DiskCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL);
        }
    }

    /**
     * Get application of app.
     *
     * @return {@link Application}.
     */
    public static Application getContext() {
        if (sApplication == null)
            throw new ExceptionInInitializerError("Please invoke NoHttp.initialize(Application) on Application#onCreate()");
        return sApplication;
    }

    /**
     * It will be called whenever the realm that the URL is pointing to requires authorization.
     *
     * @param passwordAuthentication passwordAuthentication which has to be set as default.
     */
    public static void setDefaultAuthenticator(final PasswordAuthentication passwordAuthentication) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return passwordAuthentication;
            }
        });
    }

    /**
     * Set default connect timeout.
     *
     * @param timeout ms.
     */
    public static void setDefaultConnectTimeout(int timeout) {
        sDefaultConnectTimeout = timeout;
    }

    /**
     * Get default connect timeout.
     *
     * @return ms.
     */
    public static int getDefaultConnectTimeout() {
        return sDefaultConnectTimeout;
    }

    /**
     * Set default read timeout.
     *
     * @param timeout ms.
     */
    public static void setDefaultReadTimeout(int timeout) {
        sDefaultReadTimeout = timeout;
    }

    /**
     * Get default read timeout.
     *
     * @return ms.
     */
    public static int getDefaultReadTimeout() {
        return sDefaultReadTimeout;
    }

    /**
     * Set default request thread pool size.
     *
     * @param size count.
     */
    public static void setDefaultRequestThreadSize(int size) {
        DEFAULT_REQUEST_THREAD_SIZE = size;
    }

    /**
     * Get default request thread pool size.
     *
     * @return count.
     */
    public static int getDefaultRequestThreadSize() {
        return DEFAULT_REQUEST_THREAD_SIZE;
    }

    /**
     * Set default download thread pool size.
     *
     * @param size count.
     */
    public static void setDefaultDownloadThreadSize(int size) {
        DEFAULT_DOWNLOAD_THREAD_SIZE = size;
    }

    /**
     * Get default donwload thread pool size.
     *
     * @return count.
     */
    public static int getDefaultDownloadThreadSize() {
        return DEFAULT_DOWNLOAD_THREAD_SIZE;
    }

    /**
     * Set to enable cookies.
     *
     * @param enableCookie ture enable, false disenable.
     */
    public static void setEnableCookie(boolean enableCookie) {
        isEnableCookie = enableCookie;
    }

    /**
     * Get NoHttp Cookie manager by default.
     *
     * @return {@link CookieHandler}.
     * @see #setDefaultCookieManager(CookieManager)
     */
    public static CookieManager getDefaultCookieManager() {
        return sCookieManager;
    }

    /**
     * Sets the system-wide cookie handler.
     *
     * @param cookieHandler {@link CookieHandler}.
     * @see #getDefaultCookieManager()
     */
    public static void setDefaultCookieManager(CookieManager cookieHandler) {
        if (cookieHandler == null)
            throw new IllegalArgumentException("cookieHandler == null");
        sCookieManager = cookieHandler;
    }

    /**
     * Is enable cookie.
     *
     * @return true enable, false disenable.
     */
    public static boolean isEnableCookie() {
        return isEnableCookie;
    }

    private NoHttp() {
    }

    /*
     * =================================================
     * ||                   Instance                  ||
     * =================================================
     */

    /**
     * Default thread pool size for request queue.
     */
    private static RequestQueue sRequestQueueInstance;

    /**
     * Default thread pool size for request queue.
     */
    private static DownloadQueue sDownloadQueueInstance;

    /**
     * Get default RequestQueue.
     *
     * @return {@link RequestQueue}.
     */
    public static RequestQueue getRequestQueueInstance() {
        synchronized (NoHttp.class) {
            if (sRequestQueueInstance == null) {
                sRequestQueueInstance = newRequestQueue();
            }
        }
        return sRequestQueueInstance;
    }

    /**
     * Get default DownloadQueue.
     *
     * @return {@link DownloadQueue}.
     */
    public static DownloadQueue getDownloadQueueInstance() {
        synchronized (NoHttp.class) {
            if (sDownloadQueueInstance == null) {
                sDownloadQueueInstance = newDownloadQueue();
            }
        }
        return sDownloadQueueInstance;
    }

}
