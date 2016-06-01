package com.perfoct.nohttp.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/image")
public class ImageServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public ImageServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh) throws IOException {
		response.setContentType("image/jpg");
		String method = request.getMethod().toLowerCase();
		if (!"head".equalsIgnoreCase(method)) {
			String imagePath = getServletContext().getRealPath("image") + File.separator + method + ".jpg";
			File file = new File(imagePath);
			BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		}
	}

}
