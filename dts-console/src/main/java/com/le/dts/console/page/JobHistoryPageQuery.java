package com.le.dts.console.page;
/**
 * Job历史记录;
 * @author luliang.ll
 *
 */
public class JobHistoryPageQuery extends BasePageQuery {

	private static final long serialVersionUID = 7717522734243236216L;
	private long jobId;

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
}
