package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * 报警设置
 * @author tianyao.myc
 *
 */
public class WarningSetup {
	
	public static String TIMEOUT_LIMIT = "timeoutLimit";
	
	public static String ERROR_RATE = "errorRate";
	
	public static String FORCED_TERMINATION = "forcedTermination";
	
	public static String MOBILEID = "mobileId";
	
	public static String WWID = "wwId";

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;

	private long clientGroupId;
	
	/** job的ID */
	private long jobId;
	
	/** 联系方式 */
	private String contact;
	
	/** 报警设置信息 */
	private String warningSetup;
	
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

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getWarningSetup() {
		return warningSetup;
	}

	public void setWarningSetup(String warningSetup) {
		this.warningSetup = warningSetup;
	}

    public long getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(long clientGroupId) {
        this.clientGroupId = clientGroupId;
    }
}
