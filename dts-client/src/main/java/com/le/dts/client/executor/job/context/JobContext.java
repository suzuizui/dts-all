package com.le.dts.client.executor.job.context;

import com.le.dts.client.context.ClientContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;

/**
 * job上下文
 * @author tianyao.myc
 *
 */
public class JobContext implements Constants, ClientContext {

	/** 当前Task的Job配置 */
	protected Job job;
	
	/** 当前Task的Job实例 */
	protected JobInstanceSnapshot jobInstanceSnapshot;
	
	/** 重试次数 */
	protected int retryCount;
	
	public JobContext(Job job, JobInstanceSnapshot jobInstanceSnapshot, int retryCount) {
		this.job = job;
		this.jobInstanceSnapshot = jobInstanceSnapshot;
		this.retryCount = retryCount;
	}
	
	public Job getJob() {
		return job;
	}

	public JobInstanceSnapshot getJobInstanceSnapshot() {
		return jobInstanceSnapshot;
	}

	public int getRetryCount() {
		return retryCount;
	}

	protected void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

}
