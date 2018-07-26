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
package com.yanzhenjie.nohttp.sample.app.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.entity.News;
import com.yanzhenjie.nohttp.sample.util.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YanZhenjie on 2018/3/27.
 */
public class MainAdapter
  extends BaseAdapter<MainAdapter.ViewHolder> {

    private List<News> mDataList;

    public MainAdapter(Context context) {
        super(context);
    }

    public void setDataList(List<News> dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_main_function, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    static class ViewHolder
      extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_content)
        TextView mTvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(News data) {
            mTvTitle.setText(data.getTitle());
            mTvContent.setText(data.getContent());
        }
    }
}