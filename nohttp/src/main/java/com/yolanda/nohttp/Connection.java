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

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;

/**
 * Created in May 3, 2016 11:05:03 PM.
 *
 * @author Yan Zhenjie.
 */
public interface Connection extends Closeable {

    /**
     * Get the {@link URL} of connection.
     *
     * @return {@link URL}.
     */
    URL getURL();

    /**
     * Get response headers.
     *
     * @return the responseHeaders.
     */
    Headers responseHeaders();

    /**
     * Get stream from server.
     *
     * @return the inputStream.
     */
    InputStream serverStream();

    /**
     * Get exception for execution.
     *
     * @return the exception.
     */
    Exception exception();

}
