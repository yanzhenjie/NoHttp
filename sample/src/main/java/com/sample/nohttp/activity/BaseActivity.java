/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.nohttp.activity;

import com.sample.nohttp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created in Jan 28, 2016 5:47:15 PM
 *
 * @author YOLANDA;
 */
public abstract class BaseActivity extends Activity {

    private RelativeLayout mTitleLayoutRoot;
    private ImageView mIvBackBar;
    private TextView mTvTitle;
    private LinearLayout mMenuLayoutRoot;
    private FrameLayout mContentLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setContentView(R.layout.activity_base);
        mTitleLayoutRoot = (RelativeLayout) window.findViewById(R.id.layout_activity_base_title_root);
        mIvBackBar = (ImageView) mTitleLayoutRoot.findViewById(R.id.iv_activity_base_backbar);
        mIvBackBar.setOnClickListener(mBackClickListener);
        mTvTitle = (TextView) mTitleLayoutRoot.findViewById(R.id.tv_activity_base_title);
        mMenuLayoutRoot = (LinearLayout) mTitleLayoutRoot.findViewById(R.id.layout_activity_base_menu_root);
        mContentLayoutRoot = (FrameLayout) window.findViewById(R.id.layout_activity_base_content_root);
        onActivityCreate(savedInstanceState);
    }

    protected abstract void onActivityCreate(Bundle savedInstanceState);

    protected void hideBackBar() {
        mIvBackBar.setVisibility(View.GONE);
    }

    protected void showBackBar() {
        mIvBackBar.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private View.OnClickListener mMenuButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRightButtonClick(v.getId());
        }
    };

    protected void onRightButtonClick(int what) {
    }

    protected View addTextRightButton(CharSequence text, int what) {
        View view = createTextButton(what, text);
        mMenuLayoutRoot.addView(view);
        return view;
    }

    protected View addTextRightButton(int textId, int what) {
        View view = createTextButton(what, getText(textId));
        mMenuLayoutRoot.addView(view);
        return view;
    }

    protected View addImageRightButton(int imageId, int what) {
        View view = createImageButton(what, imageId);
        mMenuLayoutRoot.addView(view);
        return view;
    }

    @SuppressLint("InflateParams")
    protected View createTextButton(int id, CharSequence text) {
        View view = getLayoutInflater().inflate(R.layout.menu_button_text, null, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_activity_base_menu_text);
        textView.setLayoutParams(new LayoutParams(-2, -1));
        textView.setId(id);
        textView.setText(text);
        textView.setOnClickListener(mMenuButtonClickListener);
        return view;
    }

    @SuppressLint("InflateParams")
    protected View createImageButton(int id, int imageId) {
        View view = getLayoutInflater().inflate(R.layout.menu_button_image, null, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_activity_base_menu_image);
        imageView.setLayoutParams(new LayoutParams(-2, -1));
        imageView.setId(id);
        imageView.setImageResource(imageId);
        imageView.setOnClickListener(mMenuButtonClickListener);
        return view;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTvTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTvTitle.setText(titleId);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(int id) {
        return (T) mContentLayoutRoot.findViewById(id);
    }

    @Override
    public View findViewById(int id) {
        return mContentLayoutRoot.findViewById(id);
    }

    private void clearContentView() {
        mContentLayoutRoot.removeAllViews();
    }

    @Override
    public void setContentView(int layoutResID) {
        clearContentView();
        getLayoutInflater().inflate(layoutResID, mContentLayoutRoot, true);
    }

    @Override
    public void setContentView(View view) {
        clearContentView();
        mContentLayoutRoot.addView(view);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        clearContentView();
        mContentLayoutRoot.addView(view, params);
    }

}
