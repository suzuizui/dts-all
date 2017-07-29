package com.le.dts.client.executor.parallel.processor;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.job.context.JobContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;

/**
 * 失败job处理器上下文
 * @author tianyao.myc
 *
 */
public class FailureJobContext extends JobContext implements Constants, ClientContext {

	public FailureJobContext(Job job, JobInstanceSnapshot jobInstanceSnapshot, int retryCount) {
		super(job, jobInstanceSnapshot, retryCount);
	}
	
	/** 当前要处理的任务 */
	private Object task;
	
	/** 异常 */
	private Throwable e;
	
	/**
	 * 初始化重试次数
	 * @param retryCount
	 */
	protected void initRetryCount(int retryCount) {
		super.setRetryCount(retryCount);
	}

	public Object getTask() {
		return task;
	}

	protected void setTask(Object task) {
		this.task = task;
	}

	public Throwable getE() {
		return e;
	}

	protected void setE(Throwable e) {
		this.e = e;
	}
	
}
