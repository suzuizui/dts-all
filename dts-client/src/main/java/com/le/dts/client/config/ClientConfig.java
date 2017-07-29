package com.le.dts.client.config;

import java.util.Map;
import java.util.UUID;

import com.le.dts.client.executor.job.processor.FailureJobProcessor;
import com.le.dts.client.executor.job.processor.StopJobProcessor;
import com.le.dts.common.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.Machine;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.service.HttpService;

/**
 * 客户端各项参数配置
 * @author tianyao.myc
 *
 */
public class ClientConfig implements Constants {
	
	private static final Log logger = LogFactory.getLog(ClientConfig.class);

	/** 远程通信服务线程数量 */
	private int remotingThreads = DEFAULT_REMOTING_THREADS;
	
	/** 心跳间隔时间 */
	private long heartBeatIntervalTime = 3 * DEFAULT_HEART_BEAT_INTERVAL_TIME;

	/** 连接超时时间 */
	private long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	
	/** 分组ID */
	private String groupId;
	
	/** ZK地址列表 */
	private String zkHosts;
	
	/** ZK根目录 */
	private String namespace = DEFAULT_ZK_ROOT_PATH;
	
	/** ZK会话超时时间 */
	private int zkSessionTimeout = DEFAULT_ZK_SESSION_TIMEOUT;
	
	/** ZK连接超时时间 */
	private int zkConnectionTimeout = DEFAULT_ZK_CONNECTION_TIMEOUT;
	
	/** 是否Spring环境 */
	private boolean isSpring = false;
	
	/** 队列大小 */
	private int queueSize = QUEUE_SIZE;
	
	/** 消费线程数量 */
	private int consumerThreads = DEFAULT_CONSUMER_THREAD_AMOUNT;
	
	/** 消费线程数量Map */
	private Map<String, Integer> consumerThreadsMap = null;
	
	/** 客户端版本信息 */
	private String version;
	
	/** 一次从服务端拉取的任务数量 */
	private int pageSize = DEFAULT_PAGE_SIZE;
	
	/** 一次从服务端拉取的任务数量Map */
	private Map<String, Integer> pageSizeMap = null;
	
	/** 访问键 */
	private String accessKey;
	
	//秘钥
	private String secretKey;
	
	/** 本地地址 */
	private String localAddress;
	
	/** 客户端ID */
	private String clientId;
	
	/** 宕机重试 */
	private boolean crashRetry = false;
	
	//空队列暂停拉取间隔时间
	private long pullTaskListOverSleepTime = 20 * 1000L;
	
	/** 失败任务处理器Map */
	private Map<String, FailureJobProcessor> failureJobProcessorMap = null;
	
	/** 停止任务处理器 */
	private StopJobProcessor stopJobProcessor = null;
	
	private boolean finishLog = true;
	
	private boolean isEveryTimeNew = false;
	
	private boolean zkHostsAutoChange = true;
	
	private long maxBodySize = 64 * 1024L;
	
	private String environment;
	
	/** 任务Map */
	private Map<String, String> jobMap;
	
	/** 机器信息 */
	private Machine machine;
	
	private String domainName;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		try {
			this.version = "1.0.0-SNAPSHOT";//Manifests.read("App-Version");
		} catch (Throwable e) {
			throw new InitException("[ClientConfig]: init version error", e);
		}
		
		this.localAddress = RemotingUtil.getLocalAddress();
		
		try {
			this.clientId = UUID.randomUUID().toString() + COLON + this.localAddress;
		} catch (Throwable e) {
			throw new InitException("[ClientConfig]: init clientId error", e);
		}

		//TODO(DELETE)
//		if(StringUtil.isBlank(this.domainName)) {
//			try {
//				this.domainName = DiamondHelper.getData(HttpService.DOMAIN_NAME_DATA_ID, 10 * 1000L);
//			} catch (Throwable e) {
//				throw new InitException("[ClientConfig]: get domainName from diamond error", e);
//			}
//		}
//
//		if(StringUtil.isBlank(this.domainName)) {
//			throw new InitException("[ClientConfig]: domainName is empty error, domainName:" + this.domainName);
//		}
		
