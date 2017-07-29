package com.le.dts.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;

/**
 * 执行任务
 * @author tianyao.myc
 *
 */
public class ExecutableTask implements Serializable, Constants {

	/** 序列化ID */
	private static final long serialVersionUID = 3198892961924943519L;

	/** 当前Task的Job配置 */
	private Job job;
	
	/** 当前Task的Job实例 */
	private JobInstanceSnapshot jobInstanceSnapshot;
	
	/** 任务 */
	private TaskSnapshot taskSnapshot;
	
	/** 任务列表 */
	private List<TaskSnapshot> taskSnapshotList = new ArrayList<TaskSnapshot>();

	/** 位点起始偏移量 */
	private long offset;
	
	/** 一次拉取的pageSize */
	private int length = DEFAULT_PAGE_SIZE;
	
	/** 是否是补偿性质的拉取任务快照数据 */
	private boolean compensation = false;
	
	//可用的机器数量
	private int availableMachineAmount;
	
	//当前机器编号
	private int currentMachineNumber;
	
	//运行线程数量
	private int runThreads;
	
	public ExecutableTask() {
		
	}
	
	public ExecutableTask(Job job, JobInstanceSnapshot jobInstanceSnapshot) {
		this.job = job;
		this.jobInstanceSnapshot = jobInstanceSnapshot;
	}
	
	public ExecutableTask(Job job, JobInstanceSnapshot jobInstanceSnapshot, long offset, int length) {
		this.job = job;
		this.jobInstanceSnapshot = jobInstanceSnapshot;
		this.offset = offset;
		this.length = length;
	}
	
	public ExecutableTask(Job job, JobInstanceSnapshot jobInstanceSnapshot, TaskSnapshot taskSnapshot) {
		this.job = job;
		this.jobInstanceSnapshot = jobInstanceSnapshot;
		this.taskSnapshot = taskSnapshot;
	}
	
	public ExecutableTask(Job job, JobInstanceSnapshot jobInstanceSnapshot, List<TaskSnapshot> taskSnapshotList) {
		this.job = job;
		this.jobInstanceSnapshot = jobInstanceSnapshot;
		this.taskSnapshotList = taskSnapshotList;
	}
	
	/**
	 * 添加任务快照
	 * @param taskSnapshot
	 */
	public void addTaskSnapshot(TaskSnapshot taskSnapshot) {
		this.taskSnapshotList.add(taskSnapshot);
	}
	
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public JobInstanceSnapshot getJobInstanceSnapshot() {
		return jobInstanceSnapshot;
	}

	public void setJobInstanceSnapshot(JobInstanceSnapshot jobInstanceSnapshot) {
		this.jobInstanceSnapshot = jobInstanceSnapshot;
	}

	public TaskSnapshot getTaskSnapshot() {
		return taskSnapshot;
	}

	public void setTaskSnapshot(TaskSnapshot taskSnapshot) {
		this.taskSnapshot = taskSnapshot;
	}

	public List<TaskSnapshot> getTaskSnapshotList() {
		return taskSnapshotList;
	}

	public void setTaskSnapshotList(List<TaskSnapshot> taskSnapshotList) {
		this.taskSnapshotList = taskSnapshotList;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isCompensation() {
		return compensation;
	}

	public void setCompensation(boolean compensation) {
		this.compensation = compensation;
	}

	public int getAvailableMachineAmount() {
		return availableMachineAmount;
	}

	public void setAvailableMachineAmount(int availableMachineAmount) {
		this.availableMachineAmount = availableMachineAmount;
	}

	public int getCurrentMachineNumber() {
		return currentMachineNumber;
	}

	public void setCurrentMachineNumber(int currentMachineNumber) {
		this.currentMachineNumber = currentMachineNumber;
	}

	public int getRunThreads() {
		return runThreads;
	}

	public void setRunThreads(int runThreads) {
		this.runThreads = runThreads;
	}

	@Override
	public String toString() {
		return "ExecutableTask [job=" + job + ", jobInstanceSnapshot="
				+ jobInstanceSnapshot + ", taskSnapshot=" + taskSnapshot
				+ ", offset=" + offset + ", length=" + length
				+ ", compensation=" + compensation
				+ ", availableMachineAmount=" + availableMachineAmount
				+ ", currentMachineNumber=" + currentMachineNumber
				+ ", runThreads=" + runThreads + "]";
	}

}
