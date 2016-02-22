package com.zhimore.mama.server;

import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/jsonObject")
public class JsonObjectServelt extends BaseJsonServlet {

	private static final long serialVersionUID = 148941348563L;

	public JsonObjectServelt() {
		super();
	}

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
		if (isZh) {
			out.put("nohttp", "Android最好用的Http框架");
			out.put("yolanda", "世界上最帅的男人");
		} else {
			out.put("nohttp", "Android had better use Http framework");
			out.put("yolanda", "The most handsome man in the world");
		}
	}

}
