/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yolanda.nohttp;

import android.graphics.Bitmap;

import com.yolanda.nohttp.tools.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * A default implementation of Binary.
 * All the methods are called in Son thread.
 * </p>
 * Created in Oct 17, 2015 12:40:54 PM.
 *
 * @author Yan Zhenjie.
 */
public class BitmapBinary extends BasicBinary {

    protected Bitmap bitmap;

    public BitmapBinary(Bitmap bitmap) {
        this(bitmap, null);
    }

    public BitmapBinary(Bitmap bitmap, String fileName) {
        this(bitmap, fileName, null);
    }

    public BitmapBinary(Bitmap bitmap, String fileName, String mimeType) {
        super(fileName, mimeType);
        this.bitmap = bitmap;
    }

    @Override
    public void cancel() {
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
        super.cancel();
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        if (bitmap != null && !bitmap.isRecycled()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            bitmap.recycle();
            IOUtils.closeQuietly(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
        return null;
    }

    @Override
    public long getBinaryLength() {
        if (bitmap != null && !bitmap.isRecycled())
            return bitmap.getRowBytes() * bitmap.getHeight();
        return 0;
    }
}