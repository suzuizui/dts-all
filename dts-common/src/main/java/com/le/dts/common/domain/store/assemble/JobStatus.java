package com.le.dts.common.domain.store.assemble;

import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

import java.util.Map;
import java.util.TreeMap;

/**
 * Job 总体的状态
 * @author luliang.ll
 *
 */
public class JobStatus {

	private long jobId;
	
	private String jobDesc;
	
	private int runningStatus;
	
	// 总体进度;
	private TreeMap<Long/** instanceId */, Map.Entry<String/** firetime */, String>/** process */> overallProgress;

    public static JobStatus newInstance(String json) {
        return RemotingSerializable.fromJson(json, JobStatus.class);
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

	public TreeMap<Long, Map.Entry<String, String>> getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(TreeMap<Long, Map.Entry<String, String>> overallProgress) {
		this.overallProgress = overallProgress;
	}

	public int getRunningStatus() {
		return runningStatus;
	}

	public void setRunningStatus(int runningStatus) {
		this.runningStatus = runningStatus;
	}

}
