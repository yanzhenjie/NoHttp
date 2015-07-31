/**
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
package com.yolanda.nohttp.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StatFs;

/**
 * Created in Jul 31, 2015 1:44:06 PM
 * 
 * @author YOLANDA
 */
public class FileUtil {

	/**
	 * Access to a directory available size
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static long getDirSize(String path) {
		StatFs stat = new StatFs(path);
		long blockSize, availableBlocks;
		if (Build.VERSION.SDK_INT >= 18) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}
		return availableBlocks * blockSize;
	}
}
