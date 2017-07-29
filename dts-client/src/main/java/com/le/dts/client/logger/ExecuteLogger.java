package com.le.dts.client.logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.le.dts.client.logger.timer.CleanLogTimer;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.logger.DtsLogger;

/**
 * 执行日志
 * @author tianyao.myc
 *
 */
public class ExecuteLogger {

	/** 定时调度服务 */
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, "DTS-Clean-Log-thread");
				}
				
			});
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		//初始化清除日志定时器
		initCleanLogTimer();
	}
	
	/**
	 * 初始化清除日志定时器
	 * @throws InitException
	 */
	public void initCleanLogTimer() throws InitException {
		try {
			executorService.scheduleAtFixedRate(new CleanLogTimer(), 
					0L, 5 * 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new InitException("[ExecuteLogger]: initCleanLogTimer error", e);
		}
	}
	
	/**
	 * 读日志文件
	 * @param jobId
	 * @param instanceId
	 * @return
	 */
	public List<String> readLog(long jobId, long instanceId) {
		return DtsLogger.read(jobId, instanceId);
	}
	
}
