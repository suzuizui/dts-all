package com.le.dts.console.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by luliang on 14/12/24.
 */
public class HttpUtil {

    private static final Log logger = LogFactory.getLog(HttpUtil.class);

    public static final String HTTP_METHOD = "POST";

    private static int timeoutInMilliSeconds = 2000;  // 2 seconds timeout

    public static String sendRequest(String requestUrl) throws IOException {

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
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("dataType", "json");

            outStream = conn.getOutputStream();
            outStream.flush();

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
