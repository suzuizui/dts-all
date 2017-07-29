package com.le.dts.client.executor.simple;

import java.util.concurrent.ConcurrentHashMap;

import com.le.dts.client.executor.simple.processor.SimpleTaskProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;

/**
 * 简单job执行池
 * @author tianyao.myc
 *
 */
public class SimplePool {

	private static final Log logger = LogFactory.getLog(SimplePool.class);
	
	/** 简单Job映射表 */
	private ConcurrentHashMap<Long, ConcurrentHashMap<Long, SimpleTaskProcessor>> simpleJobTable =
			new ConcurrentHashMap<Long, ConcurrentHashMap<Long, SimpleTaskProcessor>>();
	
	/**
	 * 执行任务
	 * @param executableTask
	 * @return
	 */
	public boolean executeTask(ExecutableTask executableTask) {
		try {
			ConcurrentHashMap<Long, SimpleTaskProcessor> instanceTable = this.simpleJobTable.get(executableTask.getJob().getId());
			if(null == instanceTable) {
				instanceTable = new ConcurrentHashMap<Long, SimpleTaskProcessor>();
				this.simpleJobTable.put(executableTask.getJob().getId(), instanceTable);
			}
			SimpleTaskProcessor simpleTaskProcessor = new SimpleTaskProcessor(executableTask);
			simpleTaskProcessor.start();
			instanceTable.put(executableTask.getJobInstanceSnapshot().getId(), simpleTaskProcessor);
		} catch (Throwable e) {
			logger.error("[SimplePool]: executeTask error, instanceId:" + executableTask.getJobInstanceSnapshot().getId(), e);
			return false;
		}
		return true;
	}
	
	/**
	 * 获取运行中实例数量
	 * @param job
	 * @return
	 */
	public int getInstanceAmount(Job job) {
		ConcurrentHashMap<Long, SimpleTaskProcessor> instanceTable = this.simpleJobTable.get(job.getId());
		if(null == instanceTable) {
			return 0;
		}
		return instanceTable.size();
	}
	
	/**
	 * 停止任务
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public boolean stopTask(long jobId, long jobInstanceId) {
		ConcurrentHashMap<Long, SimpleTaskProcessor> instanceTable = this.simpleJobTable.get(jobId);
		if(null == instanceTable) {
			return true;
		}
		SimpleTaskProcessor simpleTaskProcessor = instanceTable.get(jobInstanceId);
		if(null == simpleTaskProcessor) {
			return true;
		}
		
		/** 删除任务 */
		try {
			Job job = new Job();
			job.setId(jobId);
			JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
			jobInstanceSnapshot.setId(jobInstanceId);
			this.removeTask(new ExecutableTask(job, jobInstanceSnapshot));
		} catch (Throwable e) {
			logger.error("[SimplePool]: stopTask removeTask error, jobId:" + jobId + ", jobInstanceId:" + jobInstanceId, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 强制停止任务
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean forceStopTask(long jobId, long jobInstanceId) {
		ConcurrentHashMap<Long, SimpleTaskProcessor> instanceTable = this.simpleJobTable.get(jobId);
		if(null == instanceTable) {
			return true;
		}
		SimpleTaskProcessor simpleTaskProcessor = instanceTable.get(jobInstanceId);
		if(null == simpleTaskProcessor) {
			return true;
		}
		
		try {
			simpleTaskProcessor.stop();
		} catch (Throwable e) {
			logger.error("[SimplePool]: forceStopTask error"
					+ ", jobId:" + jobId 
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		
		/** 删除任务 */
		try {
			Job job = new Job();
			job.setId(jobId);
			JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
			jobInstanceSnapshot.setId(jobInstanceId);
			this.removeTask(new ExecutableTask(job, jobInstanceSnapshot));
		} catch (Throwable e) {
			logger.error("[SimplePool]: forceStopTask removeTask error"
					+ ", jobId:" + jobId 
					+ ", jobInstanceId:" + jobInstanceId, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 删除任务
	 * @param executableTask
	 */
	public void removeTask(ExecutableTask executableTask) {
		try {
			ConcurrentHashMap<Long, SimpleTaskProcessor> instanceTable = this.simpleJobTable.get(executableTask.getJob().getId());
			if(null == instanceTable) {
				logger.warn("[SimplePool]: removeTask warn instanceTable is null, instanceId:" + executableTask.getJobInstanceSnapshot().getId());
				return ;
			}
			SimpleTaskProcessor simpleTaskProcessor = instanceTable.get(executableTask.getJobInstanceSnapshot().getId());
			if(null == simpleTaskProcessor) {
				logger.warn("[SimplePool]: removeTask warn simpleTaskProcessor is null, instanceId:" + executableTask.getJobInstanceSnapshot().getId());
				return ;
			}
			/** 删除Job实例 */
			instanceTable.remove(executableTask.getJobInstanceSnapshot().getId());
		} catch (Throwable e) {
			logger.error("[SimplePool]: removeTask error, instanceId:" + executableTask.getJobInstanceSnapshot().getId(), e);
		}
	}

	/**
	 * 心跳检查任务状态
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public Result<String> heartBeatCheckJobInstance(long jobId, long jobInstanceId) {
		Result<String> result = new Result<String>();
		ConcurrentHashMap<Long, SimpleTaskProcessor> instanceTable = this.simpleJobTable.get(jobId);
		if(null == instanceTable) {
			result.setResultCode(ResultCode.HEART_BEAT_CHECK_FAILURE);
			return result;
		}
		SimpleTaskProcessor simpleTaskProcessor = instanceTable.get(jobInstanceId);
		if(null == simpleTaskProcessor) {
			result.setResultCode(ResultCode.HEART_BEAT_CHECK_FAILURE);
			return result;
		}
		result.setResultCode(ResultCode.HEART_BEAT_CHECK_SUCCESS);
		return result;
	}
	
	public ConcurrentHashMap<Long, ConcurrentHashMap<Long, SimpleTaskProcessor>> getSimpleJobTable() {
		return simpleJobTable;
	}
	
}
