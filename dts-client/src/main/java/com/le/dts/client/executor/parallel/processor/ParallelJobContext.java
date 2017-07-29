package com.le.dts.client.executor.parallel.processor;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.job.context.JobContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.BytesUtil;
import com.le.dts.common.util.BytesUtil4Client;
import com.le.dts.common.util.RandomUtil;
import com.le.dts.common.util.StringUtil;

/**
 * 并行job上下文
 * @author tianyao.myc
 *
 */
public class ParallelJobContext extends JobContext implements Constants, ClientContext {

	private static final Log logger = LogFactory.getLog(ParallelJobContext.class);
	
	/** 当前要处理的任务快照 */
	private TaskSnapshot taskSnapshot;
	
	/** 当前要处理的任务 */
	private Object task;
	
	//可用的机器数量
	private int availableMachineAmount;
		
	//当前机器编号
	private int currentMachineNumber;
	
	private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);
	
	public ParallelJobContext(Job job, JobInstanceSnapshot jobInstanceSnapshot, int retryCount) {
		super(job, jobInstanceSnapshot, retryCount);
	}
	
	/**
	 * 初始化重试次数
	 * @param retryCount
	 */
	protected void initRetryCount(int retryCount) {
		super.setRetryCount(retryCount);
	}
	
	/**
	 * 设置任务
	 * @param taskSnapshot
	 */
	protected void setTask(TaskSnapshot taskSnapshot) {
		this.taskSnapshot = taskSnapshot;
		if(DEFAULT_ROOT_LEVEL_TASK_NAME.equals(taskSnapshot.getTaskName())) {
			if(BytesUtil.isEmpty(taskSnapshot.getBody())) {
				logger.error("[ParallelJobContext]: BytesUtil setTask bytesToObject error, body is empty" 
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId());
				return ;
			}
			try {
				this.task = BytesUtil.bytesToObject(taskSnapshot.getBody());
			} catch (Throwable e) {
				logger.error("[ParallelJobContext]: BytesUtil setTask bytesToObject error" 
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId(), e);
			}
		} else {
			if(BytesUtil4Client.isEmpty(taskSnapshot.getBody())) {
				logger.error("[ParallelJobContext]: BytesUtil4Client setTask bytesToObject error, body is empty" 
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId());
				return ;
			}
			try {
				this.task = BytesUtil4Client.bytesToObject(taskSnapshot.getBody());
			} catch (Throwable e) {
				logger.error("[ParallelJobContext]: BytesUtil4Client setTask bytesToObject error" 
						+ ", instanceId:" + taskSnapshot.getJobInstanceId() 
						+ ", id:" + taskSnapshot.getId(), e);
			}
		}
	}
	
	/**
	 * 分发任务列表
	 * @param taskList 任务列表
	 * @param taskName 任务名称
	 * @return
	 */
	public Result<Boolean> dispatchTaskList(List<? extends Object> taskList, String taskName) {
		Result<Boolean> result = new Result<Boolean>(false);
		
		if(StringUtil.isBlank(taskName)) {
			logger.error("[ParallelJobContext]: dispatchTaskList taskName is isEmpty error"
					+ ", jobId:" + job.getId());
			result.setResultCode(ResultCode.DISPATCH_TASK_LIST_NAME_IS_NULL);
			return result;
		}
		
		if(CollectionUtils.isEmpty(taskList)) {
			logger.warn("[ParallelJobContext]: dispatchTaskList taskList is empty"
					+ ", taskName:" + taskName 
					+ ", jobId:" + job.getId());
			result.setResultCode(ResultCode.DISPATCH_TASK_LIST_IS_EMPTY);
			return result;
		}
		
		if(taskList.size() > 3000) {
			throw new RuntimeException("taskList size too large, max:" + 3000 + ", but you set " + taskList.size());
		}
		
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[ParallelJobContext]: dispatchTaskList serverList is isEmpty error"
					+ ", taskName:" + taskName 
					+ ", jobId:" + job.getId());
			result.setResultCode(ResultCode.DISPATCH_TASK_LIST_SERVER_DOWN);
			return result;
		}
		
		ExecutableTask executableTask = new ExecutableTask(job, jobInstanceSnapshot);
		executableTask.setTaskSnapshot(this.taskSnapshot);
		for(Object task : taskList) {
			fillTaskSnapshot(executableTask, task, taskName);
		}
		
		InvocationContext.setRemoteMachine(new RemoteMachine(RandomUtil.getRandomObj(serverList), 12 * DEFAULT_INVOKE_TIMEOUT));
		Result<Boolean> sendResult = serverService.send(executableTask);
		if(null == sendResult) {
			logger.error("[ParallelJobContext]: dispatchTaskList send error"
					+ ", taskName:" + taskName 
					+ ", jobId:" + job.getId());
			result.setResultCode(ResultCode.DISPATCH_TASK_LIST_SERVER_DO_NOT_RESPONSE);
			return result;
		}
		
		result.setData(sendResult.getData());
		result.setResultCode(sendResult.getResultCode());
		return result;
	}

	/**
	 * 装填任务参数
	 * @param executableTask
	 * @param task
	 * @param taskName
	 */
	private void fillTaskSnapshot(ExecutableTask executableTask, Object task, String taskName) {
		byte[] body = null;
		try {
			body = BytesUtil4Client.objectToBytes(task);
		} catch (Throwable e) {
			logger.error("[ParallelJobContext]: fillTaskSnapshot objectToBytes error"
					+ ", taskName:" + taskName 
					+ ", jobId:" + job.getId() 
					+ ", task:" + task, e);
		}
		if(BytesUtil4Client.isEmpty(body)) {
			logger.error("[ParallelJobContext]: fillTaskSnapshot objectToBytes body is empty"
					+ ", taskName:" + taskName 
					+ ", jobId:" + job.getId() 
					+ ", task:" + task);
			return ;
		}

		if(body.length > clientConfig.getMaxBodySize()) {
			throw new RuntimeException("[ParallelJobContext]: single task is too large, more than 64KB");
		}

		TaskSnapshot taskSnapshot = new TaskSnapshot();
		taskSnapshot.setGmtCreate(new Date());
		taskSnapshot.setGmtModified(new Date());
		taskSnapshot.setJobInstanceId(executableTask.getJobInstanceSnapshot().getId());
		taskSnapshot.setJobProcessor(executableTask.getJob().getJobProcessor());
		taskSnapshot.setBody(body);
		taskSnapshot.setStatus(TASK_STATUS_INIT);
		taskSnapshot.setTaskName(taskName);
		taskSnapshot.setRetryCount(0);
		
		executableTask.addTaskSnapshot(taskSnapshot);
	}
	
	public Object getTask() {
		return task;
	}

	/**
	 * 获取自定义全局变量
	 * @return
	 */
	public String getGlobalArguments() {
		
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[ParallelJobContext]: getGlobalArguments serverList is isEmpty error"
					+ ", instanceId:" + jobInstanceSnapshot.getId());
			return null;
		}
		
		InvocationContext.setRemoteMachine(new RemoteMachine(RandomUtil.getRandomObj(serverList)));
		Result<String> body = serverService.getGlobalArguments(jobInstanceSnapshot);
		if(null == body) {
			logger.error("[ParallelJobContext]: getGlobalArguments body is null"
					+ ", instanceId:" + jobInstanceSnapshot.getId());
			return null;
		}
		
		return body.getData();
	}

	/**
	 * 设置自定义全局变量
	 * @param globalArguments
	 * @return
	 */
	public Result<Boolean> setGlobalArguments(String globalArguments) {
		Result<Boolean> result = new Result<Boolean>(false);
		if(StringUtil.isBlank(globalArguments)) {
			result.setResultCode(ResultCode.SET_GLOBAL_ARGUMENTS_NULL);
			return result;
		}
		
		List<String> serverList = clientRemoting.getServerList();
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[ParallelJobContext]: setGlobalArguments serverList is isEmpty error"
					+ ", instanceId:" + jobInstanceSnapshot.getId());
			result.setResultCode(ResultCode.SET_GLOBAL_SERVER_DOWN);
			return result;
		}
		
		InvocationContext.setRemoteMachine(new RemoteMachine(RandomUtil.getRandomObj(serverList)));
		Result<Boolean> setResult = serverService.setGlobalArguments(jobInstanceSnapshot, globalArguments);
		if(null == setResult) {
			result.setResultCode(ResultCode.SET_GLOBAL_FAILURE);
			return result;
		}
		
		result.setData(setResult.getData());
		result.setResultCode(setResult.getResultCode());
		return result;
	}

	public int getAvailableMachineAmount() {
		return availableMachineAmount;
	}

	protected void setAvailableMachineAmount(int availableMachineAmount) {
		this.availableMachineAmount = availableMachineAmount;
	}

	public int getCurrentMachineNumber() {
		return currentMachineNumber;
	}

	protected void setCurrentMachineNumber(int currentMachineNumber) {
		this.currentMachineNumber = currentMachineNumber;
	}
}
