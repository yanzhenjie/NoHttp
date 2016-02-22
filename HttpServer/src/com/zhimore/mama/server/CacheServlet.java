package com.zhimore.mama.server;

import java.io.File;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/cache")
public class CacheServlet extends BaseJsonServlet {
	private static final long serialVersionUID = 1L;

	public CacheServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
		request.getRequestDispatcher("/jsonObject").forward(request, response);
	}

	@Override
	protected long getLastModified(HttpServletRequest request) {
		String imagePath = getServletContext().getRealPath("index.html");
		File file = new File(imagePath);
		long lastModified = file.lastModified();
		return lastModified;
	}

}
