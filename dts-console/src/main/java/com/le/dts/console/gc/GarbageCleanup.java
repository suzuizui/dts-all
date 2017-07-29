package com.le.dts.console.gc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.console.gc.timer.GarbageCleanupTimer;
import com.le.dts.console.store.JobInstanceSnapshotAccess;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.ListUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.TimeUtil;
import com.le.dts.console.gc.processor.GarbageCleanupProcessor;
import com.le.dts.console.store.TaskSnapshotAccess;

/**
 * 垃圾清理
 * @author tianyao.myc
 *
 */
public class GarbageCleanup implements Constants {

	private static final Log logger = LogFactory.getLog("garbageCleanup");
	
	/** 处理线程数量 */
	private static final int THREAD_AMOUNT = 10;
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(GC_THREAD_AMOUNT, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, GC_THREAD_NAME);
				}
				
			});
	
	/** 垃圾清理线程组 */
	private GarbageCleanupProcessor[] garbageCleanupProcessors = new GarbageCleanupProcessor[THREAD_AMOUNT];
	
	private Zookeeper zookeeper;
	
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	private TaskSnapshotAccess taskSnapshotAccess;
	
	public void setZookeeper(Zookeeper zookeeper) {
		this.zookeeper = zookeeper;
	}

	public void setJobInstanceSnapshotAccess(
			JobInstanceSnapshotAccess jobInstanceSnapshotAccess) {
		this.jobInstanceSnapshotAccess = jobInstanceSnapshotAccess;
	}

	public void setTaskSnapshotAccess(TaskSnapshotAccess taskSnapshotAccess) {
		this.taskSnapshotAccess = taskSnapshotAccess;
	}

	/** 执行状态 */
	private int status = STATUS_STOP;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化垃圾清理定时器 */
		initGarbageCleanupTimer();
		
	}
	
	/**
	 * 初始化垃圾清理定时器
	 * @throws InitException
	 */
	private void initGarbageCleanupTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new GarbageCleanupTimer(this),
					0L, 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[GarbageCleanup]: initGarbageCleanupTimer error", e);
		}
		logger.info("[GarbageCleanup]: initGarbageCleanupTimer success");
	}
	
	/**
	 * 开始清理
	 */
	public void start() {
		
		if(STATUS_RUNNING == this.status) {
			logger.warn("[GarbageCleanup]: start garbageCleanup task is running, so return");
			return ;
		}
		
		this.status = STATUS_RUNNING;
		logger.info("[GarbageCleanup]: start... ");
		
		List<String> consoleIpList = zookeeper.getConsoleIpList();
		if(CollectionUtils.isEmpty(consoleIpList)) {
			logger.error("[GarbageCleanup]: start consoleIpList is empty");
			this.status = STATUS_STOP;
			logger.info("[GarbageCleanup]: end... \n\n\n\n");
			return ;
		}
		
		final int consoleAmount = consoleIpList.size();
		final int consoleNumber = consoleIpList.indexOf(RemotingUtil.getLocalAddress());
		
		if(consoleNumber < 0 || consoleNumber >= consoleAmount) {
			logger.error("[GarbageCleanup]: start consoleNumber error"
					+ ", consoleAmount:" + consoleAmount 
					+ ", consoleNumber:" + consoleNumber 
					+ ", consoleIpList:" + consoleIpList);
			this.status = STATUS_STOP;
			logger.info("[GarbageCleanup]: end... \n\n\n\n");
			return ;
		}
		
		final long instanceAmount = countInstance();
		if(instanceAmount <= 0) {
			logger.warn("[GarbageCleanup]: start instanceAmount error"
					+ ", consoleAmount:" + consoleAmount 
					+ ", consoleNumber:" + consoleNumber 
					+ ", consoleIpList:" + consoleIpList 
					+ ", instanceAmount:" + instanceAmount);
			this.status = STATUS_STOP;
			logger.info("[GarbageCleanup]: end... \n\n\n\n");
			return ;
		}
		
		final AtomicLong instanceCounter = new AtomicLong(0L);
		
		/** 初始化线程计数器 */
		final CountDownLatch threadCount = 
				new CountDownLatch(THREAD_AMOUNT);
		final BlockingQueue<JobInstanceSnapshot> queue = 
				new LinkedBlockingQueue<JobInstanceSnapshot>(QUEUE_SIZE);
		/** 结束标记初始化 */
		final AtomicBoolean endTag = new AtomicBoolean(false);
		
		/** 初始化垃圾清理处理器 */
		initGarbageCleanupProcessors(this, threadCount, queue, endTag);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
					while(jobInstanceSnapshot != null) {
						jobInstanceSnapshot = handleInstanceList(consoleAmount, consoleNumber, jobInstanceSnapshot.getId(), queue, instanceCounter);
					}
				} catch (Throwable e) {
					logger.error("[GarbageCleanup]: start handleInstanceList error"
							+ ", consoleAmount:" + consoleAmount 
							+ ", consoleNumber:" + consoleNumber, e);
				} finally {
					endTag.set(true);
					logger.info("[GarbageCleanup]: start handleInstanceList over");
				}
			}
			
		}).start();
		
		/** 启动垃圾清理处理器 */
		startGarbageCleanupProcessors();
		
		/** 等待任务分发完成 */
		try {
			threadCount.await();
		} catch (Exception e) {
			logger.error("[GarbageCleanup]: threadCount await error", e);
		}
		this.status = STATUS_STOP;
		logger.info("[GarbageCleanup]: instanceCounter:" + instanceCounter.get() + " end... \n\n\n\n");
	}
	
	/**
	 * 初始化垃圾清理处理器
	 * @param garbageCleanup
	 * @param threadCount
	 * @param queue
	 * @param endTag
	 */
	private void initGarbageCleanupProcessors(GarbageCleanup garbageCleanup, 
			CountDownLatch threadCount, BlockingQueue<JobInstanceSnapshot> queue, AtomicBoolean endTag) {
		for(int i = 0 ; i < garbageCleanupProcessors.length ; i ++) {
			garbageCleanupProcessors[i] = new GarbageCleanupProcessor(garbageCleanup, threadCount, queue, endTag, i, logger);
		}
	}
	
	/**
	 * 处理实例列表
	 * @param consoleAmount
	 * @param consoleNumber
	 * @param offset
	 * @param queue
	 * @param instanceCounter
	 * @return
	 */
	private JobInstanceSnapshot handleInstanceList(int consoleAmount, int consoleNumber, 
			long offset, final BlockingQueue<JobInstanceSnapshot> queue, final AtomicLong instanceCounter) {
		List<JobInstanceSnapshot> instanceList = loadInstanceList(offset);
		if(CollectionUtils.isEmpty(instanceList)) {
			logger.error("[GarbageCleanup]: handleInstanceList instanceList is empty error, offset:" + offset);
			return null;
		}
		for(JobInstanceSnapshot instance : instanceList) {
			/** 取莫计算当前console处理的实例 */
			if(consoleNumber == (instance.getId() % consoleAmount)) {
				try {
					queue.put(instance);
				} catch (Throwable e) {
					logger.error("[GarbageCleanup]: handleInstanceList put instance error, instance:" + instance, e);
				} finally {
					instanceCounter.incrementAndGet();
				}
			}
		}
		return ListUtil.acquireLastObject(instanceList);
	}
	
	/**
	 * 启动垃圾清理处理器
	 */
	private void startGarbageCleanupProcessors() {
		for(int i = 0 ; i < garbageCleanupProcessors.length ; i ++) {
			garbageCleanupProcessors[i].start();
		}
	}
	
	/**
	 * 处理实例
	 * @param instance
	 */
	@SuppressWarnings("deprecation")
	public void handleInstance(JobInstanceSnapshot instance) {
		if(JOB_INSTANCE_STATUS_RETRY_OVER == instance.getStatus()) {
			this.delete4Instance(instance.getId());
			this.updateInstanceStatus(instance, JOB_INSTANCE_STATUS_DELETE_SELF);
			logger.info("[GarbageCleanup]: handleInstance JOB_INSTANCE_STATUS_RETRY_OVER, instance:" + instance);
			return ;
		}
		long count = 0L;
		TaskSnapshot query = new TaskSnapshot();
		query.setJobInstanceId(instance.getId());
		try {
			count = taskSnapshotAccess.queryCount4Cleanup(query);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: handleInstance queryCount4Cleanup error, query:" + query, e);
			return ;
		}
		
		if(count <= 0L) {
			this.delete4Instance(instance.getId());
			this.updateInstanceStatus(instance, JOB_INSTANCE_STATUS_DELETE_SELF);
			logger.info("[GarbageCleanup]: handleInstance JOB_INSTANCE_STATUS_DELETE_SELF, instance:" + instance);
		} else {
			this.delete4InstanceRetry(instance.getId());
			double timeInterval = START_INTERVAL_TIME + instance.getRetryCount() * START_INTERVAL_TIME * INCREASE_RATE;
			Date nextRetryTime = TimeUtil.increaseDate(new Date(), (long)timeInterval);
			this.updateInstanceNext(instance, JOB_INSTANCE_STATUS_RETRY, nextRetryTime);
			logger.info("[GarbageCleanup]: handleInstance updateInstanceNext"
					+ ", jobId:" + instance.getJobId() 
					+ ", jobInstanceId:" + instance.getId() 
					+ ", currentRetryCount:" + instance.getRetryCount() 
					+ ", timeInterval:" + timeInterval 
					+ ", nextRetryTime:" + nextRetryTime.toLocaleString() 
					+ ", currentStatus:" + instance.getStatus() 
					+ ", gmtModified:" + instance.getGmtModified().toLocaleString());
		}
	}
	
	/**
	 * 获取实例数量
	 * @return
	 */
	private long countInstance() {
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(JOB_INSTANCE_STATUS_FINISHED);
		statusList.add(JOB_INSTANCE_STATUS_FAILED);
		statusList.add(JOB_INSTANCE_STATUS_RETRY_FINISHED);
		statusList.add(JOB_INSTANCE_STATUS_RETRY_OVER);
		long result = 0;
		try {
			result = jobInstanceSnapshotAccess.queryDeleteCount(statusList);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: countInstance error, statusList:" + statusList, e);
		}
		return result;
	}
	
	/**
	 * 加载实例列表
	 * @param offset
	 * @return
	 */
	private List<JobInstanceSnapshot> loadInstanceList(long offset) {
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(JOB_INSTANCE_STATUS_FINISHED);
		statusList.add(JOB_INSTANCE_STATUS_FAILED);
		statusList.add(JOB_INSTANCE_STATUS_RETRY_FINISHED);
		statusList.add(JOB_INSTANCE_STATUS_RETRY_OVER);
		List<JobInstanceSnapshot> instanceList = null;
		try {
			instanceList = jobInstanceSnapshotAccess.queryAllInstanceList(statusList, offset, DEFAULT_PAGE_SIZE);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: loadInstanceList error, statusList:" + statusList + ", offset:" + offset, e);
		}
		return instanceList;
	}
	
	/**
	 * 删除所有数据
	 * @param jobInstanceId
	 */
	private void delete4Instance(long jobInstanceId) {
		List<Long> idList = acquireIdList(jobInstanceId);
		long deleteResult = 0L;
		while(! CollectionUtils.isEmpty(idList)) {
			deleteResult += this.delete4InstanceByIdList(jobInstanceId, idList);
			
//			try {
//				Thread.sleep(50L);
//			} catch (Throwable e) {
//				logger.error("[GarbageCleanup]: handleInstance delete4Instance error"
//				+ ", jobInstanceId:" + jobInstanceId 
//				+ ", deleteResult:" + deleteResult, e);
//			}
			
			idList = acquireIdList(jobInstanceId);
		}
		logger.info("[GarbageCleanup]: handleInstance delete4Instance"
				+ ", jobInstanceId:" + jobInstanceId 
				+ ", deleteResult:" + deleteResult);
	}
	
	/**
	 * 获取ID列表
	 * @param jobInstanceId
	 * @return
	 */
	private List<Long> acquireIdList(long jobInstanceId) {
		List<Long> idList = null;
		try {
			idList = taskSnapshotAccess.queryIdList(jobInstanceId);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: acquireIdList error"
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		return idList;
	}
	
	public List<TaskSnapshot> queryTaskSnapshotList(long jobInstanceId) {
		
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = taskSnapshotAccess.queryTaskSnapshotList(jobInstanceId);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: queryTaskSnapshotList error"
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		return taskSnapshotList;
	}
	
	/**
	 * 删除不需要重试的任务数据
	 * @param jobInstanceId
	 */
	private void delete4InstanceRetry(long jobInstanceId) {
		List<Long> idList = acquireIdListByRetryCount(jobInstanceId);
		long deleteResult = 0L;
		while(! CollectionUtils.isEmpty(idList)) {
			deleteResult += this.delete4InstanceByIdList(jobInstanceId, idList);
			idList = acquireIdListByRetryCount(jobInstanceId);
		}
		logger.info("[GarbageCleanup]: handleInstance delete4InstanceRetry"
				+ ", jobInstanceId:" + jobInstanceId + ", deleteResult:" + deleteResult);
	}
	
	/**
	 * 获取不需要重试的任务数据
	 * @param jobInstanceId
	 * @return
	 */
	private List<Long> acquireIdListByRetryCount(long jobInstanceId) {
		List<Long> idList = null;
		try {
			idList = taskSnapshotAccess.queryIdListByRetryCount(jobInstanceId);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: acquireIdListByRetryCount error"
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		return idList;
	}
	
	public List<TaskSnapshot> queryTaskSnapshotListByRetryCount(
			long jobInstanceId) {
		
		List<TaskSnapshot> taskSnapshotList = null;
		try {
			taskSnapshotList = taskSnapshotAccess.queryTaskSnapshotListByRetryCount(jobInstanceId);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: queryTaskSnapshotListByRetryCount error"
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		return taskSnapshotList;
	}
	
	/**
	 * 批量删除某个实例任务数据
	 * @param jobInstanceId
	 * @param idList
	 * @return
	 */
	private int delete4InstanceByIdList(long jobInstanceId, List<Long> idList) {
		int deleteResult = 0;
		for(Long id : idList) {
			try {
				TaskSnapshot taskSnapshot = new TaskSnapshot();
				taskSnapshot.setId(id);
				taskSnapshot.setJobInstanceId(jobInstanceId);
				deleteResult += taskSnapshotAccess.delete(taskSnapshot);
			} catch (Throwable e) {
				logger.error("[GarbageCleanup]: handleInstance delete4Instance error"
						+ ", jobInstanceId:" + jobInstanceId + ", id:" + id, e);
			}
		}
		
		return deleteResult;
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
			result = jobInstanceSnapshotAccess.updateInstanceStatus(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: updateInstanceStatus error"
					+ ", instance:" + instance + ", status:" + status, e);
		}
		if(result <= 0) {
			logger.error("[GarbageCleanup]: updateInstanceStatus failed"
					+ ", instance:" + instance + ", status:" + status);
		}
	}
	
	/**
	 * 更新下一次重试信息
	 * @param instance
	 * @param status
	 * @param nextRetryTime
	 */
	@SuppressWarnings("deprecation")
	private void updateInstanceNext(JobInstanceSnapshot instance, int status, Date nextRetryTime) {
		JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
		jobInstanceSnapshot.setId(instance.getId());
		jobInstanceSnapshot.setStatus(status);
		jobInstanceSnapshot.setNextRetryTime(nextRetryTime);
		int result = 0;
		try {
			result = jobInstanceSnapshotAccess.updateInstanceNext(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[GarbageCleanup]: updateInstanceNext error"
					+ ", instance:" + instance 
					+ ", status:" + status 
					+ ", nextRetryTime:" + nextRetryTime.toLocaleString(), e);
		}
		if(result <= 0) {
			logger.error("[GarbageCleanup]: updateInstanceNext failed"
					+ ", instance:" + instance 
					+ ", status:" + status 
					+ ", nextRetryTime:" + nextRetryTime.toLocaleString());
		}
	}
	
}
