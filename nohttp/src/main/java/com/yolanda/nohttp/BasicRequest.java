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

import android.text.TextUtils;

import com.yolanda.nohttp.cache.CacheMode;
import com.yolanda.nohttp.tools.CounterOutputStream;
import com.yolanda.nohttp.tools.Writer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>Implement all the methods of the base class {@link ImplServerRequest} and {@link ImplClientRequest}.</p>
 * Created in Nov 4, 2015 8:28:50 AM.
 *
 * @author YOLANDA;
 */
;

public abstract class BasicRequest<T> implements Request<T> {

    private final String boundary = createBoundary();
    private final String start_boundary = "--" + boundary;
    private final String end_boundary = start_boundary + "--";

    /**
     * User-Agent.
     */
    private static String userAgent;

    /**
     * Accept-Language.
     */
    private static String acceptLanguage;

    /**
     * Target address.
     */
    private String url;
    /**
     * The real .
     */
    private String buildUrl;
    /**
     * Request method.
     */
    private RequestMethod mRequestMethod;
    /**
     * Cache key.
     */
    private String mCacheKey;
    /**
     * If just read from cache.
     */
    private CacheMode mCacheMode = CacheMode.DEFAULT;
    /**
     * Proxy server.
     */
    private Proxy mProxy;
    /**
     * SSLSockets.
     */
    private SSLSocketFactory mSSLSocketFactory = null;
    /**
     * HostnameVerifier.
     */
    private HostnameVerifier mHostnameVerifier = null;
    /**
     * Connect timeout of request.
     */
    private int mConnectTimeout = NoHttp.TIMEOUT_8S;
    /**
     * Read data timeout.
     */
    private int mReadTimeout = NoHttp.TIMEOUT_8S;
    /**
     * Redirect handler.
     */
    private RedirectHandler mRedirectHandler;
    /**
     * Request heads.
     */
    private Headers mHeaders;
    /**
     * RequestBody.
     */
    private byte[] mRequestBody;
    /**
     * Queue tag.
     */
    private boolean queue = false;
    /**
     * The record has started.
     */
    private boolean isStart = false;
    /**
     * The request is completed.
     */
    private boolean isFinished = false;
    /**
     * Has been canceled.
     */
    private boolean isCanceled = false;
    /**
     * Cancel sign.
     */
    private Object cancelSign;
    /**
     * Tag of request.
     */
    private Object mTag;

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request address, like: http://www.google.com.
     */
    public BasicRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a request.
     *
     * @param url           request adress, like: http://www.google.com.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public BasicRequest(String url, RequestMethod requestMethod) {
        if (TextUtils.isEmpty(url))
            throw new IllegalArgumentException("url is null");
        this.url = url;
        this.mRequestMethod = requestMethod;
        this.mHeaders = new HttpHeaders();
    }

    @Override
    public String url() {
        if (TextUtils.isEmpty(buildUrl))
            buildUrl = buildUrl();
        return buildUrl;
    }

    /**
     * Rebuilding the URL, compatible with the GET method, using {@link Request#add(String, String)}.
     *
     * @return String url.
     */
    protected final String buildUrl() {
        StringBuffer urlBuffer = new StringBuffer(url);
        if (!doOutPut() && keySet().size() > 0) {
            StringBuffer paramBuffer = buildCommonParams();
            if (url.contains("?") && url.contains("=") && paramBuffer.length() > 0)
                urlBuffer.append("&");
            else if (paramBuffer.length() > 0)
                urlBuffer.append("?");
            urlBuffer.append(paramBuffer);
        }
        return urlBuffer.toString();
    }

    @Override
    public RequestMethod getRequestMethod() {
        return mRequestMethod;
    }

    @Override
    public boolean needCache() {
        return RequestMethod.GET == getRequestMethod() || CacheMode.REQUEST_FAILED_READ_CACHE == getCacheMode();
    }

    @Override
    public void setCacheKey(String key) {
        this.mCacheKey = key;
    }

    @Override
    public String getCacheKey() {
        return TextUtils.isEmpty(mCacheKey) ? buildUrl() : mCacheKey;
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        this.mCacheMode = cacheMode;
    }

    @Override
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    @Override
    public void setProxy(Proxy proxy) {
        this.mProxy = proxy;
    }

    @Override
    public Proxy getProxy() {
        return mProxy;
    }

    @Override
    public void setSSLSocketFactory(SSLSocketFactory socketFactory) {
        mSSLSocketFactory = socketFactory;
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    @Override
    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.mHostnameVerifier = hostnameVerifier;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    @Override
    public boolean doOutPut() {
        switch (mRequestMethod) {
            case GET:
            case DELETE:
            case HEAD:
            case OPTIONS:
            case TRACE:
                return false;
            case POST:
            case PUT:
            case PATCH:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.mConnectTimeout = connectTimeout;
    }

    @Override
    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        this.mReadTimeout = readTimeout;
    }

    @Override
    public int getReadTimeout() {
        return mReadTimeout;
    }

    @Override
    public void setRedirectHandler(RedirectHandler redirectHandler) {
        this.mRedirectHandler = redirectHandler;
    }

    @Override
    public RedirectHandler getRedirectHandler() {
        return mRedirectHandler;
    }

    @Override
    public void setHeader(String key, String value) {
        mHeaders.set(key, value);
    }

    @Override
    public void addHeader(String key, String value) {
        mHeaders.add(key, value);
    }

    @Override
    public void removeHeader(String key) {
        mHeaders.remove(key);
    }

    @Override
    public void removeAllHeader() {
        mHeaders.clear();
    }

    @Override
    public Headers headers() {
        return this.mHeaders;
    }

    @Override
    public String getAcceptLanguage() {
        if (TextUtils.isEmpty(acceptLanguage))
            acceptLanguage = createAcceptLanguage();
        return acceptLanguage;
    }

    @Override
    public long getContentLength() {
        CounterOutputStream outputStream = new CounterOutputStream();
        onWriteRequestBody(new Writer(outputStream));
        return outputStream.get();
    }

    @Override
    public String getContentType() {
        StringBuilder contentTypeBuild = new StringBuilder();
        if (doOutPut() && hasBinary())
            contentTypeBuild.append("multipart/form-data; boundary=").append(boundary);
        else
            contentTypeBuild.append("application/x-www-form-urlencoded; charset=").append(getParamsEncoding());
        return contentTypeBuild.toString();
    }

    @Override
    public String getUserAgent() {
        if (TextUtils.isEmpty(userAgent))
            userAgent = UserAgent.getUserAgent();
        return userAgent;
    }

    @Override
    public void setRequestBody(byte[] requestBody) {
        this.mRequestBody = requestBody;
    }

    @Override
    public void setRequestBody(String requestBody) {
        if (!TextUtils.isEmpty(requestBody))
            try {
                this.mRequestBody = requestBody.getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                Logger.e(e);
            }
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onWriteRequestBody(Writer writer) {
        if (mRequestBody == null && hasBinary())
            writeFormStreamData(writer);
        else if (mRequestBody == null)
            writeCommonStreamData(writer);
        else
            writeRequestBody(writer);
    }

    /**
     * Send form data.
     *
     * @param writer {@link Writer}.
     */
    protected void writeFormStreamData(Writer writer) {
        try {
            Set<String> keys = keySet();
            for (String key : keys) {// 文件或者图片
                Object value = value(key);
                if (value != null && value instanceof String) {
                    writeFormString(writer, key, value.toString());
                } else if (value != null && value instanceof Binary) {
                    writeFormBinary(writer, key, (Binary) value);
                }
            }
            writer.write(("\r\n" + end_boundary + "\r\n").getBytes());
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    /**
     * Send text data in a form.
     *
     * @param writer {@link Writer}
     * @param key    equivalent to form the name of the input label, {@code "Content-Disposition: form-data; name=key"}.
     * @param value  equivalent to form the value of the input label.
     * @throws IOException Write the data may be abnormal.
     */
    private void writeFormString(Writer writer, String key, String value) throws IOException {
        print(writer.isPrint(), key + " = " + value);

        StringBuilder stringFieldBuilder = new StringBuilder(start_boundary).append("\r\n");

        stringFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n");
        stringFieldBuilder.append("Content-Type: text/plain; charset=").append(getParamsEncoding()).append("\r\n\r\n");

        writer.write(stringFieldBuilder.toString().getBytes());

        writer.write(value.getBytes(getParamsEncoding()));
        writer.write("\r\n".getBytes());
    }

    /**
     * Send binary data in a form.
     */
    private void writeFormBinary(Writer writer, String key, Binary value) throws IOException {
        print(writer.isPrint(), key + " is Binary");

        StringBuilder binaryFieldBuilder = new StringBuilder(start_boundary).append("\r\n");
        binaryFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"");
        String filename = value.getFileName();
        if (!TextUtils.isEmpty(filename))
            binaryFieldBuilder.append("; filename=\"").append(value.getFileName()).append("\"");
        binaryFieldBuilder.append("\r\n");

        binaryFieldBuilder.append("Content-Type: ").append(value.getMimeType()).append("\r\n");
        binaryFieldBuilder.append("Content-Transfer-Encoding: binary\r\n\r\n");

        writer.write(binaryFieldBuilder.toString().getBytes());

        writer.write(value);
        writer.write("\r\n".getBytes());
    }

    /**
     * Send non form data.
     *
     * @param writer {@link Writer} structure of the Writer's need to HttpURLConnection OutputStream.
     */
    protected void writeCommonStreamData(Writer writer) {
        String requestBody = buildCommonParams().toString();
        print(writer.isPrint(), "RequestBody: " + requestBody);
        try {
            if (requestBody.length() > 0)
                writer.write(requestBody.getBytes());
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    /**
     * Send request requestBody.
     *
     * @param writer structure of the Writer's need to HttpURLConnection OutputStream.
     */
    protected void writeRequestBody(Writer writer) {
        try {
            print(writer.isPrint(), "Write RequestBody");
            if (mRequestBody.length > 0)
                writer.write(mRequestBody);
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    /**
     * split joint non form data.
     *
     * @return string parameter combination, each key value on nails with {@code "&"} space.
     */
    protected StringBuffer buildCommonParams() {
        StringBuffer paramBuffer = new StringBuffer();
        Set<String> keySet = keySet();
        for (String key : keySet) {
            Object value = value(key);
            if (value != null && value instanceof CharSequence) {
                paramBuffer.append("&");
                String paramEncoding = getParamsEncoding();
                try {
                    paramBuffer.append(URLEncoder.encode(key, paramEncoding));
                    paramBuffer.append("=");
                    paramBuffer.append(URLEncoder.encode(value.toString(), paramEncoding));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Encoding " + getParamsEncoding() + " format is not supported by the system");
                }
            }
        }
        if (paramBuffer.length() > 0)
            paramBuffer.deleteCharAt(0);
        return paramBuffer;
    }

    @Override
    public void setTag(Object tag) {
        this.mTag = tag;
    }

    @Override
    public Object getTag() {
        return this.mTag;
    }

    @Override
    public void queue(boolean queue) {
        this.queue = queue;
    }

    @Override
    public boolean isQueue() {
        return queue;
    }

    @Override
    public void toggleQueue() {
        this.queue = !queue;
    }

    @Override
    public void start(boolean start) {
        this.isStart = start;
        if (start)
            this.isFinished = false;
    }

    @Override
    public boolean isStarted() {
        return isStart;
    }

    @Override
    public void toggleStart() {
        this.isStart = !isStart;
    }

    @Override
    public void finish(boolean finish) {
        this.isFinished = finish;
        if (finish)
            this.isStart = false;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void toggleFinish() {
        this.isFinished = !isFinished;
    }

    @Override
    public void cancel(boolean cancel) {
        this.isCanceled = cancel;
        if (cancel)
            this.isStart = false;
    }

    @Override
    public void toggleCancel() {
        this.isCanceled = false;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }


    @Override
    public void setCancelSign(Object sign) {
        this.cancelSign = sign;
    }

    @Override
    public void cancelBySign(Object sign) {
        if (cancelSign == sign)
            cancel(true);
    }

    /**
     * Returns the data "Charset".
     *
     * @return Such as: {@code UTF-8}, {@code GBK}, {@code GB2312}.
     */
    public String getParamsEncoding() {
        return NoHttp.CHARSET_UTF8;
    }

    /**
     * Get the parameters set.
     *
     * @return Should return the set of all the parameters.
     */
    protected abstract Set<String> keySet();

    /**
     * Return {@link #keySet()} key corresponding to value.
     *
     * @param key from {@link #keySet()}.
     * @return Param value
     */
    protected abstract Object value(String key);

    /**
     * Is there a Binary data upload ?
     *
     * @return Said true, false said no.
     */
    protected boolean hasBinary() {
        Set<String> keys = keySet();
        for (String key : keys) {
            Object value = value(key);
            if (value instanceof Binary) {
                return true;
            }
        }
        return false;
    }

    private void print(boolean isPrint, String msg) {
        if (isPrint)
            Logger.d(msg);
    }

    /**
     * Create acceptLanguage.
     *
     * @return Returns the client can accept the language types. Such as:zh-CN,zh;0.8
     */
    public static String createAcceptLanguage() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        StringBuilder acceptLanguageBuilder = new StringBuilder(language);
        if (!TextUtils.isEmpty(country))
            acceptLanguageBuilder.append('-').append(country).append(',').append(language).append(";q=0.8");
        return acceptLanguageBuilder.toString();
    }

    /**
     * Randomly generated boundary mark.
     *
     * @return Random code.
     */
    public static String createBoundary() {
        StringBuffer sb = new StringBuffer("------------------");
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3L == 0L) {
                sb.append((char) (int) time % '\t');
            } else if (time % 3L == 1L) {
                sb.append((char) (int) (65L + time % 26L));
            } else {
                sb.append((char) (int) (97L + time % 26L));
            }
        }
        return sb.toString();
    }

}