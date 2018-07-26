/*
 * AUTHOR：YanZhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © www.mamaqunaer.com. All Rights Reserved
 *
 */
package com.yanzhenjie.nohttp.sample.mvp;

import android.app.Activity;
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
class ActivitySource
  extends Source<Activity> {

    private Unbinder mUnbinder;
    private View mSourceView;

    private Toolbar mActionBar;
    private Drawable mActionBarIcon;
    private MenuClickListener mMenuItemSelectedListener;

    ActivitySource(Activity activity) {
        super(activity);
        mSourceView = activity.findViewById(android.R.id.content);
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

        Activity activity = getSource();
        if (mActionBar != null) {
            setTitle(activity.getTitle());
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
        if (mActionBar != null) {
            mActionBar.setNavigationIcon(icon);
        }
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
        return getSource();
    }

    @Override
    View getHostView() {
        return mSourceView;
    }

    @Override
    void closeInputMethod() {
        Activity activity = getSource();
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            InputMethodManager manager =
              (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
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