/*
 * Copyright © 2018 Yan Zhenjie.
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
package com.yanzhenjie.nohttp.sample.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Yan Zhenjie on 2016/7/7.
 */
public class FileUtils {

    public static File getAppRootPath(Context context) {
        if (sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sd.canWrite();
        } else return false;
    }

    /**
     * Copy to clipboard.
     */
    public static boolean copyTextToClipboard(Context context, CharSequence content) {
        ClipboardManager clipboardManager =
          (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText(content, content);
            clipboardManager.setPrimaryClip(clipData);
            return true;
        }
        return false;
    }

    public static String getMimeType(String url) {
        String extension = getExtension(url);
        if (!MimeTypeMap.getSingleton().hasExtension(extension)) return "";

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return TextUtils.isEmpty(mimeType) ? "" : mimeType;
    }

    public static String getExtension(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return TextUtils.isEmpty(extension) ? "" : extension;
    }
}
