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
package com.yanzhenjie.nohttp.sample.app.form;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.album.widget.divider.Api21ItemDivider;
import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.app.form.entity.FileItem;
import com.yanzhenjie.nohttp.sample.util.DisplayUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public class FormView
  extends Contract.FormView {

    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    ImageAdapter mAdapter;

    public FormView(Activity activity, Contract.FormPresenter presenter) {
        super(activity, presenter);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        int size = DisplayUtils.dip2px(10);
        mRecyclerView.addItemDecoration(new Api21ItemDivider(Color.TRANSPARENT, size, size));
        mAdapter = new ImageAdapter(getContext());
        mAdapter.setAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().addFile();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setFileList(List<FileItem> fileList) {
        mAdapter.setFileList(fileList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItem(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void setStatusText(String text) {
        mTvStatus.setText(text);
    }

    @OnClick({R.id.btn_upload})
    void onViewCLick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_upload: {
                getPresenter().uploadFile();
                break;
            }
        }
    }
}