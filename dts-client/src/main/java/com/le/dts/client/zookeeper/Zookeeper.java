package com.le.dts.client.zookeeper;

import java.util.List;

import com.le.dts.common.util.PathUtil;
import com.le.dts.common.zk.ZkConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.zk.ZkManager;

/**
 * Zookeeper
 * @author tianyao.myc
 *
 */
public class Zookeeper implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(Zookeeper.class);
	
	private ZkManager zkManager = new ZkManager();
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化ZkManager */
		initZkManager();
		
	}
	
	/**
	 * 初始化ZkManager
	 * @throws InitException
	 */
	private void initZkManager() throws InitException {
		//TODO(DELETE)
		ZkConfig zkConfig = new ZkConfig();
		zkConfig.setZkHostsAutoChange(clientConfig.isZkHostsAutoChange());
		zkConfig.setNamespace(clientConfig.getNamespace());
		zkConfig.setZkHosts(clientConfig.getZkHosts());
		zkConfig.setZkConnectionTimeout(clientConfig.getZkConnectionTimeout());
		zkConfig.setZkSessionTimeout(clientConfig.getZkSessionTimeout());
		zkConfig.setEnvironment(clientConfig.getEnvironment());
		zkConfig.setDomainName(clientConfig.getDomainName());
		zkConfig.setZkHostsSource(ZkConfig.ZK_HOSTS_DIAMOND_SOURCE);
		zkManager.setZkConfig(zkConfig);
		try {
			zkManager.init();
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initZkManager error", e);
		}

		//设置zk地址列表
		clientConfig.setZkHosts(zkConfig.getZkHosts());
	}

	/**
	 * 获取当前服务器集群分组服务器列表
	 * @return
	 */
	public List<String> getServerList() {
		//TODO(DELETE)
		String serverGroupPath = PathUtil.getServerGroupPath(
				GroupIdUtil.getCluster(clientConfig.getGroupId()).getId(),
				GroupIdUtil.getClientGroup(clientConfig.getGroupId()).getServerGroupId());
		
//		List<String> serverList = httpService.acquireServers(clientConfig.getDomainName(),
//				GroupIdUtil.getCluster(clientConfig.getGroupId()).getId(),
//				GroupIdUtil.getClientGroup(clientConfig.getGroupId()).getServerGroupId());
		
		List<String> serverList = null;
		try {
			serverList = zkManager.getChildren(serverGroupPath);
		} catch (Throwable e) {
			logger.error("[Zookeeper]: getChildren error, serverGroupPath:" + serverGroupPath, e);
		}
		return serverList;
	}

	public ZkManager getZkManager() {
		return zkManager;
	}
	
}
