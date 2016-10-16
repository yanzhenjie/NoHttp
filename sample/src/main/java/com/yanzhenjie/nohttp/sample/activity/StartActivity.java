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
package com.yanzhenjie.nohttp.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.cache.CacheActivity;
import com.yanzhenjie.nohttp.sample.activity.cancel.CancelActivity;
import com.yanzhenjie.nohttp.sample.activity.download.DownloadActivity;
import com.yanzhenjie.nohttp.sample.activity.define.DefineRequestActivity;
import com.yanzhenjie.nohttp.sample.activity.json.JsonActivity;
import com.yanzhenjie.nohttp.sample.activity.rxjava.RxJavaActivity;
import com.yanzhenjie.nohttp.sample.activity.upload.UploadFileActivity;
import com.yanzhenjie.nohttp.sample.adapter.RecyclerListMultiAdapter;
import com.yanzhenjie.nohttp.sample.entity.ListItem;
import com.yanzhenjie.nohttp.sample.nohttp.CallServer;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>开始界面.</p>
 * Created in Oct 21, 2015 2:19:16 PM.
 *
 * @author Yan Zhenjie.
 */
public class StartActivity extends AppCompatActivity {

    AppBarLayout mAppBarLayout;
    Toolbar mToolbar;
    ViewGroup mTitleContainerLine;
    TextView mTvTitleToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_start);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_start);
        mTitleContainerLine = (ViewGroup) findViewById(R.id.layout_start_title_root);
        mTvTitleToolbar = (TextView) findViewById(R.id.tv_start_toolbar_title);

        mAppBarLayout.addOnOffsetChangedListener(offsetChangedListener);


        initialize();
    }

    /**
     * AppBarLayout的offset监听。
     */
    private AppBarLayout.OnOffsetChangedListener offsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            ViewCompat.setAlpha(mTitleContainerLine, 1 - percentage);
            ViewCompat.setAlpha(mTvTitleToolbar, percentage);
            mToolbar.getBackground().mutate().setAlpha((int) (255 * percentage));

//        if (percentage >= 1 || percentage <= 0) {
//            int distance = getResources().getDimensionPixelSize(android.support.design.);
//            mContentRoot.animate().translationY(distance * percentage).start();
//        }
        }
    };


    /**
     * 初始化页面功能。
     */
    private void initialize() {
        List<ListItem> listItems = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.activity_start_items);
        String[] titlesDes = getResources().getStringArray(R.array.activity_start_items_des);
        for (int i = 0; i < titles.length; i++) {
            listItems.add(new ListItem(titles[i], titlesDes[i]));
        }

        RecyclerListMultiAdapter listAdapter = new RecyclerListMultiAdapter(listItems, (v, position) -> goItemPager(position));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_start_activity);
        recyclerView.setAdapter(listAdapter);
    }

    private void goItemPager(int position) {
        Intent intent = null;
        switch (position) {
            case 0:// 最原始使用方法
                intent = new Intent(this, OriginalActivity.class);
                break;
            case 1:// 与RxJava一起使用。
                intent = new Intent(this, RxJavaActivity.class);
                break;
            case 2:// 各种请求方法演示(GET, POST, HEAD, PUT等等)
                intent = new Intent(this, MethodActivity.class);
                break;
            case 3:// 请求图片
                intent = new Intent(this, ImageActivity.class);
                break;
            case 4:// JsonObject, JsonArray
                intent = new Intent(this, JsonActivity.class);
                break;
            case 5:// POST一段JSON、XML，自定义包体等
                intent = new Intent(this, PostBodyActivity.class);
                break;
            case 6:// 自定义请求FastJson
                intent = new Intent(this, DefineRequestActivity.class);
                break;
            case 7:// NoHttp缓存演示
                intent = new Intent(this, CacheActivity.class);
                break;
            case 8:// 响应码302/303重定向演示
                intent = new Intent(this, RedirectActivity.class);
                break;
            case 9:// 文件上传
                intent = new Intent(this, UploadFileActivity.class);
                break;
            case 10: // 文件下载
                intent = new Intent(this, DownloadActivity.class);
                break;
            case 11:// 如何取消请求
                intent = new Intent(this, CancelActivity.class);
                break;
            case 12:// 同步请求
                intent = new Intent(this, SyncActivity.class);
                break;
            case 13:// 通过代理服务器请求
                intent = new Intent(this, ProXYActivity.class);
                break;
            case 14:// https请求
                intent = new Intent(this, HttpsActivity.class);
                break;
            default:
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // 程序退出时取消所有请求
        CallServer.getRequestInstance().cancelAll();

        // 程序退出时停止请求队列，如果这里的NoHttpRequestQueue是单例模式，NoHttp所在的进程没杀死而停止了队列，会导致再打开app不能请求网络
        CallServer.getRequestInstance().stopAll();

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
