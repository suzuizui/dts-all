package com.le.dts.common.domain;

import java.math.BigDecimal;

/**
 * 进度条
 * @author tianyao.myc
 *
 */
public class ProgressBar {

	/** 进度条名称，列如Job整体进度，一级Job进度等等 */
	private String name;
	
	private String instanceId;
	
	/** 任务总量 */
	private long totalAmount;
	
	/** 处于初始化状态的任务数量 */
	private long initAmount;
	
	/** 处于进入队列排队等待执行状态的任务数量 */
	private long queueAmount;
	
	/** 处于正在执行状态的任务数量 */
	private long startAmount;
	
	/** 处于执行成功状态的任务数量 */
	private long successAmount;
	
	/** 处于执行失败状态的任务数量 */
	private long failureAmount;
	
	/** 处于job处理器未找到状态的任务数量 */
	private long foundAmount;

	public ProgressBar() {
		
	}
	
	public ProgressBar(String name, long totalAmount, 
			long initAmount, long queueAmount, 
			long startAmount, long successAmount, 
			long failureAmount, long foundAmount) {
		this.name = name;
		this.totalAmount = totalAmount;
		this.initAmount = initAmount;
		this.queueAmount = queueAmount;
		this.startAmount = startAmount;
		this.successAmount = successAmount;
		this.failureAmount = failureAmount;
		this.foundAmount = foundAmount;
	}
	
	/**
	 * 获取任务执行进度百分比
	 * @return
	 */
	public String parsePercentRate() {
		double t = 0;
		if(totalAmount != 0L) {
			double percentRate = (double)(successAmount + failureAmount + foundAmount) / totalAmount;
            BigDecimal bg = new BigDecimal(percentRate);
			t = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return t * 100 + "%";
	}
	
	/**
	 * 计算
	 * @return
	 */
	public double parseProcessValue() {
        if(totalAmount != 0L) {
            double percentRate = (double)(successAmount + failureAmount + foundAmount) / totalAmount;
            BigDecimal bg = new BigDecimal(percentRate);
            return bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0;
	}
	
	/**
	 * 错误率
	 * @return
	 */
	public double errorRate() {
		if(totalAmount != 0L) {
            double percentRate = (double)(failureAmount + foundAmount) / totalAmount;
            BigDecimal bg = new BigDecimal(percentRate);
            return bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0;
	}
	
	/**
	 * 成功率
	 * @return
	 */
	public double successRate() {
		if(totalAmount != 0L) {
            double percentRate = (double)(successAmount) / totalAmount;
            BigDecimal bg = new BigDecimal(percentRate);
            return bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0;
	}
	
	public long getProcessCount() {
		return successAmount + failureAmount + foundAmount;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public long getInitAmount() {
		return initAmount;
	}

	public void setInitAmount(long initAmount) {
		this.initAmount = initAmount;
	}

	public long getQueueAmount() {
		return queueAmount;
	}

	public void setQueueAmount(long queueAmount) {
		this.queueAmount = queueAmount;
	}

	public long getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(long startAmount) {
		this.startAmount = startAmount;
	}

	public long getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(long successAmount) {
		this.successAmount = successAmount;
	}

	public long getFailureAmount() {
		return failureAmount;
	}

	public void setFailureAmount(long failureAmount) {
		this.failureAmount = failureAmount;
	}

	public long getFoundAmount() {
		return foundAmount;
	}

	public void setFoundAmount(long foundAmount) {
		this.foundAmount = foundAmount;
	}

	@Override
	public String toString() {
		return "ProgressBar [name=" + name + ", totalAmount=" + totalAmount
				+ ", initAmount=" + initAmount + ", queueAmount=" + queueAmount
				+ ", startAmount=" + startAmount + ", successAmount="
				+ successAmount + ", failureAmount=" + failureAmount
				+ ", foundAmount=" + foundAmount + "]";
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
}
