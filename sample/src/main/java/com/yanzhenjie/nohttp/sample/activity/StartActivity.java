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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.activity.cache.CacheActivity;
import com.yanzhenjie.nohttp.sample.activity.cancel.CancelActivity;
import com.yanzhenjie.nohttp.sample.activity.download.DownloadActivity;
import com.yanzhenjie.nohttp.sample.activity.json.FastJsonActivity;
import com.yanzhenjie.nohttp.sample.activity.json.JsonActivity;
import com.yanzhenjie.nohttp.sample.activity.upload.UploadFileActivity;
import com.yanzhenjie.nohttp.sample.adapter.MainBannerAdapter;
import com.yanzhenjie.nohttp.sample.adapter.RecyclerListMultiAdapter;
import com.yanzhenjie.nohttp.sample.entity.ListItem;
import com.yanzhenjie.nohttp.sample.nohttp.CallServer;
import com.yanzhenjie.nohttp.sample.util.OnItemClickListener;
import com.yanzhenjie.nohttp.sample.view.AutoPlayViewPager;
import com.yolanda.nohttp.tools.ResCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>开始界面.</p>
 * Created in Oct 21, 2015 2:19:16 PM.
 *
 * @author Yan Zhenjie.
 */
public class StartActivity extends AppCompatActivity {

    private AutoPlayViewPager autoPlayViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_start);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        CollapsingToolbarLayout collapsingtoolbarlayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout);
        collapsingtoolbarlayout.setCollapsedTitleTextColor(ResCompat.getColor(R.color.white));
        collapsingtoolbarlayout.setExpandedTitleColor(ResCompat.getColor(R.color.white));

        autoPlayViewPager = (AutoPlayViewPager) findViewById(R.id.vp_main_banner);
        autoPlayViewPager.setAdapter(new MainBannerAdapter());

        autoPlayViewPager.setCurrentItem(MainBannerAdapter.IMAGES.length * 5);
        autoPlayViewPager.start();

        List<ListItem> listItems = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.activity_start_items);
        String[] titlesDes = getResources().getStringArray(R.array.activity_start_items_des);
        for (int i = 0; i < titles.length; i++) {
            listItems.add(new ListItem(titles[i], titlesDes[i]));
        }

        RecyclerListMultiAdapter listAdapter = new RecyclerListMultiAdapter(listItems, mItemClickListener);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_start_activity);
        recyclerView.setAdapter(listAdapter);
    }

    /**
     * list item单击.
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            goItemPager(position);
        }
    };

    private void goItemPager(int position) {
        Intent intent = null;
        switch (position) {
            case 0:// 最原始使用方法
                intent = new Intent(this, OriginalActivity.class);
                break;
            case 1:// 各种请求方法演示(GET, POST, HEAD, PUT等等)
                intent = new Intent(this, MethodActivity.class);
                break;
            case 2:// 请求图片
                intent = new Intent(this, ImageActivity.class);
                break;
            case 3:// JsonObject, JsonArray
                intent = new Intent(this, JsonActivity.class);
                break;
            case 4:// POST一段JSON、XML，自定义包体等
                intent = new Intent(this, PostBodyActivity.class);
                break;
            case 5:// 自定义请求FastJson
                intent = new Intent(this, FastJsonActivity.class);
                break;
            case 6:// NoHttp缓存演示
                intent = new Intent(this, CacheActivity.class);
                break;
            case 7:// 响应码302/303重定向演示
                intent = new Intent(this, RedirectActivity.class);
                break;
            case 8:// 文件上传
                intent = new Intent(this, UploadFileActivity.class);
                break;
            case 9: // 文件下载
                intent = new Intent(this, DownloadActivity.class);
                break;
            case 10:// 如何取消请求
                intent = new Intent(this, CancelActivity.class);
                break;
            case 11:// 同步请求
                intent = new Intent(this, SyncActivity.class);
                break;
            case 12:// 通过代理服务器请求
                intent = new Intent(this, ProXYActivity.class);
                break;
            case 13:// https请求
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
        autoPlayViewPager.stop();

        // 程序退出时取消所有请求
        CallServer.getRequestInstance().cancelAll();

        // 程序退出时停止请求队列，如果这里的NoHttpRequestQueue是单例模式，NoHttp所在的进程没杀死而停止了队列，会导致再打开app不能请求网络
        CallServer.getRequestInstance().stopAll();

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
