package com.perfoct.nohttp.server;

import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class TestServlet extends BaseJsonServlet {
	private static final long serialVersionUID = 1L;

	public TestServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
		request.getRequestDispatcher("/jsonObject").forward(request, response);
	}

}
