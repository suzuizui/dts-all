package com.le.dts.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.state.LivingTaskManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.Machine;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.ServerJobInstanceMapping.JobInstanceKey;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.ExceptionUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.ListUtil;
import com.le.dts.server.store.TaskSnapshotAccess;

/**
 * 服务端基础服务
 * @author tianyao.myc
 *
 */
public class ServerServiceImpl implements ServerService, ServerContext, Constants {

    private static final Log logger = LogFactory.getLog("serverServiceImpl");
    private static final Log countLogger = LogFactory.getLog("countTaskSnapshot");
    
    private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);
    
    /** 服务端基础服务 */
    private ServerService serverService = clientRemoting.proxyInterface(ServerService.class);

    private TaskSnapshotAccess getTaskSnapshotAccess() {
        return store.getTaskSnapshotAccess();
    }

    /**
     * 建立连接
     */
    public Result<Boolean> connect(String accessKey) {
        RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
//        if(! SpasSdkServiceFacade.checkAuthority(NULL.equals(accessKey) ? null : accessKey,
//        		remoteMachine.getGroupId(), WILDCARD, null, SpasSdkClientFacade.getVersion())) {
//        	return new Result<Boolean>(false, ResultCode.CONNECT_ACCESS_FAILURE);
//        }
        return serverRemoting.connect(remoteMachine);
    }

    /**
     * 注册jobMap
     */
    @Override
	public Result<Boolean> registerJobs(Machine machine,
			Map<String, String> jobMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * 任务ACK确认
     */
    @Override
    public Result<Boolean> acknowledge(final TaskSnapshot taskSnapshot) {
        int result = 0;
        final RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
        try {
            result = getTaskSnapshotAccess().update(taskSnapshot);
            // if simple task finished the corresponding job instance is finished
            if (isSimpleTaskFinished(taskSnapshot)) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                        	long jobId = 0L;
                        	int jobType = 0;
                        	JobInstanceSnapshot jobInstanceSnapshot = null;
                        	try {
                    			jobInstanceSnapshot = jobInstanceManager.get(taskSnapshot.getJobInstanceId());
                    		} catch (Throwable e) {
                    			logger.error("[ServerServiceImpl]: acknowledge get JobInstanceSnapshot error, taskSnapshot:" + taskSnapshot, e);
                    		}
                        	if(jobInstanceSnapshot != null) {
                        		Job job = null;
                        		try {
                        			Job queryJob = new Job();
                        			queryJob.setId(jobInstanceSnapshot.getJobId());
                        			job = store.getJobAccess().queryJobById(queryJob);
                        		} catch (Throwable e) {
                        			logger.error("[ServerServiceImpl]: queryJobById error, jobInstanceSnapshot:" + jobInstanceSnapshot, e);
                        		}
                        		if(job != null) {
                        			jobId = job.getId();
                        			jobType = job.getType();
                        		}
                        	}
                        	
                        	logger.info("[ServerServiceImpl]: acknowledge"
				            		+ ", status:" + taskSnapshot.getStatus() 
				            		+ ", jobInstanceId:" + taskSnapshot.getJobInstanceId() 
				            		+ ", remoteMachine:" + remoteMachine);

                        	if(CommonUtil.isAllJob(jobType)) {
                        		return ;//如果是多机版就直接返回
                        	}
                        	
                            jobInstanceManager.finishJobInstance(
                                    new ServerJobInstanceMapping.JobInstanceKey(taskSnapshot.getJobInstanceId(),
                                    		jobId, jobType, remoteMachine.getGroupId(), taskSnapshot.isCompensation()),
                                    INVOKE_SOURCE_ACK, JOB_INSTANCE_STATUS_FINISHED, TASK_STATUS_SUCCESS == taskSnapshot.getStatus() ? true : false);
                        } catch (Throwable e) {
                            logger.error("[ServerServiceImpl]: acknowledge finishJobInstance error, taskSnapshot:" + taskSnapshot, e);
                        }
                    }

                }).start();
            }
        } catch (Throwable e) {
            logger.error("[ServerServiceImpl]: Failed to acknowledge task taskSnapshot:" + taskSnapshot, e);
            return new Result<Boolean>(false, ResultCode.FAILURE);
        }
        if(result <= 0) {
            logger.error("[ServerServiceImpl]: Failed to acknowledge task taskSnapshot:" + taskSnapshot + ", result:" + result);
            return new Result<Boolean>(false, ResultCode.FAILURE);
        }
        return new Result<Boolean>(true, ResultCode.SUCCESS);
    }

    private boolean isSimpleTaskFinished(TaskSnapshot taskSnapshot) {
        return taskSnapshot.isSimpleTask() &&
                (taskSnapshot.getStatus() == Constants.TASK_STATUS_SUCCESS
                        || taskSnapshot.getStatus() == Constants.TASK_STATUS_FAILURE
                        || taskSnapshot.getStatus() == Constants.TASK_STATUS_FOUND_PROCESSOR_FAILURE);
    }


    /**
     * 批量任务ACK确认
     */
    @Override
    public Result<Boolean> batchAcknowledge(ExecutableTask executableTask, int status) {
        try {
            getTaskSnapshotAccess().updateStatusBatch(executableTask.getTaskSnapshotList(), status);
            return new Result<Boolean>(true, ResultCode.SUCCESS);
        } catch (Throwable e) {
            logger.error("[ServerServiceImpl]: Failed to batch acknowledge. "
                    + "taskSnapshotList:" + executableTask.getTaskSnapshotList()
                    + ", status:" + status, e);
            return new Result<Boolean>(false, ResultCode.FAILURE);
        }
    }

    private boolean isRootTask(TaskSnapshot task) {
        return Constants.DEFAULT_ROOT_LEVEL_TASK_NAME.equals(task.getTaskName());
    }

    /**
     * 发送任务列表到服务端
     */
    @Override
    public Result<Boolean> send(ExecutableTask executableTask) {
    	
    	long startTime = System.currentTimeMillis();
    	
    	int taskListSize = executableTask.getTaskSnapshotList().size();//获取任务列表大小
    	
    	//批量插入任务快照列表
    	int insertResult = 0;
    	try {
			insertResult = getTaskSnapshotAccess().insertBatch(executableTask.getTaskSnapshotList());
		} catch (Throwable e) {
			
			logger.error("[ServerServiceImpl]: insertBatch error"
            		+ ", job:" + executableTask.getJob() 
            		+ ", jobInstanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
            		+ ", compensation:" + executableTask.isCompensation() 
            		+ ", length:" + executableTask.getLength() 
            		+ ", taskListSize:" + taskListSize, e);
			
            if(ExceptionUtil.isSyntaxError(e)) {
            	return new Result<Boolean>(false, ResultCode.SYNTAX_ERROR);
            }
		}
    	
    	if(insertResult <= 0) {
    		
    		logger.error("[ServerServiceImpl]: insertBatch failed"
            		+ ", job:" + executableTask.getJob() 
            		+ ", jobInstanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
            		+ ", compensation:" + executableTask.isCompensation() 
            		+ ", length:" + executableTask.getLength() 
            		+ ", taskListSize:" + taskListSize);
    		
    		return new Result<Boolean>(false, ResultCode.FAILURE);
    	}
    	
    	executableTask.setTaskSnapshotList(null);//清空任务列表减少传送数据量
    	
    	long clientTime = System.currentTimeMillis();
    	AtomicInteger clientTotal = new AtomicInteger(0);
    	AtomicInteger clientSuccess = new AtomicInteger(0);
    	try {
    		
			//通知所有客户端
			notifyAllClient(executableTask, clientTotal, clientSuccess);
		} catch (Throwable e) {
			logger.error("[ServerServiceImpl]: notifyAllClient error"
            		+ ", job:" + executableTask.getJob() 
            		+ ", jobInstanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
            		+ ", compensation:" + executableTask.isCompensation() 
            		+ ", length:" + executableTask.getLength() 
            		+ ", taskListSize:" + taskListSize, e);
		}
    	
    	long serverTime = System.currentTimeMillis();
    	AtomicInteger serverTotal = new AtomicInteger(0);
    	AtomicInteger serverSuccess = new AtomicInteger(0);
    	try {
    		
    		//通知所有服务端
			notifyAllServer(executableTask, serverTotal, serverSuccess);
		} catch (Throwable e) {
			logger.error("[ServerServiceImpl]: notifyAllServer error"
            		+ ", job:" + executableTask.getJob() 
            		+ ", jobInstanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
            		+ ", compensation:" + executableTask.isCompensation() 
            		+ ", length:" + executableTask.getLength() 
            		+ ", taskListSize:" + taskListSize, e);
		}
    	
    	long endTime = System.currentTimeMillis();
    	
    	long clientCost = serverTime - clientTime;
    	long serverCost = endTime - serverTime;
        countLogger.info("TaskSnapshot|" + taskListSize 
        		+ "|Time|" + (endTime - startTime) 
        		+ "|clientCost|" + clientCost 
        		+ "|clients|" + clientSuccess.get() + "/" + clientTotal.get() 
        		+ "|serverCost|" + serverCost 
        		+ "|servers|" + serverSuccess.get() + "/" + serverTotal.get() 
        		+ "|insertCost|" + (clientTime - startTime) 
        		+ "|insertResult|" + insertResult 
        		+ ((clientCost > 1000L || serverCost > 1000L) ? "|jobId|" + executableTask.getJob().getId() : ""));
        
        return new Result<Boolean>(true, ResultCode.SUCCESS);
    }
    
   /**
    * 通知所有客户端
    * @param executableTask
    * @param clientTotal
    * @param clientSuccess
    */
    private void notifyAllClient(ExecutableTask executableTask, AtomicInteger clientTotal, AtomicInteger clientSuccess) {
    	
    	if(! isRootTask(executableTask.getTaskSnapshot())) {
    		return ;
    	}
    	
    	List<RemoteMachine> clientMachineList = serverRemoting.getRemoteMachines(
        		GroupIdUtil.generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(),
        				serverConfig.getJobBackupAmount(), executableTask.getJob().getClientGroupId()), executableTask.getJob().getId());
    	
    	if(CollectionUtils.isEmpty(clientMachineList)) {
    		logger.error("[ServerServiceImpl]: notifyAllClient clientMachineList isEmpty error"
            		+ ", job:" + executableTask.getJob() 
            		+ ", jobInstanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
            		+ ", compensation:" + executableTask.isCompensation() 
            		+ ", length:" + executableTask.getLength());
    		return ;
    	}
    	
    	int maxThreads = executableTask.getJob().getMaxThreads();
    	
    	if(maxThreads > 0) {
    		
    		/** 随机列表顺序 */
    		Collections.shuffle(clientMachineList);
    		
    		if(maxThreads < clientMachineList.size()) {
    			clientMachineList = clientMachineList.subList(0, maxThreads);
    		}
    		
    		for(int i = 0 ; i < maxThreads ; i ++) {
    			
    			int m = i % clientMachineList.size();
    			clientMachineList.get(m).getRunThreads().incrementAndGet();
    		}
    	}
    	
    	for (RemoteMachine client : clientMachineList) {
        	
    		clientTotal.incrementAndGet();
    		
        	Result<Boolean> executeResult = null;
            try {
            	
            	if(client.getRunThreads().get() > 0) {
            		executableTask.setRunThreads(client.getRunThreads().get());
            	}
            	
				InvocationContext.setRemoteMachine(client);
				executeResult = clientService.executeTask(executableTask);
			} catch (Throwable e) {
				logger.error("[ServerServiceImpl]: executeTask error"
						+ ", executableTask:" + executableTask 
						+ ", client:" + client, e);
			}
            
            if(executeResult != null && executeResult.getData().booleanValue()) {
            	clientSuccess.incrementAndGet();
            } else {
            	logger.error("[ServerServiceImpl]: executeTask failed"
            			+ ", executableTask:" + executableTask 
            			+ ", client:" + client 
            			+ ", executeResult:" + executeResult);
            }
            
        }
    }
    
    /**
     * 通知所有服务端
     * @param executableTask
     * @param serverTotal
     * @param serverSuccess
     */
    private void notifyAllServer(ExecutableTask executableTask, AtomicInteger serverTotal, AtomicInteger serverSuccess) {
    	
    	List<String> serverList = clientRemoting.getServerList();
    	if(CollectionUtils.isEmpty(serverList)) {
    		logger.error("[ServerServiceImpl]: notifyAllServer serverList isEmpty error, serverList is empty"
    				+ ", job:" + executableTask.getJob() 
            		+ ", jobInstanceSnapshot:" + executableTask.getJobInstanceSnapshot() 
            		+ ", compensation:" + executableTask.isCompensation() 
            		+ ", length:" + executableTask.getLength());
    		return ;
    	}
    	
    	for(String server : serverList) {
    		
    		serverTotal.incrementAndGet();
    		
    		Result<Boolean> notifyResult = null;
    		try {
				InvocationContext.setRemoteMachine(new RemoteMachine(server));
				notifyResult = serverService.notifyEvent(executableTask);
			} catch (Throwable e) {
				logger.error("[ServerServiceImpl]: notify error"
            			+ ", executableTask:" + executableTask 
            			+ ", server:" + server, e);
			}
    		
    		if(notifyResult != null && notifyResult.getData().booleanValue()) {
    			serverSuccess.incrementAndGet();
            } else {
            	logger.error("[ServerServiceImpl]: notify failed"
            			+ ", executableTask:" + executableTask 
            			+ ", server:" + server 
            			+ ", notifyResult:" + notifyResult);
            }
    	}
    }

    /**
     * 通知服务器有任务了
     */
    @Override
	public Result<Boolean> notifyEvent(ExecutableTask executableTask) {
    	
    	RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
//    	logger.info("[ServerServiceImpl]: taskEvent showLogOnly, jobId:" + executableTask.getJob().getId() + ", instanceId:" + executableTask.getJobInstanceSnapshot().getId() + ", RemoteAddress:" + remoteMachine.getRemoteAddress());
    	return new Result<Boolean>(true, ResultCode.SUCCESS);
	}

	/**
     * 从服务端拉取任务快照列表
     */
    @Override
    public Result<ExecutableTask> pull(ExecutableTask executableTask) {
    	long startTime = System.currentTimeMillis();
    	RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
    	JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = jobInstanceManager.get(executableTask.getJobInstanceSnapshot().getId());
		} catch (Throwable e) {
			logger.error("[ServerServiceImpl]: pull get jobInstanceSnapshot error, executableTask:" + executableTask, e);
		}
    	if(null == jobInstanceSnapshot) {
//    		logger.error("[ServerServiceImpl]: pull jobInstanceSnapshot is null error, executableTask:" + executableTask);
        	return new Result<ExecutableTask>(null, ResultCode.PULL_OVER);
    	}
    	
    	if(JOB_INSTANCE_STATUS_FINISHED == jobInstanceSnapshot.getStatus() 
    			|| JOB_INSTANCE_STATUS_FAILED == jobInstanceSnapshot.getStatus() 
    			|| JOB_INSTANCE_STATUS_RETRY == jobInstanceSnapshot.getStatus() 
    			|| JOB_INSTANCE_STATUS_RETRY_FINISHED == jobInstanceSnapshot.getStatus() 
    			|| JOB_INSTANCE_STATUS_RETRY_OVER == jobInstanceSnapshot.getStatus() 
    			|| JOB_INSTANCE_STATUS_DELETE_SELF == jobInstanceSnapshot.getStatus()) {
//    		logger.error("[ServerServiceImpl]: pull JOB_INSTANCE_FINISHED error, executableTask:" + executableTask);
    		return new Result<ExecutableTask>(null, ResultCode.PULL_OVER);
    	}
    	
        try {
        	
        	//抢锁
        	long tasksTime = System.currentTimeMillis();
            boolean hasLock = jobInstanceManager.acquireLock(jobInstanceSnapshot.getId(), jobInstanceSnapshot.getOffset());
            long lockTime = System.currentTimeMillis();
            
            if (hasLock) {
            	
            	long nextOffset = 0L;
            	int updateLockResult = 0;
                List<TaskSnapshot> tasks = null;
                try {
                    tasks = getTaskSnapshotAccess().queryAvailableTaskPage(jobInstanceSnapshot.getId(), jobInstanceSnapshot.getOffset(),
                            executableTask.getLength());
                    // release the lock and update offset
                    JobInstanceSnapshot updater = new JobInstanceSnapshot();
                    updater.setId(jobInstanceSnapshot.getId());
                    updater.setLocked(false);
                    if (! CollectionUtils.isEmpty(tasks)) {
                    	nextOffset = ListUtil.acquireLastObject(tasks).getId();
                        updater.setOffset(nextOffset);
                    }
                    updateLockResult = jobInstanceManager.getJobInstanceSnapshotAccess().updateLockAndOffset(updater);
                } catch (Throwable e) {
                    logger.error("[ServerServiceImpl]: pull queryAvailableTaskPage error, executableTask:" + executableTask, e);
                }
                if(CollectionUtils.isEmpty(tasks)) {
                    return new Result<ExecutableTask>(null, ResultCode.PULL_TASK_LIST_OVER);
                }

                for (TaskSnapshot task : tasks) {
                    task.setClientId(remoteMachine.getClientId());
                }
                
                int updateClientIdResult = 0;
                if(tasks != null && ! tasks.isEmpty()) {
                	updateClientIdResult = getTaskSnapshotAccess().updateClientIdBatch(tasks, remoteMachine.getClientId(), TASK_STATUS_QUEUE);
                }
                executableTask.setTaskSnapshotList(tasks);
                ServerJobInstanceMapping.JobInstanceKey jobInstanceKey = new ServerJobInstanceMapping.JobInstanceKey();
                jobInstanceKey.setJobId(executableTask.getJob().getId());
                jobInstanceKey.setJobType(executableTask.getJob().getType());
                jobInstanceKey.setJobInstanceId(executableTask.getJobInstanceSnapshot().getId());
                jobInstanceKey.setGroupId(remoteMachine.getGroupId());
                jobInstanceKey.setCompensation(executableTask.isCompensation());
                
                long addTime = System.currentTimeMillis();
                LivingTaskManager.getSingleton().add(jobInstanceKey, remoteMachine);
                
                long nowTime = System.currentTimeMillis();
                
                logger.info("[ServerServiceImpl]: pull"
                		+ ", size:" + tasks.size() 
                		+ ", offset:" + jobInstanceSnapshot.getOffset() 
                		+ ", jobInstanceKey:" + jobInstanceKey 
                		+ ", remoteAddress:" + remoteMachine.getRemoteAddress() 
                		+ ", clientId:" + remoteMachine.getClientId() 
                		+ ", startTime:" + (nowTime - startTime) 
                		+ ", lockTime:" + (lockTime - tasksTime) 
                		+ ", addTime:" + (nowTime - addTime) 
                		+ ", updateClientIdResult:" + updateClientIdResult 
                		+ ", nextOffset:" + nextOffset 
                		+ ", updateLockResult:" + updateLockResult);
                return new Result<ExecutableTask>(executableTask, ResultCode.PULL_TASK_LIST_SUCCESS);
            } else {
                return new Result<ExecutableTask>(null, ResultCode.PULL_TASK_GET_LOCK_FAILURE);
            }
        } catch (Throwable e) {
        	logger.error("[ServerServiceImpl]: Error happens when pulling task from server, executableTask:" + executableTask, e);
        	return new Result<ExecutableTask>(null, ResultCode.FAILURE);
        }
    }

    /**
     * 设置全局用户自定义参数
     */
    @Override
    public Result<Boolean> setGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot,
                                              String globalArguments) {
        return jobInstanceManager.setGlobalArguments(jobInstanceSnapshot, globalArguments.getBytes());
    }

    /**
     * 获取设置的全局变量
     */
    @Override
    public Result<String> getGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot) {
        return jobInstanceManager.getGlobalArguments(jobInstanceSnapshot);
    }

    /**
     * 移除活跃任务
     */
    @Override
    public Result<Boolean> removeLivingTask(JobInstanceKey key) {
        Result<Boolean> result = new Result<Boolean>(false);
        try {
            LivingTaskManager.getSingleton().remove(key);
        } catch (Throwable e) {
            logger.error("[ServerServiceImpl]: removeLivingTask error, key:" + key, e);
            result.setResultCode(ResultCode.FAILURE);
            return result;
        }
        result.setData(true);
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    @Override
    public Result<Boolean> callDependencyJob(long beforeJobId, long dependencyJobId, long jobInstanceId, Date fireTime, long lastJobId, String uniqueId) {
        Result<Boolean> result = new Result<Boolean>();

        JobRelation jobRelation = new JobRelation();
        jobRelation.setJobId(dependencyJobId);
        jobRelation.setBeforeJobId(beforeJobId);
        Result<JobRelation> relationResult = jobRelationManager.queryRelation(jobRelation);
        if(relationResult.getResultCode() == ResultCode.SUCCESS) {
            if(relationResult.getData() == null) {
                result.setResultCode(ResultCode.NO_SUCH_RELATION);
                result.setData(false);
                return result;
            }
        } else {
            result.setResultCode(relationResult.getResultCode());
            result.setData(false);
            return result;
        }

        JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
        jobInstanceSnapshot.setId(jobInstanceId);
        boolean aquireLock = jobInstanceManager.acquireFinishCountLock(lastJobId, jobInstanceSnapshot, dependencyJobId);
        logger.info("[jobInstanceSnapshotId]:jobInstanceId->" + jobInstanceId 
        		+ ", aquireLock:" + aquireLock 
        		+ ", jobRelation:" + jobRelation 
        		+ ", relationResult:" + relationResult 
        		+ ", lastJobId:" + lastJobId 
        		+ ", uniqueId:" + uniqueId);
        if(aquireLock) {
            // 更新后置计数器;
            jobRelation = relationResult.getData();
            jobRelation.setFinishCount(jobRelation.getFinishCount() + 1);
            logger.info("[jobRelation]:->jobId" + jobRelation.getJobId() 
            		+ ";before:" + jobRelation.getBeforeJobId() 
            		+ ", jobInstanceId:" + jobInstanceId 
            		+ ", lastJobId:" + lastJobId 
            		+ ", uniqueId:" + uniqueId);
            jobRelationManager.updateJobRelation(jobRelation);
        } else {
            logger.info("[acquireFinishCountLock]:failed, jobInstanceId:" + jobInstanceId 
            		+ ", lastJobId:" + lastJobId 
            		+ ", uniqueId:" + uniqueId);
        }
//        jobRelation = relationResult.getData();
//        long oldJobInstanceId = jobRelation.getJobInstanceId();
//        // 做两个判断;
//        if(oldJobInstanceId >= jobInstanceId) {
////            result.setData(false);
////            result.setResultCode(ResultCode.NO_NEED_CALL_DEPENDENCY);
//            logger.warn("instance id is smaller than db");
////            return result;
//        } else if(oldJobInstanceId < jobInstanceId) {
//            // 更新;
//            jobRelation.setJobInstanceIdTag(1);
//            jobRelation.setJobInstanceId(jobInstanceId);
//            jobRelation.setFinishCount(jobRelation.getFinishCount() + 1);
//            logger.warn("update instance id,and can run next!");
//            // 更新;
//            jobRelationManager.updateJobRelation(jobRelation);
//        }

        // 检查Job依赖的Job是否都执行完了;
        Result<Boolean> checkResult = jobRelationManager.checkAllBeforeDone(jobRelation);
        if(!checkResult.getData()) {
            logger.warn("before not add done,cannot run!");
            result.setResultCode(checkResult.getResultCode());
            return result;
        }
        logger.warn("relation job can start! job id:" + dependencyJobId 
        		+ ",job instance id:" + jobInstanceId 
        		+ ", uniqueId:" + uniqueId);
        // 依赖的都执行完
        Job query = new Job();
        query.setId(dependencyJobId);
        Job job = null;
        try {
            job = jobPool.loadJob(query);
        } catch (Throwable e) {
            logger.error("[ServerServiceImpl]: loadJob error"
            		+ ", query:" + query.toString() 
            		+ ", uniqueId:" + uniqueId, e);
            result.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_ERROR);
            result.setData(false);
            return result;
        }
        if (null == job) {
            logger.error("[ServerServiceImpl]: job is null"
            		+ ", job:" + query 
            		+ ", uniqueId:" + uniqueId);
            result.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_IS_NULL);
            result.setData(false);
            return result;
        }
        
        if(CommonUtil.isSimpleJob(job.getType())) {
        	
        	JobRelation queryRelation = new JobRelation();
        	queryRelation.setJobId(dependencyJobId);
        	Result<List<JobRelation>> beforeJobResult = jobRelationManager.queryBeforeJob(queryRelation);
        	
        	if(null == beforeJobResult || CollectionUtils.isEmpty(beforeJobResult.getData())) {
        		logger.error("[ServerServiceImpl]: queryBeforeJob error"
        				+ ", queryRelation:" + queryRelation 
        				+ ", uniqueId:" + uniqueId);
        	} else {
        		for(JobRelation relation : beforeJobResult.getData()) {
        			if(! jobInstanceManager.isNewestInstanceSuccess(relation.getBeforeJobId())) {
        				logger.error("[ServerServiceImpl]: callDependencyJob before job failed, so return"
        						+ ", relation:" + relation 
        						+ ", jobRelation:" + jobRelation 
        						+ ", uniqueId:" + uniqueId);
        				result.setResultCode(ResultCode.BEFORE_JOB_ERROR);
        	            result.setData(false);
        	            return result;
        			}
        		}
        	}
        }
        
        List<String> machineList = new ArrayList<String>();
        
        Result<Boolean> runResult = jobManager.fireJob(job, fireTime, uniqueId, machineList);
        // 清理;
        jobRelationManager.resetJobRelation(jobRelation);

        result.setResultCode(runResult.getResultCode());
        result.setData(runResult.getData());
        return result;
    }

    /**
     * 获取分组下面的Job列表
     */
	@Override
	public List<Job> acquireJobList(String groupId) {
		ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
		return jobManager.queryJobByGroupId(clientGroup);
	}

	@Override
	public int stopAllInstance(final long jobId) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				logger.info("[ServerServiceImpl]: stopAllInstance, jobId:" + jobId);
				
				Job query = new Job();
		        query.setId(jobId);
		        Job job = null;
		        try {
		            job = jobPool.loadJob(query);
		        } catch (Throwable e) {
		            logger.error("[ServerServiceImpl]: stopAllInstance loadJob error, query:" + query.toString(), e);
		            return ;
		        }
		        
		        if(null == job) {
		            logger.error("[ServerServiceImpl]: stopAllInstance job is null, job:" + query);
		            return ;
		        }
		        
		        int amount = 0;
		        
		        try {
					List<JobInstanceSnapshot> jobInstanceList = jobInstanceManager.queryInstanceListPaging(jobId, 0L);
					while(! CollectionUtils.isEmpty(jobInstanceList)) {
						
						amount += jobInstanceManager.finishAllJobInstance(job, jobInstanceList);
						
						jobInstanceList = jobInstanceManager.queryInstanceListPaging(jobId, ListUtil.acquireLastObject(jobInstanceList).getId());
					}
				} catch (Throwable e) {
					 logger.error("[ServerServiceImpl]: stopAllInstance finishAllJobInstance error, job:" + query);
				}
		        
		        logger.info("[ServerServiceImpl]: stopAllInstance, jobId:" + jobId + ", amount:" + amount);
		        
			}
			
		}).start();
		
		return 1;
	}

	@Override
	public List<RemoteMachine> getRemoteMachines(String groupId, long jobId) {

		List<RemoteMachine> remoteMachineList = serverRemoting.getRemoteMachines(groupId, jobId);
		if(null == remoteMachineList || remoteMachineList.size() <= 0) {
			return new ArrayList<RemoteMachine>();
		}
		
		return remoteMachineList;
	}

	@Override
	public Result<Boolean> warningSwitch(boolean warningSwitch) {
		Result<Boolean> result = new Result<Boolean>();
		serverConfig.setWarningSwitch(warningSwitch);
		logger.info("[ServerServiceImpl]: warningSwitch, serverConfig:" + serverConfig);
		result.setData(true);
		result.setResultCode(ResultCode.SUCCESS);
		return result;
	}

}