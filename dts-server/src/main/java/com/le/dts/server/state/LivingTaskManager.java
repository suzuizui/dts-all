package com.le.dts.server.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.monitor.callback.Display;
import com.le.dts.server.state.timer.PushTimer;
import com.le.dts.server.store.JobAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.exception.AccessException;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.ExceptionUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.ListUtil;
import com.le.dts.common.util.LoggerUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.StringUtil;
import com.le.dts.server.store.JobInstanceSnapshotAccess;
import com.le.dts.server.store.ServerJobInstanceMappingAccess;

/**
 * Created by Moshan on 14-12-5.
 */
public class LivingTaskManager implements ServerContext, Constants {

    public static final Log logger = LogFactory.getLog("livingTaskManager");
    private static LivingTaskManager singleton = null;
    private ConcurrentHashMap<ServerJobInstanceMapping.JobInstanceKey, List<RemoteMachine>> livingJobInstanceClientMachineMap = 
    		new ConcurrentHashMap<ServerJobInstanceMapping.JobInstanceKey, List<RemoteMachine>>();
    /**
     * When server restarts we don't have ClientMachine, so we need to store ip and
     * sync it to livingJobInstanceClientMachineMap when clients connect.
     */
    private ConcurrentHashMap<String, List<ServerJobInstanceMapping.JobInstanceKey>> livingJobInstanceClientIdMap = 
    		new ConcurrentHashMap<String, List<ServerJobInstanceMapping.JobInstanceKey>>();
    
//    private ConcurrentHashMap<Long, AtomicInteger> counterMap = new ConcurrentHashMap<Long, AtomicInteger>();
    
    private final ConcurrentHashMap<Long, ServerJobInstanceMapping.JobInstanceKey> processorMap = 
    		new ConcurrentHashMap<Long, ServerJobInstanceMapping.JobInstanceKey>();
    
    private ServerJobInstanceMappingAccess serverJobInstanceMappingAccess = store
            .getServerJobInstanceMappingAccess();
    private JobInstanceSnapshotAccess jobInstanceSnapshotAccess = store.getJobInstanceSnapshotAccess();
    private JobAccess jobAccess = store.getJobAccess();
    