		if(Constants.ENVIRONMENT_JST.equals(this.environment)) {

			String configPath = PathUtil.getJSTHomeConfigPath("TaskList.ini");
			
			try {
				this.jobMap = IniUtil.getIniValuesFromFile(configPath, "TaskListSection");
			} catch (Throwable e) {
				throw new InitException("[ClientConfig]: init get task list from TaskList.ini error, please check configPath:" + configPath);
			}
			
			this.machine = new Machine(RemotingUtil.getLocalHostname(), RemotingUtil.getLocalHostname(), this.localAddress);
			
		}
	}
	
	public int getRemotingThreads() {
		return remotingThreads;
	}

	public void setRemotingThreads(int remotingThreads) {
		if(remotingThreads <= 0) {
			this.remotingThreads = DEFAULT_REMOTING_THREADS;
			logger.warn("[ClientConfig]: setRemotingThreads error, you set remotingThreads:" + remotingThreads);
			return ;
		}
		if(remotingThreads > 10 * DEFAULT_REMOTING_THREADS) {
			this.remotingThreads = 10 * DEFAULT_REMOTING_THREADS;
			logger.warn("[ClientConfig]: setRemotingThreads too large, you set remotingThreads:" + remotingThreads + ", max:" + (10 * DEFAULT_REMOTING_THREADS));
			return ;
		}
		this.remotingThreads = remotingThreads;
	}

	public long getHeartBeatIntervalTime() {
		return heartBeatIntervalTime;
	}

	public void setHeartBeatIntervalTime(long heartBeatIntervalTime) {
		if(heartBeatIntervalTime <= 0L) {
			this.heartBeatIntervalTime = DEFAULT_HEART_BEAT_INTERVAL_TIME;
			logger.warn("[ClientConfig]: setHeartBeatIntervalTime error, you set heartBeatIntervalTime:" + heartBeatIntervalTime);
			return ;
		}
		if(heartBeatIntervalTime > 20 * DEFAULT_HEART_BEAT_INTERVAL_TIME) {
			this.heartBeatIntervalTime = 20 * DEFAULT_HEART_BEAT_INTERVAL_TIME;
			logger.warn("[ClientConfig]: setHeartBeatIntervalTime too large, you set heartBeatIntervalTime:" + heartBeatIntervalTime + ", max:" + (20 * DEFAULT_HEART_BEAT_INTERVAL_TIME));
			return ;
		}
		this.heartBeatIntervalTime = heartBeatIntervalTime;
	}

	public long getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(long connectionTimeout) {
		if(connectionTimeout <= 0L) {
			this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
			logger.warn("[ClientConfig]: setConnectionTimeout error, you set connectionTimeout:" + connectionTimeout);
			return ;
		}
		if(connectionTimeout > 10 * DEFAULT_CONNECTION_TIMEOUT) {
			this.connectionTimeout = 10 * DEFAULT_CONNECTION_TIMEOUT;
			logger.warn("[ClientConfig]: setConnectionTimeout too large, you set connectionTimeout:" + connectionTimeout + ", max:" + (10 * DEFAULT_CONNECTION_TIMEOUT));
			return ;
		}
		this.connectionTimeout = connectionTimeout;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
		if(zkSessionTimeout <= 0) {
			this.zkSessionTimeout = DEFAULT_ZK_SESSION_TIMEOUT;
			logger.warn("[ClientConfig]: setZkSessionTimeout error, you set zkSessionTimeout:" + zkSessionTimeout);
			return ;
		}
		if(zkSessionTimeout > 20 * DEFAULT_ZK_SESSION_TIMEOUT) {
			this.zkSessionTimeout = 20 * DEFAULT_ZK_SESSION_TIMEOUT;
			logger.warn("[ClientConfig]: setZkSessionTimeout too large, you set zkSessionTimeout:" + zkSessionTimeout + ", max:" + (20 * DEFAULT_ZK_SESSION_TIMEOUT));
			return ;
		}
		this.zkSessionTimeout = zkSessionTimeout;
	}

	public int getZkConnectionTimeout() {
		return zkConnectionTimeout;
	}

	public void setZkConnectionTimeout(int zkConnectionTimeout) {
		if(zkConnectionTimeout <= 0) {
			this.zkConnectionTimeout = DEFAULT_ZK_CONNECTION_TIMEOUT;
			logger.warn("[ClientConfig]: setZkConnectionTimeout error, you set zkConnectionTimeout:" + zkConnectionTimeout);
			return ;
		}
		if(zkConnectionTimeout > 20 * DEFAULT_ZK_CONNECTION_TIMEOUT) {
			this.zkConnectionTimeout = 20 * DEFAULT_ZK_CONNECTION_TIMEOUT;
			logger.warn("[ClientConfig]: setZkConnectionTimeout too large, you set zkConnectionTimeout:" + zkConnectionTimeout + ", max:" + (20 * DEFAULT_ZK_CONNECTION_TIMEOUT));
			return ;
		}
		this.zkConnectionTimeout = zkConnectionTimeout;
	}

	public boolean isSpring() {
		return isSpring;
	}

	public void setSpring(boolean isSpring) {
		this.isSpring = isSpring;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		if(queueSize <= 0) {
			this.queueSize = QUEUE_SIZE;
			logger.warn("[ClientConfig]: setQueueSize error, you set queueSize:" + queueSize);
			return ;
		}
		if(queueSize > 10 * QUEUE_SIZE) {
			this.queueSize = 10 * QUEUE_SIZE;
			logger.warn("[ClientConfig]: setQueueSize too large, you set queueSize:" + queueSize + ", max:" + (10 * QUEUE_SIZE));
			return ;
		}
		this.queueSize = queueSize;
	}

	public int getConsumerThreads() {
		return consumerThreads;
	}

	public void setConsumerThreads(int consumerThreads) {
		this.consumerThreads = checkConsumerThreads(consumerThreads);
	}
	
	public int checkConsumerThreads(int consumerThreads) {
		if(consumerThreads <= 0) {
			logger.warn("[ClientConfig]: setConsumerThreads error, you set consumerThreads:" + consumerThreads);
			return DEFAULT_CONSUMER_THREAD_AMOUNT;
		}
		if(consumerThreads > 100 * DEFAULT_CONSUMER_THREAD_AMOUNT) {
			logger.warn("[ClientConfig]: setConsumerThreads too large, you set consumerThreads:" + consumerThreads + ", max:" + (100 * DEFAULT_CONSUMER_THREAD_AMOUNT));
			return 100 * DEFAULT_CONSUMER_THREAD_AMOUNT;
		}
		return consumerThreads;
	}

	public Map<String, Integer> getConsumerThreadsMap() {
		return consumerThreadsMap;
	}

	public void setConsumerThreadsMap(Map<String, Integer> consumerThreadsMap) {
		this.consumerThreadsMap = consumerThreadsMap;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = checkPageSize(pageSize);
	}
	
	public int checkPageSize(int pageSize) {
		if(pageSize <= 0) {
			logger.warn("[ClientConfig]: setPageSize error, you set pageSize:" + pageSize);
			return DEFAULT_PAGE_SIZE;
		}
		if(pageSize > 2 * DEFAULT_PAGE_SIZE) {
			logger.warn("[ClientConfig]: setPageSize too large, you set pageSize:" + pageSize + ", max:" + (2 * DEFAULT_PAGE_SIZE));
			return 2 * DEFAULT_PAGE_SIZE;
		}
		return pageSize;
	}

	public Map<String, Integer> getPageSizeMap() {
		return pageSizeMap;
	}

	public void setPageSizeMap(Map<String, Integer> pageSizeMap) {
		this.pageSizeMap = pageSizeMap;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isCrashRetry() {
		return crashRetry;
	}

	public void setCrashRetry(boolean crashRetry) {
		this.crashRetry = crashRetry;
	}

	public long getPullTaskListOverSleepTime() {
		return pullTaskListOverSleepTime;
	}

	public void setPullTaskListOverSleepTime(long pullTaskListOverSleepTime) {
		this.pullTaskListOverSleepTime = pullTaskListOverSleepTime;
	}

	public Map<String, FailureJobProcessor> getFailureJobProcessorMap() {
		return failureJobProcessorMap;
	}

	public void setFailureJobProcessorMap(
			Map<String, FailureJobProcessor> failureJobProcessorMap) {
		this.failureJobProcessorMap = failureJobProcessorMap;
	}

	public boolean isFinishLog() {
		return finishLog;
	}

	public void setFinishLog(boolean finishLog) {
		this.finishLog = finishLog;
	}

	public StopJobProcessor getStopJobProcessor() {
		return stopJobProcessor;
	}

	public void setStopJobProcessor(StopJobProcessor stopJobProcessor) {
		this.stopJobProcessor = stopJobProcessor;
	}

	public boolean isEveryTimeNew() {
		return isEveryTimeNew;
	}

	public void setEveryTimeNew(boolean isEveryTimeNew) {
		this.isEveryTimeNew = isEveryTimeNew;
	}

	public boolean isZkHostsAutoChange() {
		return zkHostsAutoChange;
	}

	public void setZkHostsAutoChange(boolean zkHostsAutoChange) {
		this.zkHostsAutoChange = zkHostsAutoChange;
	}

	public long getMaxBodySize() {
		return maxBodySize;
	}

	public void setMaxBodySize(long maxBodySize) {
		this.maxBodySize = maxBodySize;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public Map<String, String> getJobMap() {
		return jobMap;
	}

	public void setJobMap(Map<String, String> jobMap) {
		this.jobMap = jobMap;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public String toString() {
		return "ClientConfig [remotingThreads=" + remotingThreads
				+ ", heartBeatIntervalTime=" + heartBeatIntervalTime
				+ ", connectionTimeout=" + connectionTimeout + ", groupId="
				+ groupId + ", zkHosts=" + zkHosts + ", namespace=" + namespace
				+ ", zkSessionTimeout=" + zkSessionTimeout
				+ ", zkConnectionTimeout=" + zkConnectionTimeout
				+ ", isSpring=" + isSpring + ", queueSize=" + queueSize
				+ ", consumerThreads=" + consumerThreads
				+ ", consumerThreadsMap=" + consumerThreadsMap + ", version="
				+ version + ", pageSize=" + pageSize + ", pageSizeMap="
				+ pageSizeMap + ", accessKey=" + accessKey + ", localAddress="
				+ localAddress + ", clientId=" + clientId + ", crashRetry="
				+ crashRetry + ", pullTaskListOverSleepTime="
				+ pullTaskListOverSleepTime + ", failureJobProcessorMap="
				+ failureJobProcessorMap + ", stopJobProcessor="
				+ stopJobProcessor + ", finishLog=" + finishLog
				+ ", isEveryTimeNew=" + isEveryTimeNew + ", zkHostsAutoChange="
				+ zkHostsAutoChange + ", maxBodySize=" + maxBodySize
				+ ", environment=" + environment + ", jobMap=" + jobMap
				+ ", machine=" + machine + ", domainName=" + domainName + "]";
	}

}
