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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

/**
 * </br>
 * Created in Jan 29, 2016 1:59:33 PM
 *
 * @author YOLANDA;
 */
public abstract class BaseJsonServlet extends BasePrintServlet {

    private static final long serialVersionUID = 178913L;

    @Override
    protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, PrintWriter printWriter) throws IOException {
        Map<String, Object> returnedMap = new HashMap<>();
        returnedMap.put("method", request.getMethod());
        returnedMap.put("url", request.getRequestURL().toString());
        Map<String, Object> out = new HashMap<>();
        try {
            doHandler(request, response, isZh, out);
            returnedMap.put("error", 0);
        } catch (Exception e) {
            returnedMap.put("error", 1);
        }
        returnedMap.put("data", out);
        printWriter.write(JSON.toJSONString(returnedMap));
    }

    protected abstract void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception;

}
