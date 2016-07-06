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

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yanzhenjie.nohttp.sample.R;

/**
 * Created on 2016/7/6.
 *
 * @author Yan Zhenjie.
 */
public class MainBannerAdapter extends PagerAdapter {

    public static final int[] IMAGES = {R.mipmap.nohttp, R.mipmap.nohttp_delete, R.mipmap.nohttp_des, R.mipmap.nohttp_get, R.mipmap.nohttp_head, R.mipmap.nohttp_options, R.mipmap.nohttp_patch, R.mipmap.nohttp_post, R.mipmap.nohttp_put, R.mipmap.nohttp_trace};

    public MainBannerAdapter() {
    }

    @Override
    public int getCount() {
        return 200;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(IMAGES[position % IMAGES.length]);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
