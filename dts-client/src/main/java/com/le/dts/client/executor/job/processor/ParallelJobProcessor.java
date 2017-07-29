package com.le.dts.client.executor.job.processor;

import com.le.dts.client.executor.parallel.processor.ParallelJobContext;
import com.le.dts.common.domain.result.ProcessResult;

/**
 * 并行job处理器
 * @author tianyao.myc
 *
 */
public interface ParallelJobProcessor {

	/**
	 * 处理并行计算job的方法
	 */
	public ProcessResult process(ParallelJobContext context);
	
}
