/*
 * Copyright © YOLANDA. All Rights Reserved
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
package com.sample.nohttp.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.adapter.StringAbsListAdapter;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.sample.nohttp.util.OnItemClickListener;
import com.sample.nohttp.util.Toast;
import com.sample.nohttp.view.ListView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.cache.CacheMode;

/**
 * Created by YOLANDA on 2016/3/13.
 *
 * @author YOLANDA;
 */
public class CacheOnlyReadCacheActivity extends BaseActivity {
    /**
     * 显示String或Json类型的请求结果。
     */
    private TextView mTvResult;
    /**
     * 显示图片。
     */
    private ImageView mIvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().cacheTitle[3]);
        setContentView(R.layout.activity_cache);
        String[] data = getResources().getStringArray(R.array.activity_cache_item);
        StringAbsListAdapter listAdapter = new StringAbsListAdapter(this, R.layout.item_abs_list_text, data, clickListener);
        ((ListView) findView(R.id.lv)).setAdapter(listAdapter);

        mTvResult = findView(R.id.tv_result);
        mIvResult = findView(R.id.iv_result);
    }

    private OnItemClickListener clickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            if (0 == position) {// 请求String
                requestString();
            } else if (1 == position) {// 请求图片
                requestImage();
            }
        }
    };

    /**
     * 请求String。
     */
    private void requestString() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_CACHE_STRING);
        request.setCacheMode(CacheMode.ONLY_READ_CACHE);//设置为ONLY_READ_CACHE表示只请求缓存，无论失败或者成功都不会请求服务器
        CallServer.getRequestInstance().add(this, 0, request, stringHttpListener, false, true);
    }

    private HttpListener<String> stringHttpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            mTvResult.setText("是否来自缓存：" + response.isFromCache() + "\n响应码: " + response.getHeaders().getResponseCode() + "\n" + response.get());
            mIvResult.setImageBitmap(null);
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mTvResult.setText("");
            mIvResult.setImageBitmap(null);
            Toast.show("请求失败。");
        }
    };

    /**
     * 请求Image。
     */
    private void requestImage() {
        Request<Bitmap> request = NoHttp.createImageRequest(Constants.URL_NOHTTP_CACHE_IMAGE);
        request.setCacheMode(CacheMode.ONLY_READ_CACHE);//设置为ONLY_READ_CACHE表示只请求缓存，无论失败或者成功都不会请求服务器
        CallServer.getRequestInstance().add(this, 0, request, imageHttpListener, false, true);
    }

    private HttpListener<Bitmap> imageHttpListener = new HttpListener<Bitmap>() {
        @Override
        public void onSucceed(int what, Response<Bitmap> response) {
            mTvResult.setText("是否来自缓存：" + response.isFromCache() + "\n响应码: " + response.getHeaders().getResponseCode());
            mIvResult.setImageBitmap(response.get());
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mTvResult.setText("");
            mIvResult.setImageBitmap(null);
            Toast.show("请求失败。");
        }
    };
}
