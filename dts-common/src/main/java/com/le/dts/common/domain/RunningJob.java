package com.le.dts.common.domain;

import java.util.ArrayList;
import java.util.List;

import com.le.dts.common.domain.remoting.Pair;

/**
 * 运行中的Job
 * @author tianyao.myc
 *
 */
public class RunningJob {

	/** 队列大小 */
	private int queueSize;
	
	/** 排队任务数量 */
	private int queuingAmount;
	
	/** 任务处理器状态列表 */
	private List<Pair<Integer, Long>> statusList = new ArrayList<Pair<Integer, Long>>();

	public RunningJob() {
		
	}
	
	public RunningJob(List<Pair<Integer, Long>> statusList) {
		this.statusList = statusList;
	}
	
	public RunningJob(int queueSize, int queuingAmount, List<Pair<Integer, Long>> statusList) {
		this.queueSize = queueSize;
		this.queuingAmount = queuingAmount;
		this.statusList = statusList;
	}
	
	/**
	 * 添加处理器状态
	 * @param status
	 */
	public void addStatus(Pair<Integer, Long> status) {
		this.statusList.add(status);
	}
	
	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public int getQueuingAmount() {
		return queuingAmount;
	}

	public void setQueuingAmount(int queuingAmount) {
		this.queuingAmount = queuingAmount;
	}

	public List<Pair<Integer, Long>> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Pair<Integer, Long>> statusList) {
		this.statusList = statusList;
	}

}
