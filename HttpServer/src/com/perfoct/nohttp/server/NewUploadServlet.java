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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSON;

@WebServlet("/newupload")
public class NewUploadServlet extends BasePrintServlet {
	private static final long serialVersionUID = 1L;

	public NewUploadServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, PrintWriter printWriter) throws IOException {
		String uri = request.getRequestURI();
		String url = request.getRequestURL().toString();
		String method = request.getMethod();
		String contenType = request.getContentType();

		Map<String, Object> returnedMap = new HashMap<>();
		returnedMap.put("client_uri", uri);
		returnedMap.put("client_url", url);
		returnedMap.put("client_method", method);
		returnedMap.put("client_conten_type", contenType);

		if (contenType == null || contenType.isEmpty()) {
			System.out.println("普通请求。");
			Enumeration<String> enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				returnedMap.put(key, request.getParameter(key));
			}
		} else if (contenType.contains("multipart")) {
			System.out.println("传文件。");
			DiskFileItemFactory factory = new DiskFileItemFactory();
			String path = getServletContext().getRealPath("/newupload");
			File file = new File(path);
			if (!file.exists() || file.isFile()) {
				file.delete();
				file.mkdirs();
			}
			factory.setRepository(new File(path));
			factory.setSizeThreshold(1024 * 1024);
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				Map<String, List<FileItem>> fileFieldMap = upload.parseParameterMap(request);
				Set<String> fileFieldNames = fileFieldMap.keySet();
				for (String fileName : fileFieldNames) {
					List<FileItem> fileItems = fileFieldMap.get(fileName);
					for (FileItem fileItem : fileItems) {
						String name = fileItem.getFieldName();
						if (fileItem.isFormField()) {
							String value = fileItem.getString();
							request.setAttribute(name, value);
						} else {
							String value = fileItem.getName();
							int start = value.lastIndexOf("\\");
							String filename = value.substring(start + 1);
							request.setAttribute(name, filename);

							// 写到磁盘
							OutputStream outputStream = new FileOutputStream(new File(path, filename));
							InputStream in = fileItem.getInputStream();
							int length = 0;
							byte[] buf = new byte[1024];
							while ((length = in.read(buf)) != -1) {
								outputStream.write(buf, 0, length);
							}
							in.close();
							outputStream.flush();
							outputStream.close();
						}
					}
				}

			} catch (Exception e) {
				returnedMap.put("server_error", e.getMessage());
				e.printStackTrace();
			}
			
			Enumeration<String> enumeration = request.getAttributeNames();
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				returnedMap.put(key, request.getAttribute(key));
			}
		} else {
			System.out.println("不是传文件的请求。");
		}
		printWriter.write(JSON.toJSONString(returnedMap));
	}

}
