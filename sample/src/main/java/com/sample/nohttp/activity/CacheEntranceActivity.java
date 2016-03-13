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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sample.nohttp.Application;
import com.sample.nohttp.R;
import com.sample.nohttp.adapter.StringAbsListAdapter;
import com.sample.nohttp.util.OnItemClickListener;
import com.sample.nohttp.view.ListView;

/**
 * Created by YOLANDA on 2016/3/13.
 *
 * @author YOLANDA;
 */
public class CacheEntranceActivity extends BaseActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setTitle(Application.getInstance().nohttpTitleList[5]);
        setContentView(R.layout.activity_start);

        StringAbsListAdapter listAdapter = new StringAbsListAdapter(this, R.layout.item_abs_list_text, Application.getInstance().cacheTitle, mItemClickListener);
        ((ListView) findView(R.id.lv)).setAdapter(listAdapter);
    }

    /**
     * list item单击。
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            geOtherPager(position);
        }
    };

    private void geOtherPager(int position) {
        Intent intent = null;
        switch (position) {
            case 0:// Http标准协议的缓存。
                intent = new Intent(this, CacheHttpActivity.class);
                break;
            case 1:// 请求服务器失败后返回上次的缓存。
                intent = new Intent(this, CacheRequestFailedReadCacheActivity.class);
                break;
            case 2:// 没有缓存才去请求服务器。
                intent = new Intent(this, CacheIfNoneRequestActivity.class);
                break;
            case 3:// 仅仅请求缓存。
                intent = new Intent(this, CacheOnlyReadCacheActivity.class);
                break;
            default:
                break;
        }
        if (intent != null)
            startActivity(intent);
    }
}
