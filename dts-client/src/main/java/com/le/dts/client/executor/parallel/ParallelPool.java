package com.le.dts.client.executor.parallel;

import java.util.concurrent.ConcurrentHashMap;

import com.le.dts.client.executor.parallel.unit.ExecutorUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 并行计算job执行池
 * @author tianyao.myc
 *
 */
public class ParallelPool implements ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(ParallelPool.class);
	
	/** 任务执行单元映射表 */
	private ConcurrentHashMap<Long, ConcurrentHashMap<Long, ExecutorUnit>> executorUnitTable =
			new ConcurrentHashMap<Long, ConcurrentHashMap<Long, ExecutorUnit>>();
	
	/**
	 * 执行任务
	 * @param executableTask
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean executeTask(ExecutableTask executableTask) {
		ConcurrentHashMap<Long, ExecutorUnit> executorUnitMap = this.executorUnitTable.get(executableTask.getJob().getId());
		if(null == executorUnitMap) {
			executorUnitMap = new ConcurrentHashMap<Long, ExecutorUnit>();
			this.executorUnitTable.put(executableTask.getJob().getId(), executorUnitMap);
		}
		
//		if(START_POLICY_SINGLE_INSTANCE != executableTask.getJob().getMaxInstanceAmount() || executorUnitMap.isEmpty()) {
			
			ExecutorUnit executorUnit = executorUnitMap.get(executableTask.getJobInstanceSnapshot().getId());
			if(null == executorUnit) {
				executorUnit = new ExecutorUnit(this, executableTask);
				try {
					executorUnit.init();
				} catch (Throwable e) {
					logger.error("[ParallelPool]: executeTask init error"
							+ ", instanceId:" + executableTask.getJobInstanceSnapshot().getId(), e);
					return false;
				}
				executorUnitMap.put(executableTask.getJobInstanceSnapshot().getId(), executorUnit);
			}
			
//			return true;
//		}
		
//		Entry<Long, ExecutorUnit>[] entrys = (Entry<Long, ExecutorUnit>[])executorUnitMap.entrySet().toArray();
//		
//		Long instanceId = (Long)entrys[0].getKey();
//		ExecutorUnit executorUnit = (ExecutorUnit)entrys[0].getValue();
//		
//		try {
//			
//			//刷新执行单元信息
//			executorUnit.refresh(executableTask);
//		} catch (Throwable e) {
//			
//			logger.error("[ParallelPool]: executeTask refresh error"
//					+ ", executableTask:" + executableTask 
//					+ ", instanceId:" + instanceId, e);
//			
//			return false;
//		}
			
		return true;
	}
	
	/**
	 * 停止任务
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public boolean stopTask(long jobId, long jobInstanceId) {
		ConcurrentHashMap<Long, ExecutorUnit> executorUnitMap = this.executorUnitTable.get(jobId);
		if(null == executorUnitMap || executorUnitMap.isEmpty()) {
			return true;
		}
		ExecutorUnit executorUnit = executorUnitMap.get(jobInstanceId);
		if(null == executorUnit) {
			return true;
		}
		
		/** 停止任务执行单元 */
		executorUnit.stopTask();
		
		/** 删除任务执行单元 */
		try {
			executorUnitMap.remove(jobInstanceId);
		} catch (Throwable e) {
			logger.error("[ParallelPool]: stopTask remove error"
					+ ", jobId:" + jobId 
					+ ", jobInstanceId:" + jobInstanceId, e);
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
	public boolean forceStopTask(long jobId, long jobInstanceId) {
		ConcurrentHashMap<Long, ExecutorUnit> executorUnitMap = this.executorUnitTable.get(jobId);
		if(null == executorUnitMap || executorUnitMap.isEmpty()) {
			return true;
		}
		ExecutorUnit executorUnit = executorUnitMap.get(jobInstanceId);
		if(null == executorUnit) {
			return true;
		}
		
		/** 强制停止任务执行单元 */
		executorUnit.forceStopTask();
		
		/** 删除任务执行单元 */
		try {
			executorUnitMap.remove(jobInstanceId);
		} catch (Throwable e) {
			logger.error("[ParallelPool]: forceStopTask remove error"
					+ ", jobId:" + jobId 
					+ ", jobInstanceId:" + jobInstanceId, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 心跳检查任务状态
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public Result<String> heartBeatCheckJobInstance(long jobId, long jobInstanceId) {
		Result<String> result = new Result<String>();
		ConcurrentHashMap<Long, ExecutorUnit> executorUnitMap = this.executorUnitTable.get(jobId);
		if(null == executorUnitMap || executorUnitMap.isEmpty()) {
			result.setResultCode(ResultCode.HEART_BEAT_CHECK_EXIT);
			return result;
		}
		ExecutorUnit executorUnit = executorUnitMap.get(jobInstanceId);
		if(null == executorUnit) {
			result.setResultCode(ResultCode.HEART_BEAT_CHECK_EXIT);
			return result;
		}
		if(executorUnit.isExecutorStop()) {
			result.setResultCode(ResultCode.HEART_BEAT_CHECK_FAILURE);
			return result;
		}
		result.setResultCode(ResultCode.HEART_BEAT_CHECK_SUCCESS);
		return result;
	}
	
	/**
	 * 推任务
	 * @param jobId
	 * @param jobInstanceId
	 * @param taskSnapshot
	 * @return
	 */
	public Result<Boolean> push(long jobId, long jobInstanceId, TaskSnapshot taskSnapshot) {
		
		ConcurrentHashMap<Long, ExecutorUnit> executorUnitMap = this.executorUnitTable.get(jobId);
		if(null == executorUnitMap || executorUnitMap.isEmpty()) {
			return new Result<Boolean>(false, ResultCode.PUSH_UNIT_MAP_IS_EMPTY_ERROR);
		}
		
		ExecutorUnit executorUnit = executorUnitMap.get(jobInstanceId);
		if(null == executorUnit) {
			return new Result<Boolean>(false, ResultCode.PUSH_UNIT_IS_NULL_ERROR);
		}
		
		//将任务放入队列
		boolean result = executorUnit.offer(taskSnapshot);
		
		return new Result<Boolean>(result, result ? ResultCode.SUCCESS : ResultCode.FAILURE);
	}
	
}
