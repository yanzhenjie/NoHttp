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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

/**
 * Created on 2016/5/28.
 *
 * @author Yan Zhenjie;
 */
public class ImageDialog extends AlertDialog.Builder {

    private AlertDialog alertDialog;

    private ImageView imageView;

    public ImageDialog(Context context) {
        super(context);
        imageView = new ImageView(getContext());
        setView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setImage(int image) {
        imageView.setImageResource(image);
    }

    public void setImage(Drawable image) {
        imageView.setImageDrawable(image);
    }

    public void setImage(Bitmap image) {
        imageView.setImageBitmap(image);
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
        if (alertDialog == null)
            alertDialog = create();
        if (!alertDialog.isShowing())
            alertDialog.show();
        return alertDialog;
    }
}
