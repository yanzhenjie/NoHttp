/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yolanda.nohttp.rest;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yolanda.nohttp.RequestMethod;

/**
 * <p>Image request parameter.</p>
 * Created in Oct 17, 2015 12:17:57 PM.
 *
 * @author Yan Zhenjie.
 */
public class ImageRequest extends com.yolanda.nohttp.ImageRequest {

    public ImageRequest(String url, RequestMethod requestMethod, int maxWidth, int maxHeight, Bitmap.Config decodeConfig, ImageView.ScaleType scaleType) {
        super(url, requestMethod, maxWidth, maxHeight, decodeConfig, scaleType);
    }

}