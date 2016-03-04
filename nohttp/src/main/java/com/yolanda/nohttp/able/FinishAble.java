/**
 * Copyright © YOLANDA. All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.able;

/**
 * <p>Finish interface.</p>
 * Created in Jan 13, 2016 10:34:48 PM.
 *
 * @author YOLANDA;
 */
public interface FinishAble {

    /**
     * Change finish state.
     *
     * @param finish true or false.
     */
    void finish(boolean finish);

    /**
     * Has it been finished ?
     *
     * @return true: finished, false: unfinished.
     */
    boolean isFinished();

    /**
     * Change the current completion status as contrary to the current status.
     */
    void toggleFinish();
}
