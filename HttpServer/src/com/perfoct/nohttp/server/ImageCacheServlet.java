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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/imageCache")
public class ImageCacheServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    public ImageCacheServlet() {
        super();
    }

    @Override
    protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh) throws IOException, ServletException {
        request.getRequestDispatcher("/image").forward(request, response);
    }

    @Override
    protected long getLastModified(HttpServletRequest request) {
        String method = request.getMethod();
        String imagePath = getServletContext().getRealPath("image") + File.separator + method.toLowerCase() + ".jpg";
        File file = new File(imagePath);
        long lastModified = file.lastModified(); // 返回文件的最后修改时间
        return lastModified;
    }

}
