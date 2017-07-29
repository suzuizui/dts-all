package com.le.dts.server.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.state.LivingTaskManager;
import com.le.dts.server.store.JobInstanceSnapshotAccess;
import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.common.exception.DtsException;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.summary.TaskSummary;
import com.le.dts.common.summary.face.TaskList;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.IdAndKeyUtil;
import com.le.dts.common.util.JobUtil;
import com.le.dts.common.util.ListUtil;
import com.le.dts.common.util.TimeUtil;

/**
 * Created by Moshan on 14-11-19.
 * JobInstanceManager creates job instances in db and contests ZK locks
 * of job instances for current server.
 */
public class JobInstanceManager implements ServerContext, Constants {

    private static final Log logger = LogFactory.getLog(JobInstanceManager.class);
    private static final long MAX_LOCK_PERIOD = 1000 * 5;
    
    /** 客户端基础服务 */
    private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);
    
    /** 服务端基础服务 */
    private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);
    
    public void insertJobInstance(Job job, Date fireTime, String uniqueId) throws AccessException {
        JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
        jobInstanceSnapshot.setDescription(job.getDescription());

        String strFireTime = TimeUtil.date2SecondsString(fireTime);
    	if(StringUtil.isNotBlank(uniqueId)) {
    		strFireTime += SPLIT_STRING + uniqueId;
    	}

    	jobInstanceSnapshot.setFireTime(strFireTime);
        jobInstanceSnapshot.setStatus(Constants.JOB_INSTANCE_STATUS_NEW);
        jobInstanceSnapshot.setGmtCreate(new Date());
        jobInstanceSnapshot.setGmtModified(new Date());
        jobInstanceSnapshot.setJobId(job.getId());
        getJobInstanceSnapshotAccess().insert(jobInstanceSnapshot);
    }

    public JobInstanceSnapshot get(long id) throws AccessException {
        return getJobInstanceSnapshotAccess().get(id);
    }
    
    public Result<List<JobInstanceSnapshot>> queryWorkingJobInstance(long jobId) {
    	Result<List<JobInstanceSnapshot>> result = new Result<List<JobInstanceSnapshot>>();
    	
    	try {
			List<JobInstanceSnapshot> snapShotList = getJobInstanceSnapshotAccess().queryWorking(jobId);
			result.setData(snapShotList);
			result.setResultCode(ResultCode.SUCCESS);
		} catch (AccessException e) {
			result.setResultCode(ResultCode.QUERY_JOB_INSTANCE_ERROR);
		}
    	return result;
    }
    
    public JobInstanceSnapshot findJobInstance(Job job, Date fireTime, String uniqueId) {
    	
    	String strFireTime = TimeUtil.date2SecondsString(fireTime);
    	if(StringUtil.isNotBlank(uniqueId)) {
    		strFireTime += SPLIT_STRING + uniqueId;
    	}
    	
        try {
            return getJobInstanceSnapshotAccess().findByJobIdAndFireTime(job.getId(), strFireTime);
        } catch (AccessException e) {
        	logger.error("[JobInstanceManager]:findJobInstance error!");
            throw new DtsException("Failed to find job instance", e);
        }
    }

    public void deleteJobInstance(JobInstanceSnapshot jobInstanceSnapshot) {
        try {
            getJobInstanceSnapshotAccess().delete(jobInstanceSnapshot);
        } catch (AccessException e) {
            logger.error("[JobInstanceManager]:findJobInstance error!");
            throw new DtsException("Failed to find job instance", e);
        }
    }
    
    public Result<Boolean> setGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot, byte[] globalArguments) {
    	Result<Boolean> result = new Result<Boolean>(false);
    	
    	int updateResult = 0;
    	try {
    		jobInstanceSnapshot.setInstanceGlobal(globalArguments);
			updateResult = getJobInstanceSnapshotAccess().updateInstanceGlobal(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: setGlobalArguments error, jobInstanceSnapshot:" + jobInstanceSnapshot.toString(), e);
		}
    	if(updateResult <= 0) {
    		result.setResultCode(ResultCode.FAILURE);
    		return result;
    	}
    	
    	result.setData(true);
    	result.setResultCode(ResultCode.SUCCESS);
    	return result;
    }
    
    public Result<String> getGlobalArguments(JobInstanceSnapshot query) {
    	Result<String> result = new Result<String>();
    	
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = getJobInstanceSnapshotAccess().queryInstanceGlobal(query);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: setGlobalArguments error, jobInstanceSnapshot:" + jobInstanceSnapshot.toString(), e);
		}
    	
    	if(null == jobInstanceSnapshot) {
    		result.setData(NULL);
        	result.setResultCode(ResultCode.SUCCESS);
        	return result;
    	}
    	
    	byte[] bytes = jobInstanceSnapshot.getInstanceGlobal();
    	if(null == bytes || bytes.length <= 0) {
    		result.setData(NULL);
        	result.setResultCode(ResultCode.SUCCESS);
        	return result;
    	}
    	
    	result.setData(new String(bytes));
    	result.setResultCode(ResultCode.SUCCESS);
    	return result;
    }
    
    /**
     * 查询正在运行的实例数量
     * @param job
     * @return
     */
    public long queryWorkingJobInstanceAmount(Job job) {
    	JobInstanceSnapshot query = new JobInstanceSnapshot();
    	query.setJobId(job.getId());
    	long result = 0L;
    	try {
			result = getJobInstanceSnapshotAccess().queryWorkingJobInstanceAmount(query);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: queryWorkingJobInstanceAmount error, jobInstanceSnapshot:" + query, e);
		}
    	return result;
    }

    public JobInstanceSnapshotAccess getJobInstanceSnapshotAccess() {
        return store.getJobInstanceSnapshotAccess();
    }

    public boolean acquireLock(long id, long offset) {
        try {
            Date lockStartThreshold = new Date(new Date().getTime() - MAX_LOCK_PERIOD);
            return getJobInstanceSnapshotAccess().setLockedWithCondition(id, lockStartThreshold, offset);
        } catch (Exception e) {
            throw new DtsException("Failed to acquire lock.", e);
        }
    }
    
    /**
     * 结束Job所有运行实例
     * @param job
     * @param invokeSource
     * @return
     */
    public boolean finishAllJobInstance(Job job, int invokeSource) {
    	List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
    	try {
			jobInstanceSnapshotList = store.getJobInstanceSnapshotAccess().queryWorking(job.getId());
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: queryWorking error, job:" + job + ", invokeSource:" + invokeSource, e);
			return false;
		}
    	if(CollectionUtils.isEmpty(jobInstanceSnapshotList)) {
    		logger.warn("[JobInstanceManager]: finishAllJobInstance error, jobInstanceSnapshotList is empty"
    				+ ", job:" + job + ", invokeSource:" + invokeSource);
    		return true;
    	}
    	for(JobInstanceSnapshot jobInstanceSnapshot : jobInstanceSnapshotList) {
    		ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
    		key.setGroupId(GroupIdUtil.generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(), serverConfig.getJobBackupAmount(), job.getClientGroupId()));
    		key.setJobId(job.getId());
    		key.setJobInstanceId(jobInstanceSnapshot.getId());
    		key.setJobType(job.getType());
    		finishJobInstance(key, invokeSource, JOB_INSTANCE_STATUS_FINISHED, false);
    	}
    	return true;
    }
    
    public int finishAllJobInstance(Job job, List<JobInstanceSnapshot> jobInstanceSnapshotList) {
    	
    	if(CollectionUtils.isEmpty(jobInstanceSnapshotList)) {
    		logger.warn("[JobInstanceManager]: finishAllJobInstance jobInstanceSnapshotList is empty error"
    				+ ", job:" + job + ", invokeSource:" + INVOKE_SOURCE_API);
    		return 0;
    	}
    	
    	int amount = 0;
    	
    	for(JobInstanceSnapshot jobInstanceSnapshot : jobInstanceSnapshotList) {
    		ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
    		key.setGroupId(GroupIdUtil.generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(), serverConfig.getJobBackupAmount(), job.getClientGroupId()));
    		key.setJobId(job.getId());
    		key.setJobInstanceId(jobInstanceSnapshot.getId());
    		key.setJobType(job.getType());
    		finishJobInstance(key, INVOKE_SOURCE_API, JOB_INSTANCE_STATUS_DELETE_SELF, false);
    		amount ++;
    	}
    	
    	return amount;
    }
    
    public List<JobInstanceSnapshot> queryInstanceListPaging(long jobId,
			long lastId) {
    	
    	List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
    	try {
			jobInstanceSnapshotList = store.getJobInstanceSnapshotAccess().queryInstanceListPaging(jobId, lastId);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: queryInstanceListPaging error, jobId:" + jobId + ", lastId:" + lastId, e);
		}
    	
    	return jobInstanceSnapshotList;
    }
    
    /**
     * 结束Job实例
     * @param key
     * @param invokeSource
     * @param status
     * @param isSimpleJobSuccess
     */
    public void finishJobInstance(ServerJobInstanceMapping.JobInstanceKey key, int invokeSource, int status, boolean isSimpleJobSuccess) {
    	
    	/** 设置实例运行结果 */
    	setJobInstanceResult(key);
    	
    	switch(invokeSource) {
    	case INVOKE_SOURCE_ACK:
    		if(key.isCompensation()) {
    			long count = queryRetryCount(key);
    			if(count > 0L) {
    				updateJobInstanceStatus(key, JOB_INSTANCE_STATUS_RETRY_FINISHED);
    			} else {
    				updateJobInstanceStatus(key, JOB_INSTANCE_STATUS_RETRY_OVER);
    			}
    		} else {
    			updateJobInstanceStatus(key, status);
    		}
    		
    		//移除key
    		LivingTaskManager.getSingleton().remove(key.getJobInstanceId());
			
    		break ;
    	case INVOKE_SOURCE_API:
    		
    		if(CommonUtil.isSimpleJob(key.getJobType())) {
    			stopClientMachines(key);
    			finishSimpleTask(key);
    			updateJobInstanceStatus(key, status);
    			removeAllLivingTask(key);
    		} else {
    			stopClientMachines(key);
    			updateJobInstanceStatus(key, status);
    			removeAllLivingTask(key);
    			handleInitTaskList(key.getJobInstanceId(), 0);
    		}
    		
    		break ;
    	case INVOKE_SOURCE_TIMER:
    		if(key.isCompensation()) {
    			long count = queryRetryCount(key);
    			if(count > 0L) {
    				updateJobInstanceStatus(key, JOB_INSTANCE_STATUS_RETRY_FINISHED);
    			} else {
    				updateJobInstanceStatus(key, JOB_INSTANCE_STATUS_RETRY_OVER);
    			}
    		} else {
    			updateJobInstanceStatus(key, status);
    		}
			
			if(! CommonUtil.isSimpleJob(key.getJobType())) {
				stopClientMachines(key);
    		}
			LivingTaskManager.getSingleton().remove(key);
			
    		break ;
    		default:
    	}
    	
    	if(! key.isCompensation()) {
    		
	    	/** 通知后置Job */
	    	notifyAfterJobs(key, isSimpleJobSuccess);
    	}
    }
    
    /**
     * 结束简单Job实例的零级任务
     * @param key
     */
    private void finishSimpleTask(final ServerJobInstanceMapping.JobInstanceKey key) {
    	Map<String, Object> query = new HashMap<String, Object>();
        query.put("jobInstanceId", key.getJobInstanceId());
        List<TaskSnapshot> taskSnapshotList = null;
        try {
			taskSnapshotList = store.getTaskSnapshotAccess().queryByJobInstanceIdAndStatus(query);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: finishSimpleTask query error, key:" + key, e);
		}
        if(CollectionUtils.isEmpty(taskSnapshotList) || taskSnapshotList.size() != 1) {
            logger.error(
                    "[JobInstanceManager]: fatal error! Simple job should only have one sub task, but we find "
                            + ListUtil.getSizeAllowNull(taskSnapshotList) + " for key:" + key);
            return;
        }
        TaskSnapshot taskSnapshot = taskSnapshotList.get(0);
        taskSnapshot.setStatus(Constants.TASK_STATUS_FAILURE);
        int result = 0;
        try {
			result = store.getTaskSnapshotAccess().update(taskSnapshot);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: finishSimpleTask update error, taskSnapshot:" + taskSnapshot + ", key:" + key, e);
		}
        if(result <= 0) {
        	logger.error("[JobInstanceManager]: finishSimpleTask update failed, taskSnapshot:" + taskSnapshot + ", key:" + key);
        }
    }
    
    /**
     * 查询重试记录数量
     * @param key
     * @return
     */
    public long queryRetryCount(ServerJobInstanceMapping.JobInstanceKey key) {
    	TaskSnapshot query = new TaskSnapshot();
    	query.setJobInstanceId(key.getJobInstanceId());
    	long result = 0L;
    	try {
			result = store.getTaskSnapshotAccess().queryRetryCount(query);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: queryRetryCount error, key:" + key, e);
		}
    	return result;
    }
    
    /**
     * 移除所有活跃任务
     * @param key
     */
    private void removeAllLivingTask(ServerJobInstanceMapping.JobInstanceKey key) {
    	
    	List<String> serverList = clientRemoting.getServerList();
    	if(CollectionUtils.isEmpty(serverList)) {
    		logger.error("[JobInstanceManager]: removeAllLivingTask error, serverList is empty, key:" + key);
    		return ;
    	}
    	
    	for(String server : serverList) {
    		
    		Result<Boolean> removeResult = null;
    		try {
				InvocationContext.setRemoteMachine(new RemoteMachine(server));
				removeResult = serverService.removeLivingTask(key);
			} catch (Throwable e) {
				logger.error("[JobInstanceManager]: removeLivingTask error"
    					+ ", key:" + key 
    					+ ", server:" + server, e);
			}
    		
    		if(null == removeResult || ! removeResult.getData().booleanValue()) {
    			logger.error("[JobInstanceManager]: removeLivingTask failed"
    					+ ", key:" + key 
    					+ ", server:" + server 
    					+ ", removeResult:" + removeResult);
    		}
    	}
    }
    
    /**
     * 停止所有客户端机器
     * @param key
     */
    private void stopClientMachines(ServerJobInstanceMapping.JobInstanceKey key) {
    	try {
			List<RemoteMachine> remoteMachineList = serverRemoting.getRemoteMachines(key.getGroupId(), key.getJobId());
			if(CollectionUtils.isEmpty(remoteMachineList)) {
				logger.warn("[JobInstanceManager]: stopTask failed, remoteMachineList is empty"
						+ ", key:" + key);
				return ;
			}
			for(RemoteMachine remoteMachine : remoteMachineList) {
				InvocationContext.setRemoteMachine(remoteMachine);
				Result<Boolean> stopResult = clientService.stopTask(key.getJobType(), key.getJobId(), key.getJobInstanceId());
				if(null == stopResult ||!  stopResult.getData().booleanValue()) {
					logger.warn("[JobInstanceManager]: stopTask failed"
							+ ", key:" + key 
							+ ", stopResult:" + stopResult);
				}
			}
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: stopClientMachines error, key:" + key, e);
		}
    }
    
    /**
     * 更新Job实例状态
     * @param key
     * @param status
     */
    private void updateJobInstanceStatus(ServerJobInstanceMapping.JobInstanceKey key, int status) {
    	JobInstanceSnapshot updateJobInstance = new JobInstanceSnapshot();
        updateJobInstance.setId(key.getJobInstanceId());
        updateJobInstance.setStatus(status);
        int result = 0;
        try {
			result = getJobInstanceSnapshotAccess().update(updateJobInstance);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: updateJobInstanceStatus error"
					+ ", key:" + key + ", status:" + status, e);
		}
        if(result <= 0) {
        	logger.error("[JobInstanceManager]: updateJobInstanceStatus failed"
					+ ", key:" + key + ", status:" + status);
        }
    }
    
    /**
     * 设置实例运行结果
     * @param key
     */
	private void setJobInstanceResult(ServerJobInstanceMapping.JobInstanceKey key) {
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = get(key.getJobInstanceId());
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: get JobInstanceSnapshot error, key:" + key, e);
			return ;
		}
    	if(null == jobInstanceSnapshot) {
    		logger.error("[JobInstanceManager]: get JobInstanceSnapshot failed, key:" + key);
			return ;
    	}
    	
    	final AtomicLong amount = new AtomicLong(0L);
    	final AtomicLong counter = new AtomicLong(0L);
    	
    	TaskSummary taskSummary = new TaskSummary(new TaskList() {

			@Override
			public List<TaskSnapshot> aquireTaskList(long jobInstanceId,
					long lastTaskId, int retryCount) throws Throwable {
				
				TaskSnapshot query = new TaskSnapshot();
				query.setJobInstanceId(jobInstanceId);
				query.setId(lastTaskId);
				
				List<TaskSnapshot> taskSnapshotList = store.getTaskSnapshotAccess().aquireTaskList(query);
				
				amount.addAndGet(null == taskSnapshotList ? 0L : taskSnapshotList.size());
				counter.incrementAndGet();
				
				return taskSnapshotList;
			}
			
		}, 200, 10L, 100);
    	
    	ProgressDetail progressDetail = null;
    	if(key.isCompensation()) {
    		try {
				progressDetail = ProgressDetail.newInstance(jobInstanceSnapshot.getJobInstanceResult());
				ProgressDetail progressDetailNew = taskSummary.calculateProgressDetail(jobInstanceSnapshot);//gatherProgressDetail(jobInstanceSnapshot);
				/** 刷新进度 */
				refreshProgressDetail(progressDetail, progressDetailNew);
			} catch (Throwable e) {
				logger.error("[JobInstanceManager]: refreshProgressDetail error"
						+ ", key:" + key + ", jobInstanceSnapshot:" + jobInstanceSnapshot, e);
			}
    	} else {
    		long startTime = System.currentTimeMillis();
    		progressDetail = taskSummary.calculateProgressDetail(key.getJobType(), jobInstanceSnapshot);//gatherProgressDetail(jobInstanceSnapshot);
    		logger.info("[JobInstanceManager]: calculateProgressDetail"
    				+ ", jobInstanceId:" + key.getJobInstanceId() 
    				+ ", counter:" + counter.get() 
    				+ ", amount:" + amount.get() 
    				+ ", cost:" + (System.currentTimeMillis() - startTime));
//    		progressDetail.setFinishTime(TimeUtil.date2SecondsString(new Date()));
//    		progressDetail.setType(key.getJobType());
    	}
    	
    	try {
			jobInstanceSnapshot.setJobInstanceResult(progressDetail.toString());
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: setJobInstanceResult error"
					+ ", key:" + key + ", jobInstanceSnapshot:" + jobInstanceSnapshot, e);
		}
    	
    	int result = 0;
    	try {
			result = getJobInstanceSnapshotAccess().updateJobInstanceResult(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: updateJobInstanceResult error, jobInstanceSnapshot:" + jobInstanceSnapshot, e);
		}
    	if(result <= 0) {
//    		logger.error("[JobInstanceManager]: updateJobInstanceResult failed, jobInstanceSnapshot:" + jobInstanceSnapshot);
    	}
    }
    
    /**
     * 刷新进度
     * @param progressDetail
     * @param progressDetailNew
     */
    private void refreshProgressDetail(ProgressDetail progressDetail, ProgressDetail progressDetailNew) {
    	/** 刷新总体进度条 */
    	refreshProgressBar(progressDetail.getTotalProgressBar(), progressDetailNew.getTotalProgressBar());
    	
    	List<ProgressBar> progressBarList = progressDetail.getProgressBarList();
    	List<ProgressBar> progressBarListNew = progressDetailNew.getProgressBarList();
    	if(CollectionUtils.isEmpty(progressBarList) || CollectionUtils.isEmpty(progressBarListNew)) {
    		return ;
    	}
    	for(ProgressBar progressBarNew : progressBarListNew) {
    		for(ProgressBar progressBar : progressBarList) {
    			if(progressBarNew.getName().equals(progressBar.getName())) {
    				/** 刷新进度条 */
    		    	refreshProgressBar(progressBar, progressBarNew);
    		    	break ;
    			}
    		}
    	}
    }
    
    /**
     * 刷新进度条
     * @param progressBar
     * @param progressBarNew
     */
    private void refreshProgressBar(ProgressBar progressBar, ProgressBar progressBarNew) {
    	progressBar.setFailureAmount(progressBarNew.getFailureAmount());
    	progressBar.setSuccessAmount(progressBar.getTotalAmount() - progressBarNew.getFailureAmount());
    }
    
	public ProgressDetail gatherProgressDetail(JobInstanceSnapshot jobInstanceSnapshot) {
    	ProgressDetail progressDetail = new ProgressDetail();
    	progressDetail.setFireTime(JobUtil.acquireFireTime(jobInstanceSnapshot.getFireTime()));
    	progressDetail.setDescription(jobInstanceSnapshot.getDescription());
    	
    	ProgressBar totalProgressBar = new ProgressBar();
    	totalProgressBar.setName(TOTAL_PROGRESS);
    	totalProgressBar.setInstanceId(String.valueOf(jobInstanceSnapshot.getId()));
    	totalProgressBar.setInitAmount(taskSnapShotManager.queryItemCount(jobInstanceSnapshot.getId(), TASK_STATUS_INIT));
    	totalProgressBar.setQueueAmount(taskSnapShotManager.queryItemCount(jobInstanceSnapshot.getId(), TASK_STATUS_QUEUE));
    	totalProgressBar.setFoundAmount(taskSnapShotManager.queryItemCount(jobInstanceSnapshot.getId(), TASK_STATUS_FOUND_PROCESSOR_FAILURE));
    	totalProgressBar.setStartAmount(taskSnapShotManager.queryItemCount(jobInstanceSnapshot.getId(), TASK_STATUS_START));
    	totalProgressBar.setFailureAmount(taskSnapShotManager.queryItemCount(jobInstanceSnapshot.getId(), TASK_STATUS_FAILURE));
    	totalProgressBar.setSuccessAmount(taskSnapShotManager.queryItemCount(jobInstanceSnapshot.getId(), TASK_STATUS_SUCCESS));
    	totalProgressBar.setTotalAmount(taskSnapShotManager.queryTotalCount(jobInstanceSnapshot.getId()));
    	progressDetail.setTotalProgressBar(totalProgressBar);
    	
    	List<String> taskNameList = taskSnapShotManager.queryTaskNameList(jobInstanceSnapshot.getId());
    	if(CollectionUtils.isEmpty(taskNameList)) {
//    		logger.error("[JobInstanceManager]: gatherProgressDetail taskNameList is empty error, jobInstanceSnapshot:" + jobInstanceSnapshot);
    		return progressDetail;
    	}
    	
    	for(String taskName : taskNameList) {
    		ProgressBar progressBar = new ProgressBar();
    		progressBar.setName(taskName);
    		progressBar.setInstanceId(String.valueOf(jobInstanceSnapshot.getId()));
    		progressBar.setInitAmount(taskSnapShotManager.queryDetailItemCount(jobInstanceSnapshot.getId(), taskName, TASK_STATUS_INIT));
    		progressBar.setQueueAmount(taskSnapShotManager.queryDetailItemCount(jobInstanceSnapshot.getId(), taskName, TASK_STATUS_QUEUE));
    		progressBar.setFoundAmount(taskSnapShotManager.queryDetailItemCount(jobInstanceSnapshot.getId(), taskName, TASK_STATUS_FOUND_PROCESSOR_FAILURE));
    		progressBar.setStartAmount(taskSnapShotManager.queryDetailItemCount(jobInstanceSnapshot.getId(), taskName, TASK_STATUS_START));
    		progressBar.setFailureAmount(taskSnapShotManager.queryDetailItemCount(jobInstanceSnapshot.getId(), taskName, TASK_STATUS_FAILURE));
    		progressBar.setSuccessAmount(taskSnapShotManager.queryDetailItemCount(jobInstanceSnapshot.getId(), taskName, TASK_STATUS_SUCCESS));
    		progressBar.setTotalAmount(taskSnapShotManager.queryDetailTotalCount(jobInstanceSnapshot.getId(), taskName));
    		progressDetail.add(progressBar);
    	}
    	
    	return progressDetail;
    }

    /**
     * 通知后置Job
     * @param key
     * @param isSimpleJobSuccess
     */
    private void notifyAfterJobs(ServerJobInstanceMapping.JobInstanceKey key, boolean isSimpleJobSuccess) {
    	
    	JobRelation query = new JobRelation();
    	query.setBeforeJobId(key.getJobId());
    	Result<List<JobRelation>> result = jobRelationManager.queryAfterJob(query);
    	if(null == result || CollectionUtils.isEmpty(result.getData())) {
    		return ;
    	}
    	
		if(CommonUtil.isSimpleJob(key.getJobType()) && ! isSimpleJobSuccess) {
			logger.error("[JobInstanceManager]: notifyAfterJobs simple job failed, so return"
					+ ", isSimpleJobSuccess:" + isSimpleJobSuccess 
					+ ", key:" + key);
			return ;
		}
		
    	List<JobRelation> afterJobs = result.getData();
    	
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = get(key.getJobInstanceId());
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: notifyAfterJobs get JobInstanceSnapshot error"
					+ ", key:" + key, e);
			return ;
		}
    	if(null == jobInstanceSnapshot) {
    		logger.error("[JobInstanceManager]: notifyAfterJobs get JobInstanceSnapshot failed"
    				+ ", key:" + key);
			return ;
    	}
    	
    	if(jobInstanceSnapshot.getNotifyVersion() > 0) {
    		logger.warn("[JobInstanceManager]: already notifyAfterJobs"
    				+ ", key:" + key);
			return ;
    	}
    	
    	if(! acquireNotifyLock(jobInstanceSnapshot)) {
    		logger.warn("[JobInstanceManager]: acquireNotifyLock failed, already notifyAfterJobs"
    				+ ", key:" + key);
			return ;
    	}
    	
    	long lastJobId = 0L;
    	for(JobRelation jobRelation : afterJobs) {
    		int retryCount = 0;
    		ResultCode notifyResult = notifyAfterJob(jobRelation, key, retryCount, lastJobId);
    		while(notifyResult.equals(ResultCode.FAILURE) && retryCount < 100) {
    			try {
					Thread.sleep(1000);
				} catch (Throwable e) {
					logger.error("[JobInstanceManager]: notifyAfterJob sleep error"
							+ ", key:" + key 
							+ ", retryCount:" + retryCount, e);
				}
    			notifyResult = notifyAfterJob(jobRelation, key, retryCount, lastJobId);
    			retryCount ++;
    		}
    		
    		if(! notifyResult.equals(ResultCode.CAN_NOT_FIND_JOB_BACKUP_SERVER_LIST_ERROR)) {
    			lastJobId = jobRelation.getJobId();
    		}
    		
    	}
    }
    
    /**
     * 通知后置Job
     * @param jobRelation
     * @param key
     * @param retryCount
     * @param lastJobId
     * @return
     */
    private ResultCode notifyAfterJob(JobRelation jobRelation, ServerJobInstanceMapping.JobInstanceKey key, int retryCount, long lastJobId) {
    	List<String> serverList = jobServerRelationManager.acquireBackupServerList(jobRelation.getJobId());
		if(CollectionUtils.isEmpty(serverList)) {
			logger.error("[JobInstanceManager]: notifyAfterJob serverList is empty"
					+ ", jobRelation:" + jobRelation 
					+ ", retryCount:" + retryCount);
			return ResultCode.CAN_NOT_FIND_JOB_BACKUP_SERVER_LIST_ERROR;
		}
		
		int errorResultCount = 0;
		
		Date fireTime = new Date();

    	String uniqueId = IdAndKeyUtil.acquireUniqueId();
    	
		for(String server : serverList) {
			InvocationContext.setRemoteMachine(new RemoteMachine(server));
			Result<Boolean> callResult = null;
			try {
				callResult = serverService.callDependencyJob(key.getJobId(), jobRelation.getJobId(), key.getJobInstanceId(), fireTime, lastJobId, uniqueId);
			} catch (Throwable e) {
				logger.error("[JobInstanceManager]: notifyAfterJob"
    					+ ", jobInstanceId:" + key.getJobInstanceId() 
    					+ ", jobRelation:" + jobRelation 
    					+ ", server:" + server 
    					+ ", retryCount:" + retryCount 
    					+ ", uniqueId:" + uniqueId, e);
			}
			
			if(callResult != null) {
				if(ResultCode.FIRE_JOB_INSTANCE_STATUS_ERROR.equals(callResult.getResultCode()) 
						|| ResultCode.FIRE_JOB_INSTANCE_STATUS_ERROR_AFTER_LOCK.equals(callResult.getResultCode())) {
					errorResultCount ++;
				}
			}
			
			logger.info("[JobInstanceManager]: notifyAfterJob"
					+ ", jobInstanceId:" + key.getJobInstanceId() 
					+ ", jobRelation:" + jobRelation 
					+ ", server:" + server 
					+ ", callResult:" + callResult 
					+ ", retryCount:" + retryCount 
					+ ", lastJobId:" + lastJobId 
					+ ", uniqueId:" + uniqueId);
		}
		
		if(errorResultCount == serverList.size()) {
			return ResultCode.FAILURE;
		}
		return ResultCode.SUCCESS;
    }
    
    /**
     * 获取通知后置任务的锁
     * @param jobInstanceSnapshot
     * @return
     */
    public boolean acquireNotifyLock(JobInstanceSnapshot jobInstanceSnapshot) {
    	int result = 0;
    	try {
			result = store.getJobInstanceSnapshotAccess().updateNotifyVersion(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: acquireNotifyLock error, jobInstanceSnapshot:" + jobInstanceSnapshot, e);
		}
    	return result > 0 ? true : false;
    }

    /**
     * 获取通知后置任务的锁
     * @param lastJobId
     * @param jobInstanceSnapshot
     * @return
     */
    public boolean acquireFinishCountLock(long lastJobId, JobInstanceSnapshot jobInstanceSnapshot, long afterJobId) {
        int result = 0;
        try {
            result = store.getJobInstanceSnapshotAccess().updateRelationTag(jobInstanceSnapshot.getId(), lastJobId, afterJobId);
        } catch (Throwable e) {
            logger.error("[JobInstanceManager]: acquireFinishCountLock error"
            		+ ", jobInstanceSnapshot:" + jobInstanceSnapshot + ", lastJobId:" + lastJobId + ", afterJobId:" + afterJobId, e);
        }
        return result > 0 ? true : false;
    }
    
    public boolean isNewestInstanceSuccess(long jobId) {
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = store.getJobInstanceSnapshotAccess().queryNewestInstance(jobId);
		} catch (Throwable e) {
			logger.error("[JobInstanceManager]: queryNewestInstance, jobId:" + jobId, e);
			return false;
		}
    	
    	if(null == jobInstanceSnapshot || StringUtils.isBlank(jobInstanceSnapshot.getJobInstanceResult())) {
    		return true;
    	}
    	
    	ProgressDetail progressDetail = ProgressDetail.newInstance(jobInstanceSnapshot.getJobInstanceResult());
    	
    	ProgressBar progressBar = progressDetail.getProgressBarList().get(0);
    	if(null == progressBar) {
    		return true;
    	}
    	
    	return 1L == progressBar.getSuccessAmount() ? true : false;
    }
    
    public int handleInitTaskList(long jobInstanceId, int retryCount) {
    	
    	int failureCounter = 0;
    	
    	int result = 0;
    	try {
			JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
			jobInstanceSnapshot.setId(jobInstanceId);
			getJobInstanceSnapshotAccess().updateHandleUnfinishVersion(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: handleInitTaskList updateHandleUnfinishVersion error"
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
    	
    	if(result <= 0) {
    		return failureCounter;
    	}
    	
    	List<TaskSnapshot> taskSnapshotList = taskSnapShotManager.queryInitTask(jobInstanceId);
    	while(! CollectionUtils.isEmpty(taskSnapshotList)) {
    		
    		try {
				failureCounter += store.getTaskSnapshotAccess().setFailureAndRetryCountBatch(taskSnapshotList, retryCount);
			} catch (Throwable e) {
				logger.error("[LivingTaskManager]: handleInitTaskList error"
						+ ", jobInstanceId:" + jobInstanceId, e);
			}
    		
    		taskSnapshotList = taskSnapShotManager.queryInitTask(jobInstanceId);
    	}
    	
    	return failureCounter;
    }
    
}
