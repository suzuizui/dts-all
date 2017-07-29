package com.le.dts.client.executor.parallel.unit;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.parallel.ParallelPool;
import com.le.dts.client.executor.parallel.processor.ParallelTaskProcessor;
import com.le.dts.client.executor.parallel.processor.PullProcessor;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.InitException;

/**
 * 执行单元
 * @author tianyao.myc
 *
 */
public class ExecutorUnit implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(ExecutorUnit.class);
	
	/** 执行单元实例标识 */
	private ExecutableTask executableTask;
	
	/** 拉任务快照线程 */
	private PullProcessor pullProcessor = null;
	
	/** 任务队列 */
	private BlockingQueue<TaskSnapshot> queue = null;
	
	/** 任务处理器线程组 */
	private ParallelTaskProcessor[] parallelTaskProcessors = null;
	
	/** 线程计数器 */
	private final AtomicInteger threadCounter = new AtomicInteger();
	
	private final ParallelPool parallelPool;
	
	public ExecutorUnit(ParallelPool parallelPool, ExecutableTask executableTask) {
		this.parallelPool = parallelPool;
		this.executableTask = executableTask;
		
		int pageSize = clientConfig.getPageSize();
		Map<String, Integer> pageSizeMap = clientConfig.getPageSizeMap();
		if(! CollectionUtils.isEmpty(pageSizeMap) && pageSizeMap.get(executableTask.getJob().getJobProcessor()) != null) {
			pageSize = clientConfig.checkPageSize(pageSizeMap.get(executableTask.getJob().getJobProcessor()).intValue());
		}
		this.executableTask.setLength(pageSize);
	}
	
	/**
	 * 刷新执行单元信息
	 * @param executableTask
	 */
	public void refresh(ExecutableTask executableTask) {
		this.executableTask = executableTask;
		
		int pageSize = clientConfig.getPageSize();
		Map<String, Integer> pageSizeMap = clientConfig.getPageSizeMap();
		if(! CollectionUtils.isEmpty(pageSizeMap) && pageSizeMap.get(executableTask.getJob().getJobProcessor()) != null) {
			pageSize = clientConfig.checkPageSize(pageSizeMap.get(executableTask.getJob().getJobProcessor()).intValue());
		}
		this.executableTask.setLength(pageSize);
		
		for(int i = 0 ; i < this.parallelTaskProcessors.length ; i ++) {
			this.parallelTaskProcessors[i].refresh(this, i);
		}
		
		this.pullProcessor.refresh(this);
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化拉任务快照线程 */
		this.pullProcessor = new PullProcessor(this);
		
		/** 初始化任务队列 */
		this.queue = new LinkedBlockingQueue<TaskSnapshot>(clientConfig.getQueueSize());
		
		this.pullProcessor.start();
		
		int consumerThreads = clientConfig.getConsumerThreads();
		Map<String, Integer> consumerThreadsMap = clientConfig.getConsumerThreadsMap();
		if(! CollectionUtils.isEmpty(consumerThreadsMap) && consumerThreadsMap.get(executableTask.getJob().getJobProcessor()) != null) {
			consumerThreads = clientConfig.checkConsumerThreads(consumerThreadsMap.get(executableTask.getJob().getJobProcessor()).intValue());
		}
		
		if(executableTask.getRunThreads() > 0) {
			consumerThreads = executableTask.getRunThreads();
		}
		
		/** 初始化任务处理线程组 */
		this.parallelTaskProcessors = new ParallelTaskProcessor[consumerThreads];
		for(int i = 0 ; i < consumerThreads ; i ++) {
			this.parallelTaskProcessors[i] = new ParallelTaskProcessor(this, i, this.threadCounter);
			this.parallelTaskProcessors[i].start();
		}
	}

	/**
	 * 清空队列
	 */
	public void clear() {
		try {
			this.queue.clear();
		} catch (Throwable e) {
			logger.error("[ExecutorUnit]: clear error"
					+ ", instanceId:" + this.executableTask.getJobInstanceSnapshot().getId(), e);
		}
	}
	
	/**
	 * 停止任务
	 */
	public void stopTask() {
		
		/** 强制停止拉取线程 */
		pullProcessor.setStop(true);
		
		/** 强制停止执行任务线程 */
		for(int i = 0 ; i < this.parallelTaskProcessors.length ; i ++) {
			this.parallelTaskProcessors[i].setStop(true);
		}
	}
	
	/**
	 * 强制停止
	 */
	@SuppressWarnings("deprecation")
	public void forceStopTask() {
		
		try {
			pullProcessor.stop();
		} catch (Throwable e) {
			logger.error("[ExecutorUnit]: forceStopTask pullProcessor error"
					+ ", instanceId:" + this.executableTask.getJobInstanceSnapshot().getId(), e);
		}

		//清空队列
		clear();
		
		/** 强制停止执行任务线程 */
		for(int i = 0 ; i < this.parallelTaskProcessors.length ; i ++) {
			try {
				this.parallelTaskProcessors[i].stop();
			} catch (Throwable e) {
				logger.error("[ExecutorUnit]: forceStopTask parallelTaskProcessors error"
						+ ", instanceId:" + this.executableTask.getJobInstanceSnapshot().getId(), e);
			}
		}
		
	}
	
	/**
	 * 执行器是否停止
	 * @return
	 */
	public boolean isExecutorStop() {
		return queue.isEmpty() && (threadCounter.get() == 0);
	}
	
	/**
	 * 将任务放入队列
	 * @param taskSnapshot
	 * @return
	 */
	public boolean offer(TaskSnapshot taskSnapshot) {
		
		boolean result = false;
		try {
			result = queue.offer(taskSnapshot, DEFAULT_INVOKE_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			logger.error("[ExecutorUnit]: offer error"
					+ ", jobInstanceId:" + taskSnapshot.getJobInstanceId() 
					+ ", id:" + taskSnapshot.getId(), e);
		}
		
		return result;
	}
	
	public ExecutableTask getExecutableTask() {
		return executableTask;
	}

	public BlockingQueue<TaskSnapshot> getQueue() {
		return queue;
	}

	public ParallelTaskProcessor[] getParallelTaskProcessors() {
		return parallelTaskProcessors;
	}

	public AtomicInteger getThreadCounter() {
		return threadCounter;
	}

	public ParallelPool getParallelPool() {
		return parallelPool;
	}

	@Override
	public String toString() {
		return "ExecutorUnit [executableTask=" + executableTask + "]";
	}

}
