package com.le.dts.client.executor.stop.processor;

import com.le.dts.client.executor.job.context.JobContext;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;

/**
 * 停止job处理器上下文
 * @author tianyao.myc
 *
 */
public class StopJobContext extends JobContext {

	public StopJobContext(Job job, JobInstanceSnapshot jobInstanceSnapshot, int retryCount) {
		super(job, jobInstanceSnapshot, retryCount);
	}

}
