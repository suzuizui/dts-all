//package com.alibaba.dts.console.service.impl;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//
//import com.alibaba.dts.common.domain.ProgressBar;
//import com.alibaba.dts.common.domain.ProgressDetail;
//import com.alibaba.dts.common.domain.result.Result;
//import com.alibaba.dts.common.domain.result.ResultCode;
//import com.alibaba.dts.common.domain.store.ClientCluster;
//import com.alibaba.dts.common.domain.store.Job;
//import com.alibaba.dts.common.domain.store.ServerCluster;
//import com.alibaba.dts.common.domain.store.WarningSetup;
//import com.alibaba.dts.common.domain.store.assemble.AssembledMonitor;
//import com.alibaba.dts.common.domain.store.assemble.AssembledUserGroup;
//import com.alibaba.dts.common.domain.store.assemble.JobHistoryRecord;
//import com.alibaba.dts.common.domain.store.assemble.JobStatus;
//import com.alibaba.dts.common.domain.store.assemble.WarningNotifier;
//import com.alibaba.dts.common.util.GroupIdUtil;
//import com.alibaba.dts.console.service.ConsoleService;
///**
// * ConsoleService Mock实现;
// * @author luliang.ll
// *
// */
//public class ConsoleServiceMock implements ConsoleService {
//
//	@Override
//	public Result<List<AssembledUserGroup>> getUserGroups(String userId, ServerCluster serverCluster) {
//		Result<List<AssembledUserGroup>> result = new Result<List<AssembledUserGroup>>();
//		List<AssembledUserGroup> list = new ArrayList<AssembledUserGroup>();
//		for(int i = 0 ; i < 5 ; i ++) {
//			AssembledUserGroup assembledUserGroup = new AssembledUserGroup();
//			assembledUserGroup.setGroupDesc("group" + i);
//			assembledUserGroup.setGroupId(GroupIdUtil.generateGroupId("1", "1", 3, i));
//			assembledUserGroup.setGroupJobNum(20);
//			assembledUserGroup.setServerClusterId(1);
//			assembledUserGroup.setUserId("65782");
//			list.add(assembledUserGroup);
//		}
//		result.setResultCode(ResultCode.SUCCESS);
//		result.setData(list);
//		return result;
//	}
//
//	@Override
//	public Result<List<Job>> getGroupJobs(String groupId, int page, int perPageCount, ServerCluster luster) {
//		Result<List<Job>> result = new Result<List<Job>>();
//		List<Job> list = new ArrayList<Job>();
//		// 为每个组生成20个Job;
//		for(int i = 0; i < perPageCount; i++) {
//			Job aj = new Job();
//			aj.setCronExpression("*****");
//			aj.setMaxInstanceAmount(1);
//			aj.setDescription("彩票定时开奖");
//			aj.setId(i);
//			aj.setJobProcessor("com.taobao.XXXX");
//			list.add(aj);
//		}
//		result.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS);
//		result.setData(list);
//		return result;
//	}
//
//	@Override
//	public Result<List<JobStatus>> getGroupJobStatus(String groupId, int page, int perPageCount) {
//		Result<List<JobStatus>> result = new Result<List<JobStatus>>();
//		List<JobStatus> list = new ArrayList<JobStatus>();
//		for(int i = 0; i < perPageCount; i++) {
//			JobStatus st = new JobStatus();
//			st.setJobDesc("彩票定时发放");
//			st.setJobId(i);
//			st.setRunningStatus(1);
//			Map<String, String> progress = new HashMap<String, String>();
//			progress.put("1", "65%");
//			progress.put("2", "86.6%");
//			st.setOverallProgress(progress);
//			list.add(st);
//		}
//		result.setResultCode(ResultCode.SUCCESS);
//		result.setData(list);
//		return result;
//	}
//
//	@Override
//	public Result<String> grantAuth(String userId, String groupId) {
//		Result<String> result = new Result<String>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public TreeMap<Long, ServerCluster> getUserClusters() {
//		
//		TreeMap<Long, ServerCluster> clusters = new TreeMap<Long, ServerCluster>();
//		ServerCluster sc = new ServerCluster();
//		sc.setDescription("杭州");
//		sc.setId(1);
//		sc.setServerClusterId(1);
//		sc.setServerGroupId(1);
//		clusters.put(sc.getServerClusterId(), sc);
//		return clusters;
//	}
//
//	@Override
//	public Result<String> creareGroup(ClientCluster group, ServerCluster cluster) {
//		Result<String> result = new Result<String>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<String> deleteGroup(String groupId, ServerCluster cluster) {
//		Result<String> result = new Result<String>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<Long> createJob(Job job, ServerCluster serverCluster) {
//		Result<Long> result = new Result<Long>();
//		result.setResultCode(ResultCode.CREATE_JOB_SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<Long> deleteJob(long jobId, ServerCluster serverCluster) {
//		Result<Long> result = new Result<Long>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<String> updateJobMonitor(WarningSetup warningSetup) {
//		Result<String> result = new Result<String>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<String> startJob(long jobId) {
//		Result<String> result = new Result<String>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<String> stopJob(long jobId) {
//		Result<String> result = new Result<String>();
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<Object> updateJob(Job job, ServerCluster serverCluster) {
//		Result<Object> result = new Result<Object>();
//		result.setResultCode(ResultCode.UPDATE_JOB_SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<Job> getJobConfig(long jobId, ServerCluster cluster) {
//		Result<Job> result = new Result<Job>();
//		Job job = new Job();
//		job.setCronExpression("******");
//		job.setMaxInstanceAmount(0);
//		job.setType(0);
//		job.setId(1);
//		job.setJobProcessor("com.taobao.v1");
//		job.setDescription("XXXXX");
//		result.setResultCode(ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS);
//		result.setData(job);
//		return result;
//	}
//
//	@Override
//	public Result<ProgressDetail> getJobInstanceStatus(long jobId, String instanceId) {
//		Result<ProgressDetail> result = new Result<ProgressDetail>();
//		ProgressDetail progressDetail = new ProgressDetail();
//		ProgressBar overal = new ProgressBar();
//		overal.setFailureAmount(1);
//		overal.setFoundAmount(1);
//		overal.setInitAmount(2);
//		overal.setInstanceId(instanceId);
//		overal.setName("总体");
//		overal.setQueueAmount(3);
//		overal.setStartAmount(1);
//		overal.setTotalAmount(8);
//		progressDetail.setTotalProgressBar(overal);
//		
//		ProgressBar first = new ProgressBar();
//		first.setFailureAmount(1);
//		first.setFoundAmount(1);
//		first.setInitAmount(2);
//		first.setInstanceId(instanceId);
//		first.setName("第一级");
//		first.setQueueAmount(4);
//		first.setStartAmount(1);
//		first.setTotalAmount(9);
//		progressDetail.add(first);
//		
//		ProgressBar second = new ProgressBar();
//		second.setFailureAmount(1);
//		second.setFoundAmount(2);
//		second.setInitAmount(2);
//		second.setInstanceId(instanceId);
//		second.setName("第二级");
//		second.setQueueAmount(3);
//		second.setStartAmount(1);
//		second.setTotalAmount(9);
//		progressDetail.add(second);
//		
//		result.setData(progressDetail);
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<AssembledMonitor> getJobMonitor(long jobId) {
//		Result<AssembledMonitor> result = new Result<AssembledMonitor>();
//		AssembledMonitor monitor = new AssembledMonitor();
//		monitor.setErrorRate(0.65);
//		monitor.setJobId(jobId);
//		monitor.setTimeoutLimit(100);
//		List<WarningNotifier> notifiers = new LinkedList<WarningNotifier>();
//		WarningNotifier warn = new WarningNotifier();
//		warn.setMobileId("11111");
//		warn.setWwId("111111");
//		notifiers.add(warn);
//		monitor.setNotifiers(notifiers);
//		result.setResultCode(ResultCode.SUCCESS);
//		result.setData(monitor);
//		return result;
//	}
//
//	@Override
//	public Result<JobHistoryRecord> getJobHistoryRecord(long jobId, int page, int perPageCount) {
//		Result<JobHistoryRecord> result = new Result<JobHistoryRecord>();
//		JobHistoryRecord record = new JobHistoryRecord();
//		record.setJobId(jobId);
//		StringBuilder sb = new StringBuilder();
//		for(int i = 0; i < perPageCount; i++) {
//			sb.append(i).append(",");
//			sb.append("历史记录").append("#");
//		}
//		sb.append(1).append(",");
//		sb.append("历史记录");
//		record.setResult(sb.toString());
//		result.setResultCode(ResultCode.SUCCESS);
//		result.setData(record);
//		return result;
//	}
//
//	@Override
//	public Result<Integer> getGroupJobsCount(String groupId, ServerCluster cluster) {
//		Result<Integer> result = new Result<Integer>();
//		result.setData(20);
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<Integer> getJobHistoryCount(long jobId) {
//		Result<Integer> result = new Result<Integer>();
//		result.setData(20);
//		result.setResultCode(ResultCode.SUCCESS);
//		return result;
//	}
//
//	@Override
//	public Result<Integer> enableJob(long jobId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Result<Integer> disableJob(long jobId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
