package com.le.dts.common.zk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ZkManager的配置对象
 * @author qihao, hanxua.mh
 */
public class ZkConfig {
	
	public static final int ZK_HOSTS_DIAMOND_SOURCE = 1;
	public static final int ZK_HOSTS_CONSOLE_SOURCE = 2;

    private static final Log log = LogFactory.getLog(ZkConfig.class);

    // ZK的服务器地址列表，逗号分隔，默认是日常的zk地址
    protected String zkHosts;

    // zk的存储数据的namespace
    protected String namespace;

    // ZK的session超时
    protected int zkSessionTimeout = 10000;

    // ZK的连接超时
    protected int zkConnectionTimeout = 15000;

    // 构造时传入的属性，传入授权时，ZkManager创建的节点只有自己才可以修改、删除
    protected String authentication;
    
    private boolean zkHostsAutoChange = true;
    
    private String environment;
    
    private String domainName;
    
    private int zkHostsSource = ZK_HOSTS_CONSOLE_SOURCE;

    public void init() {
        if (this.namespace == null) {
            throw new RuntimeException("namespace must be set");
        }
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

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public boolean isZkHostsAutoChange() {
		return zkHostsAutoChange;
	}

	public void setZkHostsAutoChange(boolean zkHostsAutoChange) {
		this.zkHostsAutoChange = zkHostsAutoChange;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getZkHostsSource() {
		return zkHostsSource;
	}

	public void setZkHostsSource(int zkHostsSource) {
		this.zkHostsSource = zkHostsSource;
	}

	@Override
	public String toString() {
		return "ZkConfig [zkHosts=" + zkHosts + ", namespace=" + namespace
				+ ", zkSessionTimeout=" + zkSessionTimeout
				+ ", zkConnectionTimeout=" + zkConnectionTimeout
				+ ", authentication=" + authentication + ", zkHostsAutoChange="
				+ zkHostsAutoChange + ", environment=" + environment
				+ ", domainName=" + domainName + ", zkHostsSource="
				+ zkHostsSource + "]";
	}

}