package com.le.dts.client.executor.simple.processor;

import com.le.dts.client.executor.job.processor.SimpleJobProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.result.ProcessResult;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.logger.DtsLogger;

/**
 * 简单任务处理器
 * @author tianyao.myc
 *
 */
public class SimpleTaskProcessor extends Thread implements Constants, ClientContext {

	private static final Log logger = LogFactory.getLog(SimpleTaskProcessor.class);
	
	/** 可执行任务 */
	private ExecutableTask executableTask;
	
	/** 状态 */
	private int status = TASK_PROCESSOR_STATUS_STOP;
	
	/** 简单Job执行上下文 */
	private SimpleJobContext context;
	
	public SimpleTaskProcessor(ExecutableTask executableTask) {
		this.executableTask = executableTask;
		super.setName(TASK_THREAD_NAME + executableTask.getJob().getId() 
				+ HORIZONTAL_LINE + executableTask.getJob().getJobProcessor() 
				+ HORIZONTAL_LINE + executableTask.getJobInstanceSnapshot().getId() 
				+ HORIZONTAL_LINE + executableTask.getJobInstanceSnapshot().getFireTime() 
				+ HORIZONTAL_LINE + executableTask.getJobInstanceSnapshot().getRetryCount());
		this.context = new SimpleJobContext(executableTask.getJob(), executableTask.getJobInstanceSnapshot(), 
				executableTask.getJobInstanceSnapshot().getRetryCount());
		
		this.context.setAvailableMachineAmount(executableTask.getAvailableMachineAmount());
		this.context.setCurrentMachineNumber(executableTask.getCurrentMachineNumber());
	}
	
	@Override
	public void run() {
		
		/** 任务开始 计数器加一 */
		this.status = TASK_PROCESSOR_STATUS_RUNNING;
		
		if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
			DtsLogger.info(executableTask.getJob().getId(), 
					executableTask.getJobInstanceSnapshot().getId(), "task init start ...");
		}
		
		try {
			
			/** 处理器准备 */
			SimpleJobProcessor simpleJobProcessor = null;
			try {
				simpleJobProcessor = jobProcessorFactory.createAndGetSimpleJobProcessor(executableTask.getJob(), false);
			} catch (Throwable e) {
				
				logger.error("[SimpleTaskProcessor]: createAndGetSimpleJobProcessor error"
						+ ", jobProcessor:" + executableTask.getJob().getJobProcessor(), e);
				
				if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
					DtsLogger.info(executableTask.getJob().getId(), 
							executableTask.getJobInstanceSnapshot().getId(), "createAndGetSimpleJobProcessor error"
							+ ", jobProcessor:" + executableTask.getJob().getJobProcessor(), e);
				}
			}
			
			if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
				DtsLogger.info(executableTask.getJob().getId(), 
						executableTask.getJobInstanceSnapshot().getId(), "task execute start ...");
			}
			
			/** 执行任务 */
			executeTask(this.executableTask, simpleJobProcessor);
		} catch (Throwable e) {
			logger.error("[SimpleTaskProcessor]: executeTask error"
					+ ", instanceId:" + this.executableTask.getJobInstanceSnapshot().getId(), e);
		} finally {
			this.status = TASK_PROCESSOR_STATUS_STOP;
			
			/** 删除处理器 */
			executor.getSimplePool().removeTask(this.executableTask);
		}
		
	}

	/**
	 * 执行任务
	 * @param executableTask
	 * @param simpleJobProcessor
	 */
	private void executeTask(ExecutableTask executableTask, SimpleJobProcessor simpleJobProcessor) {

		TaskSnapshot taskSnapshot = executableTask.getTaskSnapshot();
		
		if(null == simpleJobProcessor) {
			
			logger.error("[SimpleTaskProcessor]: jobProcessor is null"
					+ ", please check " + executableTask.getJob().getJobProcessor());
			
			if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
				DtsLogger.info(executableTask.getJob().getId(), 
						executableTask.getJobInstanceSnapshot().getId(), "jobProcessor is null"
								+ ", please check " + executableTask.getJob().getJobProcessor());
			}
			
			//失败确认
			executor.acknowledge(taskSnapshot, TASK_STATUS_FAILURE, 0);
			return ;
		}
		
		/** 设置任务 */
		this.context.setTask(taskSnapshot);
		
		ProcessResult processResult = null;
		try {
			processResult = simpleJobProcessor.process(this.context);
		} catch (Throwable e) {
			logger.error("[SimpleTaskProcessor]: process error"
					+ ", instanceId:" + executableTask.getJobInstanceSnapshot().getId(), e);
		}
		if(null == processResult) {
			logger.error("[SimpleTaskProcessor]: process error, processResult is null"
					+ ", instanceId:" + executableTask.getJobInstanceSnapshot().getId());
			processResult = new ProcessResult(false);
		}
		
		/** ACK确认 执行结果 */
		handleRetryCount(taskSnapshot, processResult);
		executor.acknowledge(taskSnapshot, processResult.isSuccess() ? TASK_STATUS_SUCCESS : TASK_STATUS_FAILURE, processResult.getRetryCount());
		
		if(Constants.ENVIRONMENT_JST.equals(clientConfig.getEnvironment())) {
			DtsLogger.info(executableTask.getJob().getId(), 
					executableTask.getJobInstanceSnapshot().getId(), "task execute end, processResult:" + processResult);
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
		if(this.executableTask.isCompensation()) {
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
	
	public int getStatus() {
		return status;
	}

}
