package com.le.dts.common.domain.store;

import java.util.Date;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.constants.Constants;

/**
 * Job信息
 * @author tianyao.myc
 *
 */
public class Job implements Constants {

	/** 主键 job的ID */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 服务端集群分组ID */
	private long serverGroupId;
	
	/** 客户端集群ID */
	private long clientGroupId;
	
	/** Job描述 */
	private String description;
	
	/** 创建者ID */
	private String createrId;
	
	/** Job类型 */
	private int type;
	
	/** 时间表达式 */
	private String cronExpression;
	
	/** job处理器 */
	private String jobProcessor;
	
	/** 最大运行实例数量 */
	private int maxInstanceAmount;
	
	/** Job用户自定义参数 */
	private String jobArguments;

	/** Job状态 */
	private int status = JOB_STATUS_ENABLE;
	
	//Job等级
	private int level;
	
	//最大线程数量
	private int maxThreads;
	
	/** taskName */
    private String taskName;
	
	/**
	 * 重写equals方法
	 */
	public boolean equals(Object object) {
		if(null == object) {
			return false;
		}
		if(! (object instanceof Job)) {
			return false;
		}
		
		Job job = (Job)object;
		if(! job.toString().equals(this.toString())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 重写hashCode方法
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}
	

    public static Job newInstance(String json) {
        return RemotingSerializable.fromJson(json, Job.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
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

	public long getServerGroupId() {
		return serverGroupId;
	}

	public void setServerGroupId(long serverGroupId) {
		this.serverGroupId = serverGroupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreaterId() {
		return createrId;
	}

	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getJobProcessor() {
		return jobProcessor;
	}

	public void setJobProcessor(String jobProcessor) {
		this.jobProcessor = jobProcessor;
	}

	public int getMaxInstanceAmount() {
		return maxInstanceAmount;
	}

	public void setMaxInstanceAmount(int maxInstanceAmount) {
		this.maxInstanceAmount = maxInstanceAmount;
	}

	public String getJobArguments() {
		return jobArguments;
	}

	public void setJobArguments(String jobArguments) {
		this.jobArguments = jobArguments;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    public long getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(long clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMaxThreads() {
		return maxThreads;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
