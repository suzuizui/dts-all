package com.le.dts.client.executor.parallel.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.client.executor.job.processor.FailureJobProcessor;
import com.le.dts.client.executor.parallel.ParallelPool;
import com.le.dts.client.executor.parallel.unit.ExecutorUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.job.processor.ParallelJobProcessor;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.ProcessResult;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 任务处理器
 * @author tianyao.myc
 *
 */
public class ParallelTaskProcessor extends Thread implements Constants, ClientContext {

	private static final Log logger = LogFactory.getLog(ParallelTaskProcessor.class);
	
	/** 执行单元 */
	private ExecutorUnit executorUnit;
	
	/** 是否停止执行线程 */
	private volatile boolean stop = false;
	
	/** 状态 */
	private int status = TASK_PROCESSOR_STATUS_STOP;
	
	/** 线程计数器 */
	private AtomicInteger threadCounter;
	
	/** 并行计算上下文 */
	private ParallelJobContext context;
	
	//失败任务处理器上下文
	private FailureJobContext failureJobContext;
	
	//默认失败任务处理器
	private FailureJobProcessor failureJobProcessor = new DefaultFailureJobProcessor();
	
	public ParallelTaskProcessor(ExecutorUnit executorUnit, int index, AtomicInteger threadCounter) {
		this.executorUnit = executorUnit;
		super.setName(TASK_THREAD_NAME + executorUnit.getExecutableTask().getJob().getId() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJob().getJobProcessor() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getId() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getFireTime() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount()
				+ HORIZONTAL_LINE + index);
		this.threadCounter = threadCounter;
		this.context = new ParallelJobContext(this.executorUnit.getExecutableTask().getJob(), 
				this.executorUnit.getExecutableTask().getJobInstanceSnapshot(), 
				executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount());
		this.failureJobContext = new FailureJobContext(this.executorUnit.getExecutableTask().getJob(), 
				this.executorUnit.getExecutableTask().getJobInstanceSnapshot(), 
				executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount());
		
		String[] jobProcessorProperties = executorUnit.getExecutableTask().getJob().getJobProcessor().split(COLON);
		String jobProcessor = jobProcessorProperties[POSITION_PROCESSOR].trim();
		
		if(clientConfig.getFailureJobProcessorMap() != null 
				&& clientConfig.getFailureJobProcessorMap().get(jobProcessor) != null) {
			
			//初始化用户自定义失败任务处理器
			this.failureJobProcessor = clientConfig.getFailureJobProcessorMap().get(jobProcessor);
		}
		
		this.context.setAvailableMachineAmount(this.executorUnit.getExecutableTask().getAvailableMachineAmount());
		this.context.setCurrentMachineNumber(this.executorUnit.getExecutableTask().getCurrentMachineNumber());
	}
	
	/**
	 * 刷新线程信息
	 * @param executorUnit
	 * @param index
	 */
	public void refresh(ExecutorUnit executorUnit, int index) {
		this.executorUnit = executorUnit;
		super.setName(TASK_THREAD_NAME + executorUnit.getExecutableTask().getJob().getId() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJob().getJobProcessor() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getId() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getFireTime() 
				+ HORIZONTAL_LINE +  executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount()
				+ HORIZONTAL_LINE + index);
		
		this.context = new ParallelJobContext(this.executorUnit.getExecutableTask().getJob(), 
				this.executorUnit.getExecutableTask().getJobInstanceSnapshot(), 
				executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount());
		
		this.failureJobContext = new FailureJobContext(this.executorUnit.getExecutableTask().getJob(), 
				this.executorUnit.getExecutableTask().getJobInstanceSnapshot(), 
				executorUnit.getExecutableTask().getJobInstanceSnapshot().getRetryCount());
		
		String[] jobProcessorProperties = executorUnit.getExecutableTask().getJob().getJobProcessor().split(COLON);
		String jobProcessor = jobProcessorProperties[POSITION_PROCESSOR].trim();
		
		if(clientConfig.getFailureJobProcessorMap() != null 
				&& clientConfig.getFailureJobProcessorMap().get(jobProcessor) != null) {
			
			//初始化用户自定义失败任务处理器
			this.failureJobProcessor = clientConfig.getFailureJobProcessorMap().get(jobProcessor);
		}
	}
	
