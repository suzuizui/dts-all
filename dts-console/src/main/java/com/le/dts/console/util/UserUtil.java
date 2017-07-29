package com.le.dts.console.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.DtsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 用户工具类
 * @author tianyao.myc
 *
 */
public class UserUtil {

	/** UserUtil日志 */
	private static Logger log = LoggerFactory.getLogger(UserUtil.class);

    /**
     * 获取SDK访问的用户名;
     * @param request
     * @return
     */
    public static DtsUser getUserBySDK(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(null == cookies) {
            return null;
        } else {
            for(int i = 0 ; i < cookies.length ; i ++) {
                if(cookies[i].getName().equals(Constants.USER_KEY)) {
                    String value = null;
                    try {
                        value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("URLDecode error!" + cookies[i].getValue());
                        return null;
                    }
                    DtsUser dtsUser = new DtsUser();
                    dtsUser.setUserId(value);
                    dtsUser.setUserName(value);
                    return dtsUser;
                }
            }
        }
        return null;
    }

    public static String getAliyunEnvKeyFromSDK(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(null == cookies) {
            return null;
        } else {
            for(int i = 0 ; i < cookies.length ; i ++) {
                if(cookies[i].getName().equals(Constants.ALIYUN_ENVKEY)) {
                    String value = null;
                    try {
                        value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("URLDecode error!" + cookies[i].getValue());
                        return null;
                    }
                    return value;
                }
            }
        }
        return null;
    }
	
	/**
	 * 去掉工号前面的0
	 * @param str
	 * @return
	 */
	public static String removeZero(String str) {
		if("0".equals(str.substring(0, 1))) {
			str = str.substring(1, str.length());
		}
		return str;
	}
	
}
