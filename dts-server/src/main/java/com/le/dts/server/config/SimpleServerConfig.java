package com.le.dts.server.config;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 简单服务器配置
 * @author tianyao.myc
 *
 */
public class SimpleServerConfig implements Constants {

	/** 服务器集群ID */
	private long clusterId;
	
	/** 分库数据源appName */
	private String dataSourceAppName;
	
	/** Meta库数据源appName */
	private String dataSourceAppNameMeta;
	
	/** Meta库GroupKey */
	private String dbGroupKeyMeta;
	
	/** TDDL规则配置文件 */
	private String tddlAppruleFile;
	
	/** JDBC驱动类名 */
	private String driverClassName;
	
	/** MYSQL连接地址 */
	private String url;
	
	/** MYSQL登录用户名 */
	private String username;
	
	/** MYSQL登录密码 */
	private String password;
	
	/** MYSQL最大连接数 */
	private int maxActive;
	
	/** JDBC驱动类名 */
	private String driverClassName4Meta;
	
	/** MYSQL连接地址 */
	private String url4Meta;
	
	/** MYSQL登录用户名 */
	private String username4Meta;
	
	/** MYSQL登录密码 */
	private String password4Meta;
	
	/** MYSQL最大连接数 */
	private int maxActive4Meta;

	/**
     * 对象转换成json
     */
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }
	
    /**
     * String转换成对象
     * @param json
     * @return
     */
    public static SimpleServerConfig newInstance(String json) {
        return RemotingSerializable.fromJson(json, SimpleServerConfig.class);
    }
	
	public long getClusterId() {
		return clusterId;
	}

	public void setClusterId(long clusterId) {
		this.clusterId = clusterId;
	}

	public String getDataSourceAppName() {
		return dataSourceAppName;
	}

	public void setDataSourceAppName(String dataSourceAppName) {
		this.dataSourceAppName = dataSourceAppName;
	}

	public String getDataSourceAppNameMeta() {
		return dataSourceAppNameMeta;
	}

	public void setDataSourceAppNameMeta(String dataSourceAppNameMeta) {
		this.dataSourceAppNameMeta = dataSourceAppNameMeta;
	}

	public String getDbGroupKeyMeta() {
		return dbGroupKeyMeta;
	}

	public void setDbGroupKeyMeta(String dbGroupKeyMeta) {
		this.dbGroupKeyMeta = dbGroupKeyMeta;
	}

	public String getTddlAppruleFile() {
		return tddlAppruleFile;
	}

	public void setTddlAppruleFile(String tddlAppruleFile) {
		this.tddlAppruleFile = tddlAppruleFile;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
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

}
