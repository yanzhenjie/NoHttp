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

import android.util.Log;

import com.yolanda.nohttp.tools.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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
public class InputStreamBinary extends BasicBinary {

    protected InputStream inputStream;

    /**
     * An input stream {@link Binary}.
     *
     * @param inputStream must be {@link FileInputStream}, {@link ByteArrayInputStream}.
     * @param fileName    file name. Had better pass this value, unless the server tube don't care about the file name.
     */
    public InputStreamBinary(InputStream inputStream, String fileName) {
        this(inputStream, fileName, null);
    }

    /**
     * An input stream {@link Binary}.
     *
     * @param inputStream must be {@link FileInputStream}, {@link ByteArrayInputStream}.
     * @param fileName    file name. Had better pass this value, unless the server tube don't care about the file name.
     * @param mimeType    content type.
     */
    public InputStreamBinary(InputStream inputStream, String fileName, String mimeType) {
        super(fileName, mimeType);
        this.inputStream = inputStream;
        if (!(inputStream instanceof FileInputStream) && !(inputStream instanceof ByteArrayInputStream)) {
            Log.e("Binary", "Binary was cancelled, because the InputStream must be FileInputStream or ByteArrayInputStream.");
            super.cancel();
        }
    }

    @Override
    public void cancel() {
        IOUtils.closeQuietly(inputStream);
        super.cancel();
    }

    @Override
    public long getBinaryLength() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            Logger.e(e);
        }
        return 0;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return inputStream;
    }
}