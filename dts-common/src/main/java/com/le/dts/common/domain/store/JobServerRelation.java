package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * Job和Server映射关系
 * @author tianyao.myc
 *
 */
public class JobServerRelation {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** job的ID */
	private long jobId;
	
	/** 服务器信息 */
	private String server;

	/**
	 * 重写equals方法
	 */
	public boolean equals(Object object) {
		if(null == object) {
			return false;
		}
		if(! (object instanceof JobServerRelation)) {
			return false;
		}
		
		JobServerRelation jobMachineRelation = (JobServerRelation)object;
		if(! jobMachineRelation.toEqualsString().equals(this.toEqualsString())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 重写hashCode方法
	 */
	public int hashCode() {
		return this.toEqualsString().hashCode();
	}
	
	/**
	 * 比较字符串
	 * @return
	 */
	public String toEqualsString() {
		return "JobMachineRelation [jobId=" + jobId + ", server=" + server
				+ "]";
	}

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

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@Override
	public String toString() {
		return "JobMachineRelation [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", jobId=" + jobId
				+ ", server=" + server + "]";
	}

}
