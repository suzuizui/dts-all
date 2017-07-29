package com.le.dts.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import com.le.dts.sdk.context.SDKContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.util.StringUtil;

/**
 * Created by luliang on 14/12/24.
 */
public class HttpUtil {

    private static final Log logger = LogFactory.getLog(HttpUtil.class);

    public static final String HTTP_METHOD = "POST";

    private static int timeoutInMilliSeconds = 999999;  // 10 seconds timeout

    public static String sendRequest(String requestUrl, String data) throws IOException {

        URL url = null;
        url = new URL(requestUrl);

        HttpURLConnection conn = null;
        OutputStream outStream = null;
        InputStream inputStream = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(HTTP_METHOD);
            conn.setConnectTimeout(timeoutInMilliSeconds);
            conn.setReadTimeout(timeoutInMilliSeconds);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
//            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//            conn.setRequestProperty("Accept", "application/json; charset=utf-8");
//            conn.setRequestProperty("Accept-Charset", "UTF-8");
//            String cookie = SDKContext.acquireCookie();
//            if(StringUtil.isNotEmpty(cookie)) {
//                conn.setRequestProperty("Cookie", cookie);
//            }

            outStream = conn.getOutputStream();
            outStream.write(data.getBytes("UTF-8"));
            outStream.flush();
//            PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
//            out.write(data);
//            out.flush();

            inputStream = conn.getInputStream();

            ByteArrayOutputStream byteArrayBuff = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int l = -1;
            while ((l = inputStream.read(buff, 0, 1024)) != -1) {
                byteArrayBuff.write(buff, 0, l);
            }
            return new String(byteArrayBuff.toByteArray(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            logger.error("request url failed," + url, e);
            throw e;
        } catch (Exception e) {
        	logger.error("request url failed," + url, e);
        	return null;
        } finally {
            if (outStream != null)
                try {
                    outStream.close();
                } catch (IOException e) {
                    logger.error("close outStream failed", e);
                }
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("close inputStream failed", e);
                }
            if (conn != null)
                conn.disconnect();
        }

    }
}
