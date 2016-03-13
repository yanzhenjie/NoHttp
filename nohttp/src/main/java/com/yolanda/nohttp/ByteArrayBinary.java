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

import java.io.ByteArrayInputStream;

/**
 * <p>
 * A default implementation of Binary.
 * All the methods are called in Son thread.
 * </p>
 * Created in Oct 17, 2015 12:40:54 PM.
 *
 * @author Yan Zhenjie.
 */
public class ByteArrayBinary extends InputStreamBinary {

    /**
     * A byte array of {@link Binary}.
     *
     * @param byteArray byte array.
     * @param fileName  file name.  Had better pass this value, unless the server tube don't care about the file name.
     */
    public ByteArrayBinary(byte[] byteArray, String fileName) {
        super(new ByteArrayInputStream(byteArray), fileName);
    }

    /**
     * A byte array of {@link Binary}.
     *
     * @param byteArray byte array.
     * @param fileName  file name.  Had better pass this value, unless the server tube don't care about the file name.
     * @param mimeType  content type.
     */
    public ByteArrayBinary(byte[] byteArray, String fileName, String mimeType) {
        super(new ByteArrayInputStream(byteArray), fileName, mimeType);
    }
}
