package com.le.dts.client.executor.job.processor;

import com.le.dts.client.executor.stop.processor.StopJobContext;

/**
 * 停止处理器
 * @author tianyao.myc
 *
 */
public interface StopJobProcessor {

	/**
	 * 停止job
	 * @param context
	 */
	public void process(StopJobContext context);
	
}
