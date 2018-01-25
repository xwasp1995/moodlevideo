package com.moodlevideo.server.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http方法
 * 
 * @author wangxuebin
 * 
 */
public class HttpUtil {
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	
	/**
	 * 取得POST消息体
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getPostBody(HttpServletRequest request)
			throws IOException {
		String method = request.getMethod();

		if (!method.equalsIgnoreCase("POST")) {
//			logger.debug("IS NOT POST REQUEST METHOD!={}", request.getMethod());
			return "";
		}

		String httpBody = null;
		BufferedReader reader = request.getReader();
		StringBuffer sb = new StringBuffer();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		httpBody = sb.toString();
//		logger.debug("http body={}", httpBody);
		return httpBody;
	}
	
	/**
	 * RESPONSE 输出 STR
	 * 
	 * @param response
	 * @param msg
	 * @throws IOException
	 */
	public static void outStr(HttpServletResponse response, String msg) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("application/json;charset=utf-8");
			response.setContentLength(msg.getBytes("utf-8").length);
			response.getWriter().append(msg);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
	
	
}
