package com.le.dts.console.login.filter;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.console.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.le.dts.common.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

/**
 * 处理SDK相关逻辑;
 * Created by luliang on 14/12/30.
 */
public class SDKFilterManager {

    private static Logger logger = Logger.getLogger(SDKFilterManager.class);

    private static final String WILDCARD = "*";

    private static final String DauthAK = "DauthAK";

    private static final String DauthSK = "DauthSK";

    private static final String DauthData = "data";

    public static String getTimeStampKey(Cookie[] cookies) {
        for(int i = 0 ; i < cookies.length ; i++) {
            if(cookies[i].getName().equals(Constants.TIME_STAMP)) {
                String value = null;
                try {
                    value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("URLDecode error!" + cookies[i].getValue());
                }
                return value;
            }
        }
        return null;
    }

    private static String getUserId(Cookie[] cookies) {
        for(int i = 0 ; i < cookies.length ; i++) {
            if(cookies[i].getName().equals(Constants.USER_KEY)) {
                String value = null;
                try {
                    value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("URLDecode error!" + cookies[i].getValue());
                }
                return value;
            }
        }
        return null;
    }

    private static String getSDKSign(Cookie[] cookies) {
        for(int i = 0 ; i < cookies.length ; i++) {
            if(cookies[i].getName().equals(Constants.SIGN)) {
                String value = null;
                try {
                    value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("URLDecode error!" + cookies[i].getValue());
                }
                return value;
            }
        }
        return null;
    }

    /**
     * 检查下全局访问的GUID;
     * @param set
     * @param request
     * @return
     */
    public static boolean checkGuid(Set set, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for(int i = 0 ; i < cookies.length ; i++) {
            if(cookies[i].getName().equals(Constants.GUID)) {
                String value = null;
                try {
                    value = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                    if(set.contains(value)) {
                        return false;
                    } else {
                        set.add(value);
                        return true;
                    }
                } catch (UnsupportedEncodingException e) {
                    logger.error("URLDecode error!" + cookies[i].getValue());
                }
                return false;
            }
        }
        return false;
    }

    public static String getAccessKey(JSONObject dauthData) {
        return dauthData.getString(DauthAK);
    }

    public static String getSecurityKey(JSONObject dauthData) {
        return dauthData.getString(DauthSK);
    }
}
