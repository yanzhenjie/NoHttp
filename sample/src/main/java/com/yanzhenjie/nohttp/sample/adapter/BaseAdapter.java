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
package com.yanzhenjie.nohttp.sample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Yan Zhenjie on 2016/5/24.
 *
  * @author Yan Zhenjie.
 */
public abstract class BaseAdapter<T extends BaseAdapter.BaseViewHolder> extends RecyclerView.Adapter<T> {

    @Override
    public void onBindViewHolder(T holder, int position) {
        holder.setData();
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setData();
    }

}
