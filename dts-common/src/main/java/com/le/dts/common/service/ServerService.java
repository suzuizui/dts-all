package com.le.dts.common.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.Machine;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 服务端通用基础服务
 * @author tianyao.myc
 *
 */
public interface ServerService {

	/**
	 * 建立连接
	 * @param accessKey
	 * @return
	 */
	public Result<Boolean> connect(String accessKey);
	
	/**
	 * 注册jobMap
	 * @param machine
	 * @param jobMap
	 * @return
	 */
	public Result<Boolean> registerJobs(Machine machine, Map<String, String> jobMap);

	/**
	 * 任务ACK确认
	 * @param taskSnapshot
	 * @return
	 */
	public Result<Boolean> acknowledge(TaskSnapshot taskSnapshot);

	/**
	 * 批量任务ACK确认
	 * @param executableTask
	 * @param status
	 * @return
	 */
	public Result<Boolean> batchAcknowledge(ExecutableTask executableTask, int status);

	/**
	 * 发送任务列表到服务端
	 * @param executableTask
	 * @return
	 */
	public Result<Boolean> send(ExecutableTask executableTask);
	
	/**
	 * 通知服务器有任务了
	 * @param executableTask
	 * @return
	 */
	public Result<Boolean> notifyEvent(ExecutableTask executableTask);

	/**
	 * 从服务端拉取任务快照列表
	 * @param executableTask
	 * @return
	 */
	public Result<ExecutableTask> pull(ExecutableTask executableTask);
	
	/**
	 * 设置全局用户自定义参数
	 * @param jobInstanceSnapshot
	 * @param globalArguments
	 * @return
	 */
	public Result<Boolean> setGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot, String globalArguments);

	/**
	 * 获取设置的全局变量
	 * @param jobInstanceSnapshot
	 * @return
	 */
	public Result<String> getGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot);
	
	/**
	 * 移除活跃任务
	 * @param key
	 * @return
	 */
	public Result<Boolean> removeLivingTask(ServerJobInstanceMapping.JobInstanceKey key);

	/**
	 * call依赖的JOB;
	 * @param beforeJobId
	 * @param dependencyJobId
	 * @param jobInstanceId
	 * @param fireTime
	 * @param lastJobId
	 * @param uniqueId
	 * @return
	 */
	public Result<Boolean> callDependencyJob(long beforeJobId, long dependencyJobId, long jobInstanceId, Date fireTime, long lastJobId, String uniqueId);
	
	/**
	 * 获取分组下面的Job列表
	 * @param groupId
	 * @return
	 */
	public List<Job> acquireJobList(String groupId);
	
	public int stopAllInstance(long jobId);
	
	public List<RemoteMachine> getRemoteMachines(String groupId, long jobId);
	
	public Result<Boolean> warningSwitch(boolean warningSwitch);
	
}
