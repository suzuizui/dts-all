package com.le.dts.common.domain.store.assemble;

import java.util.List;

import com.le.dts.common.domain.store.SecurityControl;
import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.StringUtil;
import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.StringUtil;

/**
 * 分组列表中对应的封装用户分组类,里面每个属性都是页面看到的;
 * @author luliang.ll
 *
 */
public class AssembledUserGroup {

	private String userId;

	private String systemDefineGroupId;

	// 分组描述
	private String groupDesc;

	// 每个分组的Job数
	private int groupJobNum;
	
	// 分组所在的集群ID,有反查集群需求通过这个变量;
	private long clusterId;
	
	//安全控制
	private String securityControl;
	
	/** 创建job控制流程 */
	private boolean createJobFlow;
	
	/** 更新job控制流程 */
	private boolean updateJobFlow;
	
	/** 删除job控制流程 */
	private boolean deleteJobFlow;

	private List<UserGroupRelation> relationList;
	
    /**
     * json转换成对象
     * @param json
     * @return
     */
    public static AssembledUserGroup newInstance(String json) {
        return RemotingSerializable.fromJson(json, AssembledUserGroup.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public int getGroupJobNum() {
		return groupJobNum;
	}

	public void setGroupJobNum(int groupJobNum) {
		this.groupJobNum = groupJobNum;
	}

	public String getSystemDefineGroupId() {
		return systemDefineGroupId;
	}

	public void setSystemDefineGroupId(String systemDefineGroupId) {
		this.systemDefineGroupId = systemDefineGroupId;
	}

	public long getClusterId() {
		return clusterId;
	}

	public void setClusterId(long clusterId) {
		this.clusterId = clusterId;
	}

	public List<UserGroupRelation> getRelationList() {
		return relationList;
	}

	public void setRelationList(List<UserGroupRelation> relationList) {
		this.relationList = relationList;
	}

	public String getSecurityControl() {
		return securityControl;
	}

	public void setSecurityControl(String securityControl) {
		
		if(StringUtil.isBlank(securityControl)) {
			return ;
		}
		
		this.securityControl = securityControl;
		
		SecurityControl sc = SecurityControl.newInstance(securityControl);
		
		setCreateJobFlow(sc.isCreateJobFlow());
		setUpdateJobFlow(sc.isUpdateJobFlow());
		setDeleteJobFlow(sc.isDeleteJobFlow());
	}

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

}
