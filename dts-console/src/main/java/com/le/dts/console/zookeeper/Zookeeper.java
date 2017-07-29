package com.le.dts.console.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.PathUtil;
import com.le.dts.common.util.RandomUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.zk.ZkConfig;
import com.le.dts.common.zk.ZkManager;
import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.config.EnvData;
import com.le.dts.console.remoting.ConsoleRemoting;
import com.le.dts.console.zookeeper.timer.ZkCheckTimer;

/**
 * Zookeeper
 * @author tianyao.myc
 *
 */
public class Zookeeper implements Constants {

	private static final Log logger = LogFactory.getLog(Zookeeper.class);
	
	private ZkManager zkManager = new ZkManager();

    @Autowired
    private EnvData envData;
    
    @Autowired
    private ConsoleRemoting consoleRemoting;
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(CHECK_ZK_THREAD_AMOUNT, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, CHECK_ZK_THREAD_NAME);
				}
				
			});
	
	private ServerService serverService = null;
	
	@Autowired
	private ConsoleConfig consoleConfig;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化ZkManager */
		initZkManager();
		
		/** 初始化控制台IP地址到ZK */
		initConsoleAddress();
		
		/** 初始化ZK节点检查定时器 */
		initZkCheckTimer();
		
		this.serverService = consoleRemoting.proxyInterface(ServerService.class);
	}
	
	/**
	 * 初始化ZkManager
	 * @throws InitException
	 */
	private void initZkManager() throws InitException {
		ZkConfig zkConfig = new ZkConfig();
		
        if(StringUtils.isBlank(envData.getZkNameSpace())) {
            zkConfig.setNamespace(consoleConfig.getNamespace());
        } else {
            zkConfig.setNamespace(envData.getZkNameSpace());
        }
        
        zkConfig.setZkHostsAutoChange(consoleConfig.isZkHostsAutoChange());
        
        if(StringUtils.isNotBlank(consoleConfig.getZkHosts()) && ! NULL.equals(consoleConfig.getZkHosts())) {
        	zkConfig.setZkHosts(consoleConfig.getZkHosts());
        }
		
		zkConfig.setZkConnectionTimeout(consoleConfig.getZkConnectionTimeout());
		zkConfig.setZkSessionTimeout(consoleConfig.getZkSessionTimeout());
		//TODO 源码中console的zkHosts不是从Diamond上面获取的，而是通过HTTP接口
		zkConfig.setZkHostsSource(ZkConfig.ZK_HOSTS_DIAMOND_SOURCE);
		zkManager.setZkConfig(zkConfig);
		try {
			zkManager.init();
		} catch (Throwable e) {
			logger.error("[Zookeeper]: initZkManager error", e);
			throw new InitException("[Zookeeper]: initZkManager error", e);
		}
	}

	/**
	 * 初始化控制台IP地址到ZK
	 * @throws InitException
	 */
	private void initConsoleAddress() throws InitException {
		String consoleIpPath = PathUtil.getConsoleIpPath(RemotingUtil.getLocalAddress());
		try {
			zkManager.publishOrUpdateData(consoleIpPath, RemotingUtil.getLocalAddress(), false);
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initConsoleAddress error"
					+ ", consoleIpPath:" + consoleIpPath, e);
		}
	}
	
	/**
	 * 初始化ZK节点检查定时器
	 * @throws InitException
	 */
	private void initZkCheckTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new ZkCheckTimer(this), 
					0L, 10 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[Zookeeper]: initZkCheckTimer error", e);
		}
	}
	
	/**
	 * 获取控制台地址列表
	 * @return
	 */
	public List<String> getConsoleIpList() {
		String consolePath = PathUtil.getConsolePath();
		List<String> consoleList = zkManager.getChildren(consolePath);
		
		if(CollectionUtils.isEmpty(consoleList)) {
			return consoleList;
		}
		
		//列表排序
		Collections.sort(consoleList);
		
		return consoleList;
	}
	
	/**
	 * 这个方法暂时不用;
	 * 获取整个集群IP列表
	 * @param cluster
	 * @return
	 */
	public List<String> getServerClusterIpList(Cluster cluster) {
		List<String> ips = new ArrayList<String>();
//		String serverClusterPath = PathUtil.getServerClusterPath(serverCluster.getServerClusterId());
//		List<String> ipList = zkManager.getChildren(serverClusterPath);
//		if(CollectionUtils.isEmpty(ipList)) {
//			logger.error("[Zookeeper]: getServerClusterIpList error, serverCluster:" + serverCluster.toString());
//			throw new RuntimeException("[Zookeeper]: getServerClusterIpList error, serverCluster:" + serverCluster.toString());
//		}
		return ips;
	}
	
	/**
	 * 获取集群中某个分组的IP列表
	 * @param clusterId
	 * @param serverGroupId
	 * @return
	 */
	public List<String> getServerGroupIpList(String clusterId,  String serverGroupId) {
		List<String> ips = new ArrayList<String>();
		String serverGroupPath = PathUtil.getServerGroupPath(Long.valueOf(clusterId), Long.valueOf(serverGroupId));
		List<String> ipList = zkManager.getChildren(serverGroupPath);
		if(CollectionUtils.isEmpty(ipList)) {
			logger.error("[Zookeeper]: getServerGroupIpList error, serverCluster:" + clusterId);
			//throw new RuntimeException("[Zookeeper]: getServerGroupIpList error, serverGroupId:" + serverGroupId);
		}
		return ipList;
	}

    public List<String> getClientGroupIpList(String clientGroup, long jobId) {
        List<String> ipList = new ArrayList<String>();
//        String clientGroupPath = PathUtil.getClientGroupPath(clientGroup);
//        List<String> ipList = zkManager.getChildren(clientGroupPath);
//        if(CollectionUtils.isEmpty(ipList)) {
//            logger.error("[Zookeeper]: getClientGroupIpList error, clientGroup:" + clientGroup);
//        }
        
        Cluster cluster = GroupIdUtil.getCluster(clientGroup);
        ClientGroup group = GroupIdUtil.getClientGroup(clientGroup);
        
        List<String> serverList = getServerGroupIpList(String.valueOf(cluster.getId()), String.valueOf(group.getServerGroupId()));
        if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[Zookeeper]: getServerGroupIpList serverList is empty"
					+ ", clientGroup:" + clientGroup);
			return ipList;
		}
        
        String server = RandomUtil.getRandomObj(serverList);
        
        List<RemoteMachine> remoteMachineList = null;
        try {
			InvocationContext.setRemoteMachine(new RemoteMachine(server, 10 * 1000L));
			remoteMachineList = serverService.getRemoteMachines(clientGroup, jobId);
		} catch (Throwable e) {
			logger.error("[Zookeeper]: getRemoteMachines error, clientGroup:" + clientGroup + ", server:" + server, e);
		}
        
        if(null == remoteMachineList || remoteMachineList.size() <= 0) {
        	return ipList;
        }
        
        for(RemoteMachine remoteMachine : remoteMachineList) {
        	ipList.add(RemotingUtil.parseIpFromAddress(remoteMachine.getRemoteAddress()));
        }
        
        return ipList;
    }


	
	public ZkManager getZkManager() {
		return zkManager;
	}
	
}
