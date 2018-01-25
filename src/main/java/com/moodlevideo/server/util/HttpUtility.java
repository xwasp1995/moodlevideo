package com.moodlevideo.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpUtility {

	/**
	 * 30s
	 */
	private static final int SO_TIMEOUT = 60*1000;
	private static final int CONNECTION_TIMEOUT = 15*1000;

	/**
	 * 重试次数
	 */
	private static final int RETRY_COUNT = 5;
	public static final String DEF_USER_AGENT = "misc-service";
	public static final String DEF_CONTENT_TYPE = "application/x-json";
	public static final String default_Charset = "utf-8";
	
	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

	public static String getHttpBody(HttpServletRequest request)
			throws IOException {
		BufferedReader reader = request.getReader();
		StringBuffer sb = new StringBuffer();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	public static String post(String url, String body, String userAgent, String contentType) throws HttpException, IOException {
		String responseBodyString = null;
		
		HttpClient client = getHttpClientInstance();
	
		PostMethod method = new PostMethod(url);
	    method.getParams().setSoTimeout(SO_TIMEOUT);
		method.addRequestHeader("User-Agent", userAgent);
		method.setRequestHeader("Content-Type", contentType);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,default_Charset); 
		method.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,default_Charset);
		method.setRequestBody(body);
		try {
			client.executeMethod(method);
			responseBodyString = method.getResponseBodyAsString();
		} finally {
			method.releaseConnection();
		}
		
		return responseBodyString;
	}

    public static String get(String url, Map<String, String> params, String userAgent, String contentType) throws Exception {

    	String responseBodyString = null;

        HttpClient client = getHttpClientInstance();

        String requestUrl = makeUrl(url, params);
        GetMethod method = new GetMethod(requestUrl);
        method.getParams().setSoTimeout(SO_TIMEOUT);
        method.addRequestHeader("User-Agent", userAgent);
        method.setRequestHeader("Content-Type", contentType);

        try {
        	client.executeMethod(method);
            responseBodyString = method.getResponseBodyAsString();
        } finally {
            method.releaseConnection();
        }

        return responseBodyString;
    }
    public static String get(String url, String userAgent, String contentType) throws Exception {

    	String responseBodyString = null;

        HttpClient client = getHttpClientInstance();

//        String requestUrl = makeUrl(url, params);
        GetMethod method = new GetMethod(url);
        method.getParams().setSoTimeout(SO_TIMEOUT);
        method.addRequestHeader("User-Agent", userAgent);
        method.setRequestHeader("Content-Type", contentType);

        try {
        	client.executeMethod(method);
            responseBodyString = method.getResponseBodyAsString();
        } finally {
            method.releaseConnection();
        }

        return responseBodyString;
    }

	private static HttpClient getHttpClientInstance() {
		HttpClient client = new HttpClient(connectionManager);
		
		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(
						RETRY_COUNT, true));
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,default_Charset); 
		HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams();
		managerParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,default_Charset); 
        managerParams.setConnectionTimeout(CONNECTION_TIMEOUT);  // 设置连接超时时间(单位毫秒)
	    managerParams.setSoTimeout(SO_TIMEOUT);// 设置读数据超时时间(单位毫秒)
		return client;
	}
	
	public static String makeUrl(String url, Map<String, String> params) throws UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer(url); 
		if(url.contains("?")){
     		sb.append("&t=").append(System.currentTimeMillis());
		}else{
			sb.append("?t=").append(System.currentTimeMillis());
		}
		
		if(params != null) {
			Set<Entry<String, String>> entries = params.entrySet();
			for(Entry<String, String> entry : entries){
	            if(entry != null && entry.getValue() != null){
	                sb.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), default_Charset));
	            }
			}
		}
		
		return sb.toString();
	}
}