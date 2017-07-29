package com.le.dts.common.domain.store;

import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 安全控制
 * @author tianyao.myc
 *
 */
public class SecurityControl {

	/** 创建job控制流程 */
	private boolean createJobFlow;
	
	/** 更新job控制流程 */
	private boolean updateJobFlow;
	
	/** 删除job控制流程 */
	private boolean deleteJobFlow;
	
	public boolean isCreateJobFlow() {
		return createJobFlow;
	}

	public void setCreateJobFlow(boolean createJobFlow) {
		this.createJobFlow = createJobFlow;
	}

	public boolean isUpdateJobFlow() {
		return updateJobFlow;
	}

	public void setUpdateJobFlow(boolean updateJobFlow) {
		this.updateJobFlow = updateJobFlow;
	}

	public boolean isDeleteJobFlow() {
		return deleteJobFlow;
	}

	public void setDeleteJobFlow(boolean deleteJobFlow) {
		this.deleteJobFlow = deleteJobFlow;
	}

	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static SecurityControl newInstance(String json) {
        return RemotingSerializable.fromJson(json, SecurityControl.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }
	
}
