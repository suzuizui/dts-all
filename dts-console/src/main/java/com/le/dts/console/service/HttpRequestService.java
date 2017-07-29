package com.le.dts.console.service;


import com.le.dts.console.config.EnvData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * HTTP 请求
 * @author tianyao.myc
 *
 */
public class HttpRequestService {

	/** HttpRequestService 日志 */
	private static final Logger log = LoggerFactory.getLogger(HttpRequestService.class);
	
	@Autowired
    private EnvData envData;
	
	/** 请求参数设置 */
	private static final int timeout = 5000;
	private static final int httpConnectionFactoryTimeout = 5000;
	private static final int connectionTimeout = 500;
	
	/** 默认编码为GBK */
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	
	
	/**
	 * 启动流程
	 * @param processCode
	 * @param title
	 * @param orginatorId
	 * @param key
	 * @param value
	 * @param authKey
	 * @return
	 */
	public String startProcessInstance(String processCode, String title, String orginatorId, String key, String value, String authKey) {
//
//		String initData = "{\"" + key + "\":\"" + value + "\"}";
//
//		NameValuePair _processCode 	= new NameValuePair("processCode", processCode);
//		NameValuePair _title 		= new NameValuePair("title", title);
//		NameValuePair _orginatorId 	= new NameValuePair("orginatorId", orginatorId);
//		NameValuePair _initData 	= new NameValuePair("initData", initData);
//		NameValuePair _authKey 		= new NameValuePair("authKey", authKey);
//
//		String url = envData.getFlowDomainName() + "openapi/processInstanceService/startProcessInstance.json";
//
//		String result = null;
//		try {
//			result = requestPost(url, new NameValuePair[]{_processCode, _title, _orginatorId, _initData, _authKey});
//		} catch (Exception e) {
//			log.error("[HttpRequestService]: startProcessInstance requestPost error"
//					+ ", processCode:" + processCode
//					+ ", title:" + title
//					+ ", orginatorId:" + orginatorId
//					+ ", initData:" + initData, e);
//		}
//
//		if(StringUtil.isBlank(result)) {
//			return result;
//		}
//
//        try {
//
//        	JSONObject resultObject = JSON.parseObject(result);
//
//        	boolean hasError = resultObject.getBooleanValue("hasError");
//        	if(hasError) {
//        		return result;
//        	}
//
//        	JSONObject contentObject = JSON.parseObject(resultObject.getString("content"));
//
//        	result = contentObject.getString("processInstanceId");
//		} catch (Throwable e) {
//			log.error("[HttpRequestService]: JSON.parse error, result:" + result, e);
//			return result;
//		}
//
//		return result;
		return null;
	}

}
