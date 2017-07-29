package com.le.dts.console.api;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobInstanceSnapshot;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.domain.store.JobServerRelation;
import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.domain.store.assemble.AssembledDesignatedMachine;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.common.domain.store.assemble.JobHistoryRecord;
import com.le.dts.common.domain.store.assemble.JobStatus;

public interface ApiService {

    /**
     * 初始Dts服务,开通服务的时候调用;
     * @param userId
     * @return
     */
    public Result<Boolean> initDtsService(String userId);

    /**
     * 随即一个服务端组;
     * @param clusterId
     * @return
     */
    public Long getClusterRandomGroup(long clusterId);

    /**
     * 获取cluster信息;
     * @param clusterId
     * @return
     */
    public Cluster getCluster(long clusterId);

	/**
	 * 设置全局用户自定义参数
	 * @param jobInstanceSnapshot
	 * @param globalArguments
	 * @return
	 */
	public Result<Boolean> setGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot, byte[] globalArguments);
	
	/**
	 * 获取设置的全局变量
	 * @param jobInstanceSnapshot
	 * @return
	 */
	public Result<byte[]> getGlobalArguments(JobInstanceSnapshot jobInstanceSnapshot);
	
	/**
	 * 得到一个用的所有组在的cluster
	 * @return
	 */
	public TreeMap<Long, Cluster> getUserClusters();

	public Result<List<ServerGroup>> getClusterGroups(long cluster);

	public Result<Map<Job, List<Job>>> getUserJobRelations(String userId, Cluster cluster);
	
	/**
	 * 获取备份机器列表
	 * @param job
	 * @return
	 */
	public List<JobServerRelation> getBackupMachineList(Job job);
	
	/**
	 * 获取JobProcessor名称列表
	 * @param groupId
	 * @return
	 */
	public Result<List<String>> getJobProcessorNameList(String groupId);
	
	/**
	 * 创建持久化Job
	 * @param job
	 * @return
	 */
	public Result<Long> createJob(Job job, Cluster cluster);
	
	/**
	 * 启用持久化job
	 * @param job
	 * @return
	 */
	public Result<Integer> enableJob(Job job);
	
	/**
	 * 禁用持久化job
	 * @param job
	 * @return
	 */
	public Result<Integer> disableJob(Job job);

	public Result<Integer> enableJobOrDisableJob(final Job job, final String operate);
	
    /**
     * 关联依赖
     * @param jobRelation
     * @return
     */
    public Result<Long> createJobRelation(JobRelation jobRelation);

    /**
     * 删除依赖
     * @param jobRelation
     * @return
     */
    public Result<Integer> deleteJobRelation(JobRelation jobRelation);
	
	/**
	 * 查询持久化Job
	 * @param query
	 * @return
	 */
	public Result<Job> queryPersistenceJob(Job query);
	
	/**
	 * 更新持久化Job
	 * @param job
	 * @return
	 */
	public Result<Integer> updateJob(Job job);

    /**
     * 更新持久化Job
     * @param job
     * @return
     */
    public Result<Integer> updateJobArguments(Job job);
	
	
	public Result<Integer> updatePersistenceJobStatus(Job job);
	
	/**
	 * 删除持久化Job
	 * @param job
	 * @return
	 */
	public Result<Long> deleteJob(Job job);
	
	
	/**
	 * 拿到用户的所有分组
	 * @param userId
	 * @return
	 */
	public Result<List<AssembledUserGroup>> getUserGroups(String userId, Cluster cluster);
	
	/**
	 * 创建一个分组
	 * @param group
	 * @return
	 */
	public Result<String> createGroup(ClientGroup group, long clusterId);
	
	/**
	 * 删除一个用户分组
	 * @param groupId
	 * @return
	 */
	public Result<String> deleteGroup(String groupId, String userId);

	/**
	 * 拿到一个分组中的所有Job
	 * @param groupId
	 * @param page
	 * @param perPageCount
	 * @param searchText
	 * @return
	 */
	public Result<List<Job>> getGroupJobs(String groupId, int page, int perPageCount, String searchText);
	
	/**
	 * 得到一个分组Job的数量
	 * @param groupId
	 * @param searchText
	 * @return
	 */
	public Result<Integer> getGroupJobsCount(String groupId, String searchText);
	
	/**
	 * 得到Job的配置信息
	 * @param jobId
	 * @return
	 */
	public Result<Job> getJobConfig(long jobId);

	/**
	 * 得到Job分级执行的进度
	 * @param jobId
	 * @return
	 */
	public Result<ProgressDetail> getJobInstanceDetailStatus(long jobId, String instanceId );

    /**
     * 一个实例执行的总体的进度;
     *
     * @param instanceId
     * @return
     */
    public Result<Double> getJobInstanceOvaralProgress(long instanceId);

    /**
     * 查询一个JOB的状态;
     * @param jobId
     * @return
     */
    public Result<JobStatus> getJobStatus(long jobId);
	
	/**
	 * 得到job的历史快照
	 * @param jobId
	 * @param page
	 * @param perPageCount
	 * @return
	 */
	public Result<JobHistoryRecord> getJobHistoryRecord(long jobId, int page, int perPageCount );
	
	/**
	 * 得到展示Job的历史记录数;
	 * @param jobId
	 * @return
	 */
	public Result<Integer> getJobHistoryCount(long jobId);
	
	/**
	 * 更新监控数据
	 * @param warningSetup
	 * @return
	 */
	public Result<String> updateJobMonitor(WarningSetup warningSetup );
	
	/**
	 * 启动任务
	 * @param jobId
	 * @return
	 */
	public Result<String> startJob(String groupId, long jobId);
	
	/**
	 * 停止任务
	 * @param jobId
	 * @return
	 */
	public Result<String> stopJob(long jobId);
	
	public Result<String> stopAnyJob(final long jobId);
	
	/**
	 * 立即停止任务 后门
	 * @param jobId
	 * @return
	 */
	public Result<String> stopJobBackup(final long jobId);

	/**
	 * 拿到Job的运行状态
	 * @param groupId
	 * @param page
	 * @param perPageCount
	 * @return
	 */
	public Result<List<JobStatus>> getGroupJobStatus(String groupId, int page, int perPageCount, boolean instance);


	/**
	 * 授权
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public Result<String> grantAuth(String ownerId, String userId, String groupId);

    /**
     * 获取一个Job的指定运行机器;
     * @param jobId
     * @return
     */
    public Result<List<AssembledDesignatedMachine>> getDesignatedMachine(String groupId, long jobId);

    /**
     * 分配机器给任务;
     * @param jobId
     * @param designatedMachineList
     * @return
     */
    public Result<Integer> designatedMachine(String groupId, long jobId, List<DesignatedMachine> designatedMachineList);
    
    /**
     * 复位图式计算
     * @param jobIdList
     * @return
     */
    public boolean resetJobRelation(String[] jobIdList);

}
