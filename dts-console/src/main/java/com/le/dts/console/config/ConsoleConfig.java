package com.le.dts.console.config;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.uribroker.URIBrokerService;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.InitException;
import com.le.dts.console.api.ApiService;

/**
 * 控制台各项参数配置
 * @author tianyao.myc
 *
 */
public class ConsoleConfig implements Constants {

	/** 所有集群信息 */
	private TreeMap<Long/** clusterId */, Cluster/** 集群描述 */> serverClusterMap;
	
	/** ZK地址列表 */
	private String zkHosts;
	
	/** ZK根目录 */
	private String namespace = DEFAULT_ZK_ROOT_PATH;
	
	/** ZK会话超时时间 */
	private int zkSessionTimeout = DEFAULT_ZK_SESSION_TIMEOUT;
	
	/** ZK连接超时时间 */
	private int zkConnectionTimeout = DEFAULT_ZK_CONNECTION_TIMEOUT;

	/** JDBC驱动类名 */
	private String driverClassName;

	/** 分库数量 */
	private int dynamicDBCount;

	/** 分表数量 */
	private int dynamicTableCount;

	/** MYSQL连接地址 */
	private List<String> url = new ArrayList<String>();

	/** MYSQL登录用户名 */
	private List<String> username = new ArrayList<String>();

	/** MYSQL登录密码 */
	private List<String> password = new ArrayList<String>();
	
	/** MYSQL最大连接数 */
	private int maxActive = DEFAULT_MAX_ACTIVE;
	
	/** JDBC驱动类名 */
	private String driverClassName4Meta;
	
	/** MYSQL连接地址 */
	private String url4Meta;
	
	/** MYSQL登录用户名 */
	private String username4Meta;
	
	/** MYSQL登录密码 */
	private String password4Meta;
	
	/** MYSQL最大连接数 */
	private int maxActive4Meta = DEFAULT_MAX_ACTIVE;
	
	private boolean zkHostsAutoChange = true;
	
	@Autowired
	public static URIBrokerService uriBrokerService;
	
	/**
	 * 初始化
	 * @param apiService
	 * @throws InitException
	 */
	public void init(ApiService apiService) throws InitException {
		this.serverClusterMap = apiService.getUserClusters();
	}
	
	public TreeMap<Long, Cluster> getServerClusterMap() {
		return serverClusterMap;
	}

	public void setServerClusterMap(TreeMap<Long, Cluster> serverClusterMap) {
		this.serverClusterMap = serverClusterMap;
	}

	public String getZkHosts() {
		return zkHosts;
	}

	public void setZkHosts(String zkHosts) {
		this.zkHosts = zkHosts;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public int getZkSessionTimeout() {
		return zkSessionTimeout;
	}

	public void setZkSessionTimeout(int zkSessionTimeout) {
		this.zkSessionTimeout = zkSessionTimeout;
	}

	public int getZkConnectionTimeout() {
		return zkConnectionTimeout;
	}

	public void setZkConnectionTimeout(int zkConnectionTimeout) {
		this.zkConnectionTimeout = zkConnectionTimeout;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public int getDynamicDBCount() {
		return dynamicDBCount;
	}

	public void setDynamicDBCount(int dynamicDBCount) {
		this.dynamicDBCount = dynamicDBCount;
	}

	public int getDynamicTableCount() {
		return dynamicTableCount;
	}

	public void setDynamicTableCount(int dynamicTableCount) {
		this.dynamicTableCount = dynamicTableCount;
	}

	public List<String> getUrl() {
		return url;
	}

	public void setUrl(List<String> url) {
		this.url = url;
	}

	public List<String> getUsername() {
		return username;
	}

	public void setUsername(List<String> username) {
		this.username = username;
	}

	public List<String> getPassword() {
		return password;
	}

	public void setPassword(List<String> password) {
		this.password = password;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public String getDriverClassName4Meta() {
		return driverClassName4Meta;
	}

	public void setDriverClassName4Meta(String driverClassName4Meta) {
		this.driverClassName4Meta = driverClassName4Meta;
	}

	public String getUrl4Meta() {
		return url4Meta;
	}

	public void setUrl4Meta(String url4Meta) {
		this.url4Meta = url4Meta;
	}

	public String getUsername4Meta() {
		return username4Meta;
	}

	public void setUsername4Meta(String username4Meta) {
		this.username4Meta = username4Meta;
	}

	public String getPassword4Meta() {
		return password4Meta;
	}

	public void setPassword4Meta(String password4Meta) {
		this.password4Meta = password4Meta;
	}

	public int getMaxActive4Meta() {
		return maxActive4Meta;
	}

	public void setMaxActive4Meta(int maxActive4Meta) {
		this.maxActive4Meta = maxActive4Meta;
	}

	public boolean isZkHostsAutoChange() {
		return zkHostsAutoChange;
	}

	public void setZkHostsAutoChange(boolean zkHostsAutoChange) {
		this.zkHostsAutoChange = zkHostsAutoChange;
	}

	public static URIBrokerService getUriBrokerService() {
		return uriBrokerService;
	}

	public static void setUriBrokerService(URIBrokerService uriBrokerService) {
		ConsoleConfig.uriBrokerService = uriBrokerService;
	}

	@Override
	public String toString() {
		return "ConsoleConfig [serverClusterMap=" + serverClusterMap
				+ ", zkHosts=" + zkHosts + ", namespace=" + namespace
				+ ", zkSessionTimeout=" + zkSessionTimeout
				+ ", zkConnectionTimeout=" + zkConnectionTimeout
				+ ", dataSourceAppName=" + ", driverClassName=" + driverClassName
				+ ", url=" + url + ", username=" + username + ", password="
				+ password + ", maxActive=" + maxActive
				+ ", driverClassName4Meta=" + driverClassName4Meta
				+ ", url4Meta=" + url4Meta + ", username4Meta=" + username4Meta
				+ ", password4Meta=" + password4Meta + ", maxActive4Meta="
				+ maxActive4Meta + ", zkHostsAutoChange=" + zkHostsAutoChange
				+ "]";
	}

}
