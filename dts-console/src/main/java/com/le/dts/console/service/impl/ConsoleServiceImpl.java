//package com.alibaba.dts.console.service.impl;
//
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//import com.le.dts.common.domain.result.ResultCode;
//import com.le.dts.common.domain.store.*;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.le.dts.common.constants.Constants;
//import com.le.dts.common.domain.ProgressDetail;
//import com.le.dts.common.domain.result.Result;
//import com.le.dts.common.domain.store.assemble.AssembledMonitor;
//import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
//import com.le.dts.common.domain.store.assemble.JobHistoryRecord;
//import com.le.dts.common.domain.store.assemble.JobStatus;
//import com.le.dts.common.util.RandomUtil;
//import ApiService;
//import com.alibaba.dts.console.context.ConsoleContext;
//import com.alibaba.dts.console.service.ConsoleService;
//import SqlMapClients;
//import Zookeeper;
//
///**
// * 远程接口调用实现
// *
// * @author tianyao.myc
// *
// */
//public class ConsoleServiceImpl implements ConsoleService, ConsoleContext,
//		Constants {
//
//	private static final Log logger = LogFactory
//			.getLog(ConsoleServiceImpl.class);
//
//	@Autowired
//	private ApiService apiService;
//
//	@Autowired
//	private Zookeeper zookeeper;
//
//	@Autowired
//	private SqlMapClients sqlMapClients;
//
//	 /**
//	 * 随机获取服务端IP
//	 * @param cluster
//	 * @return
//	 */
//	 public String getRandomIp(Cluster cluster) {
//		 return RandomUtil.getRandomObj(zookeeper.getServerClusterIpList(cluster));
//	 }
//
//	/**
//	 * 得到一个用的所有组在的cluster
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public TreeMap<Long, Cluster> getUserClusters() {
//		return apiService.getUserClusters();
//	}
//
//	/**
//	 * 随机得到一个集群的里面的Group;
//	 * @param clusterId
//	 * @return
//	 */
//	public Long getClusterRandomGroup(long clusterId) {
//		return apiService.getClusterRandomGroup(clusterId);
//	}
//
//	/**
//	 * 拿到用户的所有分组
//	 */
//	@Override
//	public Result<List<AssembledUserGroup>> getUserGroups(String userId,
//			Cluster cluster) {
//		return apiService.getUserGroups(userId, cluster);
//	}
//
//	@Override
//	public Result<Map<Job, List<Job>>> getUserRelationJobsTable(String userId, Cluster cluster) {
//		return apiService.getUserJobRelations(userId, cluster);
//	}
//
//	/**
//	 * 创建一个分组
//	 */
//	@Override
//	public Result<String> createGroup(ClientGroup group, long clusterId) {
//		return apiService.createGroup(group, clusterId);
//	}
//
//	/**
//	 * 创建一个新的Job,失败要返回一个错误信息，成就返回JobId,要带进页面
//	 */
//	@Override
//	public Result<Long> createJob(Job job, Cluster cluster) {
//
//		return apiService.createJob(job, cluster);
//	}
//
//	/**
//	 * 更新一个Job
//	 */
//	@Override
//	public Result<Object> updateJob(Job job) {
//		Result<Object> result = new Result<Object>();
//
//		Result<Integer> updatePersistenceResult =
//				apiService.updatePersistenceJob(job);
//		if(updatePersistenceResult.getData().intValue() <= 0) {
//			logger.error("[ConsoleServiceImpl]: updatePersistenceJob error"
//					+ ", deletePersistenceResult:" + updatePersistenceResult.toString()
//					+ ", job:" + job.toString());
//		}
//		result.setResultCode(updatePersistenceResult.getResultCode());
//		return result;
//	}
//
//	/**
//	 * 删除job
//	 */
//	@Override
//	public Result<Long> deleteJob(long jobId) {
//
//		Job job = new Job();
//		job.setId(jobId);
//		return apiService.deleteJob(job);
//	}
//
//	/**
//	 * 删除一个用户分组
//	 */
//	@Override
//	public Result<String> deleteGroup(String groupId, String userId) {
//		return apiService.deleteGroup(groupId, userId);
//	}
//
//	/**
//	 * 拿到一个分组中的所有Job
//	 */
//	@Override
//	public Result<List<Job>> getGroupJobs(String groupId, int page,
//			int perPageCount) {
//		return apiService.getGroupJobs(groupId, page, perPageCount);
//	}
//
//	/**
//	 * 更新监控数据
//	 */
//	@Override
//	public Result<String> updateJobMonitor(WarningSetup warningSetup) {
//		return apiService.updateJobMonitor(warningSetup);
//	}
//
//	@Override
//	public Result<String> startJob(String groupId, long jobId) {
//		return apiService.startJob(groupId, jobId);
//	}
//
//	/**
//	 * 停止任务
//	 */
//	@Override
//	public Result<String> stopJob(long jobId) {
//		return apiService.stopJob(jobId);
//	}
//
//	/**
//	 * Job生效;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Integer> enableJob(long jobId) {
//		Job job = new Job();
//		job.setId(jobId);
//		return apiService.enableJob(job);
//	}
//
//	/**
//	 * Job失效;
//	 * @param jobId
//	 * @return
//	 */
//	public Result<Integer> disableJob(long jobId) {
//		Job job = new Job();
//		job.setId(jobId);
//		return apiService.disableJob(job);
//	}
//
//    /**
//     * 关联依赖
//     * @param jobRelation
//     * @return
//     */
//    public Result<Long> createJobRelation(JobRelation jobRelation) {
//        return apiService.createJobRelation(jobRelation);
//    }
//
//    /**
//     * 删除依赖
//     * @param jobRelation
//     * @return
//     */
//    public Result<Integer> deleteJobRelation(JobRelation jobRelation) {
//        return apiService.deleteJobRelation(jobRelation);
//    }
//
//	/**
//	 * 拿到Job的运行状态
//	 */
//	@Override
//	public Result<List<JobStatus>> getGroupJobStatus(String groupId, int page,
//			int perPageCount, boolean instance) {
//		return apiService.getGroupJobStatus(groupId, page, perPageCount, instance);
//	}
//
//	/**
//	 * 得到Job的配置信息
//	 */
//	@Override
//	public Result<Job> getJobConfig(long jobId) {
//		return apiService.getJobConfig(jobId);
//	}
//
//	/**
//	 * 得到Job分级执行的进度
//	 */
//	@Override
//	public Result<ProgressDetail> getJobInstanceStatus(long jobId,
//			String instanceId) {
//		return apiService.getJobInstanceDetailStatus(jobId, instanceId);
//	}
//
//    /**
//     * 一个实例执行的总体的进度;
//     *
//     * @param instanceId
//     * @return
//     */
//    public Result<Double> getJobInstanceOvaralProgress(long instanceId) {
//        return apiService.getJobInstanceOvaralProgress(instanceId);
//    }
//	/**
//	 * 得到监控配置
//	 */
//	@Override
//	public Result<AssembledMonitor> getJobMonitor(long jobId) {
//		return apiService.getJobMonitor(jobId);
//	}
//
//	/**
//	 * 得到一个分组Job的数量
//	 */
//	@Override
//	public Result<Integer> getGroupJobsCount(String groupId) {
//		return apiService.getGroupJobsCount(groupId);
//	}
//
//	public Result<Integer> getJobHistoryCount(long jobId) {
//
//		return apiService.getJobHistoryCount(jobId);
//	}
//
//	/**
//	 * 得到job的历史快照
//	 */
//	public Result<JobHistoryRecord> getJobHistoryRecord(long jobId, int page,
//			int perPageCount) {
//		return apiService.getJobHistoryRecord(jobId, page, perPageCount);
//	}
//
//	/**
//	 * 授权
//	 */
//	@Override
//	public Result<String> grantAuth(String ownerId, String userId, String groupId) {
//		return apiService.grantAuth(ownerId, userId, groupId);
//	}
//
//}
