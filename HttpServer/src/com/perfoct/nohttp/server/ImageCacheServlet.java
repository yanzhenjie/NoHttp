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
