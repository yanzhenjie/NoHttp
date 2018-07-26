/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.nohttp.sample.app.normal;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.app.BaseActivity;
import com.yanzhenjie.nohttp.sample.config.UrlConfig;
import com.yanzhenjie.nohttp.sample.entity.News;
import com.yanzhenjie.nohttp.sample.entity.NewsWrapper;
import com.yanzhenjie.nohttp.sample.entity.Page;
import com.yanzhenjie.nohttp.sample.http.EntityRequest;
import com.yanzhenjie.nohttp.sample.http.HttpCallback;
import com.yanzhenjie.nohttp.sample.http.Result;

import java.util.List;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public class NormalPresenter
  extends BaseActivity
  implements Contract.NormalPresenter {

    private Contract.NormalView mView;

    private List<News> mDataList;
    private Page mPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        mView = new NormalView(this, this);

        mView.setRefresh(true);
        refresh();
    }

    @Override
    public void refresh() {
        EntityRequest<NewsWrapper> request =
          new EntityRequest<>(UrlConfig.GET_LIST, RequestMethod.GET, NewsWrapper.class);
        request.add("pageNum", 1).add("pageSize", 50);
        request(request, false, new HttpCallback<NewsWrapper>() {
            @Override
            public void onResponse(Result<NewsWrapper> response) {
                if (response.isSucceed()) {
                    NewsWrapper wrapper = response.get();
                    mDataList = wrapper.getDataList();
                    mPage = wrapper.getPage();

                    mView.setDataList(mDataList, mPage);
                } else {
                    mView.toast(response.error());
                }

                // Finish refresh.
                mView.setRefresh(false);
            }
        });
    }

    @Override
    public void loadMore() {
        EntityRequest<NewsWrapper> request =
          new EntityRequest<>(UrlConfig.GET_LIST, RequestMethod.GET, NewsWrapper.class);
        request.add("pageNum", mPage.getPageNum() + 1).add("pageSize", 50);
        request(request, false, new HttpCallback<NewsWrapper>() {
            @Override
            public void onResponse(Result<NewsWrapper> response) {
                if (response.isSucceed()) {
                    NewsWrapper wrapper = response.get();
                    List<News> dataList = wrapper.getDataList();
                    if (dataList != null && !dataList.isEmpty()) {
                        mDataList.addAll(dataList);
                        mPage = wrapper.getPage();
                    }
                } else {
                    mView.toast(response.error());
                }

                // Finish load more.
                mView.addDataList(mPage);
            }
        });
    }

    @Override
    public void clickItem(int position) {
        News news = mDataList.get(position);
        mView.toast(news.getTitle());
    }
}