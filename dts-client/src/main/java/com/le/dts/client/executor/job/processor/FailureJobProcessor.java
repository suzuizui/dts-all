package com.le.dts.client.executor.job.processor;

import com.le.dts.client.executor.parallel.processor.FailureJobContext;

/**
 * 失败任务处理器
 * 让用户自行处理失败的任务逻辑，可以打印日志，也可放入业务重试表自己重试
 * @author tianyao.myc
 *
 */
public interface FailureJobProcessor {

	/**
	 * 处理失败的任务
	 * @param context
	 */
	public void process(FailureJobContext context);
	
}
