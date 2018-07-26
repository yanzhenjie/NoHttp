/*
 * Copyright © Yan Zhenjie
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
package com.yanzhenjie.nohttp.sample.mvp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by YanZhenjie on 2017/12/8.
 */
abstract class Source<Source> {

    private Source mSource;

    public Source(Source source) {
        mSource = source;
    }

    public Source getSource() {
        return mSource;
    }

    abstract void bind(Object target);

    abstract void setActionBar(Toolbar actionBar);

    abstract MenuInflater getMenuInflater();

    abstract Menu getMenu();

    abstract void setMenuClickListener(MenuClickListener selectedListener);

    abstract void setDisplayHomeAsUpEnabled(boolean showHome);

    abstract void setHomeAsUpIndicator(@DrawableRes int icon);

    abstract void setHomeAsUpIndicator(Drawable icon);

    abstract void setTitle(CharSequence title);

    abstract void setTitle(@StringRes int title);

    abstract void setSubTitle(CharSequence title);

    abstract void setSubTitle(@StringRes int title);

    abstract Context getContext();

    abstract View getHostView();

    abstract void closeInputMethod();

    abstract void unbind();

    interface MenuClickListener {

        void onHomeClick();

        void onMenuClick(MenuItem item);
    }

}