/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.perfoct.nohttp.sample.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.perfoct.nohttp.sample.R;
import com.perfoct.nohttp.sample.adapter.RecyclerListSingleAdapter;
import com.perfoct.nohttp.sample.nohttp.CallServer;
import com.perfoct.nohttp.sample.nohttp.HttpListener;
import com.perfoct.nohttp.sample.util.Constants;
import com.perfoct.nohttp.sample.util.OnItemClickListener;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Arrays;
import java.util.List;

/**
 * <p>请求图片.</p>
 * Created in Oct 23, 2015 7:46:17 PM.
 *
 * @author Yan Zhenjie.;
 */
public class ImageActivity extends BaseActivity implements HttpListener<Bitmap> {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_image);

        List<String> imageItems = Arrays.asList(getResources().getStringArray(R.array.activity_image_item));
        RecyclerListSingleAdapter listAdapter = new RecyclerListSingleAdapter(imageItems, mItemClickListener);
        RecyclerView recyclerView = findView(R.id.rv_image_activity);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(listAdapter);
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            request(position);
        }
    };

    private void request(int position) {
        Request<Bitmap> request = null;
        switch (position) {
            case 0:
                request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE);
                break;
            case 1:
                request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.POST);
                break;
            case 2:
                request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.PUT);
                break;
            case 3:
                request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.DELETE);
                break;
            case 4:
                request = NoHttp.createImageRequest(Constants.URL_NOHTTP_IMAGE, RequestMethod.OPTIONS);
                break;
            default:
                break;
        }
        if (request != null)
            CallServer.getRequestInstance().add(this, 0, request, this, false, true);
    }

    @Override
    public void onSucceed(int what, Response<Bitmap> response) {
        int responseCode = response.getHeaders().getResponseCode();// 服务器响应码
        if (responseCode == 200) {// 如果确定你们的服务器是get或者post，上面的不用判断
            showImageDialog(null, response.get());
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        showMessageDialog(R.string.request_failed, exception.getMessage());
    }

}
