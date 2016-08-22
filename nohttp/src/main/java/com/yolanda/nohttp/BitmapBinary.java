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
package com.yolanda.nohttp;

import android.graphics.Bitmap;
import android.util.Log;

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

    private InputStream inputStream;

    /**
     * An input stream {@link Binary}.
     *
     * @param bitmap   image.
     * @param fileName file name. Had better pass this value, unless the server tube don't care about the file name.
     */
    public BitmapBinary(Bitmap bitmap, String fileName) {
        this(bitmap, fileName, null);
    }

    /**
     * An input stream {@link Binary}.
     *
     * @param bitmap   image.
     * @param fileName file name. Had better pass this value, unless the server tube don't care about the file name.
     * @param mimeType such as: image/png.
     */
    public BitmapBinary(Bitmap bitmap, String fileName, String mimeType) {
        super(fileName, mimeType);

        if (bitmap != null && !bitmap.isRecycled()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            bitmap.recycle();
            IOUtils.closeQuietly(outputStream);
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        } else {
            Log.e("Binary", "Binary was cancelled, because the Bitmap is null or bitmap is recycled.");
            super.cancel();
        }
    }

    @Override
    public void cancel() {
        IOUtils.closeQuietly(inputStream);
        super.cancel();
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public long getBinaryLength() {
        try {
            return inputStream == null ? 0 : inputStream.available();
        } catch (IOException e) {
            return 0;
        }
    }
}