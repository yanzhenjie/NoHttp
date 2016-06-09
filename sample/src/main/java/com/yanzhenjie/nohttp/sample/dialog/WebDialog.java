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
package com.yanzhenjie.nohttp.sample.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created on 2016/5/28.
 *
 * @author Yan Zhenjie;
 */
public class WebDialog extends AlertDialog.Builder {

    private AlertDialog alertDialog;

    private WebView webView;

    public WebDialog(Context context) {
        super(context);
        webView = new WebView(context);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    view.loadUrl(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        setView(webView);
    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void loadUrl(String data, String mimeType, String encoding) {
        webView.loadData(data, mimeType, encoding);
    }

    public void dismiss() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }

    public void cancel() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.cancel();
    }

    @Override
    public AlertDialog show() {
        if (alertDialog == null) {
            alertDialog = create();
            alertDialog.setTitle(null);
        }
        if (!alertDialog.isShowing())
            alertDialog.show();
        return alertDialog;
    }
}
