package com.le.dts.server.state.timer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.util.VersionUtil;
import com.le.dts.server.state.LivingTaskManager;

/**
 * 推送定时器
 * @author tianyao.myc
 *
 */
public class PushTimer extends TimerTask implements ServerContext, Constants {

	private static final Log logger = LivingTaskManager.logger;
	
	/** 客户端基础服务 */
    private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);
	
	private final LivingTaskManager livingTaskManager;
	
	public PushTimer(LivingTaskManager livingTaskManager) {
		this.livingTaskManager = livingTaskManager;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void run() {

		Map<ServerJobInstanceMapping.JobInstanceKey, List<RemoteMachine>> livingJobInstanceClientMachineMap = 
				this.livingTaskManager.getLivingJobInstanceClientMachineMap();
		
		int instanceAmount = 0;
		final AtomicInteger pushAmount = new AtomicInteger(0);
		final AtomicInteger pushSuccessAmount = new AtomicInteger(0);
		final AtomicInteger pushFailureAmount = new AtomicInteger(0);
		
		Iterator iterator = livingJobInstanceClientMachineMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			ServerJobInstanceMapping.JobInstanceKey key = (ServerJobInstanceMapping.JobInstanceKey)entry.getKey();
			
			//处理实例
			try {
				handleInstance(key, pushAmount, pushSuccessAmount, pushFailureAmount);
			} catch (Throwable e) {
				logger.error("[PushTimer]: handleInstance error, key:" + key, e);
			}
			
			instanceAmount ++;
		}

		logger.info("[PushTimer]: run over"
				+ ", instanceAmount:" + instanceAmount 
				+ ", pushAmount:" + pushAmount.get() 
				+ ", pushSuccessAmount:" + pushSuccessAmount.get() 
				+ ", pushFailureAmount:" + pushFailureAmount.get());
	}

	/**
	 * 处理实例
	 * @param key
	 * @param pushAmount
	 * @param pushSuccessAmount
	 * @param pushFailureAmount
	 */
	private void handleInstance(ServerJobInstanceMapping.JobInstanceKey key, 
			final AtomicInteger pushAmount, AtomicInteger pushSuccessAmount, AtomicInteger pushFailureAmount) {
		
		JobInstanceSnapshot jobInstanceSnapshot = null;
    	try {
			jobInstanceSnapshot = jobInstanceManager.get(key.getJobInstanceId());
		} catch (Throwable e) {
			logger.error("[PushTimer]: handleInstance get jobInstanceSnapshot error, key:" + key, e);
		}
		
    	if(null == jobInstanceSnapshot) {
    		logger.error("[PushTimer]: handleInstance jobInstanceSnapshot is null error, key:" + key);
        	return ;
    	}
    	
    	List<TaskSnapshot> taskSnapshotList = loadSnapshotList(key, jobInstanceSnapshot);
    	while(! CollectionUtils.isEmpty(taskSnapshotList)) {
    		
    		//处理任务列表
    		handleTaskList(key, taskSnapshotList, pushAmount, pushSuccessAmount, pushFailureAmount);
    		
    		taskSnapshotList = loadSnapshotList(key, jobInstanceSnapshot);
    	}
	}
	
	/**
	 * 处理任务列表
	 * @param key
	 * @param taskSnapshotList
	 * @param pushAmount
	 * @param pushSuccessAmount
	 * @param pushFailureAmount
	 */
	private void handleTaskList(ServerJobInstanceMapping.JobInstanceKey key, List<TaskSnapshot> taskSnapshotList, 
			final AtomicInteger pushAmount, AtomicInteger pushSuccessAmount, AtomicInteger pushFailureAmount) {
		
		for(TaskSnapshot taskSnapshot : taskSnapshotList) {
			
			//推送任务
			push(key, taskSnapshot, pushAmount, pushSuccessAmount, pushFailureAmount);
			
		}
		
	}
	
	/**
	 * 推送任务
	 * @param key
	 * @param taskSnapshot
	 * @param pushAmount
	 * @param pushSuccessAmount
	 * @param pushFailureAmount
	 */
	private void push(ServerJobInstanceMapping.JobInstanceKey key, TaskSnapshot taskSnapshot, 
			final AtomicInteger pushAmount, AtomicInteger pushSuccessAmount, AtomicInteger pushFailureAmount) {
		
		pushAmount.incrementAndGet();//计数器递增
		
		List<RemoteMachine> remoteMachineList = serverRemoting.getRemoteMachines(key.getGroupId(), key.getJobId());
		if(CollectionUtils.isEmpty(remoteMachineList)) {
			logger.error("[PushTimer]: push remoteMachineList is empty error"
					+ ", key:" + key 
					+ ", taskSnapshot:" + taskSnapshot.getId());
			return ;
		}
		
		int result = 0;
		while(0 == result) {
			
			//尝试推送
			result = tryPush(key, taskSnapshot, remoteMachineList);
			
			if(1 == result) {
				
				//推送成功就返回 并且计数器递增
				pushSuccessAmount.incrementAndGet();
				
				//设置入队列
//				taskSnapshot.setStatus(TASK_STATUS_QUEUE);
//				updateTaskSnapshot(taskSnapshot);
				
				return ;
			} else if(0 == result) {
				try {
					Thread.sleep(1000L);
				} catch (Throwable e) {
					logger.error("[PushTimer]: push sleep error"
							+ ", key:" + key 
							+ ", taskSnapshot:" + taskSnapshot.getId(), e);
				}
			} else if(-1 == result) {
				
				//推送失败就返回 并且计数器递增
				pushFailureAmount.incrementAndGet();
				
				//所有机器都挂掉就设置失败
				taskSnapshot.setStatus(TASK_STATUS_FAILURE);
				updateTaskSnapshot(taskSnapshot);
				
				return ;
			}
		}
	}
	
	/**
	 * 尝试推送
	 * @param key
	 * @param taskSnapshot
	 * @param remoteMachineList
	 * @return
	 */
	private int tryPush(ServerJobInstanceMapping.JobInstanceKey key, TaskSnapshot taskSnapshot, List<RemoteMachine> remoteMachineList) {
		
		int downAmount = 0;
		for(RemoteMachine remoteMachine : remoteMachineList) {
			
			if(! VersionUtil.isClientPushVersion(remoteMachine.getRemoteVersion())) {
				return -1;
			}
			
			Result<Boolean> pushResult = null;
			try {
				remoteMachine.setTimeout(2 * DEFAULT_INVOKE_TIMEOUT);
				InvocationContext.setRemoteMachine(remoteMachine);
				pushResult = clientService.push(key.getJobType(), key.getJobId(), key.getJobInstanceId(), taskSnapshot);
			} catch (Throwable e) {
				logger.error("[PushTimer]: push error"
						+ ", key:" + key 
						+ ", taskSnapshot:" + taskSnapshot.getId(), e);
			}
			
			if(null == pushResult) {
				downAmount ++;
				continue ;//返回null就是超时了 然后继续下一次循环
			}
			
			if(pushResult.getData().booleanValue()) {
				
				//设置客户端ID
				taskSnapshot.setClientId(remoteMachine.getClientId());
				return 1;//推送成功就返回1
			}
			
			if(ResultCode.PUSH_UNIT_MAP_IS_EMPTY_ERROR.equals(pushResult.getResultCode()) 
					|| ResultCode.PUSH_UNIT_IS_NULL_ERROR.equals(pushResult.getResultCode()) 
					|| ResultCode.PUSH_JOB_TYPE_ERROR.equals(pushResult.getResultCode())) {
				downAmount ++;
			}
		}
		
		//全部挂掉就设置任务失败
		if(downAmount == remoteMachineList.size()) {
			return -1;
		} else {
			return 0;
		}
		
	}
	
	/**
	 * 加载任务列表
	 * @param key
	 * @param jobInstanceSnapshot
	 * @return
	 */
	private List<TaskSnapshot> loadSnapshotList(ServerJobInstanceMapping.JobInstanceKey key, JobInstanceSnapshot jobInstanceSnapshot) {
		
		List<TaskSnapshot> taskSnapshotList = null;
    	try {
			taskSnapshotList = store.getTaskSnapshotAccess().querySkipTaskList(key.getJobInstanceId(), TASK_STATUS_INIT, 0L, jobInstanceSnapshot.getOffset());
		} catch (Throwable e) {
			logger.error("[PushTimer]: loadSnapshotList querySkipTaskList error"
					+ ", key:" + key 
					+ ", jobInstanceSnapshot:" + jobInstanceSnapshot, e);
		}
    	
    	return taskSnapshotList;
	}
	
	/**
	 * 更新快照
	 * @param taskSnapshot
	 */
	private void updateTaskSnapshot(TaskSnapshot taskSnapshot) {
		
		int result = 0;
		try {
			result = store.getTaskSnapshotAccess().updateTaskSnapshot(taskSnapshot);
		} catch (Throwable e) {
			logger.error("[PushTimer]: updateTaskSnapshot error"
					+ ", taskSnapshot:" + taskSnapshot, e);
		}
		
		if(result <= 0) {
			logger.error("[PushTimer]: updateTaskSnapshot failed"
					+ ", taskSnapshot:" + taskSnapshot);
		}
	}
	
}
