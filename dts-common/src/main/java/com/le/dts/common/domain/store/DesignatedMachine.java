/**
 * 
 */
package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * @author tianyao.myc
 * 指定机器对象
 *
 */
public class DesignatedMachine {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 客户端集群ID */
	private long clientGroupId;
	
	/** job的ID */
	private long jobId;
	
	/** 机器信息 */
	private String machine;
	
	/** 策略 */
	private int policy;

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

	public long getClientGroupId() {
		return clientGroupId;
	}

	public void setClientGroupId(long clientGroupId) {
		this.clientGroupId = clientGroupId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	@Override
	public String toString() {
		return "DesignatedMachine [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", clientGroupId="
				+ clientGroupId + ", jobId=" + jobId + ", machine=" + machine
				+ ", policy=" + policy + "]";
	}

}
