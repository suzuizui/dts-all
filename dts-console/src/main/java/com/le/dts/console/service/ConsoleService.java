//package com.alibaba.dts.console.service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//import com.le.dts.common.domain.ProgressDetail;
//import com.le.dts.common.domain.result.Result;
//import com.le.dts.common.domain.store.*;
//import com.le.dts.common.domain.store.assemble.AssembledMonitor;
//import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
//import com.le.dts.common.domain.store.assemble.JobHistoryRecord;
//import com.le.dts.common.domain.store.assemble.JobStatus;
//
///**
// * 控制台服务
// * @author tianyao.myc
// *
// */
//public interface ConsoleService {
//
//	/**
//	 * 得到一个用的所有组在的cluster
//	 * @return
//	 */
//	public TreeMap<Long/** clusterId */, Cluster/** 集群描述 */> getUserClusters();
//
//	/**
//	 * 随机得到一个ServerGroup;
//	 * @param clusterId
//	 * @return
//	 */
//	public Long getClusterRandomGroup(long clusterId);
//
//	/**
//	 * 得到一个用户的依赖Job关系;
//	 * @return
//	 */
//	public Result<Map<Job, List<Job>>> getUserRelationJobsTable(String userId, Cluster cluster);
//
//	/**
//	 * 拿到用户的所有分组
//	 * @param userId
//	 * @return
//	 */
//	public Result<List<AssembledUserGroup>> getUserGroups(String userId, Cluster cluster);
//
//	/**
//	 * 创建一个分组;
//	 * @param group
//	 * @return
//	 */
//	public Result<String> createGroup(ClientGroup group, long clusterId);
//
//	/**
//	 * 创建一个新的Job,失败要返回一个错误信息，成就返回JobId,要带进页面;
//	 * @param job
//	 * @return
//	 */
//	public Result<Long> createJob(Job job, Cluster cluster);
//
//	/**
//	 * 更新一个Job;
//	 * @param job
//	 * @return
//	 */
//	public Result<Object> updateJob(Job job);
//
//	/**
//	 * 删除job;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Long> deleteJob(long jobId);
//
//	/**
//	 * 删除一个用户分组
//	 * @param groupId
//	 * @return
//	 */
//	public Result<String> deleteGroup(String groupId, String userId);
//
//	/**
//	 * 拿到一个分组中的所有Job
//	 * @param groupId
//	 * @return
//	 */
//	public Result<List<Job>> getGroupJobs(String groupId, int page, int perPageCount);
//
//	/**
//	 * 得到一个分组Job的数量;
//	 * @param groupId
//	 * @return
//	 */
//	public Result<Integer> getGroupJobsCount(String groupId);
//
//
//	/**
//	 * Job的历史记录数;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Integer> getJobHistoryCount(long jobId);
//
//	/**
//	 * 得到Job的配置信息;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Job> getJobConfig(long jobId);
//
//	/**
//	 * 得到Job分级执行的进度;
//	 *
//	 * @param jobId
//	 * @return
//	 */
//	public Result<ProgressDetail> getJobInstanceStatus(long jobId, String instanceId);
//
//    /**
//     * 一个实例执行的总体的进度;
//     *
//     * @param instanceId
//     * @return
//     */
//    public Result<Double> getJobInstanceOvaralProgress(long instanceId);
//
//	/**
//	 * 得到job的历史快照;
//	 * @return
//	 */
//	public Result<JobHistoryRecord> getJobHistoryRecord(long jobId, int page, int perPageCount);
//
//	/**
//	 * 得到监控配置
//	 * @param jobId
//	 * @return
//	 */
//	public Result<AssembledMonitor> getJobMonitor(long jobId);
//
//	/**
//	 * 更新监控数据;
//	 * @param warningSetup
//	 * @return
//	 */
//	public Result<String> updateJobMonitor(WarningSetup warningSetup);
//
//	/**
//	 * 启动任务
//	 * @param jobId
//	 * @return
//	 */
//	public Result<String> startJob(String groupId, long jobId);
//
//	/**
//	 * 停止任务;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<String> stopJob(long jobId);
//
//	/**
//	 * Job生效;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Integer> enableJob(long jobId);
//
//	/**
//	 * Job失效;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Integer> disableJob(long jobId);
//
//    /**
//     * 关联依赖
//     * @param jobRelation
//     * @return
//     */
//    public Result<Long> createJobRelation(JobRelation jobRelation);
//
//    /**
//     * 删除依赖
//     * @param jobRelation
//     * @return
//     */
//    public Result<Integer> deleteJobRelation(JobRelation jobRelation);
//
//	/**
//	 * 拿到Job的运行状态
//	 * @param groupId
//	 * @return
//	 */
//	public Result<List<JobStatus>> getGroupJobStatus(String groupId, int page, int perPageCount, boolean instance);
//
//
//	/**
//	 * 授权
//	 * @param userId
//	 * @param groupId
//	 * @return
//	 */
//	public Result<String> grantAuth(String ownerId, String userId, String groupId);
//
//}
