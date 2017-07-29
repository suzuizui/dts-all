package com.le.dts.client.service;

import java.util.concurrent.atomic.AtomicLong;

import com.le.dts.client.context.ClientContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.ExecutableTask;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.service.ClientService;

/**
 * 客户端通用基础服务
 * @author tianyao.myc
 *
 */
public class ClientServiceImpl implements ClientService, ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(ClientServiceImpl.class);
	
	/**
	 * 心跳检查
	 */
	@Override
	public Result<String> heartBeatCheck() {
		RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
		AtomicLong counter = null;
		try {
			counter = clientRemoting.getHeartBeatCounter4Increase(remoteMachine.getRemoteAddress());
			counter.incrementAndGet();
		} catch (Throwable e) {
			logger.error("[ClientServiceImpl]: heartBeatCheck error, server:" + remoteMachine.getRemoteAddress(), e);
			return new Result<String>("something wrong with " + clientConfig.getLocalAddress(), ResultCode.FAILURE);
		}
		return new Result<String>("I am alive ! counter:" + counter, ResultCode.SUCCESS);
	}

	/**
	 * 心跳检查任务状态
	 */
    @Override 
    public Result<String> heartBeatCheckJobInstance(int jobType, long jobId, long jobInstanceId) {
        return executor.heartBeatCheckJobInstance(jobType, jobId, jobInstanceId);
    }

    /**
	 * 执行简单触发任务
	 */
    @Override
	public Result<Boolean> executeTask(ExecutableTask executableTask) {
		return executor.executeTask(executableTask);
	}

	/**
	 * 停止任务
	 */
	@Override
	public Result<Boolean> stopTask(int jobType, long jobId, long jobInstanceId) {
		return executor.stopTask(jobType, jobId, jobInstanceId);
	}

	/**
	 * 强制停止任务
	 */
	@Override
	public Result<Boolean> forceStopTask(ExecutableTask executableTask) {
		return executor.forceStopTask(executableTask);
	}

	/**
	 * 推任务
	 */
	@Override
	public Result<Boolean> push(int jobType, long jobId, long jobInstanceId, TaskSnapshot taskSnapshot) {
		return executor.push(jobType, jobId, jobInstanceId, taskSnapshot);
	}

}
