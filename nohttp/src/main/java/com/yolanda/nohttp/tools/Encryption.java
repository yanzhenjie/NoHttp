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
package com.yolanda.nohttp.tools;

import com.yolanda.nohttp.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created in 2016/4/10 11:27.
 *
 * @author Yan Zhenjie.
 */
public class Encryption {

    /**
     * Get the MD5 value of string.
     *
     * @param content the target string.
     * @return the MD5 value.
     */
    public static String getMa5ForString(String content) {
        StringBuffer md5Buffer = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] tempBytes = digest.digest(content.getBytes());
            int digital;
            for (int i = 0; i < tempBytes.length; i++) {
                digital = tempBytes[i];
                if (digital < 0) {
                    digital += 256;
                }
                if (digital < 16) {
                    md5Buffer.append("0");
                }
                md5Buffer.append(Integer.toHexString(digital));
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.e(e);
        }
        return md5Buffer.toString();
    }

}
