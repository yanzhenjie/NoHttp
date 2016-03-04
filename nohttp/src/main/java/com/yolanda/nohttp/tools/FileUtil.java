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
package com.yolanda.nohttp.tools;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

/**
 * Created in Jul 31, 2015 1:44:06 PM.
 *
 * @author YOLANDA;
 */
public class FileUtil {

    /**
     * Access to a directory available size.
     *
     * @param path path.
     * @return Long size.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
    public static long getDirSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
    }

    /**
     * If the folder can be written.
     *
     * @param path path.
     * @return True: success, or false: failure.
     */
    public static boolean canWrite(String path) {
        return new File(path).canWrite();
    }

    /**
     * If the folder can be read.
     *
     * @param path path.
     * @return True: success, or false: failure.
     */
    public static boolean canRead(String path) {
        return new File(path).canRead();
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param folderPath folder path.
     * @return True: success, or false: failure.
     */
    public static boolean createFolder(String folderPath) {
        if (!TextUtils.isEmpty(folderPath)) {
            File folder = new File(folderPath);
            return createFolder(folder);
        }
        return false;
    }

    /**
     * Create a folder, If the folder exists is not created.
     *
     * @param targetFolder folder path.
     * @return True: success, or false: failure.
     */
    public static boolean createFolder(File targetFolder) {
        if (targetFolder.exists())
            return true;
        return targetFolder.mkdirs();
    }

    /**
     * Create a file, If the file exists is not created.
     *
     * @param filePath file path.
     * @return True: success, or false: failure.
     */
    public static boolean createFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createFile(file);
        }
        return false;
    }

    /**
     * Create a file, If the file exists is not created.
     *
     * @param targetFile file.
     * @return True: success, or false: failure.
     */
    public static boolean createFile(File targetFile) {
        if (targetFile.exists())
            return true;
        try {
            return targetFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Create a new file, if the file exists, delete and create again.
     *
     * @param filePath file path.
     * @return True: success, or false: failure.
     */
    public static boolean createNewFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return createNewFile(file);
        }
        return false;
    }

    /**
     * Create a new file, if the file exists, delete and create again.
     *
     * @param targetFile file.
     * @return True: success, or false: failure.
     */
    public static boolean createNewFile(File targetFile) {
        if (targetFile.exists())
            targetFile.delete();
        try {
            return targetFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Return the MIME type for the given url.
     *
     * @param url the url, path…
     * @return The MIME type for the given extension or null iff there is none.
     */
    public static String getMimeTypeByUrl(String url) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
    }
}
