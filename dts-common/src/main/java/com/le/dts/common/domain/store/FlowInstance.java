package com.le.dts.common.domain.store;

import java.util.Date;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * 流程实例
 * @author tianyao.myc
 *
 */
public class FlowInstance implements Constants {

	public static final int STATUS_START 	= 0;
	public static final int STATUS_AGREE 	= 1;
	public static final int STATUS_DISAGREE = 2;
	public static final int STATUS_END 		= 3;
	
	/** 主键ID */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	//流程实例ID
	private String flowInstanceId;
	
	//操作对象的ID
	private String operationObjectId;
	
	/** 状态 */
	private int status;
	
	//操作者ID
	private String operatorId;

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

	public String getFlowInstanceId() {
		return flowInstanceId;
	}

	public void setFlowInstanceId(String flowInstanceId) {
		this.flowInstanceId = flowInstanceId;
	}

	public String getOperationObjectId() {
		return operationObjectId;
	}

	public void setOperationObjectId(String operationObjectId) {
		this.operationObjectId = operationObjectId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	@Override
	public String toString() {
		return "FlowInstance [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", flowInstanceId="
				+ flowInstanceId + ", operationObjectId=" + operationObjectId
				+ ", status=" + status + ", operatorId=" + operatorId + "]";
	}
	
}
