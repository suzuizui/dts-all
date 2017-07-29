package com.le.dts.client.executor.parallel.processor;

import com.le.dts.client.executor.job.processor.FailureJobProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 默认失败任务处理器
 * @author tianyao.myc
 *
 */
public class DefaultFailureJobProcessor implements FailureJobProcessor {

	private static final Log logger = LogFactory.getLog(DefaultFailureJobProcessor.class);
	
	/**
	 * 处理失败任务，这里只是打印日志
	 */
	@Override
	public void process(FailureJobContext context) {
		logger.error("Task:" + context.getTask() + " process failed"
				+ ", jobId:" + context.getJob().getId() 
				+ ", instanceId:" + context.getJobInstanceSnapshot().getId() 
				+ ", fireTime:" + context.getJobInstanceSnapshot().getFireTime() 
				+ ", retryCount:" + context.getRetryCount(), context.getE());
	}

}
