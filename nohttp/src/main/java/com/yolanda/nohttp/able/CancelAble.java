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
 * <p>Cancel interface.</p>
 * Created in Dec 17, 2015 11:42:10 AM.
 *
 * @author YOLANDA;
 */
public interface CancelAble {

    /**
     * Change cancel state.
     *
     * @param cancel true or yes.
     */
    void cancel(boolean cancel);

    /**
     * Has it been canceled.
     *
     * @return true: canceled, false: no cancellation.
     */
    boolean isCanceled();

    /**
     * Change the current cancel status as contrary to the current status.
     */
    void toggleCancel();

}
