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
package com.sample.nohttp.activity;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.adapter.StringAbsListAdapter;
import com.sample.nohttp.nohttp.CallServer;
import com.sample.nohttp.nohttp.HttpListener;
import com.sample.nohttp.util.Constants;
import com.sample.nohttp.util.OnItemClickListener;
import com.sample.nohttp.util.Toast;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Http相应头304缓存演示
 * </br>
 * Created in Jan 31, 2016 12:11:03 PM
 *
 * @author YOLANDA
 */
public class CacheActivity extends BaseActivity {

	/*
     * 先来普及一下响应码304缓存是什么意思:
	 * 在RESTFUL的api中，http响应码很重要，一般响应码都是200; 当响应码是304时，表示客户端缓存有效, 客户端可以使用缓存;
	 * 
	 * NoHttp实现了Http协议1.1, 很好的支持RESTFUL接口; 根据协议当请求方式是GET时, 且服务器响应头包涵Last-Modified时,
	 * 响应内容可以被客户端缓存起来, 下次请求时只需要验证缓存, 验证缓存时如果服务器响应码为304时, 表示客户端缓存有效, 可以
	 * 继续使用缓存数据.
	 * 
	 * 由于NoHttp只是缓存了byte[], 所以不论图片, 还是String都可以很好的被缓存.
	 */

    private TextView mTvResult;

    private ImageView mIvResult;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[5]);
        setContentView(R.layout.activity_cache);
        String[] data = getResources().getStringArray(R.array.activity_cache_item);
        StringAbsListAdapter listAdapter = new StringAbsListAdapter(this, R.layout.item_abs_list_text, data, clickListener);
        ((AbsListView) findView(R.id.lv)).setAdapter(listAdapter);

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
     * 请求String
     */
    private void requestString() {
        Request<String> request = NoHttp.createStringRequest(Constants.URL_NOHTTP_CACHE_STRING);
        CallServer.getRequestInstance().add(this, 0, request, stringHttpListener, false, true);
    }

    private HttpListener<String> stringHttpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            mTvResult.setText("响应码: " + response.getHeaders().getResponseCode() + "\n" + response.get());
            mIvResult.setImageBitmap(null);
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mTvResult.setText("");
            mIvResult.setImageBitmap(null);
            Toast.show("请求失败");
        }
    };

    /**
     * 请求Image
     */
    private void requestImage() {
        Request<Bitmap> request = NoHttp.createImageRequest(Constants.URL_NOHTTP_CACHE_IMAGE);
        CallServer.getRequestInstance().add(this, 0, request, imageHttpListener, false, true);
    }

    private HttpListener<Bitmap> imageHttpListener = new HttpListener<Bitmap>() {
        @Override
        public void onSucceed(int what, Response<Bitmap> response) {
            mTvResult.setText("响应码: " + response.getHeaders().getResponseCode());
            mIvResult.setImageBitmap(response.get());
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mTvResult.setText("");
            mIvResult.setImageBitmap(null);
            Toast.show("请求失败");
        }
    };

}
