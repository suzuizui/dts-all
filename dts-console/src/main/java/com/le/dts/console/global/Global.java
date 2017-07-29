package com.le.dts.console.global;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.util.UserEnvUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.DtsUser;
import com.le.dts.common.domain.store.Cluster;

/**
 * 全局方法
 * @author tianyao.myc
 *
 */
public class Global implements Constants {

	private static final Log logger = LogFactory.getLog(Global.class);

    public static final String ALIYUN_USER_TAG = "isAliyunUser";
    
    public static final String DEFAULT_USER = "defaultUser";
    
	/**
	 * 设置环境全局变量
	 * @param request
	 * @param response
	 * @param cluster
	 */
	public static void setServerCluster(HttpServletRequest request, HttpServletResponse response, Cluster cluster) {
		Cookie cookie = null;
		try {
			cookie = new Cookie(SERVER_CLUSTER, URLEncoder.encode(cluster.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("ServerCluster cookie error!" + cluster.toString());
		}
		response.addCookie(cookie);
	}

	/**
	 * 获取环境全局变量
	 * @param request
	 * @return
	 */
	public static Cluster getServerCluster(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(null == cookies) {
			return null;
		} else {
			for(int i = 0 ; i < cookies.length ; i ++) {
				if(cookies[i].getName().equals(SERVER_CLUSTER)) {
					String value = null;
					try {
						value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						logger.error("URLDecode error!" + cookies[i].getValue());
					}
					return Cluster.newInstance(value);
				}
			}
		}
		return null;
	}
	
	/**
	 * 设置用户全局变量
	 * @param request
	 * @param response
	 * @param dtsUser
	 */
	public static void setDtsUser(HttpServletRequest request, HttpServletResponse response, DtsUser dtsUser) {
		Cookie cookie = null;
		try {
			cookie = new Cookie(DTS_USER, URLEncoder.encode(dtsUser.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("DtsUser error!" + dtsUser.toString());
		}
		response.addCookie(cookie);
	}
	
	public static Cookie setUser(String name, DtsUser dtsUser, HttpServletRequest request, HttpServletResponse response) {
		
		String value = null;
		try {
			value = URLEncoder.encode(dtsUser.toString(),"UTF-8");
		} catch (Throwable e) {
			throw new RuntimeException("[Global]: encode error, dtsUser:" + dtsUser, e);
		}
		
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				
				if (name.equals(cookie.getName())) {
					
					//修改值
					cookie.setValue(value);
					
					//修改后，要更新到浏览器中
					response.addCookie(cookie);
					
					return cookie;
				}
				
			}
		}
		
		Cookie cookie = new Cookie(name, value);
		response.addCookie(cookie);
		
		return cookie;
	}
	
	public static DtsUser getUser(String name, HttpServletRequest request) {
		
		Cookie[] cookies = request.getCookies();
        
		Cookie findCookie = null;
        if (cookies != null) {
        	for (Cookie cookie : cookies) {
            	
                if (name.equals(cookie.getName())) {
                	findCookie = cookie;
                    break ;
                }
                
            }
        }
        
        if(null == findCookie) {
        	return null;
        }
        
        String value = null;
		try {
			value = URLDecoder.decode(findCookie.getValue(), "UTF-8");
			
			return DtsUser.newInstance(value);
		} catch (Throwable e) {
			
			logger.error("[Global]: decode error, findCookie:" + findCookie, e);
			
			return null;
		}
	}
	
	/**
	 * 获取用户全局变量
	 * @param request
	 * @return
	 */
	public static DtsUser getDtsUser(HttpServletRequest request) {
		
        try {
            return UserEnvUtil.getUserId(request);
        } catch (IOException e) {
            logger.error("get dts user error!");
            return null;
        }
	}
	
}
