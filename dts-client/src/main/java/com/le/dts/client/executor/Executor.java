package com.le.dts.client.executor;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.parallel.ParallelPool;
import com.le.dts.client.executor.simple.SimplePool;
import com.le.dts.client.executor.stop.StopJob;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.RandomUtil;

/**
 * 执行任务容器
 * @author tianyao.myc
 *
 */
public class Executor implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(Executor.class);
	
	/** 简单job执行池 */
	private SimplePool simplePool = new SimplePool();
	
	/** 并行计算job执行池 */
	private ParallelPool parallelPool = new ParallelPool();
	
	private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);
	
	private StopJob stopJob = new StopJob();
	
	/**
	 * 执行任务
	 * @param executableTask
	 * @return
	 */
	public Result<Boolean> executeTask(ExecutableTask executableTask) {
		Result<Boolean> result = new Result<Boolean>(false);
		boolean executeResult = false;
		if(CommonUtil.isSimpleJob(executableTask.getJob().getType())) {
			executeResult = simplePool.executeTask(executableTask);
		} else {
			executeResult = parallelPool.executeTask(executableTask);
		}
		result.setData(executeResult);
		result.setResultCode(executeResult ? ResultCode.SUCCESS : ResultCode.FAILURE);
		return result;
	}
	
	/**
	 * 停止任务
	 * @param jobType
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public Result<Boolean> stopTask(int jobType, long jobId, long jobInstanceId) {
		Result<Boolean> result = new Result<Boolean>(false);
		
		boolean stopResult = false;
		if(CommonUtil.isSimpleJob(jobType)) {
			stopResult = simplePool.stopTask(jobId, jobInstanceId);
		} else {
			stopResult = parallelPool.stopTask(jobId, jobInstanceId);
		}
		
		//停止任务
		this.stopJob.stopTask(jobId, jobInstanceId);
		
		result.setData(stopResult);
		result.setResultCode(stopResult ? ResultCode.SUCCESS : ResultCode.FAILURE);
		return result;
	}
	
	/**
	 * 强制停止任务
	 * @param executableTask
	 * @return
	 */
	public Result<Boolean> forceStopTask(ExecutableTask executableTask) {
		Result<Boolean> result = new Result<Boolean>(false);
		
		boolean stopResult = false;
		if(CommonUtil.isSimpleJob(executableTask.getJob().getType())) {
			stopResult = simplePool.forceStopTask(executableTask.getJob().getId(), executableTask.getJobInstanceSnapshot().getId());
		} else {
			stopResult = parallelPool.forceStopTask(executableTask.getJob().getId(), executableTask.getJobInstanceSnapshot().getId());
		}
		
		//停止任务
		this.stopJob.stopTask(executableTask.getJob().getId(), executableTask.getJobInstanceSnapshot().getId());
		
		result.setData(stopResult);
		result.setResultCode(stopResult ? ResultCode.SUCCESS : ResultCode.FAILURE);
		return result;
	}
	
	/**
	 * 拉取任务快照列表
	 * @param executableTask
	 * @return
	 */
	public Result<ExecutableTask> pull(ExecutableTask executableTask) {
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[Executor]: pull serverList is isEmpty error"
					+ ", instanceId:" + executableTask.getJobInstanceSnapshot().getId());
			return null;
		}
		
		InvocationContext.setRemoteMachine(new RemoteMachine(RandomUtil.getRandomObj(serverList), 12 * DEFAULT_INVOKE_TIMEOUT));
		return serverService.pull(executableTask);
	}
	
	/**
	 * ACK确认
	 * @param taskSnapshot
	 * @param status
	 * @param retryTimes
	 */
	public void acknowledge(TaskSnapshot taskSnapshot, int status, int retryTimes) {
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[Executor]: acknowledge serverList is isEmpty error"
					+ ", status:" + status 
					+ ", retryTimes:" + retryTimes 
					+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
					+ ", id:" + taskSnapshot.getId());
			return ;
		}
		
		/** 随机列表顺序 */
		Collections.shuffle(serverList);
		
		//如果clientId为空就补上
		if(StringUtils.isBlank(taskSnapshot.getClientId())) {
			taskSnapshot.setClientId(clientConfig.getClientId());
		}
		
		taskSnapshot.setStatus(status);
		taskSnapshot.setRetryCount(retryTimes);
		
		for(String server : serverList) {
			
			Result<Boolean> acknowledgeResult = null;
			try {
				InvocationContext.setRemoteMachine(new RemoteMachine(server, 12 * DEFAULT_INVOKE_TIMEOUT));
				acknowledgeResult = serverService.acknowledge(taskSnapshot);
			} catch (Throwable e) {
				logger.error("[Executor]: acknowledge error"
						+ ", status:" + status 
						+ ", retryTimes:" + retryTimes 
						+ ", server:" + server 
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId(), e);
			}
			
			if(null == acknowledgeResult || ! acknowledgeResult.getData().booleanValue()) {
				logger.error("[Executor]: acknowledge failed"
						+ ", status:" + status 
						+ ", retryTimes:" + retryTimes 
						+ ", server:" + server 
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId() 
						+ ", acknowledgeResult:" + acknowledgeResult);
				
				try {
					Thread.sleep(1000L);
				} catch (Throwable e) {
					logger.error("[Executor]: acknowledge sleep error"
							+ ", status:" + status 
							+ ", retryTimes:" + retryTimes 
							+ ", server:" + server 
							+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
							+ ", id:" + taskSnapshot.getId() 
							+ ", acknowledgeResult:" + acknowledgeResult, e);
				}
				
			} else {
				return ;
			}
		}
	}
	
	/**
	 * 批量任务ACK确认
	 * @param executableTask
	 * @param status
	 * @param retryTimes
	 */
	public void batchAcknowledge(ExecutableTask executableTask, int status, int retryTimes) {
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[Executor]: batchAcknowledge serverList is isEmpty error"
					+ ", status:" + status 
					+ ", retryTimes:" + retryTimes);
			return ;
		}
		
		/** 随机列表顺序 */
		Collections.shuffle(serverList);
		
		for(String server : serverList) {
			InvocationContext.setRemoteMachine(new RemoteMachine(server, 12 * DEFAULT_INVOKE_TIMEOUT));
			Result<Boolean> batchAcknowledgeResult = serverService.batchAcknowledge(
					executableTask,
                    status);
			if(null == batchAcknowledgeResult || ! batchAcknowledgeResult.getData().booleanValue()) {
				logger.error("[Executor]: batchAcknowledge failed"
						+ ", status:" + status 
						+ ", retryTimes:" + retryTimes 
						+ ", server:" + server);
			} else {
				return ;
			}
		}
	}

	/**
	 * 心跳检查任务状态
	 * @param jobType
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public Result<String> heartBeatCheckJobInstance(int jobType, long jobId, long jobInstanceId) {
		Result<String> result = new Result<String>();
		
		if(CommonUtil.isSimpleJob(jobType)) {
			result = simplePool.heartBeatCheckJobInstance(jobId, jobInstanceId);
		} else {
			result = parallelPool.heartBeatCheckJobInstance(jobId, jobInstanceId);
		}
		
		return result;
	}
	
	/**
	 * 推任务
	 * @param jobType
	 * @param jobId
	 * @param jobInstanceId
	 * @param taskSnapshot
	 * @return
	 */
	public Result<Boolean> push(int jobType, long jobId, long jobInstanceId, TaskSnapshot taskSnapshot) {
		
		if(! CommonUtil.isSimpleJob(jobType)) {
			
			//推送任务到执行队列
			return parallelPool.push(jobId, jobInstanceId, taskSnapshot);
			
		} else {
			return new Result<Boolean>(false, ResultCode.PUSH_JOB_TYPE_ERROR);
		}
		
	}
	
	public SimplePool getSimplePool() {
		return simplePool;
	}
	
}
