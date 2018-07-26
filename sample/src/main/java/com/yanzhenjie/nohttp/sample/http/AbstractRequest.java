/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.http;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.sample.App;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.tools.HeaderUtils;
import com.yanzhenjie.nohttp.tools.IOUtils;

/**
 * <p>基本请求，添加Appkey、Nonce、Timestamp、Signature。</p> Created by Yan Zhenjie on 2016/11/16.
 */
public abstract class AbstractRequest<Entity>
  extends Request<Result<Entity>> {

    public AbstractRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
        setAccept(Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
    }

    @Override
    public void onPreExecute() {
        // TODO 此方法在子线程中被调用，所以很合适用来做加密。并且是在真正发起请求前被调用。
    }

    @Override
    public final Result<Entity> parseResponse(Headers responseHeaders, byte[] responseBody) throws Exception {
        String body = "";
        if (responseBody != null && responseBody.length > 0) {
            body = parseResponseString(responseHeaders, responseBody);
        }
        Logger.i("服务器数据：" + body);

        int resCode = responseHeaders.getResponseCode();
        if (resCode >= 200 && resCode < 300) { // Http层成功，这里只可能业务逻辑错误。
            HttpEntity httpEntity;
            try {
                httpEntity = JSON.parseObject(body, HttpEntity.class);
            } catch (Exception e) {
                httpEntity = new HttpEntity();
                httpEntity.setSucceed(false);
                httpEntity.setMessage(App.get().getString(R.string.http_server_data_format_error));
            }

            if (httpEntity.isSucceed()) { // 业务成功，解析data。
                try {
                    Entity result = parseEntity(httpEntity.getData());
                    return new Result<>(true, responseHeaders, result, resCode, null);
                } catch (Throwable throwable) {
                    // 解析data失败，data和预期不一致，服务器返回格式错误。
                    return new Result<>(false, responseHeaders, null, resCode,
                                        App.get().getString(R.string.http_server_data_format_error));
                }
            } else {
                // The server failed to read the wrong information.
                return new Result<>(false, responseHeaders, null, resCode, httpEntity.getMessage());
            }
        } else if (resCode >= 400 && resCode < 500) {
            return new Result<>(false, responseHeaders, null, resCode,
                                App.get().getString(R.string.http_unknow_error));
        }
        return new Result<>(false, responseHeaders, null, resCode,
                            App.get().getString(R.string.http_server_error));
    }

    /**
     * 把数据解析为泛型对象。
     *
     * @param responseBody 响应包体。
     *
     * @return 泛型对象。
     */
    protected abstract Entity parseEntity(String responseBody) throws Throwable;

    private static String parseResponseString(Headers responseHeaders, byte[] responseBody) {
        if (responseBody == null || responseBody.length == 0) return "";
        String charset = HeaderUtils.parseHeadValue(responseHeaders.getContentType(), "charset", "");
        return IOUtils.toString(responseBody, charset);
    }
}