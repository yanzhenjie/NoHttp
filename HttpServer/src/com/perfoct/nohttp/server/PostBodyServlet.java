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
package com.perfoct.nohttp.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.perfoct.nohttp.server.util.HeaderParser;

/**
 * @author YOLANDA
 */
@WebServlet("/postBody")
public class PostBodyServlet extends BaseJsonServlet {

    private static final long serialVersionUID = 164646L;

    @Override
    protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            arrayOutputStream.write(buffer, 0, len);
        }
        arrayOutputStream.close();
        inputStream.close();
        buffer = arrayOutputStream.toByteArray();

        String charset = HeaderParser.parseHeadValue(request.getContentType(), "charset", "");
        String string = "";
        if (charset != null && charset.trim().length() != 0) {
            try {
                string = new String(buffer, charset);
            } catch (Exception e) {
            }
        } else {
            string = new String(buffer);
        }
        out.put("userData", string);
    }

}
