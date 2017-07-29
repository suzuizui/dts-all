package com.le.dts.common.domain.store.assemble;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * 页面对应的封装的Job
 * @author luliang.ll
 *
 */
public class AssembledJob {

	private long jobId;
	
	private String jobDesc;
	
	// Job类型;
	private int jobType;
	// 实例数;
	private int firePolicy;
	// 时间表达式可能为空;
	private String cronExpression;
	// 处理器;
	private String jobProcessor;
	/** Job状态 */
	private int status = Constants.JOB_STATUS_DISABLE;
	
	public int getFirePolicy() {
		return firePolicy;
	}

	public void setFirePolicy(int firePolicy) {
		this.firePolicy = firePolicy;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getJobProcessor() {
		return jobProcessor;
	}

	public void setJobProcessor(String jobProcessor) {
		this.jobProcessor = jobProcessor;
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
