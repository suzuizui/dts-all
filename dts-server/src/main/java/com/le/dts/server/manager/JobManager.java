package com.le.dts.server.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.job.InternalJob;
import com.le.dts.server.state.LivingTaskManager;
import jodd.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.DtsState;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.util.BytesUtil;
import com.le.dts.common.util.CheckUtil;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.ExceptionUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.PathUtil;
import com.le.dts.common.util.RandomUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.TimeUtil;

/**
 * Job管理器
 * @author tianyao.myc
 *
 */
public class JobManager implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(JobManager.class);
	
	/** 客户端基础服务 */
    private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);
	
	/**
	 * 创建内部Job
	 * @param job
	 * @return
	 */
	public Result<Boolean> createInternalJob(Job job) {
		Result<Boolean> createResult = new Result<Boolean>(false);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(! checkResult.getData().booleanValue()) {
			logger.error("[JobManager]: createInternalJob checkJob error"
					+ ", job:" + job.toString() 
					+ ", checkResult:" + checkResult.toString());
			createResult.setData(checkResult.getData());
			createResult.setResultCode(checkResult.getResultCode());
			return createResult;
		}
		
		InternalJob internalJob = jobPool.get(job);
		if(internalJob != null) {
			logger.warn("[JobManager]: createInternalJob internalJob is already exists"
					+ ", job:" + job.toString() 
					+ ", checkResult:" + checkResult.toString());
			createResult.setData(false);
			createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_EXISTS);
			return createResult;
		}
		
		internalJob = new InternalJob(job);
		try {
			internalJob.init();
		} catch (Throwable e) {
			logger.error("[JobManager]: createInternalJob init error, job:" + job.toString(), e);
			if(ExceptionUtil.isNeverFire(e)) {
				createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_NEVER_FIRE);
			} else {
				createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_INIT_FAILURE);
			}
			return createResult;
		}
		
		/** 放入内存Job池 */
		try {
			jobPool.put(job, internalJob);
		} catch (Throwable e) {
			logger.error("[JobManager]: createInternalJob put error, job:" + job.toString(), e);
			createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_PUT_FAILURE);
			return createResult;
		}
		
		createResult.setData(true);
		createResult.setResultCode(ResultCode.CREATE_INTERNAL_JOB_SUCCESS);
		return createResult;
	}
	
	/**
	 * 查询内部Job
	 * @param query
	 * @return
	 */
	public Result<Job> queryInternalJob(Job query) {
		Result<Job> queryResult = new Result<Job>();
		InternalJob internalJob = jobPool.get(query);
		if(null == internalJob) {
			logger.error("[JobManager]: queryInternalJob error, internalJob is null, query:" + query.toString());
			queryResult.setResultCode(ResultCode.QUERY_INTERNAL_JOB_IS_NULL);
			return queryResult;
		}
		queryResult.setData(internalJob.getJob());
		queryResult.setResultCode(ResultCode.QUERY_INTERNAL_JOB_SUCCESS);
		return queryResult;
	}
	
	/**
	 * 更新内部Job
	 * @param job
	 * @return
	 */
	public Result<Boolean> updateInternalJob(Job job) {
		Result<Boolean> updateResult = new Result<Boolean>(false);
		
		Result<Boolean> checkResult = CheckUtil.checkJob(job);
		if(! checkResult.getData().booleanValue()) {
			updateResult.setData(checkResult.getData());
			updateResult.setResultCode(checkResult.getResultCode());
			return updateResult;
		}
		
		Result<Boolean> deleteResult = deleteInternalJob(job);
		if(! deleteResult.getData().booleanValue()) {
			updateResult.setData(deleteResult.getData());
			updateResult.setResultCode(deleteResult.getResultCode());
			return updateResult;
		}
		
		Result<Boolean> createResult = createInternalJob(job);
		if(! createResult.getData().booleanValue()) {
			updateResult.setData(createResult.getData());
			updateResult.setResultCode(createResult.getResultCode());
			return updateResult;
		}
		
		updateResult.setData(true);
		updateResult.setResultCode(ResultCode.UPDATE_INTERNAL_JOB_SUCCESS);
		return updateResult;
	}
	
	/**
	 * 更新内存Job状态
	 * @param job
	 * @return
	 */
	public Result<Boolean> updateInternalJobStatus(Job job) {
		Result<Boolean> updateResult = new Result<Boolean>(false);
		InternalJob internalJob = jobPool.get(job);
		if(null == internalJob) {
			logger.error("[JobManager]: updateInternalJobStatus error, internalJob is null, job:" + job.toString());
			updateResult.setResultCode(ResultCode.UPDATE_INTERNAL_JOB_IS_NULL);
			return updateResult;
		}
		internalJob.getJob().setStatus(job.getStatus());
		updateResult.setData(true);
		updateResult.setResultCode(ResultCode.SUCCESS);
		return updateResult;
	}
	
	/**
	 * 删除内部Job
	 * @param job
	 * @return
	 */
	public Result<Boolean> deleteInternalJob(Job job) {
		Result<Boolean> deleteResult = new Result<Boolean>(false);
		InternalJob internalJob = jobPool.get(job);
		if(null == internalJob) {
			logger.error("[JobManager]: deleteInternalJob error, internalJob is null, job:" + job.toString());
			deleteResult.setResultCode(ResultCode.DELETE_INTERNAL_JOB_IS_NULL);
			return deleteResult;
		}
		
		try {
			internalJob.delete();
		} catch (Throwable e) {
			logger.error("[JobManager]: deleteInternalJob error, job:" + job.toString(), e);
			deleteResult.setResultCode(ResultCode.DELETE_INTERNAL_JOB_FAILURE);
			return deleteResult;
		}
		
		try {
			jobPool.remove(job);
		} catch (Throwable e) {
			logger.error("[JobManager]: remove error, job:" + job.toString(), e);
			deleteResult.setResultCode(ResultCode.DELETE_INTERNAL_JOB_REMOVE_FAILURE);
			return deleteResult;
		}
		
		deleteResult.setData(true);
		deleteResult.setResultCode(ResultCode.DELETE_INTERNAL_JOB_SUCCESS);
		return deleteResult;
	}
	
	/**
	 * 触发job
	 * @param job
	 * @param fireTime
	 * @param uniqueId
	 * @param machineList
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Result<Boolean> fireJob(Job job, Date fireTime, String uniqueId, final List<String> machineList) {
		Result<Boolean> fireResult = new Result<Boolean>(false);
		try {
			
			String groupId = GroupIdUtil.generateGroupId(
					serverConfig.getClusterId(), serverConfig.getServerGroupId(), 
					serverConfig.getJobBackupAmount(), job.getClientGroupId());
			
			List<RemoteMachine> remoteMachineList = serverRemoting.getRemoteMachines(groupId, job.getId());
			
            if (remoteMachineList == null || remoteMachineList.isEmpty()) {
                logger.error("[JobManager]: fireJob error, No client available"
                		+ ", job:" + job 
                		+ ", fireTime:" + fireTime.toLocaleString() 
                		+ ", uniqueId:" + uniqueId 
                		+ ", groupId:" + groupId);
                fireResult.setResultCode(ResultCode.FIRE_JOB_NO_CLIENT_FAILURE);
				return fireResult;
            }
			if(START_POLICY_SINGLE_INSTANCE == job.getMaxInstanceAmount()) {
				long instanceAmount = jobInstanceManager.queryWorkingJobInstanceAmount(job);
				if(instanceAmount > 0) {
					fireResult.setResultCode(ResultCode.FIRE_JOB_WORKING_FAILURE);
					return fireResult;
				}
			}
			/** 多个备份插入Job执行实例 */
			tryInsertJobInstance(job, fireTime, uniqueId);
			fireResult = tryHandleJobInstanceWithLock(job, fireTime, uniqueId, machineList);
			return fireResult;
		} catch (Throwable e) {
			logger.error("[JobManager]: fireJob error"
					+ ", job:" + job 
					+ ", fireTime:" + fireTime.toLocaleString() 
					+ ", uniqueId:" + uniqueId, e);
			
			failureFinish(job, fireTime, uniqueId);//设置失败结束
			
			fireResult.setResultCode(ResultCode.FAILURE);
			return fireResult;
		}
	}
	
	/**
	 * 抢锁执行任务
	 * @param job
	 * @param fireTime
	 * @param uniqueId
	 * @param machineList
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Result<Boolean> tryHandleJobInstanceWithLock(Job job, Date fireTime, String uniqueId, final List<String> machineList) {
    	Result<Boolean> fireResult = new Result<Boolean>(false);
        JobInstanceSnapshot instanceSnapshot = tryLoadJobInstance(job, fireTime, uniqueId);
        if(instanceSnapshot == null) {
        	fireResult.setResultCode(ResultCode.FIRE_JOB_LOAD_INSTANCE_FAILURE);
            return fireResult;
        }
        if(isJobInstanceReady4Client(instanceSnapshot)) {
        	fireResult.setResultCode(ResultCode.FIRE_JOB_INSTANCE_STATUS_ERROR);
            return fireResult;
        }
        
        String timeInSecond = TimeUtil.date2SecondsString(fireTime);
        if(StringUtil.isNotBlank(uniqueId)) {
        	timeInSecond += SPLIT_STRING + uniqueId;
    	}
        
        String lockPath = PathUtil.getJobInstanceLockPath(String.valueOf(
                instanceSnapshot.getId()), timeInSecond);
        InterProcessMutex lock = zookeeper.getZkManager().createLock(lockPath);
        try {
            boolean hasLock = lock.acquire(Constants.JOB_INSTANCE_LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
            if(! hasLock) {
            	fireResult.setResultCode(ResultCode.FIRE_JOB_ACQUIRE_LOCK_FAILURE);
                return fireResult;
            }
            // We need to load the job instance again here because the process may be blocked by lock
            // and during the time the job instance's status is already changed.
            instanceSnapshot = jobInstanceManager.findJobInstance(job, fireTime, uniqueId);
            if(instanceSnapshot == null) {
            	fireResult.setResultCode(ResultCode.FIRE_JOB_LOAD_INSTANCE_FAILURE_AFTER_LOCK);
                return fireResult;
            }
            if(isJobInstanceReady4Client(instanceSnapshot)) {
            	fireResult.setResultCode(ResultCode.FIRE_JOB_INSTANCE_STATUS_ERROR_AFTER_LOCK);
                return fireResult;
            }

            List<RemoteMachine> remoteMachineList = serverRemoting
                    .getRemoteMachines(GroupIdUtil
                            .generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(),
                                    serverConfig.getJobBackupAmount(), job.getClientGroupId()), job.getId());
            if (remoteMachineList.isEmpty()) {
                jobInstanceManager.deleteJobInstance(instanceSnapshot);
                logger.error("[JobManager]: fireJob error, after has lock, No client available"
                		+ ", job:" + job 
                		+ ", fireTime:" + fireTime.toLocaleString() 
                		+ ", uniqueId:" + uniqueId);
                fireResult.setResultCode(ResultCode.FIRE_JOB_NO_CLIENT_FAILURE);
				return fireResult;
            }

            ExecutableTask executableTask = new ExecutableTask(job, instanceSnapshot);
            
            if(CommonUtil.isAllJob(job.getType())) {
            	
            	//设置可用的机器数量
            	executableTask.setAvailableMachineAmount(remoteMachineList.size());
            	
            	boolean atLeastOneFire = false;
            	//遍历所有有效机器
            	for(int i = 0 ; i < remoteMachineList.size() ; i ++) {
            		
            		//设置当前机器编号
            		executableTask.setCurrentMachineNumber(i);
            		
            		RemoteMachine remoteMachine = remoteMachineList.get(i);
            		
            		//触发
                	Result<Boolean> result = fire(executableTask, remoteMachine);
                	
                	machineList.add(remoteMachine.getRemoteAddress());
                	
                	if (result != null && result.getData()) {
                		
                		//至少有一个触发成功
                		atLeastOneFire = true;
                		
                		ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
                        key.setJobId(job.getId());
                        key.setJobInstanceId(instanceSnapshot.getId());
                        key.setJobType(job.getType());
                        key.setGroupId(remoteMachine.getGroupId());
                        LivingTaskManager.getSingleton().add(key, remoteMachine);
                		
                	} else {
                		logger.error("[JobManager]: fire remoteMachine error"
                				+ ", result:" + result 
                				+ ", executableTask:" + executableTask 
                				+ ", remoteMachine:" + remoteMachine);
                	}
            	}
            	
            	if(atLeastOneFire) {
            		
            		instanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_RUNNING);
            		
                    fireResult.setData(true);
                    fireResult.setResultCode(ResultCode.SUCCESS);
            		
            	} else {
            		
            		instanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_FAILED);
            		
            		fireResult.setResultCode(ResultCode.FAILURE);
            	}
            	
            } else {
            	
            	RemoteMachine remoteMachine = RandomUtil.getRandomObj(remoteMachineList);
            	
            	//设置可用的机器数量
            	executableTask.setAvailableMachineAmount(1);
            	
            	//设置当前机器编号
        		executableTask.setCurrentMachineNumber(0);
            	
            	//触发
            	Result<Boolean> result = fire(executableTask, remoteMachine);
            	
            	machineList.add(remoteMachine.getRemoteAddress());
            	
            	if (result != null && result.getData()) {
            		
            		instanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_RUNNING);
            		
            		ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
                    key.setJobId(job.getId());
                    key.setJobInstanceId(instanceSnapshot.getId());
                    key.setJobType(job.getType());
                    key.setGroupId(remoteMachine.getGroupId());
                    LivingTaskManager.getSingleton().add(key, remoteMachine);
            		
                    fireResult.setData(result.getData());
                    fireResult.setResultCode(result.getResultCode());
                    
            	} else {
            		
            		instanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_FAILED);
            		
            		fireResult.setResultCode(null == result ? ResultCode.FIRE_JOB_EXECUTE_TASK_ERROR : result.getResultCode());
            	}
            }
            
            store.getJobInstanceSnapshotAccess().updateInitInstance(instanceSnapshot);
            return fireResult;
        } catch (Throwable e) {
            logger.warn("Failed to execute job instance:" + instanceSnapshot.getId() + ", job:" + job, e);
            
            failureFinish(job, fireTime, uniqueId);//设置失败结束
            
            fireResult.setResultCode(ResultCode.FAILURE);
			return fireResult;
        } finally {
            if (lock != null)
                try {
                    lock.release();
                    // we need clean the lock path
                    if (isJobInstanceReady4Client(instanceSnapshot))
                        zookeeper.getZkManager().delete(lockPath);
                } catch (Throwable e) {
                    logger.error("Failed to release the lock of job instance:" + instanceSnapshot + ", job:" + job);
                }
        }
    }
	
	/**
	 * 触发
	 * @param executableTask
	 * @param remoteMachine
	 * @return
	 */
	private Result<Boolean> fire(ExecutableTask executableTask, RemoteMachine remoteMachine) {
		
		Result<Boolean> fireResult = new Result<Boolean>(false);
		
		TaskSnapshot taskSnapshot = fillTaskSnapshot(executableTask.getJob(), executableTask.getJobInstanceSnapshot(), remoteMachine);
        
        long insertResult = 0L;
        try {
			insertResult = taskSnapShotManager.insertTaskSnapshot(taskSnapshot);
		} catch (Throwable e) {
			logger.error("[JobManager]: fire insertTaskSnapshot error"
					+ ", executableTask:" + executableTask 
					+ ", remoteMachine:" + remoteMachine, e);
		}
        
        if(insertResult > 0L) {
        	taskSnapshot.setId(insertResult);
        	executableTask.setTaskSnapshot(taskSnapshot);
        } else {
        	fireResult.setResultCode(ResultCode.FIRE_JOB_INIT_ROOT_TASK_ERROR);
            return fireResult;
        }
        
        Result<Boolean> result = null;
        try {
			InvocationContext.setRemoteMachine(remoteMachine);
			result = clientService.executeTask(executableTask);
		} catch (Throwable e) {
			logger.error("[JobManager]: fire executeTask error"
					+ ", executableTask:" + executableTask 
					+ ", remoteMachine:" + remoteMachine, e);
		}
        
        if (result != null && result.getData()) {
            
            fireResult.setData(result.getData());
            fireResult.setResultCode(result.getResultCode());
            
        } else {
        	
        	logger.error("[JobManager]: fire executeTask failed"
        			+ ", result:" + result 
        			+ ", instanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
        			+ ", remoteMachine:" + remoteMachine);
            
            fireResult.setResultCode(null == result ? ResultCode.FIRE_JOB_EXECUTE_TASK_ERROR : result.getResultCode());
        }
        
		return fireResult;
	}
    
	/**
	 * 设置失败结束
	 * @param job
	 * @param fireTime
	 * @param uniqueId
	 */
	private void failureFinish(Job job, Date fireTime, String uniqueId) {

		String strFireTime = TimeUtil.date2SecondsString(fireTime);
    	if(StringUtil.isNotBlank(uniqueId)) {
    		strFireTime += SPLIT_STRING + uniqueId;
    	}
		
		JobInstanceSnapshot instanceSnapshot = new JobInstanceSnapshot();
		instanceSnapshot.setJobId(job.getId());
		instanceSnapshot.setFireTime(strFireTime);
		instanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_FAILED);
		
        try {
			store.getJobInstanceSnapshotAccess().updateFailureInstanceStatus(instanceSnapshot);
		} catch (Throwable e) {
			logger.error("[JobManager]: failureFinish error, job:" + job + ", fireTime:" + fireTime);
		}
        
	}
	
    /**
     * 加载实例
     * @param job
     * @param fireTime
     * @param uniqueId
     * @return
     */
	private JobInstanceSnapshot tryLoadJobInstance(Job job, Date fireTime, String uniqueId) {
        long start = System.currentTimeMillis();
        JobInstanceSnapshot result = null;
        while (System.currentTimeMillis() - start < Constants.JOB_INSTANCE_LOAD_TIMEOUT && result == null) {
            result = jobInstanceManager.findJobInstance(job, fireTime, uniqueId);
        }
        if (result == null)
            logger.error("server fail to load job instance for job " + job.getId() + " at " + new Date().toString() + ", uniqueId:" + uniqueId);
        return result;
    }

    /**
     * 装填TaskSnapshot
     * @param job
     * @param jobInstanceSnapshot
     * @param remoteMachine
     * @return
     */
    private TaskSnapshot fillTaskSnapshot(Job job, JobInstanceSnapshot jobInstanceSnapshot, RemoteMachine remoteMachine) {
    	TaskSnapshot taskSnapshot = new TaskSnapshot();
    	taskSnapshot.setJobInstanceId(jobInstanceSnapshot.getId());
    	taskSnapshot.setJobProcessor(job.getJobProcessor());
    	try {
			taskSnapshot.setBody(BytesUtil.objectToBytes(DtsState.START));
		} catch (Throwable e) {
			logger.error("[JobManager]: fillTaskSnapshot error"
					+ ", job:" + job 
					+ ", jobInstanceSnapshot:" + jobInstanceSnapshot 
					+ ", remoteMachine:" + remoteMachine, e);
		}
    	taskSnapshot.setStatus(TASK_STATUS_INIT);
    	if(CommonUtil.isSimpleJob(job.getType())) {
    		
    		String clientId = remoteMachine.getClientId();
    		String ip = RemotingUtil.parseIpFromAddress(remoteMachine.getRemoteAddress());
    		
    		taskSnapshot.setClientId(clientId.split(COLON).length > 1 ? clientId : clientId + COLON + ip);
    		taskSnapshot.setSimpleTask(true);
    	}
    	taskSnapshot.setTaskName(DEFAULT_ROOT_LEVEL_TASK_NAME);
    	taskSnapshot.setRetryCount(0);
    	return taskSnapshot;
    }

    private boolean isJobInstanceReady4Client(JobInstanceSnapshot instanceSnapshot) {
        return instanceSnapshot.getStatus() == Constants.JOB_INSTANCE_STATUS_RUNNING
                || instanceSnapshot.getStatus() == Constants.JOB_INSTANCE_STATUS_FINISHED
                || instanceSnapshot.getStatus() == Constants.JOB_INSTANCE_STATUS_RETRYING
                || instanceSnapshot.getStatus() == Constants.JOB_INSTANCE_STATUS_RETRY_FINISHED
                || instanceSnapshot.getStatus() == Constants.JOB_INSTANCE_STATUS_RETRY_OVER
                || instanceSnapshot.getStatus() == Constants.JOB_INSTANCE_STATUS_DELETE_SELF;
    }

    /**
     * Just one server can succeed.
     * @param job
     * @param fireTime
     * @param uniqueId
     */
    @SuppressWarnings("deprecation")
	private void tryInsertJobInstance(Job job, Date fireTime, String uniqueId) {
        try {
            jobInstanceManager.insertJobInstance(job, fireTime, uniqueId);
        } catch (Throwable e) {
            // we should not let exception get out of here
        	if(! ExceptionUtil.isDuplicate(e)) {
        		throw new RuntimeException("[JobManager]: tryInsertJobInstance error"
        				+ ", job:" + job 
        				+ ", fireTime:" + fireTime.toLocaleString() 
        				+ ", uniqueId:" + uniqueId, e);
        	}
        }
    }
    
    /**
     * 获取某个分组下面的job列表
     * @param clientGroup
     * @return
     */
    public List<Job> queryJobByGroupId(ClientGroup clientGroup) {
    	Job query = new Job();
    	query.setServerGroupId(clientGroup.getServerGroupId());
    	query.setClientGroupId(clientGroup.getId());
    	List<Job> jobList = null;
    	try {
			jobList = store.getJobAccess().queryJobByGroupId(query);
		} catch (Throwable e) {
			logger.error("[JobManager]: queryJobByGroupId error, clientGroup:" + clientGroup, e);
		}
    	if(null == jobList) {
    		jobList = new ArrayList<Job>();
    	}
    	return jobList;
    }
	
}
