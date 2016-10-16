/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.nohttp.sample.activity.rxjava;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.BaseActivity;
import com.yanzhenjie.nohttp.sample.adapter.RecyclerListSingleAdapter;
import com.yanzhenjie.nohttp.sample.entity.YanZhenjie;
import com.yanzhenjie.nohttp.sample.nohttp.FastJsonRequest;
import com.yanzhenjie.nohttp.sample.nohttp.JavaBeanRequest;
import com.yanzhenjie.nohttp.sample.rxjava.RxNoHttp;
import com.yanzhenjie.nohttp.sample.rxjava.SimpleSubscriber;
import com.yanzhenjie.nohttp.sample.util.Constants;
import com.yanzhenjie.nohttp.sample.util.OnItemClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/10/16.
 */
public class RxJavaActivity extends BaseActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_rxjava);

        List<String> cacheDataTypes = Arrays.asList(getResources().getStringArray(R.array.activity_rxjava_item));
        RecyclerListSingleAdapter listAdapter = new RecyclerListSingleAdapter(cacheDataTypes, mItemClickListener);
        RecyclerView recyclerView = findView(R.id.rv_rx_demo_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    private OnItemClickListener mItemClickListener = (v, position) -> {
        switch (position) {
            case 0: {
                requestString();
                break;
            }
            case 1: {
                requestImage();
                break;
            }
            case 2: {
                requestFastJson();
                break;
            }
            case 3: {
                requestJavaBean();
                break;
            }
            default:
                break;
        }
    };

    /**
     * 请求String。
     */
    private void requestString() {
        Request<String> stringRequest = NoHttp.createStringRequest(Constants.URL_NOHTTP_TEST);
        RxNoHttp.request(this, stringRequest, new SimpleSubscriber<Response<String>>() {
            @Override
            public void onNext(Response<String> stringResponse) {
                showMessageDialog(getText(R.string.request_succeed), stringResponse.get());
            }
        });
    }

    /**
     * 请求Image。
     */
    private void requestImage() {
        Request<Bitmap> imageRequest = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE);
        RxNoHttp.request(this, imageRequest, new SimpleSubscriber<Response<Bitmap>>() {
            @Override
            public void onNext(Response<Bitmap> bitmapResponse) {
                showImageDialog(getText(R.string.request_succeed), bitmapResponse.get());
            }
        });
    }

    /**
     * 自定义请求FastJson。
     */
    private void requestFastJson() {
        Request<JSONObject> jsonRequest = new FastJsonRequest(Constants.URL_NOHTTP_JSONOBJECT);
        RxNoHttp.request(this, jsonRequest, new SimpleSubscriber<Response<JSONObject>>() {
            @Override
            public void onNext(Response<JSONObject> jsonResponse) {
                showMessageDialog(getText(R.string.request_succeed), jsonResponse.get().toJSONString());
            }
        });
    }

    /**
     * 自定义请求JavaBean。
     */
    private void requestJavaBean() {
        Request<YanZhenjie> entityRequest = new JavaBeanRequest<>(Constants.URL_NOHTTP_JSONOBJECT, YanZhenjie.class);
        RxNoHttp.request(this, entityRequest, new SimpleSubscriber<Response<YanZhenjie>>() {
            @Override
            public void onNext(Response<YanZhenjie> entityResponse) {
                YanZhenjie yanZhenjie = entityResponse.get();
                showMessageDialog(getText(R.string.request_succeed), yanZhenjie.toString());
            }
        });
    }

}
