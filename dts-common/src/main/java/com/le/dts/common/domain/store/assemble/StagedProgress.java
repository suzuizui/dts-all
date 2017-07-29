package com.le.dts.common.domain.store.assemble;
/**
 * 分级任务进度;
 * @author luliang.ll
 *
 */
public class StagedProgress {
	// 下一级任务数
	private int taskCount;
	// 进度
	private double progress;
	// 下一级已经完成的任务数
	private int completeCount;
	
	public StagedProgress(int taskCount, double progress, int completeCount) {
		this.taskCount = taskCount;
		this.setProgress(progress);
		this.completeCount = completeCount;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public int getCompleteCount() {
		return completeCount;
	}

	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}
	
}
