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
package com.yanzhenjie.nohttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.ByteArrayRequest;
import com.yanzhenjie.nohttp.rest.ImageRequest;
import com.yanzhenjie.nohttp.rest.JsonArrayRequest;
import com.yanzhenjie.nohttp.rest.JsonObjectRequest;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <p>
 * NoHttp.
 * </p>
 * Created in Jul 28, 2015 7:32:22 PM.
 *
 * @author Yan Zhenjie.
 */
public class NoHttp {

    private static InitializationConfig sInitializeConfig;

    private NoHttp() {
    }

    /**
     * Initialize NoHttp, should invoke on {@link android.app.Application#onCreate()}.
     *
     * @param context {@link Context}.
     */
    public static void initialize(Context context) {
        initialize(InitializationConfig.newBuilder(context).build());
    }

    /**
     * Initialize NoHttp, should invoke on {@link android.app.Application#onCreate()}.
     */
    public static void initialize(InitializationConfig initializeConfig) {
        sInitializeConfig = initializeConfig;
    }

    /**
     * Test initialized.
     */
    private static void testInitialize() {
        if (sInitializeConfig == null)
            throw new ExceptionInInitializerError("Please invoke NoHttp.initialize(Application) on Application#onCreate()");
    }

    /**
     * Gets context of app.
     */
    public static Context getContext() {
        testInitialize();
        return sInitializeConfig.getContext();
    }

    /**
     * Get InitializationConfig.
     */
    public static InitializationConfig getInitializeConfig() {
        testInitialize();
        return sInitializeConfig;
    }

    /**
     * Create a queue of handle, the default thread pool size is 3.
     *
     * @return returns the handle queue, the queue is used to control the entry of the handle.
     * @see #newRequestQueue(int)
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(3);
    }

    /**
     * Create a queue of handle.
     *
     * @param threadPoolSize handle the number of concurrent.
     * @return returns the handle queue, the queue is used to control the entry of the handle.
     * @see #newRequestQueue()
     */
    public static RequestQueue newRequestQueue(int threadPoolSize) {
        RequestQueue requestQueue = new RequestQueue(threadPoolSize);
        requestQueue.start();
        return requestQueue;
    }

    /**
     * Create a String type handle, the handle method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.nohttp.net}.
     * @return {@code Request<String>}.
     * @see #createStringRequest(String, RequestMethod)
     */
    public static Request<String> createStringRequest(String url) {
        return new StringRequest(url);
    }

    /**
     * Create a String type handle, custom handle method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.nohttp.net}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<String>}.
     * @see #createStringRequest(String)
     */
    public static Request<String> createStringRequest(String url, RequestMethod requestMethod) {
        return new StringRequest(url, requestMethod);
    }

    /**
     * Create a JSONObject type handle, the handle method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.nohttp.net}.
     * @return {@code Request<JSONObject>}.
     * @see #createJsonObjectRequest(String, RequestMethod)
     */
    public static Request<JSONObject> createJsonObjectRequest(String url) {
        return new JsonObjectRequest(url);
    }

    /**
     * Create a JSONObject type handle, custom handle method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.nohttp.net}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<JSONObject>}.
     * @see #createJsonObjectRequest(String)
     */
    public static Request<JSONObject> createJsonObjectRequest(String url, RequestMethod requestMethod) {
        return new JsonObjectRequest(url, requestMethod);
    }

    /**
     * Create a JSONArray type handle, the handle method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.nohttp.net}.
     * @return {@code Request<JSONArray>}.
     * @see #createJsonArrayRequest(String, RequestMethod)
     */
    public static Request<JSONArray> createJsonArrayRequest(String url) {
        return new JsonArrayRequest(url);
    }

    /**
     * Create a JSONArray type handle, custom handle method, method from {@link RequestMethod}.
     *
     * @param url           such as: {@code http://www.nohttp.net}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<JSONArray>}.
     * @see #createJsonArrayRequest(String)
     */
    public static Request<JSONArray> createJsonArrayRequest(String url, RequestMethod requestMethod) {
        return new JsonArrayRequest(url, requestMethod);
    }

    /**
     * Create a Image type handle, the handle method is {@link RequestMethod#GET}.
     *
     * @param url such as: {@code http://www.nohttp.net}.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String, RequestMethod)
     * @see #createImageRequest(String, RequestMethod, int, int, Bitmap.Config, ImageView.ScaleType)
     */
    public static Request<Bitmap> createImageRequest(String url) {
        return createImageRequest(url, RequestMethod.GET);
    }

