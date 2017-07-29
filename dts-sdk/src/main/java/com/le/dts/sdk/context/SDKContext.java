package com.le.dts.sdk.context;

import com.le.dts.common.exception.DtsException;

/**
 *
 * Created by luliang on 14/12/29.
 */
public class SDKContext {

    // DTS online domain
    public static String DTS_DOMAIN_ONLINE_URL  = "http://ops.dts.le.com/dts-console";
    // DTS daily
    public static String DTS_DOMAIN_DAILY_URL   = "http://dts.le.com:8080/dts-console";

    private static ThreadLocal<String> dtsCookie = new ThreadLocal<String>();

    /**
     * 获取当前访问的cookie
     * @return
     */
    public static String acquireCookie() {
        String cookie = dtsCookie.get();
        if(null == cookie) {
            throw new DtsException("cookie is null, acquire should not happen before set.");
        }
        return cookie;
    }

    /**
     * 更新cookie;
     * @param cookie
     */
    public static void setCookie(String cookie) {
        dtsCookie.set(cookie);
    }

    /**
     * 清除cookie上下文
     */
    public static void clean() {
        dtsCookie.remove();
    }
}
