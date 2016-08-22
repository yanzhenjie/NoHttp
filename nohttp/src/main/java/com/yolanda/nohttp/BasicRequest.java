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

import android.text.TextUtils;

import com.yolanda.nohttp.tools.CounterOutputStream;
import com.yolanda.nohttp.tools.HeaderUtil;
import com.yolanda.nohttp.tools.IOUtils;
import com.yolanda.nohttp.tools.LinkedMultiValueMap;
import com.yolanda.nohttp.tools.MultiValueMap;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * <p>
 * Implement all the methods of the base class {@link IBasicRequest}.
 * </p>
 * Created in Nov 4, 2015 8:28:50 AM.
 *
 * @author Yan Zhenjie.
 */
public abstract class BasicRequest implements IBasicRequest {

    private final String boundary = createBoundary();
    private final String startBoundary = "--" + boundary;
    private final String endBoundary = startBoundary + "--";

    /**
     * Request priority.
     */
    private Priority mPriority = Priority.DEFAULT;
    /**
     * The sequence.
     */
    private int sequence;
    /**
     * Target address.
     */
    private String url;
    /**
     * Request method.
     */
    private RequestMethod mRequestMethod;
    /**
     * MultipartFormEnable.
     */
    private boolean isMultipartFormEnable = false;
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
    private int mConnectTimeout = NoHttp.getDefaultConnectTimeout();
    /**
     * Read data timeout.
     */
    private int mReadTimeout = NoHttp.getDefaultReadTimeout();
    /**
     * Request heads.
     */
    private Headers mHeaders;
    /**
     * After the failure of retries.
     */
    private int mRetryCount;
    /**
     * The params encoding.
     */
    private String mParamEncoding;
    /**
     * Param collection.
     */
    private MultiValueMap<String, Object> mParamKeyValues;
    /**
     * RequestBody.
     */
    private InputStream mRequestBody;
    /**
     * Redirect handler.
     */
    private RedirectHandler mRedirectHandler;
    /**
     * Request queue
     */
    private BlockingQueue<?> blockingQueue;
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
    private Object mCancelSign;
    /**
     * Tag of request.
     */
    private Object mTag;

    /**
     * Create a request, RequestMethod is {@link RequestMethod#GET}.
     *
     * @param url request address, like: http://www.yanzhenjie.com.
     */
    public BasicRequest(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Create a request.
     *
     * @param url           request adress, like: http://www.yanzhenjie.com.
     * @param requestMethod request method, like {@link RequestMethod#GET}, {@link RequestMethod#POST}.
     */
    public BasicRequest(String url, RequestMethod requestMethod) {
        this.url = url;
        mRequestMethod = requestMethod;

        mHeaders = new HttpHeaders();
        mHeaders.set(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_VALUE_ACCEPT_ALL);
        mHeaders.set(Headers.HEAD_KEY_ACCEPT_ENCODING, Headers.HEAD_VALUE_ACCEPT_ENCODING_GZIP_DEFLATE);
        mHeaders.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, HeaderUtil.systemAcceptLanguage());
        mHeaders.set(Headers.HEAD_KEY_USER_AGENT, UserAgent.instance());

        mParamKeyValues = new LinkedMultiValueMap<String, Object>();
    }

    @Override
    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    @Override
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public int getSequence() {
        return this.sequence;
    }

    @Override
    public final int compareTo(IBasicRequest another) {
        final Priority me = getPriority();
        final Priority it = another.getPriority();
        return me == it ? getSequence() - another.getSequence() : it.ordinal() - me.ordinal();
    }

    @Override
    public String url() {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (!getRequestMethod().allowRequestBody() && mParamKeyValues.size() > 0) {
            StringBuffer paramBuffer = buildCommonParams(getParamKeyValues(), getParamsEncoding());
            if (url.contains("?") && url.contains("=") && paramBuffer.length() > 0)
                urlBuilder.append("&");
            else if (paramBuffer.length() > 0 && !url.endsWith("?")) // end with '?', not append '?'.
                urlBuilder.append("?");
            urlBuilder.append(paramBuffer);
        }
        return urlBuilder.toString();
    }

    @Override
    public RequestMethod getRequestMethod() {
        return mRequestMethod;
    }

    @Override
    public void setMultipartFormEnable(boolean enable) {
        if (enable && !getRequestMethod().allowRequestBody())
            throw new IllegalArgumentException("MultipartFormEnable is request method is the premise of the POST/PUT/PATCH/DELETE, but the Android system under API level 19 does not support the DELETE.");
        isMultipartFormEnable = enable;
    }

    @Override
    public boolean isMultipartFormEnable() {
        if (isMultipartFormEnable) {
            return true;
        } else {
            Set<String> keys = mParamKeyValues.keySet();
            for (String key : keys) {
                List<Object> values = mParamKeyValues.getValues(key);
                for (Object value : values) {
                    if (value instanceof Binary)
                        return true;
                }
            }
        }
        return false;
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
        mHostnameVerifier = hostnameVerifier;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
    }

    @Override
    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
    }

