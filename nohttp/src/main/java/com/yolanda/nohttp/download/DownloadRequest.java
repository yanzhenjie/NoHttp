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
package com.yolanda.nohttp.download;

import com.yolanda.nohttp.Request;

/**
 * <p>Download task request interface.</p>
 * Created in Oct 21, 2015 11:09:04 AM.
 *
 * @author YOLANDA;
 */
public interface DownloadRequest extends Request<Void> {

    /**
     * Also didn't download to start download again.
     */
    int STATUS_RESTART = 0;
    /**
     * Part has been downloaded, continue to download last time.
     */
    int STATUS_RESUME = 1;
    /**
     * Has the download is complete, not to download operation.
     */
    int STATUS_FINISH = 2;

    /**
     * Return the mFileDir.
     *
     * @return String, not empty.
     */
    String getFileDir();

    /**
     * Return the mFileName.
     *
     * @return String, not empty.
     */
    String getFileName();

    /**
     * Return the isRange.
     *
     * @return true: breakpoint continuing, false: don't need a breakpoint continuing.
     */
    boolean isRange();

    /**
     * If there is a old files, whether to delete the old files.
     *
     * @return true: deleted, false: don't delete.
     */
    boolean isDeleteOld();

    /**
     * <p>Query before download status
     * STATUS_RESTART representative no download do to download again; Download STATUS_RESUME represents a part of, to
     * continue to download; STATUS_FINISH representatives have finished downloading.</p>
     *
     * @return Int value, compared with the {@value #STATUS_RESTART}, {@value #STATUS_RESUME}, {@value #STATUS_FINISH}.
     * @see #STATUS_RESTART
     * @see #STATUS_RESUME
     * @see #STATUS_FINISH
     */
    int checkBeforeStatus();
}
