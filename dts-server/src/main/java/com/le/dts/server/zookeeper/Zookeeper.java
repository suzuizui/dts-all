package com.le.dts.server.zookeeper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.zookeeper.timer.ZkCheckTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.locks.ChildReaper;
import org.apache.curator.framework.recipes.locks.Reaper.Mode;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.PathUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.StringUtil;
import com.le.dts.common.zk.ZkConfig;
import com.le.dts.common.zk.ZkManager;

/**
 * Zookeeper
 * @author tianyao.myc
 *
 */
public class Zookeeper implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(Zookeeper.class);
	
	private ZkManager zkManager = new ZkManager();
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(CHECK_ZK_THREAD_AMOUNT, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, CHECK_ZK_THREAD_NAME);
				}
				
			});
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化ZkManager */
		initZkManager();
		
		/** 初始化服务器IP地址到ZK */
		initServerAddress();
		
		/** 初始化ZK节点检查定时器 */
		initZkCheckTimer();
		
	}
	
	/**
	 * 初始化ZkManager
	 * @throws InitException
	 */
	private void initZkManager() throws InitException {
		ZkConfig zkConfig = new ZkConfig();
		zkConfig.setZkHostsAutoChange(serverConfig.isZkHostsAutoChange());
		zkConfig.setNamespace(serverConfig.getNamespace());
		
		if(StringUtil.isNotBlank(serverConfig.getZkHosts()) && ! NULL.equals(serverConfig.getZkHosts())) {
			zkConfig.setZkHosts(serverConfig.getZkHosts());
		}
		
		zkConfig.setZkConnectionTimeout(serverConfig.getZkConnectionTimeout());
		zkConfig.setZkSessionTimeout(serverConfig.getZkSessionTimeout());
		zkConfig.setZkHostsSource(ZkConfig.ZK_HOSTS_DIAMOND_SOURCE);
		zkManager.setZkConfig(zkConfig);
		try {
			zkManager.init();
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initZkManager error", e);
		}
		
		//设置zk地址列表
		serverConfig.setZkHosts(zkConfig.getZkHosts());
		
        String jobInstanceLockPath = PathUtil.getJobInstanceLockPath();
        ChildReaper childReaper = new ChildReaper(zkManager.getZkClient(), jobInstanceLockPath, Mode.REAP_INDEFINITELY);
        try {
        	childReaper.start();
		} catch (Throwable e) {
			throw new RuntimeException("childReaper start error, jobInstanceLockPath:" + jobInstanceLockPath);
		}
	}

	/**
	 * 初始化服务器IP地址到ZK
	 * @throws InitException
	 */
	private void initServerAddress() throws InitException {
		String serverPath = PathUtil.getServerPath(serverConfig.getClusterId(), 
				serverConfig.getServerGroupId(), serverConfig.getLocalAddress());
		ZkManager zkManager = zookeeper.getZkManager();
		try {
			zkManager.publishOrUpdateData(serverPath, serverConfig.getDescription(), false);
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initMachineAddress error"
					+ ", serverPath:" + serverPath, e);
		}
	}
	
	/**
	 * 初始化ZK节点检查定时器
	 * @throws InitException
	 */
	private void initZkCheckTimer() throws InitException {
		try {
			
			//zk服务端地址列表检查
			executorService.scheduleAtFixedRate(new ZkCheckTimer(),
					0L, 30 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initZkCheckTimer error", e);
		}
	}
	
	/**
	 * 查询path下的所有节点并包含节点中的数据，实时查询zk
	 * @param path
	 * @return
	 */
	public Map<String, String> getChildDatas(String path) {
		return zkManager.getChildDatas(path);
	}
	
	/**
	 * 获取当前服务器集群列表
	 * @return
	 */
	public List<String> getServerList() {
		String serverGroupPath = PathUtil.getServerGroupPath(serverConfig.getClusterId(), serverConfig.getServerGroupId());
		return zkManager.getChildren(serverGroupPath);
	}
	
	/**
	 * 写客户端节点
	 * @param remoteMachine
	 */
	public void writeClient(RemoteMachine remoteMachine) {
		String clientPath = PathUtil.getClientPath(remoteMachine.getGroupId(), 
				RemotingUtil.parseIpFromAddress(remoteMachine.getRemoteAddress()));
		try {
			zkManager.publishOrUpdateData(clientPath, NULL, false);
		} catch (Throwable e) {
			logger.error("[Zookeeper]: writeClient error, clientPath:" + clientPath, e);
		}
	}
	
	/**
	 * 删除客户端节点
	 * @param groupId
	 * @param remoteAddress
	 */
	public void deleteClient(String groupId, String remoteAddress) {
		RemoteMachine remoteMachine = new RemoteMachine();
		remoteMachine.setGroupId(groupId);
		remoteMachine.setRemoteAddress(remoteAddress);
		this.deleteClient(remoteMachine);
	}
	
	/**
	 * 删除客户端节点
	 * @param remoteMachine
	 */
	public void deleteClient(RemoteMachine remoteMachine) {
		String clientPath = PathUtil.getClientPath(remoteMachine.getGroupId(), 
				RemotingUtil.parseIpFromAddress(remoteMachine.getRemoteAddress()));
		try {
			zkManager.delete(clientPath);
		} catch (Throwable e) {
			logger.error("[Zookeeper]: deleteClient error, clientPath:" + clientPath, e);
		}
	}
	
	public ZkManager getZkManager() {
		return zkManager;
	}
	
}
