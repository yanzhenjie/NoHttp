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
package com.perfoct.nohttp.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * </br>
 * Created in Jan 29, 2016 1:55:37 PM
 * 
 * @author YOLANDA;
 */
public abstract class BasePrintServlet extends BaseServlet {

	private static final long serialVersionUID = 14891L;

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh) throws IOException {
		PrintWriter printWriter = response.getWriter();
		doHandler(request, response, isZh, printWriter);
		printWriter.flush();
		printWriter.close();
	}

	protected abstract void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, PrintWriter printWriter) throws IOException;

}
