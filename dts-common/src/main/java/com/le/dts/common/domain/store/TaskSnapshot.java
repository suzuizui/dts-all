package com.le.dts.common.domain.store;

import com.le.dts.common.util.DBShardUtil;

import java.util.Date;

/**
 * 任务快照
 * @author tianyao.myc
 *
 */
public class TaskSnapshot {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 实例ID */
	private long jobInstanceId;
	
	/** job处理器 */
	private String jobProcessor;
	
	/** 任务快照字节数组 */
	private byte[] body;
	
	/** 任务执行状态 */
	private int status;
	
	/** 客户端IP */
	private String clientId;

    /** 任务名称 */
	private String taskName;
	
	/** 重试次数 */
	private int retryCount;

    // 简单任务，不会做上传，无依赖。
    private boolean simpleTask;
    
    /** 是否是补偿性质的拉取任务快照数据 */
	private boolean compensation = false;

	private String tableIndex;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public long getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public String getJobProcessor() {
		return jobProcessor;
	}

	public String getTableIndex() {
		return tableIndex;
	}

	public void setTableIndex(String tableIndex) {
		this.tableIndex = tableIndex;
	}

	public void setJobProcessor(String jobProcessor) {
		this.jobProcessor = jobProcessor;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public boolean isSimpleTask() {
		return simpleTask;
	}

	public void setSimpleTask(boolean simpleTask) {
		this.simpleTask = simpleTask;
	}

	public boolean isCompensation() {
		return compensation;
	}

	public void setCompensation(boolean compensation) {
		this.compensation = compensation;
	}

    public String getClientId() {
        return clientId;
    }

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "TaskSnapshot [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", jobInstanceId="
				+ jobInstanceId + ", jobProcessor=" + jobProcessor + ", status=" + status + ", clientId="
				+ clientId + ", taskName=" + taskName + ", retryCount="
				+ retryCount + ", simpleTask=" + simpleTask + ", compensation="
				+ compensation + "]";
	}

}
