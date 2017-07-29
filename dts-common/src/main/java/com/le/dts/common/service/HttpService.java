package com.le.dts.common.service;

import java.util.List;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ServerList;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 服务
 * @author tianyao.myc
 *
 */
public class HttpService implements Constants {
	
	public static final String DOMAIN_NAME_DATA_ID = "com.le.dts.common.domainName";

	private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
	
	/** 请求参数设置 */
	private static final int timeout 						= 10000;
	private static final int httpConnectionFactoryTimeout 	= 10000;
	private static final int connectionTimeout 				= 10000;
	
	/** 默认编码为 */
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * 获取zk地址列表
	 * @return
	 */
	public String acquireZkHosts(String domainName) {
		
		String url = "http://" + domainName + "/dts-console/apiManager.do?action=ApiAction&event_submit_do_acquire_zkHosts=1";
		
		String result = null;
		try {
			result = go(url);
		} catch (Throwable e) {
			logger.error("[HttpService]: acquireZkHosts error, url:" + url , e);
		}
		
		return StringUtils.trim(result);
	}
	
	/**
	 * 获取服务端地址列表
	 * @param domainName
	 * @param serverClusterId
	 * @param serverGroupId
	 * @return
	 */
	public List<String> acquireServers(String domainName, long serverClusterId, long serverGroupId) {
		
		String url = "http://" + domainName + "/dts-console/apiManager.do?action=ApiAction&event_submit_do_acquire_servers=1&clusterId=" + serverClusterId + "&serverGroupId=" + serverGroupId;
		
		String result = null;
		try {
			result = go(url);
		} catch (Throwable e) {
			logger.error("[HttpService]: acquireServers error, url:" + url , e);
		}
		
		ServerList serverList = null;
		try {
			serverList = ServerList.newInstance(result);
		} catch (Throwable e) {
			logger.error("[HttpService]: ServerList.newInstance error"
					+ ", url:" + url 
					+ ", result:" + result , e);
		}
		
		if(null == serverList) {
			logger.error("[HttpService]: ServerList.newInstance failed"
					+ ", url:" + url 
					+ ", result:" + result);
			return null;
		}
		
		return serverList.getServers();
	}
	
	/**
	 * 获取zk地址列表
	 * @param url
	 * @return
	 */
	private String go(String url) {
		return null;
	}

}
