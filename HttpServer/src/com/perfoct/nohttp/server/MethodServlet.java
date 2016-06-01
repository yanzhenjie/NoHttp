package com.perfoct.nohttp.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/method")
public class MethodServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	public MethodServlet() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh) throws IOException, ServletException {
		request.getRequestDispatcher("/jsonObject").forward(request, response);
	}

}
