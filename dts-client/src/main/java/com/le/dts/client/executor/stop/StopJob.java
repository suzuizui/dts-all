package com.le.dts.client.executor.stop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.le.dts.client.executor.job.processor.StopJobProcessor;
import com.le.dts.client.executor.stop.processor.StopTaskProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;

/**
 * 终止job
 * @author tianyao.myc
 *
 */
public class StopJob implements ClientContext, Constants {
	
	private static final Log logger = LogFactory.getLog(StopJob.class);

	/** 指令队列 */
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	/** 消息消费线程池 */
	private ThreadPoolExecutor executor = null;
	
	public StopJob() {
		
		this.executor = new ThreadPoolExecutor(0, 30, 
				30 * 1000L, TimeUnit.MILLISECONDS, this.queue, new ThreadFactory(){

			int index = 0;
			
			public Thread newThread(Runnable runnable) {
				index ++;
				return new Thread(runnable, "DTS-StopJobProcessor-" + index);
			}
			
		});
		
	}
	
	/**
	 * 停止任务
	 * @param jobId
	 * @param jobInstanceId
	 */
	public void stopTask(long jobId, long jobInstanceId) {
		
		StopJobProcessor stopJobProcessor = clientConfig.getStopJobProcessor();
		if(null == stopJobProcessor) {
			return ;
		}
		
		try {
			this.executor.execute(new StopTaskProcessor(jobId, jobInstanceId, stopJobProcessor));
		} catch (Throwable e) {
			logger.error("[StopJob]: execute error"
					+ ", jobId:" + jobId 
					+ ", jobInstanceId:" + jobInstanceId, e);
		}
		
	}
	
}
