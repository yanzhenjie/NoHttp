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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yanzhenjie.nohttp.sample.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by YanZhenjie on 2017/12/8.
 */
class ViewSource
  extends Source<View> {

    private Unbinder mUnbinder;

    private Toolbar mActionBar;
    private Drawable mActionBarIcon;
    private MenuClickListener mMenuItemSelectedListener;

    ViewSource(View view) {
        super(view);
    }

    @Override
    void bind(Object target) {
        mUnbinder = ButterKnife.bind(target, getSource());
        Toolbar toolbar = getSource().findViewById(R.id.toolbar);
        setActionBar(toolbar);
    }

    @Override
    void setActionBar(Toolbar actionBar) {
        this.mActionBar = actionBar;

        if (mActionBar != null) {
            mActionBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (mMenuItemSelectedListener != null) {
                        mMenuItemSelectedListener.onMenuClick(item);
                    }
                    return true;
                }
            });
            mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemSelectedListener != null) {
                        mMenuItemSelectedListener.onHomeClick();
                    }
                }
            });
            mActionBarIcon = mActionBar.getNavigationIcon();
        }
    }

    @Override
    MenuInflater getMenuInflater() {
        return new MenuInflater(getContext());
    }

    @Override
    Menu getMenu() {
        return mActionBar == null ? null : mActionBar.getMenu();
    }

    @Override
    void setMenuClickListener(MenuClickListener selectedListener) {
        this.mMenuItemSelectedListener = selectedListener;
    }

    @Override
    void setDisplayHomeAsUpEnabled(boolean showHome) {
        if (mActionBar != null) {
            if (showHome) {
                mActionBar.setNavigationIcon(mActionBarIcon);
            } else {
                mActionBar.setNavigationIcon(null);
            }
        }
    }

    @Override
    void setHomeAsUpIndicator(@DrawableRes int icon) {
        setHomeAsUpIndicator(ContextCompat.getDrawable(getContext(), icon));
    }

    @Override
    void setHomeAsUpIndicator(Drawable icon) {
        this.mActionBarIcon = icon;
        if (mActionBar != null) mActionBar.setNavigationIcon(icon);
    }

    @Override
    final void setTitle(CharSequence title) {
        if (mActionBar != null) mActionBar.setTitle(title);
    }

    @Override
    final void setTitle(@StringRes int title) {
        if (mActionBar != null) mActionBar.setTitle(title);
    }

    @Override
    final void setSubTitle(CharSequence title) {
        if (mActionBar != null) mActionBar.setSubtitle(title);
    }

    @Override
    final void setSubTitle(@StringRes int title) {
        if (mActionBar != null) mActionBar.setSubtitle(title);
    }

    @Override
    Context getContext() {
        return getSource().getContext();
    }

    @Override
    View getHostView() {
        return getSource();
    }

    @Override
    void closeInputMethod() {
        View focusView = getHostView().findFocus();
        if (focusView != null) {
            InputMethodManager manager =
              (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    @Override
    void unbind() {
        mUnbinder.unbind();
    }
}