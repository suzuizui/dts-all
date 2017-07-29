package com.le.dts.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 进度详情
 * @author tianyao.myc
 *
 */
public class ProgressDetail {

	/** Job描述 */
	private String description;
	
	/** 实例触发时间 */
	private String fireTime;
	
	/** 总体执行进度条 */
	private ProgressBar totalProgressBar;
	
	/** 各级进度条列表 */
	private List<ProgressBar> progressBarList = new ArrayList<ProgressBar>();

	/** 实例结束时间 */
	private String finishTime;
	
	/** Job类型 */
	private int type;
	
	/** 按机器维度统计结果 */
	private Map<String, ProgressBar> machineProgressBarMap = new HashMap<String, ProgressBar>();
	
	private boolean failureWarning;
	
	public ProgressDetail() {
		
	}
	
	public ProgressDetail(String fireTime, String description, ProgressBar totalProgressBar) {
		this.fireTime = fireTime;
		this.description = description;
		this.totalProgressBar = totalProgressBar;
	}
	
	public ProgressDetail(String fireTime, String description, ProgressBar totalProgressBar, List<ProgressBar> progressBarList) {
		this.fireTime = fireTime;
		this.description = description;
		this.totalProgressBar = totalProgressBar;
		this.progressBarList = progressBarList;
	}
	
	/**
	 * 添加各级进度条
	 * @param progressBar
	 */
	public void add(ProgressBar progressBar) {
		this.progressBarList.add(progressBar);
	}
	
	/**
	 * 添加各级进度条
	 * @param progressBarList
	 */
	public void add(List<ProgressBar> progressBarList) {
		this.progressBarList.addAll(progressBarList);
	}
	
	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static ProgressDetail newInstance(String json) {
		return RemotingSerializable.fromJson(json, ProgressDetail.class);
	}
	
	/**
	 * 对象转换成json
	 */
	@Override
	public String toString() {
		return RemotingSerializable.toJson(this, false);
	}
	
	/**
	 * 获取机器维度统计信息
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getMachineProgress() {
		
		StringBuilder machineProgress = new StringBuilder();
		
		Iterator iterator = machineProgressBarMap.entrySet().iterator();
		while (iterator.hasNext()) {
		    Map.Entry entry = (Map.Entry) iterator.next();
		    String ip = (String)entry.getKey();
		    ProgressBar progressBar = (ProgressBar)entry.getValue();
		    
		    machineProgress.append(ip 
		    		+ "[总量:" + progressBar.getTotalAmount() 
		    		+ ", 排队:" + progressBar.getQueueAmount() 
		    		+ ", 成功:" + progressBar.getSuccessAmount() 
		    		+ ", 失败:" + progressBar.getFailureAmount() + "]    ");
		}
		
		return machineProgress.toString();
	}
	
	public String getFireTime() {
		return fireTime;
	}

	public void setFireTime(String fireTime) {
		this.fireTime = fireTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProgressBar getTotalProgressBar() {
		return totalProgressBar;
	}

	public void setTotalProgressBar(ProgressBar totalProgressBar) {
		this.totalProgressBar = totalProgressBar;
	}

	public List<ProgressBar> getProgressBarList() {
		return progressBarList;
	}

	public void setProgressBarList(List<ProgressBar> progressBarList) {
		this.progressBarList = progressBarList;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Map<String, ProgressBar> getMachineProgressBarMap() {
		return machineProgressBarMap;
	}

	public void setMachineProgressBarMap(
			Map<String, ProgressBar> machineProgressBarMap) {
		this.machineProgressBarMap = machineProgressBarMap;
	}

	public boolean isFailureWarning() {
		return failureWarning;
	}

	public void setFailureWarning(boolean failureWarning) {
		this.failureWarning = failureWarning;
	}

}