    /**
     * Create a Image type handle.
     *
     * @param url           such as: {@code http://www.nohttp.net}.
     * @param requestMethod {@link RequestMethod}.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String)
     * @see #createImageRequest(String, RequestMethod, int, int, Bitmap.Config, ImageView.ScaleType)
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod) {
        return createImageRequest(url, requestMethod, 1000, 1000, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * Create a Image type handle.
     *
     * @param url           such as: {@code http://www.nohttp.net}.
     * @param requestMethod {@link RequestMethod}.
     * @param maxWidth      width.
     * @param maxHeight     height.
     * @param config        config.
     * @param scaleType     scaleType.
     * @return {@code Request<Bitmap>}.
     * @see #createImageRequest(String)
     * @see #createImageRequest(String, RequestMethod)
     */
    public static Request<Bitmap> createImageRequest(String url, RequestMethod requestMethod,
                                                     int maxWidth, int maxHeight,
                                                     Bitmap.Config config, ImageView.ScaleType scaleType) {
        return new ImageRequest(url, requestMethod, maxWidth, maxHeight, config, scaleType);
    }

    /**
     * Create a byte array handle, the handle method is {@link RequestMethod#GET}.
     *
     * @param url url.
     * @return {@code Request<byte[]>}.
     * @see #createByteArrayRequest(String, RequestMethod)
     */
    public static Request<byte[]> createByteArrayRequest(String url) {
        return new ByteArrayRequest(url);
    }

    /**
     * Create a byte array handle.
     *
     * @param url    url.
     * @param method {@link RequestMethod}.
     * @return {@code Request<byte[]>}.
     * @see #createByteArrayRequest(String)
     */
    public static Request<byte[]> createByteArrayRequest(String url, RequestMethod method) {
        return new ByteArrayRequest(url, method);
    }

    /**
     * Initiate a synchronization handle.
     *
     * @param request handle object.
     * @param <T>     {@link T}.
     * @return {@link Response}.
     */
    public static <T> Response<T> startRequestSync(Request<T> request) {
        return SyncRequestExecutor.INSTANCE.execute(request);
    }

    /**
     * Create a new download queue, the default thread pool size is 3.
     *
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue(int)
     */
    public static DownloadQueue newDownloadQueue() {
        return newDownloadQueue(3);
    }

    /**
     * Create a new download queue.
     *
     * @param threadPoolSize thread pool number, here is the number of concurrent tasks.
     * @return {@link DownloadQueue}.
     * @see #newDownloadQueue()
     */
    public static DownloadQueue newDownloadQueue(int threadPoolSize) {
        DownloadQueue downloadQueue = new DownloadQueue(threadPoolSize);
        downloadQueue.start();
        return downloadQueue;
    }

    /**
     * Create a download object, auto named file. The handle method is {@link RequestMethod#GET}.
     *
     * @param url         download address.
     * @param fileFolder  folder to save file.
     * @param isDeleteOld find the same when the file is deleted after download, or on behalf of the download is
     *                    complete, not to handle the network.
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
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is
     *                      complete, not to handle the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, RequestMethod, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, boolean isDeleteOld) {
        return new DownloadRequest(url, requestMethod, fileFolder, true, isDeleteOld);
    }

    /**
     * Create a download object, auto named file.
     *
     * @param url           download address.
     * @param requestMethod {@link RequestMethod}.
     * @param fileFolder    folder to save file.
     * @param isRange       whether the breakpoint continuing.
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is
     *                      complete, not to handle the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, RequestMethod, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, boolean isRange, boolean isDeleteOld) {
        return new DownloadRequest(url, requestMethod, fileFolder, isRange, isDeleteOld);
    }

    /**
     * Create a download object. The handle method is {@link RequestMethod#GET}.
     *
     * @param url         download address.
     * @param fileFolder  folder to save file.
     * @param filename    filename.
     * @param isRange     whether the breakpoint continuing.
     * @param isDeleteOld find the same when the file is deleted after download, or on behalf of the download is
     *                    complete, not to handle the network.
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
     * @param isDeleteOld   find the same when the file is deleted after download, or on behalf of the download is
     *                      complete, not to handle the network.
     * @return {@link DownloadRequest}.
     * @see #createDownloadRequest(String, String, String, boolean, boolean)
     */
    public static DownloadRequest createDownloadRequest(String url, RequestMethod requestMethod, String fileFolder, String filename, boolean isRange, boolean isDeleteOld) {
        return new DownloadRequest(url, requestMethod, fileFolder, filename, isRange, isDeleteOld);
    }

    /**
     * Default thread pool size for handle queue.
     */
    private static RequestQueue sRequestQueueInstance;

    /**
     * Default thread pool size for handle queue.
     */
    private static DownloadQueue sDownloadQueueInstance;

    /**
     * Get default RequestQueue.
     *
     * @return {@link RequestQueue}.
     */
    public static RequestQueue getRequestQueueInstance() {
        if (sRequestQueueInstance == null)
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
        if (sDownloadQueueInstance == null)
            synchronized (NoHttp.class) {
                if (sDownloadQueueInstance == null) {
                    sDownloadQueueInstance = newDownloadQueue();
                }
            }
        return sDownloadQueueInstance;
    }
}
