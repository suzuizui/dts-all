package com.le.dts.client.executor.job.processor;

import com.le.dts.client.executor.simple.processor.SimpleJobContext;
import com.le.dts.common.domain.result.ProcessResult;

/**
 * 简单job处理器
 * @author tianyao.myc
 *
 */
public interface SimpleJobProcessor {

	/**
	 * 处理简单job的方法
	 */
	public ProcessResult process(SimpleJobContext context);
	
}
