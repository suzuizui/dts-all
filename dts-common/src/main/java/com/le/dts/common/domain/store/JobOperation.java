package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * Created by luliang on 15/1/15.
 */
public class JobOperation {
    // 主键;
    private long id;
    // jobId
    private long jobId;
    // job操作;
    private String operation;
    /** 创建时间 */
    private Date gmtCreate;
    /** 修改时间 */
    private Date gmtModified;
    // server
    private String server;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

	@Override
	public String toString() {
		return "JobOperation [id=" + id + ", jobId=" + jobId + ", operation="
				+ operation + ", gmtCreate=" + gmtCreate + ", gmtModified="
				+ gmtModified + ", server=" + server + "]";
	}
    
}
