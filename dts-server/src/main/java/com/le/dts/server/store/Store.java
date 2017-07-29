package com.le.dts.server.store;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.store.hbase.ClientGroupAccess4Hbase;
import com.le.dts.server.store.hbase.ClusterAccess4Hbase;
import com.le.dts.server.store.hbase.DesignatedMachineAccess4Hbase;
import com.le.dts.server.store.hbase.JobAccess4Hbase;
import com.le.dts.server.store.hbase.JobInstanceSnapshotAccess4Hbase;
import com.le.dts.server.store.hbase.JobOperationAccess4Hbase;
import com.le.dts.server.store.hbase.JobRelationAccess4Hbase;
import com.le.dts.server.store.hbase.JobServerRelationAccess4Hbase;
import com.le.dts.server.store.hbase.ServerAccess4Hbase;
import com.le.dts.server.store.hbase.ServerGroupAccess4Hbase;
import com.le.dts.server.store.hbase.ServerJobInstanceMappingAccess4Hbase;
import com.le.dts.server.store.hbase.TaskSnapshotAccess4Hbase;
import com.le.dts.server.store.hbase.WarningSetupAccess4Hbase;
import com.le.dts.server.store.mysql.ClientGroupAccess4Mysql;
import com.le.dts.server.store.mysql.ClusterAccess4Mysql;
import com.le.dts.server.store.mysql.DesignatedMachineAccess4Mysql;
import com.le.dts.server.store.mysql.JobAccess4Mysql;
import com.le.dts.server.store.mysql.JobInstanceSnapshotAccess4Mysql;
import com.le.dts.server.store.mysql.JobOperationAccess4Mysql;
import com.le.dts.server.store.mysql.JobRelationAccess4Mysql;
import com.le.dts.server.store.mysql.JobServerRelationAccess4Mysql;
import com.le.dts.server.store.mysql.ServerAccess4Mysql;
import com.le.dts.server.store.mysql.ServerGroupAccess4Mysql;
import com.le.dts.server.store.mysql.ServerJobInstanceMappingAccess4Mysql;
import com.le.dts.server.store.mysql.TaskSnapshotAccess4Mysql;
import com.le.dts.server.store.mysql.WarningSetupAccess4Mysql;

/**
 * 存储
 * @author tianyao.myc
 *
 */
public class Store implements ServerContext, Constants {

	/** 客户端集群信息访问接口 */
	private ClientGroupAccess clientGroupAccess;
	
	/** Job信息访问接口 */
	private JobAccess jobAccess;
	
	/** Job实例快照访问接口 */
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	/** Job依赖关系访问接口 */
	private JobRelationAccess jobRelationAccess;
	
	/** 服务端集群信息访问接口 */
	private ClusterAccess clusterAccess;
	
	/** 任务快照访问接口 */
	private TaskSnapshotAccess taskSnapshotAccess;
	
	/** 报警设置访问接口 */
	private WarningSetupAccess warningSetupAccess;
	
	/** job和机器关系映射访问接口 */
	private JobServerRelationAccess jobServerRelationAccess;
	
    private ServerJobInstanceMappingAccess serverJobInstanceMappingAccess;
    
    /** Server访问接口 */
    private ServerAccess serverAccess;
    
    /** 服务端分组访问接口 */
    private ServerGroupAccess serverGroupAccess;
	
    /** Job操作访问接口 */
    private JobOperationAccess jobOperationAccess;
    
