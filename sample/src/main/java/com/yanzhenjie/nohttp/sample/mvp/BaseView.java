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

import android.app.Activity;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.yanzhenjie.nohttp.sample.R;

/**
 * <p>View of MVP.</p> Created by YanZhenjie on 2017/7/17.
 */
public abstract class BaseView<Presenter extends BasePresenter> {

    private Source mSource;
    private Presenter mPresenter;

    public BaseView(Activity activity, Presenter presenter) {
        this(new ActivitySource(activity), presenter);
    }

    public BaseView(View view, Presenter presenter) {
        this(new ViewSource(view), presenter);
    }

    private BaseView(Source source, Presenter presenter) {
        this.mSource = source;
        this.mPresenter = presenter;
        this.mSource.bind(this);

        invalidateOptionsMenu();
        mSource.setMenuClickListener(new Source.MenuClickListener() {
            @Override
            public void onHomeClick() {
                getPresenter().bye();
            }

            @Override
            public void onMenuClick(MenuItem item) {
                optionsItemSelected(item);
            }
        });

        getPresenter().getLifecycle().addObserver(new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    resume();
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    pause();
                } else if (event == Lifecycle.Event.ON_STOP) {
                    stop();
                } else if (event == Lifecycle.Event.ON_DESTROY) {
                    destroy();
                }
            }
        });
    }

    public final Presenter getPresenter() {
        return mPresenter;
    }

    private void resume() {
        onResume();
    }

    protected void onResume() {
    }

    private void pause() {
        onPause();
    }

    protected void onPause() {
    }

    private void stop() {
        onStop();
    }

    protected void onStop() {
    }

    private void destroy() {
        closeInputMethod();
        onDestroy();
        this.mSource.unbind();
    }

    protected void onDestroy() {
    }

    /**
     * Set actionBar.
     */
    protected final void setActionBar(Toolbar actionBar) {
        mSource.setActionBar(actionBar);
        invalidateOptionsMenu();
    }

    /**
     * ReCreate menu.
     */
    protected final void invalidateOptionsMenu() {
        Menu menu = mSource.getMenu();
        if (menu != null) {
            onCreateOptionsMenu(menu);
        }
    }

    /**
     * Get menu inflater.
     */
    protected final MenuInflater getMenuInflater() {
        return mSource.getMenuInflater();
    }

    /**
     * Create menu.
     */
    protected void onCreateOptionsMenu(Menu menu) {
    }

    private void optionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!onInterceptToolbarBack()) {
                getPresenter().bye();
            }
        } else {
            onOptionsItemSelected(item);
        }
    }

    /**
     * When the menu is clicked.
     */
    protected void onOptionsItemSelected(MenuItem item) {
    }

    /**
     * Intercept the return button.
     */
    protected boolean onInterceptToolbarBack() {
        return false;
    }

    protected final void openInputMethod(View view) {
        view.requestFocus();
        InputMethodManager manager =
          (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    protected final void closeInputMethod() {
        mSource.closeInputMethod();
    }

    protected final void setDisplayHomeAsUpEnabled(boolean showHome) {
        mSource.setDisplayHomeAsUpEnabled(showHome);
    }

    protected final void setHomeAsUpIndicator(@DrawableRes int icon) {
        mSource.setHomeAsUpIndicator(icon);
    }

    public final void setTitle(String title) {
        mSource.setTitle(title);
    }

    public final void setTitle(@StringRes int title) {
        mSource.setTitle(title);
    }

    public final void setSubTitle(String title) {
        mSource.setSubTitle(title);
    }

    public final void setSubTitle(@StringRes int title) {
        mSource.setSubTitle(title);
    }

    protected Context getContext() {
        return mSource.getContext();
    }

    protected Resources getResources() {
        return getContext().getResources();
    }

    public final CharSequence getText(@StringRes int id) {
        return getContext().getText(id);
    }

    public final String getString(@StringRes int id) {
        return getContext().getString(id);
    }

    public final String getString(@StringRes int id, Object... formatArgs) {
        return getContext().getString(id, formatArgs);
    }

    public final Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(mSource.getContext(), id);
    }

    @ColorInt
    public final int getColor(@ColorRes int id) {
        return ContextCompat.getColor(mSource.getContext(), id);
    }

    public final String[] getStringArray(@ArrayRes int id) {
        return getResources().getStringArray(id);
    }

    public final int[] getIntArray(@ArrayRes int id) {
        return getResources().getIntArray(id);
    }

    public void showMessageDialog(@StringRes int title, @StringRes int message) {
        showMessageDialog(getText(title), getText(message));
    }

    public void showMessageDialog(@StringRes int title, CharSequence message) {
        showMessageDialog(getText(title), message);
    }

    public void showMessageDialog(CharSequence title, @StringRes int message) {
        showMessageDialog(title, getText(message));
    }

    public void showMessageDialog(CharSequence title, CharSequence message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle(title)
          .setMessage(message)
          .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
          })
          .create();
        alertDialog.show();
    }

    public void showConfirmDialog(@StringRes int title, @StringRes int message,
                                  OnDialogClickListener confirmClickListener) {
        showConfirmDialog(getText(title), getText(message), confirmClickListener);
    }

    public void showConfirmDialog(@StringRes int title, CharSequence message,
                                  OnDialogClickListener confirmClickListener) {
        showConfirmDialog(getText(title), message, confirmClickListener);
    }

    public void showConfirmDialog(CharSequence title, @StringRes int message,
                                  OnDialogClickListener confirmClickListener) {
        showConfirmDialog(title, getText(message), confirmClickListener);
    }

    public void showConfirmDialog(CharSequence title, CharSequence message,
                                  final OnDialogClickListener confirmClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle(title)
          .setMessage(message)
          .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
          })
          .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  confirmClickListener.onClick(which);
              }
          })
          .create();
        alertDialog.show();
    }

    public void showMessageDialog(@StringRes int title, @StringRes int message,
                                  OnDialogClickListener cancelClickListener,
                                  OnDialogClickListener confirmClickListener) {
        showMessageDialog(getText(title), getText(message), cancelClickListener, confirmClickListener);
    }

    public void showMessageDialog(@StringRes int title, CharSequence message,
                                  OnDialogClickListener cancelClickListener,
                                  OnDialogClickListener confirmClickListener) {
        showMessageDialog(getText(title), message, cancelClickListener, confirmClickListener);
    }

    public void showMessageDialog(CharSequence title, @StringRes int message,
                                  OnDialogClickListener cancelClickListener,
                                  OnDialogClickListener confirmClickListener) {
        showMessageDialog(title, getText(message), cancelClickListener, confirmClickListener);
    }

    public void showMessageDialog(CharSequence title, CharSequence message,
                                  final OnDialogClickListener cancelClickListener,
                                  final OnDialogClickListener confirmClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle(title)
          .setMessage(message)
          .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  cancelClickListener.onClick(which);
              }
          })
          .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  confirmClickListener.onClick(which);
              }
          })
          .create();
        alertDialog.show();
    }

    public interface OnDialogClickListener {

        void onClick(int which);
    }

    public void toast(CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void toast(@StringRes int message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}