package com.le.dts.console.gc;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.console.gc.timer.JobInstanceSnapshotCleanupTimer;
import com.le.dts.console.manager.JobManager;
import com.le.dts.console.store.JobInstanceSnapshotAccess;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.console.gc.processor.JobInstanceSnapshotCleanupProcessor;

/**
 * 实例记录清除
 * @author tianyao.myc
 *
 */
public class JobInstanceSnapshotCleanup implements Constants {

	private static final Log logger = LogFactory.getLog(JobInstanceSnapshotCleanup.class);
	
	/** 处理线程数量 */
	private static final int THREAD_AMOUNT = 10;
	
	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, "DTS-jobInstanceSnapshotCleanup-thread");
				}
				
			});
	
	/** 实例记录清理处理器线程组 */
	private JobInstanceSnapshotCleanupProcessor[] jobInstanceSnapshotCleanupProcessors = new JobInstanceSnapshotCleanupProcessor[THREAD_AMOUNT];
	
	/** 执行状态 */
	private int status = STATUS_STOP;
	
	private Zookeeper zookeeper;
	
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	@Autowired
	private JobManager jobManager;
	
	public void setZookeeper(Zookeeper zookeeper) {
		this.zookeeper = zookeeper;
	}
	
	public void setJobInstanceSnapshotAccess(
			JobInstanceSnapshotAccess jobInstanceSnapshotAccess) {
		this.jobInstanceSnapshotAccess = jobInstanceSnapshotAccess;
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化job实例记录清理定时器 */
		initJobInstanceSnapshotCleanupTimer();
		
	}
	
	/**
	 * 初始化job实例记录清理定时器
	 * @throws InitException
	 */
	private void initJobInstanceSnapshotCleanupTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new JobInstanceSnapshotCleanupTimer(this),
					0L, 60 * 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[JobInstanceSnapshotCleanup]: initJobInstanceSnapshotCleanupTimer error", e);
		}
		logger.info("[JobInstanceSnapshotCleanup]: initJobInstanceSnapshotCleanupTimer success");
	}
	
	/**
	 * 开始清理
	 */
	public void start() {
		
		if(STATUS_RUNNING == this.status) {
			logger.warn("[JobInstanceSnapshotCleanup]: start jobInstanceSnapshotCleanup task is running, so return");
			return ;
		}
		
		this.status = STATUS_RUNNING;
		logger.info("[JobInstanceSnapshotCleanup]: start... ");
		
		List<String> consoleIpList = zookeeper.getConsoleIpList();
		if(CollectionUtils.isEmpty(consoleIpList)) {
			logger.error("[JobInstanceSnapshotCleanup]: start consoleIpList is empty");
			this.status = STATUS_STOP;
			logger.info("[JobInstanceSnapshotCleanup]: end... \n\n\n\n");
			return ;
		}
		
		final int consoleAmount = consoleIpList.size();
		final int consoleNumber = consoleIpList.indexOf(RemotingUtil.getLocalAddress());
		if(consoleNumber < 0 || consoleNumber >= consoleAmount) {
			logger.error("[JobInstanceSnapshotCleanup]: start consoleNumber error"
					+ ", consoleAmount:" + consoleAmount 
					+ ", consoleNumber:" + consoleNumber 
					+ ", consoleIpList:" + consoleIpList);
			this.status = STATUS_STOP;
			logger.info("[JobInstanceSnapshotCleanup]: end... \n\n\n\n");
			return ;
		}
		
		final List<Long> jobIdList = loadAllJobIdList();
		if(CollectionUtils.isEmpty(jobIdList)) {
			logger.warn("[JobInstanceSnapshotCleanup]: start jobIdList is empty"
					+ ", consoleAmount:" + consoleAmount 
					+ ", consoleNumber:" + consoleNumber 
					+ ", consoleIpList:" + consoleIpList);
			this.status = STATUS_STOP;
			logger.info("[JobInstanceSnapshotCleanup]: end... \n\n\n\n");
			return ;
		}
		
		/** 初始化线程计数器 */
		final CountDownLatch threadCount = 
				new CountDownLatch(THREAD_AMOUNT);
		final BlockingQueue<Long> queue = 
				new LinkedBlockingQueue<Long>(QUEUE_SIZE);
		/** 结束标记初始化 */
		final AtomicBoolean endTag = new AtomicBoolean(false);
		
		/** 初始化实例记录清理处理器 */
		initJobInstanceSnapshotCleanupProcessors(this, threadCount, queue, endTag);
		
		final AtomicInteger jobCounter = new AtomicInteger(0);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					for(Long jobId : jobIdList) {
						/** 取莫计算当前console处理的jobId */
						if(consoleNumber == (jobId % consoleAmount)) {
							try {
								queue.put(jobId);
							} catch (Throwable e) {
								logger.error("[JobInstanceSnapshotCleanup]: put jobId error, jobId:" + jobId, e);
							} finally {
								jobCounter.incrementAndGet();
							}
						}
					}
				} catch (Throwable e) {
					logger.error("[JobInstanceSnapshotCleanup]: start handleJobIdList error"
							+ ", consoleAmount:" + consoleAmount 
							+ ", consoleNumber:" + consoleNumber 
							+ ", jobIdList:" + jobIdList, e);
				} finally {
					endTag.set(true);
					logger.info("[JobInstanceSnapshotCleanup]: start handleJobIdList over");
				}
			}
			
		}).start();
		
		/** 启动实例记录清理处理器 */
		startJobInstanceSnapshotCleanupProcessors();
		
		/** 等待任务分发完成 */
		try {
			threadCount.await();
		} catch (Exception e) {
			logger.error("[JobInstanceSnapshotCleanup]: threadCount await error", e);
		}
		this.status = STATUS_STOP;
		logger.info("[JobInstanceSnapshotCleanup]: jobCounter:" + jobCounter.get() + " end... \n\n\n\n");
	}
	
	/**
	 * 初始化实例记录清理处理器
	 * @param jobInstanceSnapshotCleanup
	 * @param threadCount
	 * @param queue
	 * @param endTag
	 */
	private void initJobInstanceSnapshotCleanupProcessors(JobInstanceSnapshotCleanup jobInstanceSnapshotCleanup, 
			CountDownLatch threadCount, BlockingQueue<Long> queue, AtomicBoolean endTag) {
		for(int i = 0 ; i < jobInstanceSnapshotCleanupProcessors.length ; i ++) {
			jobInstanceSnapshotCleanupProcessors[i] = new JobInstanceSnapshotCleanupProcessor(jobInstanceSnapshotCleanup, threadCount, queue, endTag, i);
		}
	}
	
	/**
	 * 启动实例记录清理处理器
	 */
	private void startJobInstanceSnapshotCleanupProcessors() {
		for(int i = 0 ; i < jobInstanceSnapshotCleanupProcessors.length ; i ++) {
			jobInstanceSnapshotCleanupProcessors[i].start();
		}
	}
	
	/**
	 * 处理JobId
	 * @param jobId
	 * @param instanceCounter
	 */
	public void handleJobId(Long jobId, final AtomicLong instanceCounter) {
		
		boolean all = false;
		
		Job job = jobManager.getJobById(jobId);
		if(null == job) {
			//如果job不存在就全部删除
			all = true;
		}
		
		List<Long> jobInstanceIdList = loadJobInstanceIdList(jobId, all);
		while(! CollectionUtils.isEmpty(jobInstanceIdList)) {
			
			int deleteResult = deleteInstanceList(jobInstanceIdList);
			
			instanceCounter.addAndGet(deleteResult);
			
			try {
				Thread.sleep(500L);
			} catch (Throwable e) {
				logger.error("[JobInstanceSnapshotCleanup]: sleep error"
						+ ", jobId:" + jobId, e);
			}
			
			jobInstanceIdList = loadJobInstanceIdList(jobId, all);
		}
	}
	
	/**
	 * 加载要删除的实例记录ID列表
	 * @param jobId
	 * @param all
	 * @return
	 */
	private List<Long> loadJobInstanceIdList(Long jobId, boolean all) {
		List<Long> jobInstanceIdList = null;
		JobInstanceSnapshot query = new JobInstanceSnapshot();
		query.setJobId(jobId);
		try {
			if(all) {
				jobInstanceIdList = jobInstanceSnapshotAccess.queryInstanceIdList4DeleteAllInstanceByJobId(query);
			} else {
				jobInstanceIdList = jobInstanceSnapshotAccess.queryInstanceIdList4DeleteByJobId(query);
			}
		} catch (Throwable e) {
			logger.error("[JobInstanceSnapshotCleanup]: queryInstanceIdList4DeleteByJobId error, jobId:" + jobId, e);
		}
		return jobInstanceIdList;
	}
	
	/**
	 * 加载所有JobId列表
	 * @return
	 */
	private List<Long> loadAllJobIdList() {
		List<Long> jobIdList = null;
		try {
			jobIdList = jobInstanceSnapshotAccess.queryAllJobIdList();
		} catch (Throwable e) {
			logger.error("[JobInstanceSnapshotCleanup]: queryAllJobIdList error", e);
		}
		return jobIdList;
	}
	
	/**
	 * 删除实例列表
	 * @param idList
	 * @return
	 */
	private int deleteInstanceList(List<Long> idList) {
		int deleteResult = 0;
		try {
			deleteResult = jobInstanceSnapshotAccess.deleteInstanceByIdList(idList);
		} catch (Throwable e) {
			logger.error("[JobInstanceSnapshotCleanup]: deleteInstanceByIdList error, idList:" + idList, e);
		}
		return deleteResult;
	}
	
}
