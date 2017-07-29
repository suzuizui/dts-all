package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * Job依赖关系
 * @author tianyao.myc
 *
 */
public class JobRelation {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** job的ID */
	private long jobId;

	/** 前缀Job Id*/
	private long beforeJobId;
    /** Job执行完调用依赖的次数，会被清0**/
    private int finishCount;
    /** job instance id */
    private long jobInstanceId;
    /** tag*/
    private int jobInstanceIdTag;

	public long getBeforeJobId() {
		return beforeJobId;
	}

	public void setBeforeJobId(long beforeJobId) {
		this.beforeJobId = beforeJobId;
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

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }

    public long getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(long jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
    }

    public int getJobInstanceIdTag() {
        return jobInstanceIdTag;
    }

    public void setJobInstanceIdTag(int jobInstanceIdTag) {
        this.jobInstanceIdTag = jobInstanceIdTag;
    }

    @Override
	public String toString() {
		return "JobRelation [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", jobId=" + jobId
				+ ", beforeJobId=" + beforeJobId + ", finishCount" + finishCount + "]";
	}

}