    /** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(2, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					
					return new Thread(runnable, "DTS-instance-check-thread");
				}
				
			});
	
	private RemoteTaskStatusSniffer remoteTaskStatusSniffer = new RemoteTaskStatusSniffer();
    
    private LivingTaskManager() {

    }

    public synchronized static LivingTaskManager getSingleton() {
        if (singleton == null)
            singleton = new LivingTaskManager();
        return singleton;
    }

    public void startSniffer(ScheduledExecutorService executorService) {
        executorService.scheduleAtFixedRate(remoteTaskStatusSniffer,
                0L, serverConfig.getHeartBeatIntervalTime(), TimeUnit.MILLISECONDS);
    }
    
    /**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
	
		//This method should only be invoked when server starts.
		restoreFromDatabase();
		
		//初始化推送定时器
		initPushTimer();
		
		//添加展现
		serverMonitor.addDisplay(new Display(){

			@Override
			public String content() {
				
				//检查processorMap并发出报警信息
				serverMonitor.checkProcessorMapAndAlertMsg(processorMap.size());
				
				return "processorMap > size:" + processorMap.size();
			}
			
		});
		
	}
	
	/**
	 * 初始化推送定时器
	 * @throws InitException
	 */
	private void initPushTimer() throws InitException {
		
		try {
			executorService.scheduleAtFixedRate(new PushTimer(this),
					1 * 60 * 1000L, 1 * 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[LivingTaskManager]: initPushTimer error", e);
		}
		
		logger.warn("[LivingTaskManager]: initPushTimer success");
	}
	
    public void add(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey, RemoteMachine client) {
    	long startTime = System.currentTimeMillis();
    	long addTime = 0L;
    	long addResult = 0L;
        try {
			addJobInstance4MachineMap(jobInstanceKey, client);
			
			addJobInstance4IdMap(jobInstanceKey, client.getClientId());
			
			addTime = System.currentTimeMillis();
			
			addResult = addJobInstance4Db(jobInstanceKey, client);
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: add error, jobInstanceKey:" + jobInstanceKey, e);
		}
        long nowTime = System.currentTimeMillis();
        logger.info("[LivingTaskManager]: add jobInstanceKey:" + jobInstanceKey 
        		+ ", startTime:" + (nowTime - startTime) 
        		+ ", updateTime:" + (nowTime - addTime) + ", addResult:" + addResult);
    }

    private void addJobInstance4MachineMap(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey, RemoteMachine client) {
    	try {
    		
//    		counterMap.put(jobInstanceKey.getJobInstanceId(), new AtomicInteger(0));
    		
			List<RemoteMachine> machineList = livingJobInstanceClientMachineMap.get(jobInstanceKey);
			if(null == machineList) {
				machineList = Collections.synchronizedList(new ArrayList<RemoteMachine>());
				List<RemoteMachine> existMachineList = livingJobInstanceClientMachineMap.putIfAbsent(jobInstanceKey, machineList);
				if(existMachineList != null) {
					machineList = existMachineList;
				}
			}
			
			if(! machineList.contains(client)) {
				machineList.add(client);
			}
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: addJobInstance4MachineMap error, jobInstanceKey:" + jobInstanceKey + ", client:" + client, e);
		}
    }
    
    private void addJobInstance4IdMap(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey, String clientId) {
    	try {
    		
			List<ServerJobInstanceMapping.JobInstanceKey> instanceKeyList = livingJobInstanceClientIdMap.get(clientId);
			if(null == instanceKeyList) {
				instanceKeyList = Collections.synchronizedList(new ArrayList<ServerJobInstanceMapping.JobInstanceKey>());
				livingJobInstanceClientIdMap.put(clientId, instanceKeyList);
			}
			
			if(! instanceKeyList.contains(jobInstanceKey)) {
				instanceKeyList.add(jobInstanceKey);
			}
			
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: addJobInstance4IdMap error, jobInstanceKey:" + jobInstanceKey + ", clientId:" + clientId, e);
		}
    }
    
    /**
     * 全量删除
     * @param jobInstanceId
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void removeJobInstance4IdMap(long jobInstanceId) {
    	
    	ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
		key.setJobInstanceId(jobInstanceId);
    	
		Iterator iterator = livingJobInstanceClientIdMap.entrySet().iterator();
		while(iterator.hasNext()) { 
			Map.Entry entry = (Map.Entry)iterator.next();
			
			String clientId = (String)entry.getKey();
			List<ServerJobInstanceMapping.JobInstanceKey> instanceKeyList = (List<ServerJobInstanceMapping.JobInstanceKey>)entry.getValue();
			
			removeJobInstance4IdMap(instanceKeyList, key, clientId);
		}
    }
    
    /**
     * 增量删除
     * @param jobInstanceKey
     * @param clientId
     */
    private void removeJobInstance4IdMap(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey, String clientId) {
    	
    	List<ServerJobInstanceMapping.JobInstanceKey> instanceKeyList = livingJobInstanceClientIdMap.get(clientId);
		if(CollectionUtils.isEmpty(instanceKeyList)) {
			return ;
		}
		
		removeJobInstance4IdMap(instanceKeyList, jobInstanceKey, clientId);
    }
    
    private void removeJobInstance4IdMap(List<ServerJobInstanceMapping.JobInstanceKey> instanceKeyList, 
    		ServerJobInstanceMapping.JobInstanceKey jobInstanceKey, String clientId) {
    	try {
    		
			if(CollectionUtils.isEmpty(instanceKeyList)) {
				return ;
			}
			
			synchronized(instanceKeyList) {
				Iterator<ServerJobInstanceMapping.JobInstanceKey> iterator = instanceKeyList.iterator();
				while(iterator.hasNext()) {
					
					try {
						ServerJobInstanceMapping.JobInstanceKey key = iterator.next();
						if(key.equals(jobInstanceKey)) {
							iterator.remove();
						}
					} catch (Throwable e) {
						logger.error("[LivingTaskManager]: removeJobInstance4IdMap iterator.remove() error, jobInstanceKey:" + jobInstanceKey + ", clientId:" + clientId, e);
					}
					
				}
			}
			
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: removeJobInstance4IdMap error, jobInstanceKey:" + jobInstanceKey + ", clientId:" + clientId, e);
		}
    }
    
    /**
     * 添加实例记录
     * @param jobInstanceKey
     * @param client
     * @return
     */
    private long addJobInstance4Db(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey, RemoteMachine client) {
    	
    	ServerJobInstanceMapping serverJobInstanceMapping = new ServerJobInstanceMapping();
    	serverJobInstanceMapping.setServer(serverConfig.getLocalAddress());
    	serverJobInstanceMapping.setJobInstanceId(jobInstanceKey.getJobInstanceId());
    	serverJobInstanceMapping.setJobId(jobInstanceKey.getJobId());
    	serverJobInstanceMapping.setJobType(jobInstanceKey.getJobType());
    	serverJobInstanceMapping.setGroupId(jobInstanceKey.getGroupId());
    	serverJobInstanceMapping.setCompensation(jobInstanceKey.isCompensation());
    	serverJobInstanceMapping.setClientId(client.getClientId());
    	
    	long id = 0L;
    	try {
			id = serverJobInstanceMappingAccess.insert(serverJobInstanceMapping);
		} catch (Throwable e) {
			
			if(! ExceptionUtil.isDuplicate(e)) {
				logger.error("[LivingTaskManager]: addJobInstance4Db error, jobInstanceKey:" + jobInstanceKey + ", client:" + client, e);
			} else {
				id = -1L;
			}
			
		}
    	
    	return id;
    }
    
    /**
     * 删除实例记录
     * @param jobInstanceKey
     * @return
     */
    private boolean removeJobInstance4Db(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey) {
    	
    	ServerJobInstanceMapping serverJobInstanceMapping = new ServerJobInstanceMapping();
    	serverJobInstanceMapping.setServer(serverConfig.getLocalAddress());
    	serverJobInstanceMapping.setJobInstanceId(jobInstanceKey.getJobInstanceId());
    	
    	int result = 0;
    	try {
			result = serverJobInstanceMappingAccess.delete(serverJobInstanceMapping);
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: removeJobInstance4Db error, jobInstanceKey:" + jobInstanceKey, e);
		}
    	
    	return result > 0 ? true : false;
    }
    
    /**
     * This method should only be invoked when server starts.
     * @throws InitException
     */
    public void restoreFromDatabase() throws InitException {
    	
    	long id = 0L;
    	while(true) {
	    	List<ServerJobInstanceMapping> mappingList = null;
	    	try {
				mappingList = serverJobInstanceMappingAccess.loadByServer(serverConfig.getLocalAddress(), id);
			} catch (Throwable e) {
				throw new InitException("[LivingTaskManager]: restoreFromDatabase error, localAddress:" + serverConfig.getLocalAddress(), e);
			}
	    	
	    	if(CollectionUtils.isEmpty(mappingList)) {
	    		logger.warn("[LivingTaskManager]: restoreFromDatabase mappingList is empty");
	    		return ;
	    	}
	    	
	    	id = ListUtil.acquireLastObject(mappingList).getId();
	    	
	    	initAddJobInstance4IdMap(mappingList);
    	}
    }
    
    public void initAddJobInstance4IdMap(List<ServerJobInstanceMapping> mappingList) {
    	for(ServerJobInstanceMapping mapping : mappingList) {
    		
    		boolean throwable = false;
    		JobInstanceSnapshot jobInstanceSnapshot = null;
        	try {
    			jobInstanceSnapshot = jobInstanceManager.get(mapping.getJobInstanceId());
    		} catch (Throwable e) {
    			logger.error("[LivingTaskManager]: initAddJobInstance4IdMap get JobInstanceSnapshot error, mapping:" + mapping, e);
    			throwable = true;
    		}
        	
        	if(! throwable && null == jobInstanceSnapshot) {
        		logger.error("[LivingTaskManager]: initAddJobInstance4IdMap get JobInstanceSnapshot failed, mapping:" + mapping);
        		continue ;
        	}
        	
        	if(jobInstanceSnapshot != null && JOB_INSTANCE_STATUS_DELETE_SELF == jobInstanceSnapshot.getStatus()) {
        		logger.error("[LivingTaskManager]: initAddJobInstance4IdMap instance finish, mapping:" + mapping);
        		continue ;
        	}
    		
    		ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
			key.setJobInstanceId(mapping.getJobInstanceId());
			key.setJobId(mapping.getJobId());
			key.setJobType(mapping.getJobType());
			key.setGroupId(mapping.getGroupId());
			key.setCompensation(mapping.isCompensation());
    		
    		addJobInstance4IdMap(key, mapping.getClientId());
    	}
    }

    public void remove(ServerJobInstanceMapping.JobInstanceKey jobInstanceKey) {
        try {
        	
			if(removeJobInstance4Db(jobInstanceKey)){
				
				livingJobInstanceClientMachineMap.remove(jobInstanceKey);
				
				removeJobInstance4IdMap(jobInstanceKey.getJobInstanceId());
			}
			
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: remove error, jobInstanceKey:" + jobInstanceKey, e);
		}
        logger.info("[LivingTaskManager]: remove jobInstanceKey:" + jobInstanceKey);
    }

    public void remove(long jobInstanceId) {
        try {
        	
			ServerJobInstanceMapping.JobInstanceKey key = new ServerJobInstanceMapping.JobInstanceKey();
			key.setJobInstanceId(jobInstanceId);
			
			if(removeJobInstance4Db(key)) {
				
				livingJobInstanceClientMachineMap.remove(key);
				
				removeJobInstance4IdMap(jobInstanceId);
			}
			
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: remove error, jobInstanceId:" + jobInstanceId, e);
		}
        logger.info("[LivingTaskManager]: remove jobInstanceId:" + jobInstanceId);
    }

    public boolean hasTask(long jobInstanceId) {
        return livingJobInstanceClientMachineMap.containsKey(jobInstanceId);
    }

    public Map<ServerJobInstanceMapping.JobInstanceKey, List<RemoteMachine>> getLivingJobInstanceClientMachineMap() {
		return livingJobInstanceClientMachineMap;
	}

	public Map<String, List<ServerJobInstanceMapping.JobInstanceKey>> getLivingJobInstanceClientIdMap() {
		return livingJobInstanceClientIdMap;
	}

	public void restoreLivingJobInstanceClientMachineMapOfClient(RemoteMachine remoteMachine) {
        if (StringUtil.isBlank(remoteMachine.getClientId())) {
            logger.error("Blank id should not be used in restoreLivingJobInstanceClientMachineMapOfClient.");
            return ;
        }
        
        List<ServerJobInstanceMapping.JobInstanceKey> instanceKeyList = livingJobInstanceClientIdMap.get(remoteMachine.getClientId());
		if(CollectionUtils.isEmpty(instanceKeyList)) {
			return ;
		}
        
        for (ServerJobInstanceMapping.JobInstanceKey key : instanceKeyList) {
        	
        	addJobInstance4MachineMap(key, remoteMachine);
        }
    }

    public class RemoteTaskStatusSniffer implements Runnable, ServerContext, Constants {

        private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);

        @Override 
        public void run() {
            try {
				checkJobInstanceStatus();
			} catch (Throwable e) {
				logger.error("[LivingTaskManager]: RemoteTaskStatusSniffer run error.", e);
			}
        }

        /**
         * We judge if a job instance is finished here.
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
		private void checkJobInstanceStatus() {
        	
        	String checkId = UUID.randomUUID().toString();
        	
        	logger.info(checkId + ", [LivingTaskManager]: start checkJobInstanceStatus"
        			+ ", mapSize:" + livingJobInstanceClientMachineMap.size() 
        			+ ", keySet:" + LoggerUtil.displayJobInstanceId(livingJobInstanceClientMachineMap.keySet()));
        	
        	int checkAmount = 0;
        	
        	Iterator iterator = livingJobInstanceClientMachineMap.entrySet().iterator();
    		while(iterator.hasNext()) { 
    			Map.Entry entry = (Map.Entry)iterator.next();
    			ServerJobInstanceMapping.JobInstanceKey key = (ServerJobInstanceMapping.JobInstanceKey)entry.getKey();
    			
    			List<RemoteMachine> machineList = (List<RemoteMachine>)entry.getValue();
    			
    			if(CollectionUtils.isEmpty(machineList)) {
    				logger.info(checkId + ", [LivingTaskManager]: checkJobInstanceStatus machineList isEmpty"
    	        			+ ", mapSize:" + livingJobInstanceClientMachineMap.size() + ", key:" + key);
    				continue ;
    			}
    			
    			synchronized(machineList) {
    				Iterator<RemoteMachine> iteratorMachineList = machineList.iterator();
    				while(iteratorMachineList.hasNext()) {
    					
    					RemoteMachine client = iteratorMachineList.next();
    					
    					Result<String> checkResult = null;
                        try {
                            client.setTimeout(serverConfig.getHeartBeatCheckTimeout());
                            InvocationContext.setRemoteMachine(client);
                            checkResult = clientService.heartBeatCheckJobInstance(key.getJobType(), key.getJobId(), key.getJobInstanceId());
                            handCheckResult(key, checkResult, client);
                        } catch (Throwable e) {
                            logger.error("[LivingTaskManager]: task checkJobInstanceStatus error, key:" + key, e);
                            handCheckResult(key, null, client);
                        } finally {
                        	checkAmount ++;
                        }
    					
    				}
    			}
    			
    		}
    		
    		logger.info(checkId + ", [LivingTaskManager]: finish checkJobInstanceStatus"
    				+ ", mapSize:" + livingJobInstanceClientMachineMap.size() + ", checkAmount:" + checkAmount);
        }

        private void handCheckResult(final ServerJobInstanceMapping.JobInstanceKey key, Result<String> ckResult, final RemoteMachine remoteMachine) {
            // This mean job instance is finished or client can't connect.
            if (ckResult == null || ResultCode.HEART_BEAT_CHECK_FAILURE == ckResult.getResultCode() || ResultCode.HEART_BEAT_CHECK_EXIT == ckResult.getResultCode()) {
                if (CommonUtil.isSimpleJob(key.getJobType())) {
                    handleSimpleTaskCheckingFailure(key, (ckResult != null), null == ckResult ? null : ckResult.getResultCode(), remoteMachine.isCrashRetry(), remoteMachine);
                } else {
                    handleParallelTaskCheckingFailure(key, (ckResult != null), null == ckResult ? null : ckResult.getResultCode(), remoteMachine.isCrashRetry(), remoteMachine.getClientId());
                }
            }

        }

        private List<Integer> unfinishedStatusList() {
            List<Integer> result = new ArrayList<Integer>();
            result.add(Constants.TASK_STATUS_INIT);
            result.add(Constants.TASK_STATUS_START);
            result.add(Constants.TASK_STATUS_QUEUE);
            return result;
        }

        private boolean isAllClientsDead(long jobInstanceId) throws AccessException {
            JobInstanceSnapshot jobInstanceSnapshot = jobInstanceSnapshotAccess.get(jobInstanceId);
            
            if(null == jobInstanceSnapshot) {
            	logger.error("[LivingTaskManager]: isAllClientsDead jobInstanceSnapshot is null error"
            			+ ", jobInstanceId:" + jobInstanceId);
            	return true;
            }
            
            Job query = new Job();
            query.setId(jobInstanceSnapshot.getJobId());
            Job job = jobAccess.queryJobById(query);
            
            if(null == job) {
            	logger.error("[LivingTaskManager]: isAllClientsDead job is null error"
            			+ ", jobInstanceSnapshot:" + jobInstanceSnapshot);
            	return true;
            }
            
            List<RemoteMachine> remoteMachineList = serverRemoting
                    .getRemoteMachines(GroupIdUtil
                            .generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(),
                                    serverConfig.getJobBackupAmount(), job.getClientGroupId()), job.getId());
            return CollectionUtils.isEmpty(remoteMachineList);
        }
        
        public boolean isAllClientJobInstanceExit(long jobInstanceId) throws Throwable {
        	JobInstanceSnapshot jobInstanceSnapshot = jobInstanceSnapshotAccess.get(jobInstanceId);
        	
        	if(null == jobInstanceSnapshot) {
            	logger.error("[LivingTaskManager]: isAllClientJobInstanceExit jobInstanceSnapshot is null error"
            			+ ", jobInstanceId:" + jobInstanceId);
            	return true;
            }
        	
            Job query = new Job();
            query.setId(jobInstanceSnapshot.getJobId());
            Job job = jobAccess.queryJobById(query);
            
            if(null == job) {
            	logger.error("[LivingTaskManager]: isAllClientJobInstanceExit job is null error"
            			+ ", jobInstanceSnapshot:" + jobInstanceSnapshot);
            	return true;
            }
            
            List<RemoteMachine> remoteMachineList = serverRemoting
                    .getRemoteMachines(GroupIdUtil
                            .generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(),
                                    serverConfig.getJobBackupAmount(), job.getClientGroupId()), job.getId());
            
            if(CollectionUtils.isEmpty(remoteMachineList)) {
            	
            	logger.warn("[LivingTaskManager]: isAllClientJobInstanceExit remoteMachineList is empty error"
            			+ ", jobInstanceSnapshot:" + jobInstanceSnapshot);
            	return true;
            }
            
            Result<String> checkResult = null;
            int exitCount = 0;
            int checkCount = 0;
            for(RemoteMachine remoteMachine : remoteMachineList) {
            	try {
            		remoteMachine.setTimeout(serverConfig.getHeartBeatCheckTimeout());
                    InvocationContext.setRemoteMachine(remoteMachine);
					checkResult = clientService.heartBeatCheckJobInstance(job.getType(), job.getId(), jobInstanceId);
				} catch (Throwable e) {
					logger.error("[LivingTaskManager]: heartBeatCheckJobInstance error, jobInstanceId:" + jobInstanceId + ", remoteMachine:" + remoteMachine, e);
				}
            	if(checkResult != null) {
            		checkCount ++;
            		if(ResultCode.HEART_BEAT_CHECK_EXIT.equals(checkResult.getResultCode()) || ResultCode.HEART_BEAT_CHECK_FAILURE.equals(checkResult.getResultCode())) {
            			exitCount ++;
            		}
            	}
            }
            return exitCount == checkCount;
        }
        
        public boolean isAllClientJobInstanceOver(long jobInstanceId) throws Throwable {
        	JobInstanceSnapshot jobInstanceSnapshot = jobInstanceSnapshotAccess.get(jobInstanceId);
        	
        	if(null == jobInstanceSnapshot) {
            	logger.error("[LivingTaskManager]: isAllClientJobInstanceOver jobInstanceSnapshot is null error"
            			+ ", jobInstanceId:" + jobInstanceId);
            	return true;
            }
        	
            Job query = new Job();
            query.setId(jobInstanceSnapshot.getJobId());
            Job job = jobAccess.queryJobById(query);
            
            if(null == job) {
            	logger.error("[LivingTaskManager]: isAllClientJobInstanceOver job is null error"
            			+ ", jobInstanceSnapshot:" + jobInstanceSnapshot);
            	return true;
            }
            
            List<RemoteMachine> remoteMachineList = serverRemoting
                    .getRemoteMachines(GroupIdUtil
                            .generateGroupId(serverConfig.getClusterId(), serverConfig.getServerGroupId(),
                                    serverConfig.getJobBackupAmount(), job.getClientGroupId()), job.getId());
            
            if(CollectionUtils.isEmpty(remoteMachineList)) {
            	
            	logger.warn("[LivingTaskManager]: isAllClientJobInstanceOver remoteMachineList is empty error"
            			+ ", jobInstanceSnapshot:" + jobInstanceSnapshot);
            	return true;
            }
            
            Result<String> checkResult = null;
            int exitCount = 0;
            int checkCount = 0;
            for(RemoteMachine remoteMachine : remoteMachineList) {
            	try {
            		remoteMachine.setTimeout(serverConfig.getHeartBeatCheckTimeout());
                    InvocationContext.setRemoteMachine(remoteMachine);
					checkResult = clientService.heartBeatCheckJobInstance(job.getType(), job.getId(), jobInstanceId);
				} catch (Throwable e) {
					logger.error("[LivingTaskManager]: heartBeatCheckJobInstance error, jobInstanceId:" + jobInstanceId + ", remoteMachine:" + remoteMachine, e);
				}
            	if(checkResult != null) {
            		checkCount ++;
            		if(ResultCode.HEART_BEAT_CHECK_FAILURE == checkResult.getResultCode()) {
            			exitCount ++;
            		}
            	}
            }
            return exitCount == checkCount;
        }

        public void handleParallelTaskCheckingFailure(final ServerJobInstanceMapping.JobInstanceKey key,
                final boolean isClientAlive, final ResultCode resultCode, final boolean crashRetry, final String clientId) {
        	try {
        		
    			new Thread(new Runnable() {
    				
					@Override 
					public void run() {
				        try {
				        	
				        	boolean isAllClientsDead = isAllClientsDead(key.getJobInstanceId());
				        	boolean isAllClientJobInstanceExit = isAllClientJobInstanceExit(key.getJobInstanceId());
				        	
				        	int failureCounter = 0;
				            if (!isClientAlive) {
				                List<TaskSnapshot> taskList = store.getTaskSnapshotAccess().queryByStatusAndClient(
				                        key.getJobInstanceId(), clientId, unfinishedStatusList());
				                
				                if(crashRetry) {
				                	failureCounter += handleUnfinishTaskList(taskList, 1);
				                } else {
				                	failureCounter += handleUnfinishTaskList(taskList, 0);
				                }
				                
				                if (isAllClientsDead || isAllClientJobInstanceExit) {
				                    
				                    if(crashRetry) {
				                    	failureCounter += jobInstanceManager.handleInitTaskList(key.getJobInstanceId(), 1);
				                    } else {
				                    	failureCounter += jobInstanceManager.handleInitTaskList(key.getJobInstanceId(), 0);
				                    }
				                    
				                }
				            }
				            if(!isClientAlive) {
				                List<TaskSnapshot> taskList = store.getTaskSnapshotAccess().queryByStatusAndClient(
				                        key.getJobInstanceId(), clientId, unfinishedStatusList());
				                
				                failureCounter +=  handleUnfinishTaskList(taskList, 0);
				                
				                if (isAllClientJobInstanceExit) {
				                	
				                	failureCounter += jobInstanceManager.handleInitTaskList(key.getJobInstanceId(), 0);
				                	
				                }
				            }
				            if(resultCode != null && ResultCode.HEART_BEAT_CHECK_EXIT == resultCode) {
				            	if(isAllClientJobInstanceExit) {
				                    List<TaskSnapshot> taskList = store.getTaskSnapshotAccess().queryByStatusAndClient(
				                            key.getJobInstanceId(), clientId, unfinishedStatusList());

				                    failureCounter += handleUnfinishTaskList(taskList, 0);
				                    failureCounter += jobInstanceManager.handleInitTaskList(key.getJobInstanceId(), 0);
				            	}
				            }
				            
				            if(resultCode != null && ResultCode.HEART_BEAT_CHECK_CRASH == resultCode) {
				            	List<TaskSnapshot> taskList = store.getTaskSnapshotAccess().queryByStatusAndClient(
			                            key.getJobInstanceId(), clientId, unfinishedStatusList());

				            	failureCounter += handleUnfinishTaskList(taskList, 0);
				            	failureCounter += jobInstanceManager.handleInitTaskList(key.getJobInstanceId(), 0);
				            }
				            
//    				            if(isAllClientJobInstanceExit || ResultCode.HEART_BEAT_CHECK_FAILURE == resultCode) {
//    				            	List<TaskSnapshot> taskList = store.getTaskSnapshotAccess().queryByStatusAndClient(
//    			                            key.getJobInstanceId(), clientId, unfinishedStatusList());
//
//    				            	failureCounter += handleUnfinishTaskList(taskList, 0);
//    				            	failureCounter += handleInitTaskList(key.getJobInstanceId(), 0);
//    				            }
				            
				            boolean isNotFinish = store.getTaskSnapshotAccess()
				                    .queryExistsStatus(key.getJobInstanceId(), unfinishedStatusList());
				            
				            logger.info("[LivingTaskManager]: handleParallelTaskCheckingFailure"
				            		+ ", isNotFinish:" + isNotFinish 
				            		+ ", key:" + key 
				            		+ ", isClientAlive:" + isClientAlive 
				            		+ ", resultCode:" + resultCode 
				            		+ ", failureCounter:" + failureCounter 
				            		+ ", isAllClientsDead:" + isAllClientsDead 
				            		+ ", isAllClientJobInstanceExit:" + isAllClientJobInstanceExit 
				            		+ ", clientId:" + clientId);
				            
				            if (! isNotFinish && isAllClientJobInstanceExit) {
				            	
				            	ServerJobInstanceMapping.JobInstanceKey keyExists = processorMap.putIfAbsent(key.getJobInstanceId(), key);
								
								if(null == keyExists) {
									try {
										
										/** 结束Job运行实例 */
										jobInstanceManager.finishJobInstance(key, INVOKE_SOURCE_TIMER, JOB_INSTANCE_STATUS_FINISHED, false);
										
										remove(key);
									} catch (Throwable e) {
										logger.error("[LivingTaskManager]: handleParallelTaskCheckingFailure finishJobInstance error"
							            		+ ", isNotFinish:" + isNotFinish 
							            		+ ", key:" + key 
							            		+ ", isClientAlive:" + isClientAlive 
							            		+ ", resultCode:" + resultCode 
							            		+ ", failureCounter:" + failureCounter 
							            		+ ", isAllClientsDead:" + isAllClientsDead 
							            		+ ", isAllClientJobInstanceExit:" + isAllClientJobInstanceExit 
							            		+ ", clientId:" + clientId, e);
									} finally {
							        	try {
											processorMap.remove(key.getJobInstanceId());
										} catch (Throwable e) {
											logger.error("[LivingTaskManager]: ParallelTask failed to remove instance"
			    				            		+ ", key:" + key 
			    				            		+ ", isClientAlive:" + isClientAlive 
			    				            		+ ", resultCode:" + resultCode, e);
										}
							        }
								}
				            }
				        } catch (Throwable e) {
				            logger.error("[LivingTaskManager]: failed to update the status of job instance"
				            		+ ", key:" + key 
				            		+ ", isClientAlive:" + isClientAlive 
				            		+ ", resultCode:" + resultCode, e);
				        }
				    }
				}).start();
        			
				
			} catch (Throwable e) {
				logger.error("[LivingTaskManager]: handleParallelTaskCheckingFailure start error"
						+ ", key:" + key 
						+ ", isClientAlive:" + isClientAlive 
						+ ", resultCode:" + resultCode, e);
			}
        }

        private int handleUnfinishTaskList(List<TaskSnapshot> taskList, int retryCount) {

        	int failureCounter = 0;
        	
        	if(! CollectionUtils.isEmpty(taskList)) {
        		try {
					failureCounter = store.getTaskSnapshotAccess().setFailureAndRetryCountBatch(taskList, retryCount);
				} catch (Throwable e) {
					logger.error("[LivingTaskManager]: handleUnfinishTaskList error"
							+ ", retryCount:" + retryCount, e);
				}
        	}
        	
        	return failureCounter;
        }
        
