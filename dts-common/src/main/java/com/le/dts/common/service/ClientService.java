package com.le.dts.common.service;

import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 客户端通用基础服务
 * @author tianyao.myc
 *
 */
public interface ClientService {

	/**
	 * 心跳检查
	 * @return
	 */
	public Result<String> heartBeatCheck();

   /**
    * 心跳检查任务状态
    * @param jobType
    * @param jobId
    * @param jobInstanceId
    * @return
    */
    public Result<String> heartBeatCheckJobInstance(int jobType, long jobId, long jobInstanceId);
	
	/**
	 * 执行简单触发任务
	 * @param executableTask
	 * @return
	 */
	public Result<Boolean> executeTask(ExecutableTask executableTask);
	
	/**
	 * 停止任务
	 * @param jobType
	 * @param jobId
	 * @param jobInstanceId
	 * @return
	 */
	public Result<Boolean> stopTask(int jobType, long jobId, long jobInstanceId);
	
	/**
	 * 强制停止任务
	 * @param executableTask
	 * @return
	 */
	public Result<Boolean> forceStopTask(ExecutableTask executableTask);
	
	/**
	 * 推任务
	 * @param jobType
	 * @param jobId
	 * @param jobInstanceId
	 * @param taskSnapshot
	 * @return
	 */
	public Result<Boolean> push(int jobType, long jobId, long jobInstanceId, TaskSnapshot taskSnapshot);
	
}
