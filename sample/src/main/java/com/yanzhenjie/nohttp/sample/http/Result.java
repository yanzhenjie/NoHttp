/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

import com.yanzhenjie.nohttp.Headers;

/**
 * <p>封装的请求结果。</p>
 * Created by Yan Zhenjie on 2016/11/23.
 */
public class Result<T> {

    /**
     * 业务是否成功。
     */
    private boolean isSucceed;
    /**
     * 是否来自缓存的数据。
     */
    private boolean isFromCache;
    /**
     * 响应头。
     */
    private Headers headers;
    /**
     * 结果。
     */
    private T t;
    /**
     * 业务代码，目前和Http相同。
     */
    private int mLogicCode;

    /**
     * 错误消息。
     */
    private String message;

    Result(boolean isSucceed, Headers headers, T t, int logicCode, String message) {
        this.isSucceed = isSucceed;
        this.headers = headers;
        this.t = t;
        this.mLogicCode = logicCode;
        this.message = message;
    }

    /**
     * 业务是否成功。
     */
    public boolean isSucceed() {
        return isSucceed;
    }

    /**
     * 是否是框架级别的错误。
     */
    public boolean isLocalError() {
        return mLogicCode == -1;
    }

    /**
     * 设置是否是来自缓存的数据。
     */
    void setFromCache(boolean fromCache) {
        isFromCache = fromCache;
    }

    /**
     * 是否是来自缓存的数据。
     */
    public boolean isFromCache() {
        return isFromCache;
    }

    /**
     * Http响应头。
     */
    public Headers headers() {
        return headers;
    }

    /**
     * 请求data体结果。
     */
    public T get() {
        return t;
    }

    /**
     * 业务状态码。
     */
    public int getLogicCode() {
        return mLogicCode;
    }

    /**
     * 服务器错误信息。
     */
    public String error() {
        return message;
    }
}
