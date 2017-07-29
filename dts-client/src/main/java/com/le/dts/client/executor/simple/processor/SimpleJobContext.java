package com.le.dts.client.executor.simple.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.client.context.ClientContext;
import com.le.dts.client.executor.job.context.JobContext;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.TaskSnapshot;
import com.le.dts.common.util.BytesUtil;

/**
 * 简单job上下文
 * @author tianyao.myc
 *
 */
public class SimpleJobContext extends JobContext implements Constants, ClientContext {

	private static final Log logger = LogFactory.getLog(SimpleJobContext.class);
	
	/** 当前要处理的任务 */
	private Object task;
	
	//可用的机器数量
	private int availableMachineAmount;
		
	//当前机器编号
	private int currentMachineNumber;
	
	public SimpleJobContext(Job job, JobInstanceSnapshot jobInstanceSnapshot, int retryCount) {
		super(job, jobInstanceSnapshot, retryCount);
	}
	
	/**
	 * 设置任务
	 * @param taskSnapshot
	 */
	protected void setTask(TaskSnapshot taskSnapshot) {
		if(BytesUtil.isEmpty(taskSnapshot.getBody())) {
			logger.error("[SimpleJobContext]: BytesUtil setTask bytesToObject error, body is empty"
					+ ", instanceId:" + taskSnapshot.getJobInstanceId() + ", id:" + taskSnapshot.getId());
			return ;
		}
		try {
			task = BytesUtil.bytesToObject(taskSnapshot.getBody());
		} catch (Throwable e) {
			logger.error("[SimpleJobContext]: BytesUtil setTask bytesToObject error" 
					+ ", instanceId:" + taskSnapshot.getJobInstanceId() + ", id:" + taskSnapshot.getId(), e);
		}
	}
	
	public Object getTask() {
		return task;
	}

	public int getAvailableMachineAmount() {
		return availableMachineAmount;
	}

	protected void setAvailableMachineAmount(int availableMachineAmount) {
		this.availableMachineAmount = availableMachineAmount;
	}

	public int getCurrentMachineNumber() {
		return currentMachineNumber;
	}

	protected void setCurrentMachineNumber(int currentMachineNumber) {
		this.currentMachineNumber = currentMachineNumber;
	}

}
