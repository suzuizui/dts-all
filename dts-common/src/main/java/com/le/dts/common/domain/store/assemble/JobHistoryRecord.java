package com.le.dts.common.domain.store.assemble;
/**
 * 任务历史记录
 * @author luliang.ll
 *
 */
public class JobHistoryRecord {

	private long jobId;
	
	/**
	 * 这是一个拼成的字符串
	 * T1,R1#T2,R2
	 */
	private String result;

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
