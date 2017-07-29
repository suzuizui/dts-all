package com.le.dts.server.compensation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.le.dts.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.RandomUtil;
import com.le.dts.server.compensation.processor.CompensationProcessor;
import com.le.dts.server.compensation.timer.CompensationTimer;
import com.le.dts.server.state.LivingTaskManager;

/**
 * 失败补偿
 * @author tianyao.myc
 *
 */
public class Compensation implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog("compensation");
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(COMPENSATION_THREAD_AMOUNT, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, COMPENSATION_THREAD_NAME);
				}
				
			});
	
	/** 补偿线程组 */
	private CompensationProcessor[] compensationProcessors = null;
	
	/** 执行状态 */
	private int status = STATUS_STOP;
	
	/** 客户端基础服务 */
    private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化失败补偿定时器 */
		initCompensationTimer();
		
	}
	
	/**
	 * 初始化失败补偿定时器
	 * @throws InitException
	 */
	private void initCompensationTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new CompensationTimer(), 
					0L, serverConfig.getCompensationIntervalTime(), TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[Compensation]: initCompensationTimer error"
					+ ", compensationIntervalTime:" + serverConfig.getCompensationIntervalTime(), e);
		}
		logger.warn("[Compensation]: initCompensationTimer success"
					+ ", compensationIntervalTime:" + serverConfig.getCompensationIntervalTime());
	}
	
	/**
	 * 启动补偿
	 */
	public void start() {
		
		String serverAddress = serverConfig.getLocalAddress();
		
		if(STATUS_RUNNING == this.status) {
			logger.warn("[Compensation]: start compensation task is running, so return, serverAddress:" + serverAddress);
			return ;
		}
		
		this.status = STATUS_RUNNING;
		logger.info("[Compensation]: start... ");
		
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[Compensation]: start serverIpList is empty, serverAddress:" + serverAddress);
			this.status = STATUS_STOP;
			logger.info("[Compensation]: end... \n\n\n\n");
			return ;
		}
		
		final int serverAmount = serverList.size();
		final int serverNumber = serverList.indexOf(serverAddress);
		
		if(serverNumber < 0 || serverNumber >= serverAmount) {
			logger.error("[Compensation]: start serverNumber error"
					+ ", serverAmount:" + serverAmount 
					+ ", serverNumber:" + serverNumber 
					+ ", serverAddress:" + serverAddress 
					+ ", serverList:" + serverList);
			this.status = STATUS_STOP;
			logger.info("[Compensation]: end... \n\n\n\n");
			return ;
		}
		
		final long instanceAmount = countInstance();
		if(instanceAmount <= 0) {
			logger.warn("[Compensation]: start instanceAmount error"
					+ ", serverAmount:" + serverAmount 
					+ ", serverNumber:" + serverNumber 
					+ ", serverAddress:" + serverAddress 
					+ ", serverList:" + serverList 
					+ ", instanceAmount:" + instanceAmount);
			this.status = STATUS_STOP;
			logger.info("[Compensation]: end... \n\n\n\n");
			return ;
		}
		
		/** 初始化线程计数器 */
		final CountDownLatch threadCount = 
				new CountDownLatch(serverConfig.getCompensationThreads());
		final BlockingQueue<JobInstanceSnapshot> queue = 
				new LinkedBlockingQueue<JobInstanceSnapshot>(QUEUE_SIZE);
		/** 结束标记初始化 */
		final AtomicBoolean endTag = new AtomicBoolean(false);
		
		/** 初始化补偿线程组 */
		initCompensationProcessors(this, threadCount, queue, endTag);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				long pageAmount = instanceAmount / DEFAULT_PAGE_SIZE;
				if((instanceAmount % DEFAULT_PAGE_SIZE) != 0) {
					pageAmount += 1;
				}
				try {
					for(int pageNumber = 0 ; pageNumber < pageAmount ; pageNumber ++) {
						handleInstanceList(serverAmount, serverNumber, pageNumber, queue);
					}
				} catch (Throwable e) {
					logger.warn("[Compensation]: start handleTagList error"
							+ ", serverAmount:" + serverAmount 
							+ ", serverNumber:" + serverNumber 
							+ ", pageAmount:" + pageAmount 
							+ ", instanceAmount:" + instanceAmount, e);
				} finally {
					endTag.set(true);
					logger.info("[Compensation]: start handleInstanceList over");
				}
			}
			
		}).start();
		
		/** 启动补偿线程组 */
		startCompensationProcessors();
		
		/** 等待任务分发完成 */
		try {
			threadCount.await();
		} catch (Exception e) {
			logger.error("[Compensation]: threadCount await error", e);
		}
		this.status = STATUS_STOP;
		logger.info("[Compensation]: end... \n\n\n\n");
	}
	
	/**
	 * 初始化补偿线程组
	 * @param compensation
	 * @param threadCount
	 * @param queue
	 * @param endTag
	 */
	private void initCompensationProcessors(Compensation compensation, 
			CountDownLatch threadCount, BlockingQueue<JobInstanceSnapshot> queue, AtomicBoolean endTag) {
		compensationProcessors = new CompensationProcessor[serverConfig.getCompensationThreads()];
		for(int i = 0 ; i < serverConfig.getCompensationThreads() ; i ++) {
			compensationProcessors[i] = new CompensationProcessor(compensation, threadCount, queue, endTag, i, logger);
		}
	}
	
	/**
	 * 处理实例列表
	 * @param serverAmount
	 * @param serverNumber
	 * @param pageNumber
	 * @param queue
	 */
	private void handleInstanceList(int serverAmount, int serverNumber, int pageNumber, final BlockingQueue<JobInstanceSnapshot> queue) {
		List<JobInstanceSnapshot> instanceList = loadInstanceList(pageNumber);
		if(CollectionUtils.isEmpty(instanceList)) {
			logger.error("[Compensation]: handleInstanceList instanceList is empty error, pageNumber:" + pageNumber);
			return ;
		}
		for(JobInstanceSnapshot instance : instanceList) {
			/** 取莫计算当前server处理的实例 */
			if(serverNumber == (instance.getId() % serverAmount)) {
				try {
					queue.put(instance);
				} catch (Throwable e) {
					logger.error("[Compensation]: handleInstanceList put instance error, instance:" + instance, e);
				}
			}
		}
	}
	
	/**
	 * 启动补偿线程组
	 */
	public void startCompensationProcessors() {
		for(int i = 0 ; i < serverConfig.getCompensationThreads() ; i ++) {
			compensationProcessors[i].start();
		}
	}
	
	/**
	 * 处理标记
	 * @param instance
	 */
	public void handleInstance(JobInstanceSnapshot instance) {
		JobInstanceSnapshot jobInstanceSnapshot = null;
		try {
			jobInstanceSnapshot = store.getJobInstanceSnapshotAccess().get(instance.getId());
		} catch (Throwable e) {
			logger.error("[Compensation]: get jobInstanceSnapshot error, instance:" + instance, e);
		}
		if(null == jobInstanceSnapshot) {
			logger.error("[Compensation]: get jobInstanceSnapshot failed, instance:" + instance);
			return ;
		}
		
		if(JOB_INSTANCE_STATUS_RETRYING == jobInstanceSnapshot.getStatus()) {
			logger.warn("[Compensation]: jobInstanceSnapshot is retrying, instance:" + instance);
			return ;
		}
		
		TaskSnapshot query = new TaskSnapshot();
		query.setJobInstanceId(instance.getId());
		long result = 0L;
		try {
			result = store.getTaskSnapshotAccess().queryTaskSnapshotRetryCount(query);
		} catch (Throwable e) {
			logger.error("[Compensation]: handleInstance queryTaskSnapshotRetryCount error, query:" + query, e);
			return ;
		}
		if(result <= 0L) {
			/** 设置为重试结束 */
			this.updateInstanceStatus(instance, JOB_INSTANCE_STATUS_RETRY_OVER);
			return ;
		}
		
		Job job = null;
		try {
			Job queryJob = new Job();
			queryJob.setId(instance.getJobId());
			job = store.getJobAccess().queryJobById(queryJob);
		} catch (Throwable e) {
			logger.error("[Compensation]: queryJobById error, instance:" + instance, e);
		}
		if(null == job) {
			logger.error("[Compensation]: queryJobById failed, instance:" + instance);
			return ;
		}
		
		List<RemoteMachine> remoteMachineList = serverRemoting.getRemoteMachines(
				GroupIdUtil.generateGroupId(serverConfig.getClusterId(), 
						serverConfig.getServerGroupId(), serverConfig.getJobBackupAmount(), job.getClientGroupId()), job.getId());
		if(CollectionUtils.isEmpty(remoteMachineList)) {
			logger.warn("[Compensation]: clientMachineList is empty, instance:" + instance);
			return ;
		}
		
		int unLock = updateInstanceLock(instance);
		if(unLock <= 0) {
			logger.error("[Compensation]: unLock failed, instance:" + instance);
			return ;
		}
		
		ExecutableTask executableTask = new ExecutableTask(job, instance);
		executableTask.setCompensation(true);
		if(CommonUtil.isSimpleJob(job.getType())) {
			TaskSnapshot taskSnapshot = getRootTask(instance);
			if(null == taskSnapshot) {
				logger.error("[Compensation]: getRootTask taskSnapshot is null, executableTask:" + executableTask);
				return ;
			}
			taskSnapshot.setCompensation(true);
			executableTask.setTaskSnapshot(taskSnapshot);
		}
		
        RemoteMachine remoteMachine = RandomUtil.getRandomObj(remoteMachineList);
        InvocationContext.setRemoteMachine(remoteMachine);
        Result<Boolean> executeResult = clientService.executeTask(executableTask);
        
		if(executeResult != null && executeResult.getData().booleanValue()) {
			/** 设置为重试中 */
			this.updateInstanceStatus(instance, JOB_INSTANCE_STATUS_RETRYING);
			
			ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
            key.setJobId(job.getId());
            key.setJobInstanceId(instance.getId());
            key.setJobType(job.getType());
            key.setGroupId(remoteMachine.getGroupId());
            key.setCompensation(true);
            LivingTaskManager.getSingleton().add(key, remoteMachine);
		}
		logger.info("[Compensation]: executeTask, executableTask:" + executableTask + ", executeResult:" + executeResult);
	}
	
	/**
	 * 获取实例数量
	 * @return
	 */
	private long countInstance() {
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(JOB_INSTANCE_STATUS_RETRY);
		long result = 0;
		try {
			result = store.getJobInstanceSnapshotAccess().queryRetryCount(statusList);
		} catch (Throwable e) {
			logger.error("[Compensation]: countInstance error, statusList:" + statusList, e);
		}
		return result;
	}
	
	/**
	 * 加载实例列表
	 * @param pageNumber
	 * @return
	 */
	private List<JobInstanceSnapshot> loadInstanceList(int pageNumber) {
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(JOB_INSTANCE_STATUS_RETRY);
		List<JobInstanceSnapshot> instanceList = null;
		try {
			instanceList = store.getJobInstanceSnapshotAccess().queryRetryInstanceList(statusList, pageNumber * DEFAULT_PAGE_SIZE, DEFAULT_PAGE_SIZE);
		} catch (Throwable e) {
			logger.error("[Compensation]: loadInstanceList error, statusList:" + statusList + ", pageNumber:" + pageNumber, e);
		}
		return instanceList;
	}
	
	/**
	 * 更新实例状态
	 * @param instance
	 * @param status
	 */
	private void updateInstanceStatus(JobInstanceSnapshot instance, int status) {
		JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
		jobInstanceSnapshot.setId(instance.getId());
		jobInstanceSnapshot.setStatus(status);
		int result = 0;
		try {
			result = store.getJobInstanceSnapshotAccess().updateInstanceStatus(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[Compensation]: updateInstanceStatus error, instance:" + instance + ", status:" + status, e);
		}
		if(result <= 0) {
			logger.error("[Compensation]: updateInstanceStatus failed, instance:" + instance + ", status:" + status);
		}
	}
	
	/**
	 * 更新锁信息恢复到初始状态
	 * @param instance
	 * @return
	 */
	private int updateInstanceLock(JobInstanceSnapshot instance) {
		int result = 0;
		try {
			result = store.getJobInstanceSnapshotAccess().updateInstanceLock(instance);
		} catch (Throwable e) {
			logger.error("[Compensation]: updateInstanceLock error, instance:" + instance + ", status:" + status, e);
		}
		return result;
	}
	
	/**
	 * 获取根任务
	 * @param instance
	 * @return
	 */
	private TaskSnapshot getRootTask(JobInstanceSnapshot instance) {
		 Map<String, Object> query = new HashMap<String, Object>();
		 query.put("jobInstanceId", instance.getId());
         List<TaskSnapshot> taskSnapshotList = null;
         try {
			taskSnapshotList = store.getTaskSnapshotAccess().queryByJobInstanceIdAndStatus(query);
		} catch (Throwable e) {
			logger.error("[Compensation]: getRootTask error, instance:" + instance, e);
		}
        if(CollectionUtils.isEmpty(taskSnapshotList) || taskSnapshotList.size() != 1) {
        	logger.error("[Compensation]: getRootTask taskSnapshotList error, instance:" + instance + ", taskSnapshotList:" + taskSnapshotList);
        	return null;
        }
        return taskSnapshotList.get(0);
	}
	
}
