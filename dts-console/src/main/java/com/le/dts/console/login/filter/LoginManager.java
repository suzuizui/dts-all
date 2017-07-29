package com.le.dts.console.login.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.le.dts.common.constants.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class LoginManager {
	private static Logger logger = Logger.getLogger(LoginManager.class);
	public static String LOGIN_URL = "";
	public static String LOGOUT_URL = "";
	
	public static Cookie getCookie(String key,
			HttpServletRequest request) {
		if (request == null) return null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        Cookie value = null;
        for (Cookie c : cookies) {
            if (key.equals(c.getName())) {
                value = c;
                break;
            }
        }
        return value;
	}

	public static boolean checkSessionUser(HttpServletRequest request,
			HttpServletResponse response) {
		if(null == request){
			return false;
		}
		Object user = request.getSession().getAttribute(Constants.KEY_LOGING_USER_COOKIE);
		if(null == user){
			return false;
		}
		return true;
	}
	
	public static String getFullUrl(HttpServletRequest request) {

		StringBuffer requestUrl = new StringBuffer();
		requestUrl.append(request.getRequestURL());
		
		//logout
		if(request.getRequestURI().equals("/logout")){
			String baseUrl = requestUrl.substring(0, requestUrl.length()-"/logout".length());
			return baseUrl + "/index.htm";
		}
		
		
		if(request.getRequestURI().equals("/")){
			requestUrl.append("index.htm");
		}
		
		Map<String, String[]> params = request.getParameterMap();
		if(!params.isEmpty()){
			requestUrl.append("?");
			for(Map.Entry<String, String[]> entry : params.entrySet()){
				requestUrl.append(entry.getKey()).append("=")
					.append(entry.getValue()[0]).append("&");
			}
		}
		return requestUrl.toString();
	}
	
	public static void doRedirect(String redirectUrl, String fullUrl, 
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendRedirect(redirectUrl +"?oauth_callback="+ URLEncoder.encode(fullUrl, "UTF-8"));
		
	}
	

}