    @Override
    public int getReadTimeout() {
        return mReadTimeout;
    }

    @Override
    public void addHeader(String key, String value) {
        mHeaders.add(key, value);
    }

    @Override
    public void setHeader(String key, String value) {
        mHeaders.set(key, value);
    }

    @Override
    public void addHeader(HttpCookie cookie) {
        if (cookie != null)
            mHeaders.add(Headers.HEAD_KEY_COOKIE, cookie.getName() + "=" + cookie.getValue());
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
        return mHeaders;
    }

    @Override
    public void setAccept(String accept) {
        mHeaders.set(Headers.HEAD_KEY_ACCEPT, accept);
    }

    @Override
    public void setAcceptLanguage(String acceptLanguage) {
        mHeaders.set(Headers.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
    }

    @Override
    public long getContentLength() {
        CounterOutputStream outputStream = new CounterOutputStream();
        try {
            onWriteRequestBody(outputStream);
        } catch (IOException e) {
            Logger.e(e);
        }
        return outputStream.get();
    }

    @Override
    public void setContentType(String contentType) {
        mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
    }

    @Override
    public String getContentType() {
        String contentType = mHeaders.getValue(Headers.HEAD_KEY_CONTENT_TYPE, 0);
        if (!TextUtils.isEmpty(contentType))
            return contentType;
        if (getRequestMethod().allowRequestBody() && isMultipartFormEnable())
            return Headers.HEAD_VALUE_ACCEPT_MULTIPART_FORM_DATA + "; boundary=" + boundary;
        else
            return Headers.HEAD_VALUE_ACCEPT_APPLICATION_X_WWW_FORM_URLENCODED + "; charset=" + getParamsEncoding();
    }

    @Override
    public void setUserAgent(String userAgent) {
        mHeaders.set(Headers.HEAD_KEY_USER_AGENT, userAgent);
    }

    @Override
    public void setRetryCount(int count) {
        this.mRetryCount = count;
    }

    @Override
    public int getRetryCount() {
        return mRetryCount;
    }

    @Override
    public void setParamsEncoding(String encoding) {
        this.mParamEncoding = encoding;
    }

    @Override
    public String getParamsEncoding() {
        if (TextUtils.isEmpty(mParamEncoding))
            mParamEncoding = NoHttp.CHARSET_UTF8;
        return mParamEncoding;
    }

    @Override
    public void add(String key, int value) {
        add(key, Integer.toString(value));
    }

    @Override
    public void add(String key, long value) {
        add(key, Long.toString(value));
    }

    @Override
    public void add(String key, boolean value) {
        add(key, String.valueOf(value));
    }

    @Override
    public void add(String key, char value) {
        add(key, String.valueOf(value));
    }

    @Override
    public void add(String key, double value) {
        add(key, Double.toString(value));
    }

    @Override
    public void add(String key, float value) {
        add(key, Float.toString(value));
    }

    @Override
    public void add(String key, short value) {
        add(key, Integer.toString(value));
    }

    @Override
    public void add(String key, byte value) {
        add(key, Integer.toString(value));
    }

    @Override
    public void add(String key, String value) {
        if (value != null) {
            mParamKeyValues.set(key, value);
        }
    }

    @Override
    public void set(String key, String value) {
        if (value != null)
            mParamKeyValues.set(key, value);
    }

    @Override
    public void add(String key, Binary binary) {
        mParamKeyValues.add(key, binary);
    }

    @Override
    public void set(String key, Binary binary) {
        mParamKeyValues.set(key, binary);
    }

    @Override
    public void add(String key, File file) {
        add(key, new FileBinary(file));
    }

    @Override
    public void set(String key, File file) {
        set(key, new FileBinary(file));
    }

    @Override
    public void add(String key, List<Binary> binaries) {
        if (binaries != null) {
            for (Binary binary : binaries)
                mParamKeyValues.add(key, binary);
        }
    }

    @Override
    public void set(String key, List<Binary> binaries) {
        mParamKeyValues.remove(key);
        add(key, binaries);
    }

    @Override
    public void add(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> stringEntry : params.entrySet())
                add(stringEntry.getKey(), stringEntry.getValue());
        }
    }

    @Override
    public void set(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> stringEntry : params.entrySet())
                set(stringEntry.getKey(), stringEntry.getValue());
        }
    }

    @Override
    public List<Object> remove(String key) {
        return mParamKeyValues.remove(key);
    }

    @Override
    public void removeAll() {
        mParamKeyValues.clear();
    }

    @Override
    public MultiValueMap<String, Object> getParamKeyValues() {
        return mParamKeyValues;
    }

    @Override
    public void setDefineRequestBody(InputStream requestBody, String contentType) {
        if (requestBody == null || contentType == null)
            throw new IllegalArgumentException("The requestBody and contentType must be can't be null");
        if (requestBody instanceof ByteArrayInputStream || requestBody instanceof FileInputStream) {
            this.mRequestBody = requestBody;
            mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType);
        } else {
            throw new IllegalArgumentException("Can only accept ByteArrayInputStream and FileInputStream type of stream");
        }
    }

    @Override
    public void setDefineRequestBody(String requestBody, String contentType) {
        if (!TextUtils.isEmpty(requestBody)) {
            try {
                mRequestBody = IOUtils.toInputStream(requestBody, getParamsEncoding());
                if (!TextUtils.isEmpty(contentType))
                    mHeaders.set(Headers.HEAD_KEY_CONTENT_TYPE, contentType + "; charset=" + getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                setDefineRequestBody(IOUtils.toInputStream(requestBody), contentType);
            }
        }
    }

    @Override
    public void setDefineRequestBodyForJson(String jsonBody) {
        if (!TextUtils.isEmpty(jsonBody))
            setDefineRequestBody(jsonBody, Headers.HEAD_VALUE_ACCEPT_APPLICATION_JSON);
    }

    @Override
    public void setDefineRequestBodyForJson(JSONObject jsonBody) {
        if (jsonBody != null)
            setDefineRequestBody(jsonBody.toString(), Headers.HEAD_VALUE_ACCEPT_APPLICATION_JSON);
    }

    @Override
    public void setDefineRequestBodyForXML(String xmlBody) {
        if (!TextUtils.isEmpty(xmlBody))
            setDefineRequestBody(xmlBody, Headers.HEAD_VALUE_ACCEPT_APPLICATION_XML);
    }

    /**
     * Is there a custom request inclusions.
     *
     * @return Returns true representatives have, return false on behalf of the no.
     */
    protected boolean hasDefineRequestBody() {
        return mRequestBody != null;
    }

    /**
     * To get custom inclusions.
     *
     * @return {@link InputStream}.
     */
    protected InputStream getDefineRequestBody() {
        return mRequestBody;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onWriteRequestBody(OutputStream writer) throws IOException {
        if (mRequestBody != null) {
            writeRequestBody(writer);
        } else if (isMultipartFormEnable()) {
            writeFormStreamData(writer);
        } else {
            writeCommonStreamData(writer);
        }
    }

    /**
     * Send form data.
     *
     * @param writer {@link OutputStream}.
     * @throws IOException write error.
     */
    protected void writeFormStreamData(OutputStream writer) throws IOException {
        Set<String> keys = mParamKeyValues.keySet();
        for (String key : keys) {
            List<Object> values = mParamKeyValues.getValues(key);
            for (Object value : values) {
                if (!isCanceled()) {
                    if (value != null && value instanceof String) {
                        if (!(writer instanceof CounterOutputStream))
                            Logger.i(key + "=" + value);
                        writeFormString(writer, key, value.toString());
                    } else if (value != null && value instanceof Binary) {
                        if (!(writer instanceof CounterOutputStream))
                            Logger.i(key + " is Binary");
                        writeFormBinary(writer, key, (Binary) value);
                    }
                    writer.write("\r\n".getBytes());
                }
            }
        }
        writer.write((endBoundary).getBytes());
    }

    /**
     * Send text data in a form.
     *
     * @param writer {@link OutputStream}
     * @param key    equivalent to form the name of the input label, {@code "Content-Disposition: form-data; name=key"}.
     * @param value  equivalent to form the value of the input label.
     * @throws IOException Write the data may be abnormal.
     */
    private void writeFormString(OutputStream writer, String key, String value) throws IOException {
        StringBuilder stringFieldBuilder = new StringBuilder(startBoundary).append("\r\n");

        stringFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n");
        stringFieldBuilder.append("Content-Type: text/plain; charset=").append(getParamsEncoding()).append("\r\n\r\n");

        writer.write(stringFieldBuilder.toString().getBytes(getParamsEncoding()));

        writer.write(value.getBytes(getParamsEncoding()));
    }

    /**
     * Send binary data in a form.
     */
    private void writeFormBinary(OutputStream writer, String key, Binary value) throws IOException {
        if (!value.isCanceled()) {
            StringBuilder binaryFieldBuilder = new StringBuilder(startBoundary).append("\r\n");

            binaryFieldBuilder.append("Content-Disposition: form-data; name=\"").append(key).append("\"");
            binaryFieldBuilder.append("; filename=\"").append(value.getFileName()).append("\"\r\n");

            binaryFieldBuilder.append("Content-Type: ").append(value.getMimeType()).append("\r\n");
            binaryFieldBuilder.append("Content-Transfer-Encoding: binary\r\n\r\n");

            writer.write(binaryFieldBuilder.toString().getBytes());

            if (writer instanceof CounterOutputStream) {
                ((CounterOutputStream) writer).write(value.getLength());
            } else {
                value.onWriteBinary(writer);
            }
        }
    }

    /**
     * Send non form data.
     *
     * @param writer {@link OutputStream}.
     * @throws IOException write error.
     */
    protected void writeCommonStreamData(OutputStream writer) throws IOException {
        String requestBody = buildCommonParams(getParamKeyValues(), getParamsEncoding()).toString();
        if (!(writer instanceof CounterOutputStream))
            Logger.i("Push RequestBody: " + requestBody);
        writer.write(requestBody.getBytes());
    }

    /**
     * Send request requestBody.
     *
     * @param writer {@link OutputStream}.
     * @throws IOException write error.
     */
    protected void writeRequestBody(OutputStream writer) throws IOException {
        if (mRequestBody != null) {
            if (writer instanceof CounterOutputStream) {
                writer.write(mRequestBody.available());
            } else {
                IOUtils.write(mRequestBody, writer);
                IOUtils.closeQuietly(mRequestBody);
                mRequestBody = null;
            }
        }
    }

    @Override
    public void setRedirectHandler(RedirectHandler redirectHandler) {
        mRedirectHandler = redirectHandler;
    }

    @Override
    public RedirectHandler getRedirectHandler() {
        return mRedirectHandler;
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
    public void setQueue(BlockingQueue<?> queue) {
        blockingQueue = queue;
    }

    @Override
    public boolean inQueue() {
        return blockingQueue != null && blockingQueue.contains(this);
    }

    @Override
    public void start() {
        this.isStart = true;
    }

    @Override
    public boolean isStarted() {
        return isStart;
    }

    @Override
    public void finish() {
        this.isFinished = true;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void cancel() {
        if (!isCanceled) {
            isCanceled = true;
            if (mRequestBody != null)
                IOUtils.closeQuietly(mRequestBody);

            if (blockingQueue != null)
                blockingQueue.remove(this);

            // cancel file upload
            Set<String> keys = mParamKeyValues.keySet();
            for (String key : keys) {
                List<Object> values = mParamKeyValues.getValues(key);
                for (Object value : values)
                    if (value != null && value instanceof Binary)
                        ((Binary) value).cancel();
            }
        }
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCancelSign(Object sign) {
        this.mCancelSign = sign;
    }

    @Override
    public void cancelBySign(Object sign) {
        if (mCancelSign == sign)
            cancel();
    }

    ////////// static module /////////

    /**
     * Split joint non form data.
     *
     * @param paramMap      param map.
     * @param encodeCharset charset.
     * @return string parameter combination, each key value on nails with {@code "&"} space.
     */
    public static StringBuffer buildCommonParams(MultiValueMap<String, Object> paramMap, String encodeCharset) {
        StringBuffer paramBuffer = new StringBuffer();
        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            List<Object> values = paramMap.getValues(key);
            for (Object value : values) {
                if (value != null && value instanceof CharSequence) {
                    paramBuffer.append("&");
                    try {
                        paramBuffer.append(URLEncoder.encode(key, encodeCharset));
                        paramBuffer.append("=");
                        paramBuffer.append(URLEncoder.encode(value.toString(), encodeCharset));
                    } catch (UnsupportedEncodingException e) {
                        Logger.e("Encoding " + encodeCharset + " format is not supported by the system");
                        paramBuffer.append(key);
                        paramBuffer.append("=");
                        paramBuffer.append(value.toString());
                    }
                }
            }
        }
        if (paramBuffer.length() > 0)
            paramBuffer.deleteCharAt(0);
        return paramBuffer;
    }

    /**
     * Create acceptLanguage.
     *
     * @return Returns the client can accept the language types. Such as:zh-CN,zh.
     * @deprecated use {@link HeaderUtil#systemAcceptLanguage()} instead.
     */
    @Deprecated
    public static String defaultAcceptLanguage() {
        return HeaderUtil.systemAcceptLanguage();
    }

    /**
     * Randomly generated boundary mark.
     *
     * @return Random code.
     */
    public static String createBoundary() {
        StringBuffer sb = new StringBuffer("----NoHttpFormBoundary");
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