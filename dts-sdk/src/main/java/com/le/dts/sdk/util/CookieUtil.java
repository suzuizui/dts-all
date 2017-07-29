package com.le.dts.sdk.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.util.StringUtil;

/**
 * Cookie操作;
 * Created by luliang on 14/12/30.
 */
public class CookieUtil {

    private static final Log logger = LogFactory.getLog(CookieUtil.class);

    private static final String CHAR_SET = "UTF-8";

    public static String cookieToString(List<Cookie> cookies) {
        StringBuilder sb = new StringBuilder();
        if(cookies.size() > 0) {
            StringBuilder[] cookieEntry = new StringBuilder[cookies.size()];
            for(int i = 0; i < cookies.size(); i++) {
                cookieEntry[i] = new StringBuilder();
                try {
                    cookieEntry[i].append(URLEncoder.encode(cookies.get(i).getName(), CHAR_SET))
                            .append("=").append(URLEncoder.encode(cookies.get(i).getValue(), CHAR_SET));
                } catch (UnsupportedEncodingException e) {
                    logger.error("url encode error", e);
                }
            }
            sb.append(StringUtil.join(cookieEntry, ";"));
        }
        return sb.toString();
    }
}
