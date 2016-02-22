package com.zhimore.mama.server;

import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/upload")
public class UploadServlet extends BaseJsonServlet {
	private static final long serialVersionUID = 1L;

	public UploadServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
//		// 获得磁盘文件条目工厂
//		DiskFileItemFactory factory = new DiskFileItemFactory();
//
//		// 获取文件需要上传到的路径
//		String path = getServletContext().getRealPath("/upload");
//
//		File file = new File(path);
//		if (!file.exists() || file.isFile()) {
//			file.delete();
//			file.mkdirs();
//		}
//		factory.setRepository(new File(path));
//		factory.setSizeThreshold(1024 * 1024);
//
//		// 高水平的API文件上传处理
//		ServletFileUpload upload = new ServletFileUpload(factory);
//
//		// 可以上传多个文件
//		Map<String, List<FileItem>> fileFieldMap = upload.parseParameterMap(request);
//
//		Set<String> fileFieldNames = fileFieldMap.keySet();
//
//		for (String fileField : fileFieldNames) {
//			List<FileItem> fileItems = fileFieldMap.get(fileField);
//			for (FileItem item : fileItems) {
//				String fieldoutKey = item.getFieldName();
//
//				if (item.isFormField()) {
//					String fieldValue = item.getString();
//					request.setAttribute(fieldoutKey, fieldValue);
//				} else {
//					String value = item.getName();
//					int start = value.lastIndexOf("\\");
//					String filename = value.substring(start + 1);
//					request.setAttribute(fieldoutKey, filename);
//					
//					InputStream inputStream = item.getInputStream();
//					OutputStream outputStream = new FileOutputStream(new File(filename));
//					int len = -1;
//					byte[] buffer = new byte[1024];
//					while ((len = inputStream.read(buffer)) != -1) {
//						outputStream.write(buffer, 0, len);
//					}
//					outputStream.flush();
//					outputStream.close();
//					inputStream.close();
//				}
//			}
//		}
		if (isZh) {
			out.put("des", "上传成功");
		} else {
			out.put("des", "updaload succeed");
		}
	}

}
