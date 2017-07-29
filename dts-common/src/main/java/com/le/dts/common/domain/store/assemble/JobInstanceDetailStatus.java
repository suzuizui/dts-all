package com.le.dts.common.domain.store.assemble;

import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * Job详细的状态,包含分级任务的运行状态;
 * @author luliang.ll
 *
 */
public class JobInstanceDetailStatus {

	private long jobId;
	// Job描述
	private String jobDesc;
	
	// layer任务展示;
	private ProgressDetail progressDetail;

    public static JobInstanceDetailStatus newInstance(String json) {
        return RemotingSerializable.fromJson(json, JobInstanceDetailStatus.class);
    }

    public String toString() {
        return RemotingSerializable.toJson(this, false);
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

	public ProgressDetail getProgressDetail() {
		return progressDetail;
	}

	public void setProgressDetail(ProgressDetail progressDetail) {
		this.progressDetail = progressDetail;
	}
	
}
