package com.perfoct.nohttp.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.perfoct.nohttp.server.util.HeaderParser;

/**
 * @author YOLANDA
 *
 */
@WebServlet("/postBody")
public class PostBodyServlet extends BaseJsonServlet {

	private static final long serialVersionUID = 164646L;

	@Override
	protected void doHandler(HttpServletRequest request, HttpServletResponse response, boolean isZh, Map<String, Object> out) throws Exception {
		InputStream inputStream = request.getInputStream();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputStream.read(buffer)) != -1) {
			arrayOutputStream.write(buffer, 0, len);
		}
		arrayOutputStream.close();
		inputStream.close();
		buffer = arrayOutputStream.toByteArray();

		String charset = HeaderParser.parseHeadValue(request.getContentType(), "charset", "");
		String string = "";
		if (charset != null && charset.trim().length() != 0) {
			try {
				string = new String(buffer, charset);
			} catch (Exception e) {
			}
		} else {
			string = new String(buffer);
		}
		out.put("userData", string);
	}

}
