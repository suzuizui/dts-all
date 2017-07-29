package com.le.dts.common.domain.store;

import java.util.Date;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 服务端集群信息
 * @author tianyao.myc
 *
 */
public class Cluster implements Constants {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 集群描述 */
	private String description;
	
	/** job备份数量 */
	private int jobBackupAmount = DEFAULT_JOB_BACKUP_AMOUNT;

	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static Cluster newInstance(String json) {
		return RemotingSerializable.fromJson(json, Cluster.class);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getJobBackupAmount() {
		return jobBackupAmount;
	}

	public void setJobBackupAmount(int jobBackupAmount) {
		this.jobBackupAmount = jobBackupAmount;
	}
	
}