    /** 指定机器访问接口 */
    private DesignatedMachineAccess designatedMachineAccess;
    
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		switch(serverConfig.getStoreType()) {
		case STORE_TYPE_MYSQL:
			
			this.clientGroupAccess 				= new ClientGroupAccess4Mysql();
			this.jobAccess 						= new JobAccess4Mysql();
			this.jobInstanceSnapshotAccess 		= new JobInstanceSnapshotAccess4Mysql();
			this.jobRelationAccess 				= new JobRelationAccess4Mysql();
			this.clusterAccess 					= new ClusterAccess4Mysql();
			this.warningSetupAccess 			= new WarningSetupAccess4Mysql();
			this.jobServerRelationAccess 		= new JobServerRelationAccess4Mysql();
			this.serverJobInstanceMappingAccess = new ServerJobInstanceMappingAccess4Mysql();
			this.serverAccess 					= new ServerAccess4Mysql();
			this.serverGroupAccess 				= new ServerGroupAccess4Mysql();
			this.jobOperationAccess 			= new JobOperationAccess4Mysql();
			this.designatedMachineAccess 		= new DesignatedMachineAccess4Mysql();

			this.taskSnapshotAccess 			= TaskSnapshotAccess4Mysql.newInstance();
			
//			this.clientGroupAccess 				= (ClientGroupAccess)new MethodInvocationHandler().bind(new ClientGroupAccess4Mysql());
//			this.jobAccess 						= (JobAccess)new MethodInvocationHandler().bind(new JobAccess4Mysql());
//			this.jobInstanceSnapshotAccess 		= (JobInstanceSnapshotAccess)new MethodInvocationHandler().bind(new JobInstanceSnapshotAccess4Mysql());
//			this.jobRelationAccess 				= (JobRelationAccess)new MethodInvocationHandler().bind(new JobRelationAccess4Mysql());
//			this.clusterAccess 					= (ClusterAccess)new MethodInvocationHandler().bind(new ClusterAccess4Mysql());
//			this.taskSnapshotAccess 			= (TaskSnapshotAccess)new MethodInvocationHandler().bind(new TaskSnapshotAccess4Mysql());
//			this.warningSetupAccess 			= (WarningSetupAccess)new MethodInvocationHandler().bind(new WarningSetupAccess4Mysql());
//			this.jobServerRelationAccess 		= (JobServerRelationAccess)new MethodInvocationHandler().bind(new JobServerRelationAccess4Mysql());
//			this.serverJobInstanceMappingAccess = (ServerJobInstanceMappingAccess)new MethodInvocationHandler().bind(new ServerJobInstanceMappingAccess4Mysql());
//			this.serverAccess 					= (ServerAccess)new MethodInvocationHandler().bind(new ServerAccess4Mysql());
//			this.serverGroupAccess 				= (ServerGroupAccess)new MethodInvocationHandler().bind(new ServerGroupAccess4Mysql());
//			this.jobOperationAccess 			= (JobOperationAccess)new MethodInvocationHandler().bind(new JobOperationAccess4Mysql());
//			this.designatedMachineAccess 		= (DesignatedMachineAccess)new MethodInvocationHandler().bind(new DesignatedMachineAccess4Mysql());
			
			break ;
		case STORE_TYPE_HBASE:
			
			this.clientGroupAccess 				= new ClientGroupAccess4Hbase();
			this.jobAccess 						= new JobAccess4Hbase();
			this.jobInstanceSnapshotAccess 		= new JobInstanceSnapshotAccess4Hbase();
			this.jobRelationAccess 				= new JobRelationAccess4Hbase();
			this.clusterAccess 					= new ClusterAccess4Hbase();
			this.taskSnapshotAccess 			= new TaskSnapshotAccess4Hbase();
			this.warningSetupAccess 			= new WarningSetupAccess4Hbase();
			this.jobServerRelationAccess 		= new JobServerRelationAccess4Hbase();
			this.serverJobInstanceMappingAccess = new ServerJobInstanceMappingAccess4Hbase();
			this.serverAccess 					= new ServerAccess4Hbase();
			this.serverGroupAccess 				= new ServerGroupAccess4Hbase();
			this.jobOperationAccess 			= new JobOperationAccess4Hbase();
			this.designatedMachineAccess 		= new DesignatedMachineAccess4Hbase();
			break ;
			default:
				throw new InitException("[Store]: init error, storeType is not correct, storeType:" + serverConfig.getStoreType());
		}
		
	}

	public ClientGroupAccess getClientGroupAccess() {
		return clientGroupAccess;
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

	public ClusterAccess getClusterAccess() {
		return clusterAccess;
	}

	public TaskSnapshotAccess getTaskSnapshotAccess() {
		return taskSnapshotAccess;
	}

	public WarningSetupAccess getWarningSetupAccess() {
		return warningSetupAccess;
	}

	public JobServerRelationAccess getJobServerRelationAccess() {
		return jobServerRelationAccess;
	}

    public ServerJobInstanceMappingAccess getServerJobInstanceMappingAccess() {
        return serverJobInstanceMappingAccess;
    }

	public ServerAccess getServerAccess() {
		return serverAccess;
	}

	public ServerGroupAccess getServerGroupAccess() {
		return serverGroupAccess;
	}

	public JobOperationAccess getJobOperationAccess() {
		return jobOperationAccess;
	}

	public DesignatedMachineAccess getDesignatedMachineAccess() {
		return designatedMachineAccess;
	}
    
}
