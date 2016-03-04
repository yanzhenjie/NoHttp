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
package com.yolanda.nohttp.able;

/**
 * <p>Start interface.</p>
 * Created in Nov 12, 2015 5:03:54 PM.
 *
 * @author YOLANDA;
 */
public interface StartAble {

    /**
     * Change start state.
     *
     * @param start true: start, false: un start.
     */
    void start(boolean start);

    /**
     * Has it been started ?
     *
     * @return true: has already started, false: haven't started.
     */
    boolean isStarted();

    /**
     * Change the current start status as contrary to the current status.
     */
    void toggleStart();

}
