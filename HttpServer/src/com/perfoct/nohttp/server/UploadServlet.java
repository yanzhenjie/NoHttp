package com.perfoct.nohttp.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

@WebServlet("/upload")
public class UploadServlet extends BaseJsonServlet {
	private static final long serialVersionUID = 1L;

	public UploadServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		String path = getServletContext().getRealPath("/upload");
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
			e.printStackTrace();
		}
		if (isZh) {
			out.put("des", "上传成功");
		} else {
			out.put("des", "updaload succeed");
		}
	}

}
