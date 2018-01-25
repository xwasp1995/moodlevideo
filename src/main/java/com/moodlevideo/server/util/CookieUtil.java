package com.moodlevideo.server.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie相关方法
 * 
 * @author wangxuebin
 * 
 */
public class CookieUtil {
	
	
	/**  
     * 根据名字获取cookie  
     * @param request  
     * @param name cookie名字  
     * @return  
     */  
    public static Cookie getCookieByName(HttpServletRequest request,String name){  
        Map<String,Cookie> cookieMap = ReadCookieMap(request);  
        if(cookieMap.containsKey(name)){  
            Cookie cookie = (Cookie)cookieMap.get(name);  
            return cookie;  
        }else{  
            return null;  
        }     
    }  
	
	/**
	 * 添加cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param days
	 * @return
	 * @throws IOException
	 */
	public static void addCookie(HttpServletResponse response,
			String name, String value, int days){  
        Cookie cookie = new Cookie(name.trim(), value.trim());  
        cookie.setMaxAge(days * 24 * 60 * 60);// 设置cookie保持的天数  
        cookie.setPath("/");  
        System.out.println("已添加===============");  
        response.addCookie(cookie);  
    }  
	
	/**  
     * 将cookie封装到Map里面  
     * @param request  
     * @return  
     */  
    private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){    
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();  
        Cookie[] cookies = request.getCookies();  
        if(null!=cookies){  
            for(Cookie cookie : cookies){  
                cookieMap.put(cookie.getName(), cookie);  
            }  
        }  
        return cookieMap;  
    }  

}