	@Override
	public void run() {
		try {
			/** 处理器准备 */
			ParallelJobProcessor parallelJobProcessor = null;
			try {
				parallelJobProcessor = jobProcessorFactory.createAndGetParallelJobProcessor(this.executorUnit.getExecutableTask().getJob(), false);
			} catch (Throwable e) {
				logger.error("[ParallelTaskProcessor]: createAndGetParallelJobProcessor error"
						+ ", jobProcessor:" + this.executorUnit.getExecutableTask().getJob().getJobProcessor(), e);
			}
			
			BlockingQueue<TaskSnapshot> queue = this.executorUnit.getQueue();
			while(! stop || ! queue.isEmpty()) {
				
				TaskSnapshot taskSnapshot = null;
				try {
					taskSnapshot = queue.poll(DEFAULT_POLL_TIMEOUT, TimeUnit.MILLISECONDS);
				} catch (Throwable e) {
					logger.error("[ParallelTaskProcessor]: take executableTask error"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
				}
				
				if(null == taskSnapshot) {
					continue ;
				}
				
				/** 执行任务 */
				executeTask(taskSnapshot, parallelJobProcessor);
			}
		} catch (Throwable e) {
			logger.error("[ParallelTaskProcessor]: run error"
					+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
		} finally {
			try {
				ParallelPool parallelPool = executorUnit.getParallelPool();
				parallelPool.stopTask(this.executorUnit.getExecutableTask().getJob().getId(), 
						this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
			} catch (Throwable e) {
				logger.error("[ParallelTaskProcessor]: finally stopTask error"
						+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId(), e);
			} finally {
				if(clientConfig.isFinishLog()) {
					logger.warn("[ParallelTaskProcessor]: finally stopTask"
							+ ", instanceId:" + this.executorUnit.getExecutableTask().getJobInstanceSnapshot().getId());
				}
			}
		}
	}

	/**
	 * 执行任务
	 * @param taskSnapshot
	 * @param parallelJobProcessor
	 */
	private void executeTask(TaskSnapshot taskSnapshot, ParallelJobProcessor parallelJobProcessor) {
		
		if(null == parallelJobProcessor) {
			
			logger.error("[ParallelTaskProcessor]: jobProcessor is null"
					+ ", please check " + this.executorUnit.getExecutableTask().getJob().getJobProcessor());
			
			//失败确认
			executor.acknowledge(taskSnapshot, TASK_STATUS_FAILURE, 0);
			return ;
		}
		
		/** 任务开始 计数器加一 */
		this.status = TASK_PROCESSOR_STATUS_RUNNING;
		this.threadCounter.incrementAndGet();
		
		try {
			/** 设置任务 */
			this.context.setTask(taskSnapshot);
			
			//初始化重试次数
			this.context.initRetryCount(taskSnapshot.getRetryCount());
			
			/** 设置任务 */
			this.failureJobContext.setTask(this.context.getTask());
			
			//初始化重试次数
			this.failureJobContext.initRetryCount(taskSnapshot.getRetryCount());
			
			ProcessResult processResult = null;
			try {
				processResult = parallelJobProcessor.process(this.context);
			} catch (Throwable e) {
				
				logger.error("[ParallelTaskProcessor]: process error"
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId(), e);
				
				this.failureJobContext.setE(e);//设置异常
				
			}
			
			if(null == processResult) {
				
				logger.error("[ParallelTaskProcessor]: process error, processResult is null"
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId());
				processResult = new ProcessResult(false);
				
				//设置异常
				if(null == this.failureJobContext.getE()) {
					this.failureJobContext.setE(new RuntimeException("processResult is null"));
				}
				
				//处理失败逻辑
				try {
					this.failureJobProcessor.process(this.failureJobContext);
				} catch (Throwable e) {
					logger.error("[ParallelTaskProcessor]: process failure job error"
							+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
							+ ", id:" + taskSnapshot.getId(), e);
				}
			}
			
			/** ACK确认 执行结果 */
			if(! stop) {
				
				/** 处理重试次数 */
				handleRetryCount(taskSnapshot, processResult);
				executor.acknowledge(taskSnapshot, processResult.isSuccess() ? TASK_STATUS_SUCCESS : TASK_STATUS_FAILURE, processResult.getRetryCount());
			}
		} catch (Throwable e) {
			logger.error("[ParallelTaskProcessor]: executeTask error"
					+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
					+ ", id:" + taskSnapshot.getId(), e);
		} finally {
			/** 任务结束 计数器减一 */
			this.threadCounter.decrementAndGet();
			this.status = TASK_PROCESSOR_STATUS_STOP;
		}
	}
	
	/**
	 * 处理重试次数
	 * @param taskSnapshot
	 * @param processResult
	 */
	private void handleRetryCount(TaskSnapshot taskSnapshot, ProcessResult processResult) {
		
		/** 如果是执行成功就不设置重试 */
		if(processResult.isSuccess()) {
			processResult.setRetryCount(0);
			return ;
		}
		
		/** 如果是补偿任务就重试次数减一 */
		if(this.executorUnit.getExecutableTask().isCompensation()) {
			if(taskSnapshot.getRetryCount() > 0) {
				processResult.setRetryCount(taskSnapshot.getRetryCount() - 1);
			} else {
				processResult.setRetryCount(0);
			}
			return ;
		}
		
		/** 不能超过最大重试次数 */
		if(processResult.getRetryCount() > MAX_RETRY_COUNT) {
			processResult.setRetryCount(MAX_RETRY_COUNT);
			return ;
		}
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public int getStatus() {
		return status;
	}

}
