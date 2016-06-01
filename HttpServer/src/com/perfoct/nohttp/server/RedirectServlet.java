package com.perfoct.nohttp.server;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/redirect")
public class RedirectServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public RedirectServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh) throws IOException {
		response.sendRedirect("/index.html");
	}

}
