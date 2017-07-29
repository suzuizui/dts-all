package com.le.dts.console.module.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.global.Global;
import com.le.dts.console.util.ConsoleUtil;
import com.le.dts.console.util.LoggerUtil;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.DtsUser;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.common.domain.store.WarningSetup;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.common.domain.store.assemble.JobExecuteHistory;
import com.le.dts.common.domain.store.assemble.JobHistoryRecord;
import com.le.dts.common.domain.store.assemble.JobStatus;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.console.api.ApiService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * DTS SDK Action;
 * Created by luliang on 14/12/29.
 */
public class SDKAction {

    private static final Log logger = LogFactory.getLog(SDKAction.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ApiService apiService;
    
    @Autowired
	private Zookeeper zookeeper;

    public void doGetClusters(Context context, Navigator navigator) {

        TreeMap<Long, Cluster> clusterTreeMap = apiService.getUserClusters();
        Collection<Cluster> clusterList = clusterTreeMap.values();
        JSONObject jsonObject = new JSONObject();
        if(clusterList.size() == 0) {
            jsonObject.put("success", false);
        } else {
            jsonObject.put("success", true);
            JSONArray jsonArray = new JSONArray();
            for(Cluster cluster: clusterList) {
                JSONObject json = JSON.parseObject(cluster.toString());
                jsonArray.add(json);
            }
            jsonObject.put("clusters", jsonArray);
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doCreateGroup(Context context, Navigator navigator,
                              @Param(name = "clusterId ") String clusterId,
                              @Param(name = "groupDesc") String groupDesc) {
        JSONObject jsonObject = new JSONObject();
        ClientGroup group = new ClientGroup();
        group.setDescription(StringUtils.trim(groupDesc));
//        try {
//            String txt = new String(groupDesc.toString().getBytes("iso8859-1"), "utf-8");
//            group.setDescription(StringUtil.trim(txt));
//        } catch (UnsupportedEncodingException e) {
//            logger.error("encode error!" + groupDesc);
//        }
//        group.setDescription(StringUtil.trim(groupDesc));
        long serverGroupId = apiService.getClusterRandomGroup(Long.valueOf(clusterId));
        group.setServerGroupId(Long.valueOf(serverGroupId));
        // 创建一个分组;
        Result<String> result = apiService.createGroup(group, Long.valueOf(clusterId));
        
        LoggerUtil.printLog(logger, request, "SDKAction.doCreateGroup",
        		new Object[]{"clusterId:" + clusterId, "groupDesc:" + groupDesc, "result:" + result});
        
        // 返回页面处理;
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("userGroupId", result.getData());
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doDeleteGroup(Context context, Navigator navigator,
                              @Param(name = "userGroupId") String userGroupId) {
        JSONObject jsonObject = new JSONObject();
        DtsUser user = Global.getDtsUser(request);
        Result<String> result = apiService.deleteGroup(userGroupId, user.getUserId());
        
        LoggerUtil.printLog(logger, request, "SDKAction.doDeleteGroup", 
        		new Object[]{"userGroupId:" + userGroupId, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doGetUserGroups(Context context, Navigator navigator,
                                @Param(name = "clusterId") String clusterId) {
        JSONObject jsonObject = new JSONObject();
        DtsUser user = Global.getDtsUser(request);
        Cluster cluster = new Cluster();
        cluster.setId(Long.valueOf(clusterId));
        Result<List<AssembledUserGroup>> clusterUserGroupResult
                   = apiService.getUserGroups(user.getUserId(), cluster);
        if(clusterUserGroupResult.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            List<AssembledUserGroup> assembledUserGroups = clusterUserGroupResult.getData();
            JSONArray jsonArray = new JSONArray();
            for(AssembledUserGroup assembledUserGroup: assembledUserGroups) {
                JSONObject json = JSON.parseObject(assembledUserGroup.toString());
                jsonArray.add(json);
            }
            jsonObject.put("groups", jsonArray);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", clusterUserGroupResult.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doCreateJob(Context context, Navigator navigator,
                            @Param(name = "userGroupId") String userGroupId,
                            @Param(name = "jobType") int jobType,
                            @Param(name = "cronExpression") String cronExpression,
                            @Param(name = "jobProcessor") String jobProcessor,
                            @Param(name = "firePolicy") String firePolicy,
                            @Param(name = "jobDesc") String jobDesc,
                            @Param(name = "jobArguments") String jobArguments) {
        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        Cluster cluster = GroupIdUtil.getCluster(userGroupId);
        job.setServerGroupId(Long.valueOf(GroupIdUtil.getClientGroup(userGroupId).getServerGroupId()));
        job.setClientGroupId(Long.valueOf(GroupIdUtil.getClientGroup(userGroupId).getId()));
        job.setCreaterId(Global.getDtsUser(request).getUserId());
        job.setType(jobType);
        job.setCronExpression(cronExpression);
        job.setJobProcessor(jobProcessor);
        job.setDescription(jobDesc);
        job.setCreaterId(Global.getDtsUser(request).getUserId());
        job.setMaxInstanceAmount(Integer.valueOf(firePolicy));
        job.setJobArguments(StringUtils.isEmpty(jobArguments)? StringUtils.EMPTY: jobArguments);


        Result<Long> result = new Result<Long>();
        if(CommonUtil.isApiJob(jobType)) {
            result = apiService.createJob(job, cluster);
        } else {
            if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
                result.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
                jsonObject.put("invalid", true);
            } else {
                result = apiService.createJob(job, cluster);
            }
        }
        
        LoggerUtil.printLog(logger, request, "SDKAction.doCreateJob", 
        		new Object[]{"job:" + job, "result:" + result});

        if(result.getResultCode() == ResultCode.CREATE_JOB_SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("jobId", result.getData());
        } else {
            jsonObject.put("success", false);
            jsonObject.put("code", result.getResultCode().getCode());
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doDeleteJob(Context context, Navigator navigator,
                            @Param(name = "jobId") String jobId) {
        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
        Result<Long> result = apiService.deleteJob(job);
        
        LoggerUtil.printLog(logger, request, "SDKAction.doDeleteJob", 
        		new Object[]{"job:" + job, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("deleteCount", 1);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doUpdateJob(Context context, Navigator navigator,
                            @Param(name = "jobId") String jobId,
                            @Param(name = "groupId") String groupId,
                            @Param(name = "jobType") int jobType,
                            @Param(name = "cronExpression") String cronExpression,
                            @Param(name = "jobProcessor") String jobProcessor,
                            @Param(name = "firePolicy") String firePolicy,
                            @Param(name = "jobDesc") String jobDesc,
                            @Param(name = "jobArguments") String jobArguments) {
        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
        job.setServerGroupId(Long.valueOf(GroupIdUtil.getClientGroup(groupId).getServerGroupId()));
        job.setCreaterId(Global.getDtsUser(request).getUserId());
        job.setType(jobType);
        job.setCronExpression(cronExpression);
        job.setJobProcessor(jobProcessor);
        job.setDescription(jobDesc);
        job.setMaxInstanceAmount(Integer.valueOf(firePolicy));
        job.setJobArguments(jobArguments);

        Result<Integer> result = new Result<Integer>();
        if(CommonUtil.isApiJob(jobType)) {
            result = apiService.updateJob(job);
        } else {
            if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
                result.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
                jsonObject.put("invalid", true);
            }  else {
                result = apiService.updateJob(job);
            }
        }
        
        LoggerUtil.printLog(logger, request, "SDKAction.doUpdateJob", 
        		new Object[]{"job:" + job, "result:" + result});

        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("updateCount", 1);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doUpdateJobWithArguments(Context context, Navigator navigator,
                            @Param(name = "jobId") String jobId,
                            @Param(name = "groupId") String groupId,
                            @Param(name = "jobType") int jobType,
                            @Param(name = "cronExpression") String cronExpression,
                            @Param(name = "jobProcessor") String jobProcessor,
                            @Param(name = "firePolicy") String firePolicy,
                            @Param(name = "jobDesc") String jobDesc,
                            @Param(name = "jobArguments") String jobArguments) {
        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
        job.setServerGroupId(Long.valueOf(GroupIdUtil.getClientGroup(groupId).getServerGroupId()));
        job.setCreaterId(Global.getDtsUser(request).getUserId());
        job.setType(jobType);
        job.setCronExpression(cronExpression);
        job.setJobProcessor(jobProcessor);
        job.setDescription(jobDesc);
        job.setMaxInstanceAmount(Integer.valueOf(firePolicy));
        job.setJobArguments(jobArguments);

        Result<Integer> result = new Result<Integer>();
        if(CommonUtil.isApiJob(jobType)) {
            result = apiService.updateJob(job);
        } else {
            if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
                result.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
                jsonObject.put("invalid", true);
            }  else {
                result = apiService.updateJob(job);
            }
        }
        
        LoggerUtil.printLog(logger, request, "SDKAction.doUpdateJobWithArguments", 
        		new Object[]{"job:" + job, "result:" + result});

        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("updateCount", 1);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doUpdateJobArguments(Context context, Navigator navigator,
                            @Param(name = "jobId") String jobId,
                            @Param(name = "jobArguments") String jobArguments) {
        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
        job.setJobArguments(StringUtils.isEmpty(jobArguments)? StringUtils.EMPTY: jobArguments);

        Result<Integer> result = new Result<Integer>();
        result = apiService.updateJobArguments(job);

        LoggerUtil.printLog(logger, request, "SDKAction.doUpdateJobArguments", 
        		new Object[]{"job:" + job, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("updateCount", 1);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }


    public void doGetGroupJobs(Context context, Navigator navigator,
                               @Param(name = "groupId") String groupId) {
        JSONObject jsonObject = new JSONObject();
        Result<List<Job>> result = apiService.getGroupJobs(groupId, 0, Integer.MAX_VALUE, null);

        // 转成一个JSON返回
        if(result.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
            JSONArray groupJobsJson = new JSONArray();
            for(Job job: result.getData()) {
                JSONObject json = JSON.parseObject(job.toString());
                groupJobsJson.add(json);
            }
            jsonObject.put("success", true);
            jsonObject.put("jobs", groupJobsJson);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doEnableJob(Context context, Navigator navigator,
                            @Param(name = "jobId") String jobId) {

        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
        Result<Integer> result = apiService.enableJob(job);
        
        LoggerUtil.printLog(logger, request, "SDKAction.doEnableJob", 
        		new Object[]{"job:" + job, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getData());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doDisableJob(Context context, Navigator navigator,
                             @Param(name = "jobId") String jobId) {
        JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
        Result<Integer> result = apiService.disableJob(job);
        
        LoggerUtil.printLog(logger, request, "SDKAction.doDisableJob", 
        		new Object[]{"job:" + job, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doInstanceStartJob(Context context, Navigator navigator,
                            @Param(name = "groupId") String groupId,
                            @Param(name = "jobId") String jobId) {

        JSONObject jsonObject = new JSONObject();
        Result<String> result = apiService.startJob(groupId, Long.valueOf(jobId));
        
        LoggerUtil.printLog(logger, request, "SDKAction.doInstanceStartJob", 
        		new Object[]{"groupId:" + groupId, "jobId:" + jobId, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doInstanceStopJob(Context context, Navigator navigator,
                             @Param(name = "jobId") String jobId) {
        JSONObject jsonObject = new JSONObject();
        Result<String> result = apiService.stopJob(Long.valueOf(jobId));
        
        LoggerUtil.printLog(logger, request, "SDKAction.doInstanceStopJob", 
        		new Object[]{"jobId:" + jobId, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doSetMonitor(Context context, Navigator navigator,
                             @Param(name = "jobId") String jobId,
                             @Param(name = "contact") String contact,
                             @Param(name = "warningSetup") String warningSetup) {
        JSONObject jsonObject = new JSONObject();
        WarningSetup warnInfo = new WarningSetup();
        warnInfo.setJobId(Long.valueOf(jobId));
        warnInfo.setContact(contact);
        warnInfo.setWarningSetup(warningSetup);

        Result<String> result = apiService.updateJobMonitor(warnInfo);

        LoggerUtil.printLog(logger, request, "SDKAction.doSetMonitor", 
        		new Object[]{"jobId:" + jobId, "contact:" + contact, "warningSetup:" + warningSetup, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doGetJobStatus(Context context, Navigator navigator,
                               @Param(name = "jobId") String jobId) {
        JSONObject jsonObject = new JSONObject();
        Result<JobStatus> result = apiService.getJobStatus(Long.valueOf(jobId));
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            JobStatus jobStatus = result.getData();
            jsonObject.put("status", jobStatus.toString());
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doGetJobDetailStatistics(Context context, Navigator navigator,
                                         @Param(name = "jobId") String jobId,
                                         @Param(name = "instanceId") String instanceId) {
        JSONObject jsonObject = new JSONObject();
        Result<ProgressDetail>  instanceStatusResult = apiService.getJobInstanceDetailStatus(Long
                .valueOf(jobId), instanceId);
        if(instanceStatusResult.getResultCode() == ResultCode.SUCCESS) {
            // 构造一个JSONArray将分级进度放进去;
            ProgressDetail progressDetail = instanceStatusResult.getData();
            jsonObject.put("statistics", progressDetail.toString());
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", instanceStatusResult.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);

    }

    public void doGetJobHistory(Context context, Navigator navigator,
                                @Param(name = "jobId") String jobId) {
        JSONObject jsonObject = new JSONObject();
        Result<JobHistoryRecord> recordResult = apiService.getJobHistoryRecord(
                Long.valueOf(jobId), 0, Integer.MAX_VALUE);
        if(recordResult.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            JobHistoryRecord jobHistoryRecord = recordResult.getData();
            JobExecuteHistory jobExecuteHistory = new JobExecuteHistory();
            jobExecuteHistory.setJobId(Long.valueOf(jobId));
            String historyResult = jobHistoryRecord.getResult();
            if(StringUtils.isNotEmpty(historyResult)) {
                TreeMap<String, String> historyRecords = jobExecuteHistory.getHistoryRecords();
                String[] historyItems = StringUtils.split(historyResult, "#");
                for(String historyItem: historyItems) {
                    if(StringUtils.isNotEmpty(historyItem)) {
                        String[] entry = StringUtils.split(historyItem, "@");
                        if(entry.length == 2) {
                            historyRecords.put(entry[0], entry[1]);
                        }
                    }
                }
                jobExecuteHistory.setHistoryRecords(historyRecords);
            }
            jsonObject.put("history", jobExecuteHistory.toString());
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", recordResult.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doGrantAuth(Context context, Navigator navigator,
                            @Param(name = "ownerUserId") String ownerUserId,
                            @Param(name = "grantUserId") String grantUserId,
                            @Param(name = "groupId") String groupId) {
        JSONObject jsonObject = new JSONObject();
        Result<String> result = apiService.grantAuth(ownerUserId, grantUserId, groupId);
        
        LoggerUtil.printLog(logger, request, "SDKAction.doGrantAuth", 
        		new Object[]{"ownerUserId:" + ownerUserId, "grantUserId:" + grantUserId, "groupId:" + groupId, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", result.getResultCode().getInformation());
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }


    /**
     * 复位图式计算
     * @param context
     * @param navigator
     */
    public void doResetJobRelation(Context context, Navigator navigator, @Param(name = "jobIds") String jobIds) {
    	if(StringUtils.isBlank(jobIds)) {
    		logger.error("[JobManageAction]: doResetJobRelation jobIds is blank error");
    		return ;
    	}
    	String[] jobIdList = jobIds.split(Constants.COLON);
    	
    	if(null == jobIdList || jobIdList.length <= 0) {
    		logger.error("[JobManageAction]: doResetJobRelation split error, jobIds:" + jobIds + ", jobIdList:" + Arrays.toString(jobIdList));
    		return ;
    	}
    	
    	logger.info("[JobManageAction]: doResetJobRelation, jobIds:" + jobIds + ", jobIdList:" + Arrays.toString(jobIdList));
    	
    	boolean result = apiService.resetJobRelation(jobIdList);
    	
    	JSONObject jsonObject = new JSONObject();
    	
    	LoggerUtil.printLog(logger, request, "SDKAction.doResetJobRelation", 
        		new Object[]{"jobIds:" + jobIds, "result:" + result});
    	
    	if(result) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("errMsg", "系统内部异常");
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }
    
    public void doCreateRelation(Context context, Navigator navigator,
            @Param(name = "afterJobId") String afterJobId,
            @Param(name = "beforeJobId") String beforeJobId) {
		JobRelation jobRelation = new JobRelation();
		jobRelation.setJobId(Long.valueOf(afterJobId));
		jobRelation.setBeforeJobId(Long.valueOf(beforeJobId));
		
		Result<Long> result = apiService.createJobRelation(jobRelation);
		
		LoggerUtil.printLog(logger, request, "SDKAction.doCreateRelation", 
        		new Object[]{"afterJobId:" + afterJobId, "beforeJobId:" + beforeJobId, "result:" + result});
		
		JSONObject json = new JSONObject();
		if(result.getResultCode() == ResultCode.SUCCESS) {
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("errMsg", "系统内部的异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		
		ConsoleUtil.writeJsonToResponse(response, json);
	
	}
	
	public void doDeleteRelation(Context context, Navigator navigator,
	            @Param(name = "afterJobId") String afterJobId,
	            @Param(name = "beforeJobId") String beforeJobId) {
		JobRelation jobRelation = new JobRelation();
		jobRelation.setJobId(Long.valueOf(afterJobId));
		jobRelation.setBeforeJobId(Long.valueOf(beforeJobId));
		
		Result<Integer> result = apiService.deleteJobRelation(jobRelation);
		
		LoggerUtil.printLog(logger, request, "SDKAction.doDeleteRelation", 
        		new Object[]{"afterJobId:" + afterJobId, "beforeJobId:" + beforeJobId, "result:" + result});
		
		JSONObject json = new JSONObject();
		if(result.getResultCode() == ResultCode.SUCCESS) {
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("errMsg", "系统内部的异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		
		ConsoleUtil.writeJsonToResponse(response, json);
	}
	
	public void doQueryClientGroupIpList(Context context, Navigator navigator, 
			@Param(name = "clientGroup") String clientGroup, 
			@Param(name = "jobId") long jobId) {
		
		JSONObject jsonObject = new JSONObject();
		
		List<String> clientMachines = zookeeper.getClientGroupIpList(clientGroup, jobId);
		
		 if(CollectionUtils.isEmpty(clientMachines)) {
			 
			 jsonObject.put("success", false);
			 jsonObject.put("errMsg", "there is no ip list");
			 ConsoleUtil.writeJsonToResponse(response, jsonObject);
			 return ;
		 }
		 
		String json = RemotingSerializable.toJson(clientMachines, false);
		
		jsonObject.put("success", true);
		jsonObject.put("ipList", json);
		 
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
    
}