//        private int handleInitTaskList(long jobInstanceId, int retryCount) {
//        	
//        	int failureCounter = 0;
//        	
//        	List<TaskSnapshot> taskSnapshotList = taskSnapShotManager.queryInitTask(jobInstanceId);
//        	while(! CollectionUtils.isEmpty(taskSnapshotList)) {
//        		
//        		try {
//					failureCounter += store.getTaskSnapshotAccess().setFailureAndRetryCountBatch(taskSnapshotList, retryCount);
//				} catch (Throwable e) {
//					logger.error("[LivingTaskManager]: handleInitTaskList error"
//							+ ", jobInstanceId:" + jobInstanceId 
//							+ ", retryCount:" + retryCount, e);
//				}
//        		
//        		taskSnapshotList = taskSnapShotManager.queryInitTask(jobInstanceId);
//        	}
//        	
//        	return failureCounter;
//        }
        
        public void handleSimpleTaskCheckingFailure(final ServerJobInstanceMapping.JobInstanceKey key,
                final boolean isClientAlive, final ResultCode resultCode, final boolean crashRetry, final RemoteMachine remoteMachine) {
            // If heart beat fails, we consider the task succeed and update the status.
        	try {
    			new Thread(new Runnable() {
    				
					@Override 
					public void run() {
				        try {
				        	
				            Map<String, Object> queryObj = new HashMap<String, Object>();
				            queryObj.put("jobInstanceId", key.getJobInstanceId());
				            List<TaskSnapshot> taskSnapshotList = null;
				            try {
								taskSnapshotList = store.getTaskSnapshotAccess().queryByJobInstanceIdAndStatus(queryObj);
							} catch (Throwable e) {
								logger.error("[LivingTaskManager]: queryByJobInstanceIdAndStatus erro"
										+ ", key:" + key 
										+ ", remoteMachine:" + remoteMachine, e);
								return ;
							}
				            
				            int total = (null == taskSnapshotList) ? 0 : taskSnapshotList.size();
				            int finish = 0;
				            boolean isSimpleJobSuccess = true;
				            
				            if(taskSnapshotList != null && ! taskSnapshotList.isEmpty()) {
				            	
				            	for(TaskSnapshot taskSnapshot : taskSnapshotList) {
				            		
				            		if(TASK_STATUS_SUCCESS == taskSnapshot.getStatus()) {
				            			finish ++;
				            			continue ;
				            		}
				            		
				            		if(TASK_STATUS_FAILURE == taskSnapshot.getStatus() 
				            				|| TASK_STATUS_FOUND_PROCESSOR_FAILURE == taskSnapshot.getStatus()) {
				            			finish ++;
				            			isSimpleJobSuccess = false;
				            			continue ;
				            		}
				            		
				            		if(! CommonUtil.isAllJob(key.getJobType())) {
				            			
				            			//更新状态和重试计数
				            			updateStatusAndRetryCount(key, isClientAlive, resultCode, crashRetry, remoteMachine, taskSnapshot);
				            			
				            			finish ++;
				            			
				            		} else {
				            			
				            			String clientId = taskSnapshot.getClientId();
				            			String ip = RemotingUtil.parseIpFromAddress(remoteMachine.getRemoteAddress());
				            			
				            			if(clientId.contains(ip)) {

					            			//更新状态和重试计数
					            			updateStatusAndRetryCount(key, isClientAlive, resultCode, crashRetry, remoteMachine, taskSnapshot);
					            			
					            			finish ++;
					            			
				            			}
				            		}
				            	}
				            	
				            }
				            
				            if(total != finish) {
				            	return ;
				            }

				            logger.info("[LivingTaskManager]: handleSimpleTaskCheckingFailure"
				            		+ ", key:" + key 
				            		+ ", isClientAlive:" + isClientAlive 
				            		+ ", resultCode:" + resultCode 
				            		+ ", remoteMachine:" + remoteMachine 
				            		+ ", total:" + total + ", finish:" + finish);
				            
				            /** 结束Job运行实例 */
				            jobInstanceManager.finishJobInstance(key, INVOKE_SOURCE_TIMER, isClientAlive ? JOB_INSTANCE_STATUS_FINISHED : JOB_INSTANCE_STATUS_FAILED, isSimpleJobSuccess);
				            
				            remove(key);
				        } catch (Throwable e) {
				            logger.error("[LivingTaskManager]: failed to update the status of job instance"
				            		+ ", key:" + key 
				            		+ ", isClientAlive:" + isClientAlive 
				            		+ ", resultCode:" + resultCode 
				            		+ ", remoteMachine:" + remoteMachine, e);
				        }
				    }
				}).start();
			} catch (Throwable e) {
				logger.error("[LivingTaskManager]: handleSimpleTaskCheckingFailure start error"
						+ ", key:" + key 
						+ ", isClientAlive:" + isClientAlive 
						+ ", remoteMachine:" + remoteMachine, e);
			}
        }
    }
    
    /**
     * 更新状态和重试计数
     * @param key
     * @param isClientAlive
     * @param resultCode
     * @param crashRetry
     * @param remoteMachine
     * @param taskSnapshot
     */
    private void updateStatusAndRetryCount(final ServerJobInstanceMapping.JobInstanceKey key,
            final boolean isClientAlive, final ResultCode resultCode, 
            final boolean crashRetry, final RemoteMachine remoteMachine, TaskSnapshot taskSnapshot) {
    	
    	if (isClientAlive) {
			taskSnapshot.setStatus(TASK_STATUS_SUCCESS);
		} else {
			taskSnapshot.setStatus(TASK_STATUS_FAILURE);
			taskSnapshot.setRetryCount(crashRetry ? 1 : 0);
		}
    	
    	int result = 0;
		try {
			result = store.getTaskSnapshotAccess().updateStatusAndRetryCount(taskSnapshot);
		} catch (Throwable e) {
			logger.error("[LivingTaskManager]: updateStatusAndRetryCount error"
            		+ ", key:" + key 
            		+ ", isClientAlive:" + isClientAlive 
            		+ ", resultCode:" + resultCode 
            		+ ", remoteMachine:" + remoteMachine 
            		+ ", taskSnapshot:" + taskSnapshot 
            		+ ", crashRetry:" + crashRetry, e);
		}
		
		if(result <= 0) {
			logger.error("[LivingTaskManager]: updateStatusAndRetryCount failed"
            		+ ", key:" + key 
            		+ ", isClientAlive:" + isClientAlive 
            		+ ", resultCode:" + resultCode 
            		+ ", remoteMachine:" + remoteMachine 
            		+ ", taskSnapshot:" + taskSnapshot 
            		+ ", crashRetry:" + crashRetry);
		}
    }

	public RemoteTaskStatusSniffer getRemoteTaskStatusSniffer() {
		return remoteTaskStatusSniffer;
	}
    
    
}
