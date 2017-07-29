package com.le.dts.client;

import java.util.Map;

import com.le.dts.client.executor.job.processor.FailureJobProcessor;
import com.le.dts.client.executor.job.processor.StopJobProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.GroupIdUtil;

/**
 * DTS客户端初始化入口
 * @author tianyao.myc
 *
 */
public class DtsClient implements ClientContext, ApplicationContextAware {

	private static final Log logger = LogFactory.getLog(DtsClient.class);
	
	/** 是否已经初始化 */
	private static volatile boolean initialized = false;
	
	/**
	 * 判断是否已经初始化
	 */
	private synchronized static void initialized() {

		logger.warn("[DtsClient]: tell initialized, thread:" + Thread.currentThread().getName());
		
		if(initialized) {
			throw new RuntimeException("DtsClient has already initialized");
		}
		
		initialized = true;//标记客户端已经初始化
		
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		//判断是否已经初始化
		initialized();
		
		if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
			clientConfig.setGroupId("1-3-3-1");	
		}
		
		/** 检查分组ID */
		GroupIdUtil.checkGroupId(clientConfig.getGroupId());
		
		/** 客户端各项参数配置初始化 */
		clientConfig.init();
		
		/** Zookeeper初始化 */
		zookeeper.init();
		
		/** 客户端远程通信初始化 */
		clientRemoting.init();
		
		/** 初始化Job处理器工厂 */
		jobProcessorFactory.init();
		
		if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
			
			//执行日志初始化
			executeLogger.init();
		}
		
		logger.warn("[DtsClient]: init over, clientConfig:" + clientConfig.toString());
	}

	/**
	 * 远程通信服务线程数量
	 * @param remotingThreads
	 */
	public void setRemotingThreads(int remotingThreads) {
		clientConfig.setRemotingThreads(remotingThreads);
	}

	/**
	 * 心跳间隔时间
	 * @param heartBeatIntervalTime
	 */
	public void setHeartBeatIntervalTime(long heartBeatIntervalTime) {
		clientConfig.setHeartBeatIntervalTime(heartBeatIntervalTime);
	}

	/**
	 * 连接超时时间
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(long connectionTimeout) {
		clientConfig.setConnectionTimeout(connectionTimeout);
	}

	/**
	 * 分组ID
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		clientConfig.setGroupId(groupId);
	}

	/**
	 * ZK地址列表
	 * @param zkHosts
	 */
	public void setZkHosts(String zkHosts) {
		clientConfig.setZkHosts(zkHosts);
	}

	/**
	 * ZK根目录
	 * @param namespace
	 */
	public void setNamespace(String namespace) {
		clientConfig.setNamespace(namespace);
	}

	/**
	 * ZK会话超时时间
	 * @param zkSessionTimeout
	 */
	public void setZkSessionTimeout(int zkSessionTimeout) {
		clientConfig.setZkSessionTimeout(zkSessionTimeout);
	}

	/**
	 * ZK连接超时时间
	 * @param zkConnectionTimeout
	 */
	public void setZkConnectionTimeout(int zkConnectionTimeout) {
		clientConfig.setZkConnectionTimeout(zkConnectionTimeout);
	}

	/**
	 * 队列大小
	 * @param queueSize
	 */
	public void setQueueSize(int queueSize) {
		clientConfig.setQueueSize(queueSize);
	}

	/**
	 * 消费线程数量
	 * @param consumerThreads
	 */
	public void setConsumerThreads(int consumerThreads) {
		clientConfig.setConsumerThreads(consumerThreads);
	}
	
	/**
	 * 消费线程数量Map
	 * @param consumerThreadsMap
	 */
	public void setConsumerThreadsMap(Map<String, Integer> consumerThreadsMap) {
		clientConfig.setConsumerThreadsMap(consumerThreadsMap);
	}
	
	/**
	 * 一次从服务端拉取的任务数量
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		clientConfig.setPageSize(pageSize);
	}

	/**
	 * 一次从服务端拉取的任务数量Map
	 * @param pageSizeMap
	 */
	public void setPageSizeMap(Map<String, Integer> pageSizeMap) {
		clientConfig.setPageSizeMap(pageSizeMap);
	}
	
	/**
	 * 访问键
	 * @param accessKey
	 */
	public void setAccessKey(String accessKey) {
		clientConfig.setAccessKey(accessKey);
	}

	public void setSecretKey(String secretKey) {
		clientConfig.setSecretKey(secretKey);
	}
	
	/**
	 * 宕机重试
	 * @param crashRetry
	 */
	public void setCrashRetry(boolean crashRetry) {
		clientConfig.setCrashRetry(crashRetry);
	}
	
	public void setZkHostsAutoChange(boolean zkHostsAutoChange) {
		clientConfig.setZkHostsAutoChange(zkHostsAutoChange);
	}
	
	/**
	 * 空队列暂停拉取间隔时间
	 * @param pullTaskListOverSleepTime
	 */
	public void setPullTaskListOverSleepTime(long pullTaskListOverSleepTime) {
		clientConfig.setPullTaskListOverSleepTime(pullTaskListOverSleepTime);
	}
	
	public void setFailureJobProcessorMap(Map<String, FailureJobProcessor> failureJobProcessorMap) {
		clientConfig.setFailureJobProcessorMap(failureJobProcessorMap);
	}
	
	public void setFinishLog(boolean finishLog) {
		clientConfig.setFinishLog(finishLog);
	}
	
	public void setStopJobProcessor(StopJobProcessor stopJobProcessor) {
		clientConfig.setStopJobProcessor(stopJobProcessor);
	}
	
	public void setEveryTimeNew(boolean isEveryTimeNew) {
		clientConfig.setEveryTimeNew(isEveryTimeNew);
	}
	
	public void setEnvironment(String environment) {
		clientConfig.setEnvironment(environment);
	}
	
	public void setDomainName(String domainName) {
		clientConfig.setDomainName(domainName);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		jobProcessorFactory.setApplicationContext(applicationContext);
		clientConfig.setSpring(true);
		logger.warn("[DtsClient]: setApplicationContext over, applicationContext:" + applicationContext);
	}

}
