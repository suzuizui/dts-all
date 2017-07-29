package com.le.dts.console.store;

import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.constants.Constants;

/**
 * 存储
 * @author tianyao.myc
 *
 */
public class Store implements Constants {

	/** 客户端集群信息访问接口 */
	@Autowired
	private ClientGroupAccess clientClusterAccess;
	
	/** Job信息访问接口 */
	@Autowired
	private JobAccess jobAccess;
	
	/** Job实例快照访问接口 */
	@Autowired
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	/** Job依赖关系访问接口 */
	@Autowired
	private JobRelationAccess jobRelationAccess;
	
	/** 服务端集群信息访问接口 */
	@Autowired
	private ClusterAccess serverClusterAccess;
	
	/** 任务快照访问接口 */
	@Autowired
	private TaskSnapshotAccess taskSnapshotAccess;
	
	@Autowired
	private ServerGroupAccess serverGroupAccess;
	
	/** 报警设置访问接口 */
	@Autowired
	private WarningSetupAccess warningSetupAccess;

	/** 用户资源关系表 */
	@Autowired
	private UserGroupRelationAccess userGroupRelationAccess;

	/** job和机器关系映射访问接口 */
	@Autowired
	private JobServerRelationAccess jobMachineRelationAccess;

	public UserGroupRelationAccess getUserGroupRelationAccess() {
		return userGroupRelationAccess;
	}

	public void setUserGroupRelationAccess(UserGroupRelationAccess userGroupRelationAccess) {
		this.userGroupRelationAccess = userGroupRelationAccess;
	}

	public void setJobInstanceSnapshotAccess(JobInstanceSnapshotAccess jobInstanceSnapshotAccess) {
		this.jobInstanceSnapshotAccess = jobInstanceSnapshotAccess;
	}

	public void setJobAccess(JobAccess jobAccess) {
		this.jobAccess = jobAccess;
	}

	public void setClientClusterAccess(ClientGroupAccess clientClusterAccess) {
		this.clientClusterAccess = clientClusterAccess;
	}

	public ClientGroupAccess getClientClusterAccess() {
		return clientClusterAccess;
	}

	public JobAccess getJobAccess() {
		return jobAccess;
	}

	public JobInstanceSnapshotAccess getJobInstanceSnapshotAccess() {
		return jobInstanceSnapshotAccess;
	}

	public JobRelationAccess getJobRelationAccess() {
		return jobRelationAccess;
	}

	public ClusterAccess getServerClusterAccess() {
		return serverClusterAccess;
	}

	public TaskSnapshotAccess getTaskSnapshotAccess() {
		return taskSnapshotAccess;
	}

	public WarningSetupAccess getWarningSetupAccess() {
		return warningSetupAccess;
	}

	public JobServerRelationAccess getJobMachineRelationAccess() {
		return jobMachineRelationAccess;
	}

	public ServerGroupAccess getServerGroupAccess() {
		return serverGroupAccess;
	}

	public void setServerGroupAccess(ServerGroupAccess serverGroupAccess) {
		this.serverGroupAccess = serverGroupAccess;
	}
	
}
