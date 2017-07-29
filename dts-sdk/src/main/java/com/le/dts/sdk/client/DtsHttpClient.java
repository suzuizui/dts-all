package com.le.dts.sdk.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.sdk.util.HttpUtil;

/**
 * Http 客户端;
 * Created by luliang on 14/12/29.
 */
public class DtsHttpClient {
	
	private static final Log logger = LogFactory.getLog(DtsHttpClient.class);

    private String domainUrl;

    private static final String ACTION_NAME = "SDKAction";

    private String target;

    private String submitAction;

    //请求参数;
    private ConcurrentHashMap<String, String> parameterMap = new ConcurrentHashMap<String, String>();

    public DtsHttpClient() {}

    public String doPost() {
        String requestURL = domainUrl;
        requestURL += "/" + target + "?";
        requestURL += "action=" + ACTION_NAME + "&";
        requestURL += submitAction + "=1";
        String data = parseParameter(requestURL);
        String result = null;
        try {
			result = HttpUtil.sendRequest(requestURL, data);
		} catch (Throwable e) {
			logger.error("[DtsHttpClient]: doPost, requestURL:" + requestURL + ", data:" + data, e);
		}
        logger.error("[DtsHttpClient]: doPost, requestURL:" + requestURL + ", data:" + data + ", result:" + result);
        return result;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public DtsHttpClient addParameter(String name, String value) {
        parameterMap.put(name, value);
        return this;
    }

    public DtsHttpClient setTarget(String target) {
        this.target = target;
        return this;
    }

    private String parseParameter(String url) {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry: parameterMap.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
//            try {
//                sb.append("&").append(entry.getKey()).append("=").append(new String(entry.getValue().getBytes("UTF-8")));
//            } catch (UnsupportedEncodingException e) {
//
//            }
//            try {
//                sb.append("&").append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//
//            }
        }
        parameterMap.clear();
        return sb.toString();
    }

    public DtsHttpClient setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
        return this;
    }
}
