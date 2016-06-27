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

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.nohttp.sample.R;
import com.yanzhenjie.nohttp.sample.dialog.ImageDialog;
import com.yanzhenjie.nohttp.sample.dialog.WebDialog;
import com.yanzhenjie.nohttp.sample.nohttp.CallServer;
import com.yanzhenjie.nohttp.sample.nohttp.HttpListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;
import com.yolanda.nohttp.tools.HeaderParser;

/**
 * Created in 2016/5/8 18:19.
 *
 * @author Yan Zhenjie.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewGroup viewGroup;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_base);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewGroup = (ViewGroup) findViewById(R.id.content);
        setBackBar(true);

        onActivityCreate(savedInstanceState);
    }

    protected abstract void onActivityCreate(Bundle savedInstanceState);

    public <T extends View> T findView(int viewId) {
        return (T) viewGroup.findViewById(viewId);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setTitle(CharSequence title) {
        toolbar.setSubtitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        toolbar.setTitle(titleId);
    }

    public void setSubtitle(CharSequence title) {
        toolbar.setSubtitle(title);
    }

    public void setSubtitle(int titleId) {
        toolbar.setSubtitle(titleId);
    }

    public void setSubtitleTextColor(int color) {
        toolbar.setSubtitleTextColor(color);
    }

    public void setTitleTextColor(int color) {
        toolbar.setTitleTextColor(color);
    }

    public void setBackBar(boolean isShow) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShow);
    }

    public void setContentBackground(int color) {
        viewGroup.setBackgroundResource(color);
    }

    @Override
    public void setContentView(int layoutResID) {
        viewGroup.removeAllViews();
        getLayoutInflater().inflate(layoutResID, viewGroup, true);
    }

    @Override
    public void setContentView(View view) {
        viewGroup.removeAllViews();
        viewGroup.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        viewGroup.addView(view, params);
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return onOptionsItemSelectedCompat(item);
    }

    protected boolean onOptionsItemSelectedCompat(MenuItem item) {
        return false;
    }

    /**
     * Show message dialog.
     *
     * @param title   title.
     * @param message message.
     */
    public void showMessageDialog(int title, int message) {
        showMessageDialog(getText(title), getText(message));
    }

    /**
     * Show message dialog.
     *
     * @param title   title.
     * @param message message.
     */
    public void showMessageDialog(int title, CharSequence message) {
        showMessageDialog(getText(title), message);
    }

    /**
     * Show message dialog.
     *
     * @param title   title.
     * @param message message.
     */
    public void showMessageDialog(CharSequence title, int message) {
        showMessageDialog(title, getText(message));
    }

    /**
     * Show message dialog.
     *
     * @param title   title.
     * @param message message.
     */
    public void showMessageDialog(CharSequence title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.know, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 显示图片dialog。
     *
     * @param title  标题。
     * @param bitmap 图片。
     */
    public void showImageDialog(CharSequence title, Bitmap bitmap) {
        ImageDialog imageDialog = new ImageDialog(this);
        imageDialog.setTitle(title);
        imageDialog.setImage(bitmap);
        imageDialog.show();
    }

    /**
     * 显示一个WebDialog。
     *
     * @param response 响应。
     */
    public void showWebDialog(Response<?> response) {
        String result = StringRequest.parseResponseString(response.url(), response.getHeaders(), response.getByteArray());
        WebDialog webDialog = new WebDialog(this);
        String contentType = response.getHeaders().getContentType();
        webDialog.loadUrl(result, contentType, HeaderParser.parseHeadValue(contentType, "charset", "utf-8"));
        webDialog.show();
    }

    public <T> void request(int what, Request<T> request, HttpListener<T> callback, boolean canCancel, boolean isLoading) {
        request.setCancelSign(this);
        CallServer.getRequestInstance().add(this, what, request, callback, canCancel, isLoading);
    }

    @Override
    protected void onDestroy() {
        CallServer.getRequestInstance().cancelBySign(this);
        super.onDestroy();
    }
}
