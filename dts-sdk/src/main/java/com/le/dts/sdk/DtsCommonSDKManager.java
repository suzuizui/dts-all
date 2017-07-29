package com.le.dts.sdk;

import java.util.ArrayList;
import java.util.List;

import com.le.dts.sdk.client.DtsHttpClient;
import com.le.dts.sdk.context.SDKContext;
import com.le.dts.sdk.util.exception.SDKModeUnsupportException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.common.domain.store.assemble.JobExecuteHistory;
import com.le.dts.common.domain.store.assemble.JobInstanceDetailStatus;
import com.le.dts.common.domain.store.assemble.JobStatus;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.CheckUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.common.util.StringUtil;
import com.le.dts.common.fastjson.JSON;
import com.le.dts.common.fastjson.JSONArray;
import com.le.dts.common.fastjson.JSONObject;

/**
 * Created by luliang on 14/12/24.
 */
public class DtsCommonSDKManager implements DtsSDKManager {

    private static final Log logger = LogFactory.getLog(DtsCommonSDKManager.class);

    private SDKMode mode = SDKMode.DAILY_MODE;

    public long clusterId;

    private DtsHttpClient client = new DtsHttpClient();

    public DtsCommonSDKManager(SDKMode mode) {
        this.mode = mode;
        if(mode == SDKMode.DAILY_MODE) {
            client.setDomainUrl(SDKContext.DTS_DOMAIN_DAILY_URL);
        } else if(mode == SDKMode.ONLINE_MODE){
            client.setDomainUrl(SDKContext.DTS_DOMAIN_ONLINE_URL);
        } else {
            throw new SDKModeUnsupportException("sdk mode not support!");
        }
    }

    public DtsCommonSDKManager() {
        client.setDomainUrl(SDKContext.DTS_DOMAIN_DAILY_URL);
    }

    @Override
    public Result<List<Cluster>> getDtsClustersInfo() {
        Result<List<Cluster>> result = new Result<List<Cluster>>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_clusters");
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getDtsClustersInfo json isBlank, postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        List<Cluster> dtsClusters = null;
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                JSONArray clusterArray = jsonResult.getJSONArray("clusters");
                dtsClusters = new ArrayList<Cluster>(clusterArray.size());
                for(int i = 0; i < clusterArray.size(); i++) {
                    JSONObject json = (JSONObject) clusterArray.get(i);
                    Cluster cluster = Cluster.newInstance(json.toJSONString());
                    dtsClusters.add(cluster);
                }
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(dtsClusters);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getDtsClustersInfo error, postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<String> createGroup(long clusterId, String groupDescription) {
        Result<String> result = new Result<String>();
        if(StringUtil.isBlank(groupDescription)) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.setData("组描述不能为空!");
            return result;
        }
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_create_group");
        client.addParameter("clusterId", String.valueOf(clusterId))
                .addParameter("groupDesc", groupDescription);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: createGroup json isBlank" 
        			+ ", clusterId:" + clusterId 
            		+ ", groupDescription:" + groupDescription 
            		+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData((String)jsonResult.get("userGroupId"));
            }

        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: createGroup error"
            		+ ", clusterId:" + clusterId 
            		+ ", groupDescription:" + groupDescription 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Boolean> deleteGroup(String userGroupId) {
        Result<Boolean> result = new Result<Boolean>();
        if(StringUtil.isBlank(userGroupId) || !GroupIdUtil.checkClientGroupId(userGroupId)) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.setData(false);
            return result;
        }
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_delete_group");
        client.addParameter("userGroupId", userGroupId);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: deleteGroup json isBlank"
        			+ ", userGroupId:" + userGroupId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }

        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: deleteGroup error"
            		+ ", userGroupId:" + userGroupId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<List<AssembledUserGroup>> getUserGroups(long clusterId) {
        Result<List<AssembledUserGroup>> result = new Result<List<AssembledUserGroup>>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_user_groups");
        client.addParameter("clusterId", String.valueOf(clusterId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getUserGroups json isBlank"
        			+ ", clusterId:" + clusterId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                JSONArray jsonArray = jsonResult.getJSONArray("groups");
                List<AssembledUserGroup> userGroups = new ArrayList<AssembledUserGroup>(jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    AssembledUserGroup userGroup = AssembledUserGroup.newInstance(jsonArray.get(i).toString());
                    userGroups.add(userGroup);
                }
                result.setData(userGroups);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getUserGroups error"
            		+ ", clusterId:" + clusterId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }


    @Override
    public Result<Long> createJob(String userGroupId, Job job) {
        Result<Long> result = new Result<Long>();
        // 检查userGroupId
        if(StringUtil.isBlank(userGroupId) || !GroupIdUtil.checkClientGroupId(userGroupId)) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("组名不正确!");
            return result;
        }
        Result<Boolean> checkResult = CheckUtil.checkUserConfigJob(job);
        if(!checkResult.getData()) {
            result.setResultCode(checkResult.getResultCode());
            result.getResultCode().setInformation(checkResult.getResultCode().getInformation());
            return result;
        }

        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_create_job");
        client.addParameter("userGroupId", userGroupId);
        client.addParameter("jobProcessor", job.getJobProcessor())
                .addParameter("jobType", String.valueOf(job.getType()))
                .addParameter("cronExpression", job.getCronExpression())
                .addParameter("jobDesc", job.getDescription())
                .addParameter("firePolicy", String.valueOf(job.getMaxInstanceAmount()))
                .addParameter("jobArguments", job.getJobArguments());
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: createJob json isBlank" 
        			+ ", userGroupId:" + userGroupId 
            		+ ", job:" + job 
            		+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(jsonResult.getLong("jobId"));
            }

        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: createJob"
            		+ ", userGroupId:" + userGroupId 
            		+ ", job:" + job 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Integer> deleteJob(long jobId) {
        Result<Integer> result = new Result<Integer>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_delete_job");
        client.addParameter("jobId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: deleteJob json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(jsonResult.getInteger("deleteCount"));
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: deleteJob error"
            		+ ", jobId:" + jobId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Integer> updateJob(String groupId, Job job) {
        Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkResult = CheckUtil.checkUserConfigJob(job);
        if(!checkResult.getData()) {
            result.setResultCode(checkResult.getResultCode());
            result.getResultCode().setInformation(checkResult.getResultCode().getInformation());
            return result;
        }
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_update_job");
        client.addParameter("jobId", String.valueOf(job.getId()))
                .addParameter("jobProcessor", job.getJobProcessor())
                .addParameter("jobType", String.valueOf(job.getType()))
                .addParameter("cronExpression", job.getCronExpression())
                .addParameter("jobDesc", job.getDescription())
                .addParameter("firePolicy", String.valueOf(job.getMaxInstanceAmount()))
                .addParameter("jobArguments", job.getJobArguments())
                .addParameter("groupId", groupId);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: updateJob json isBlank"
        			+ ", groupId:" + groupId 
        			+ ", job:" + job 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(jsonResult.getInteger("updateCount"));
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: updateJob error"
            		+ ", groupId:" + groupId 
            		+ ", job:" + job 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Integer> updateJobWithArguments(String groupId, Job job) {
        Result<Integer> result = new Result<Integer>();
        Result<Boolean> checkResult = CheckUtil.checkUserConfigJob(job);
        if(!checkResult.getData()) {
            result.setResultCode(checkResult.getResultCode());
            result.getResultCode().setInformation(checkResult.getResultCode().getInformation());
            return result;
        }
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_update_job");
        client.addParameter("jobId", String.valueOf(job.getId()))
                .addParameter("jobProcessor", job.getJobProcessor())
                .addParameter("jobType", String.valueOf(job.getType()))
                .addParameter("cronExpression", job.getCronExpression())
                .addParameter("jobDesc", job.getDescription())
                .addParameter("firePolicy", String.valueOf(job.getMaxInstanceAmount()))
                .addParameter("jobArguments", job.getJobArguments())
                .addParameter("groupId", groupId);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: updateJobWithArguments json isBlank"
        			+ ", groupId:" + groupId 
        			+ ", job:" + job 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(jsonResult.getInteger("updateCount"));
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: updateJobWithArguments error"
            		+ ", groupId:" + groupId 
            		+ ", job:" + job 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Integer> updateJobArguments(long jobId, String jobArguments) {
        Result<Integer> result = new Result<Integer>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_update_job_arguments");
        client.addParameter("jobId", String.valueOf(jobId))
                .addParameter("jobArguments", jobArguments);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: updateJobArguments json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", jobArguments:" + jobArguments 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(jsonResult.getInteger("updateCount"));
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: updateJobArguments error"
            		+ ", jobId:" + jobId 
            		+ ", jobArguments:" + jobArguments 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    public Result<List<Job>> getJobsForGroup(String userGroupId) {
        Result<List<Job>> result = new Result<List<Job>>();
        // 检查userGroupId
        if(StringUtil.isBlank(userGroupId) || !GroupIdUtil.checkClientGroupId(userGroupId)) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("组名不正确!");
            return result;
        }
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_group_jobs");
        client.addParameter("groupId", userGroupId);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getJobsForGroup json isBlank"
        			+ ", userGroupId:" + userGroupId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                JSONArray jsonArray = jsonResult.getJSONArray("jobs");
                List<Job> groupJobs = new ArrayList<Job>(jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    Job job = Job.newInstance(jsonArray.get(i).toString());
                    groupJobs.add(job);
                }
                result.setData(groupJobs);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getJobsForGroup error"
            		+ ", userGroupId:" + userGroupId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }
    
    public Result<List<Job>> getJobsForGroupByPage(String userGroupId, int pageSize, int pageNumber) {
    	
    	Result<List<Job>> result = new Result<List<Job>>();
        // 检查userGroupId
        if(StringUtil.isBlank(userGroupId) || !GroupIdUtil.checkClientGroupId(userGroupId)) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("组名不正确!");
            return result;
        }
    	
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_group_jobs_by_page");
        client.addParameter("groupId", userGroupId);
        client.addParameter("pageSize", String.valueOf(pageSize));
        client.addParameter("pageNumber", String.valueOf(pageNumber));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getJobsForGroupByPage json isBlank"
        			+ ", userGroupId:" + userGroupId 
        			+ ", postJson:" + postJson 
        			+ ", pageSize:" + pageSize 
        			+ ", pageNumber:" + pageNumber);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                JSONArray jsonArray = jsonResult.getJSONArray("jobs");
                List<Job> groupJobs = new ArrayList<Job>(jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    Job job = Job.newInstance(jsonArray.get(i).toString());
                    groupJobs.add(job);
                }
                result.setData(groupJobs);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getJobsForGroupByPage error"
            		+ ", userGroupId:" + userGroupId 
            		+ ", postJson:" + postJson 
            		+ ", pageSize:" + pageSize 
        			+ ", pageNumber:" + pageNumber, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        
        return result;
    }

    @Override
    public Result<Boolean> enableJob(long jobId) {
        Result<Boolean> result = new Result<Boolean>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_enable_job");
        client.addParameter("jobId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: enableJob json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: enableJob error"
            		+ ", jobId:" + jobId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Boolean> disableJob(long jobId) {
        Result<Boolean> result = new Result<Boolean>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_disable_job");
        client.addParameter("jobId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: disableJob json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: disableJob error"
            		+ ", jobId:" + jobId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Boolean> instanceRunJob(String userGroupId, long jobId) {
        Result<Boolean> result = new Result<Boolean>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_instance_start_job");
        client.addParameter("jobId", String.valueOf(jobId)).addParameter("groupId", userGroupId);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: instanceRunJob json isBlank"
        			+ ", userGroupId:" + userGroupId 
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: instanceRunJob error"
            		+ ", userGroupId:" + userGroupId 
            		+ ", jobId:" + jobId 
            		+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Boolean> instanceStopJob(long jobId) {
        Result<Boolean> result = new Result<Boolean>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_instance_stop_job");
        client.addParameter("jobId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: instanceStopJob json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: instanceStopJob error"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<JobStatus> getJobRunningStatus(long jobId) {
        Result<JobStatus> result = new Result<JobStatus>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_job_status");
        client.addParameter("jobId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getJobRunningStatus json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                JobStatus jobStatus = JobStatus.newInstance(jsonResult.get("status").toString());
                result.setData(jobStatus);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getJobRunningStatus error"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<JobInstanceDetailStatus> getJobDetailRunningStatus(long jobId, long instanceId) {
        Result<JobInstanceDetailStatus> result = new Result<JobInstanceDetailStatus>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_job_detail_statistics");
        client.addParameter("jobId", String.valueOf(jobId))
                .addParameter("instanceId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getJobDetailRunningStatus json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", instanceId:" + instanceId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                JobInstanceDetailStatus jobStatus = JobInstanceDetailStatus.newInstance(jsonResult.get("statistics").toString());
                result.setData(jobStatus);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getJobDetailRunningStatus error"
        			+ ", jobId:" + jobId 
        			+ ", instanceId:" + instanceId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<Boolean> grantGroupAuth(String userGroupId, String ownerUserId, String grantUserId) {
        Result<Boolean> result = new Result<Boolean>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_grant_auth");
        client.addParameter("userGroupId", userGroupId)
                .addParameter("ownerUserId", ownerUserId)
                .addParameter("grantUserId", grantUserId);
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: grantGroupAuth json isBlank"
        			+ ", userGroupId:" + userGroupId 
        			+ ", ownerUserId:" + ownerUserId 
        			+ ", grantUserId:" + grantUserId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: grantGroupAuth error"
        			+ ", userGroupId:" + userGroupId 
        			+ ", ownerUserId:" + ownerUserId 
        			+ ", grantUserId:" + grantUserId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }

    @Override
    public Result<JobExecuteHistory> getJobRunningHistoryStatus(long jobId) {
        Result<JobExecuteHistory> result = new Result<JobExecuteHistory>();
        client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_get_job_history");
        client.addParameter("jobId", String.valueOf(jobId));
        
        String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: getJobRunningHistoryStatus json isBlank"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
        
        try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                JobExecuteHistory jobExecuteHistory
                        = JobExecuteHistory.newInstance(jsonResult.get("history").toString());
                result.setData(jobExecuteHistory);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: getJobRunningHistoryStatus error"
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }
    
    public Result<Boolean> resetJobRelation(List<Long> startJobIdList) {
    	client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_reset_job_relation");
    	String jobIds = "";
    	for(Long jobId : startJobIdList) {
    		jobIds += jobId.longValue() + Constants.COLON;
    	}
    	jobIds = jobIds.substring(0, jobIds.length() - 1);
    	client.addParameter("jobIds", jobIds);
    	
    	Result<Boolean> result = new Result<Boolean>();
    	
    	String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: resetJobRelation json isBlank"
        			+ ", startJobIdList:" + startJobIdList 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
    	
    	try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: resetJobRelation error"
        			+ ", startJobIdList:" + startJobIdList 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }
    
    public Result<Boolean> createRelation(long afterJobId, long beforeJobId) {
    	client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_create_relation");
    	
    	client.addParameter("afterJobId", String.valueOf(afterJobId));
    	client.addParameter("beforeJobId", String.valueOf(beforeJobId));
    	
    	Result<Boolean> result = new Result<Boolean>();
    	
    	String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: createRelation json isBlank"
        			+ ", afterJobId:" + afterJobId 
        			+ ", beforeJobId:" + beforeJobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
    	
    	try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: createRelation error"
        			+ ", afterJobId:" + afterJobId 
        			+ ", beforeJobId:" + beforeJobId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }
    
    public Result<Boolean> deleteRelation(long afterJobId, long beforeJobId) {
    	client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_delete_relation");
    	
    	client.addParameter("afterJobId", String.valueOf(afterJobId));
    	client.addParameter("beforeJobId", String.valueOf(beforeJobId));
    	
    	Result<Boolean> result = new Result<Boolean>();
    	
    	String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: deleteRelation json isBlank"
        			+ ", afterJobId:" + afterJobId 
        			+ ", beforeJobId:" + beforeJobId 
        			+ ", postJson:" + postJson);
        	result.setResultCode(ResultCode.FAILURE);
        	return result;
        }
    	
    	try {
            JSONObject jsonResult = JSON.parseObject(postJson);
            Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
            if(success == false) {
                result.setResultCode(ResultCode.FAILURE);
                result.getResultCode().setInformation((String)jsonResult.get(Constants.ERROR_MSG));
            } else {
                result.setResultCode(ResultCode.SUCCESS);
                result.setData(true);
            }
        } catch (Throwable e) {
            logger.error("[DtsCommonSDKManager]: deleteRelation error"
        			+ ", afterJobId:" + afterJobId 
        			+ ", beforeJobId:" + beforeJobId 
        			+ ", postJson:" + postJson, e);
            result.setResultCode(ResultCode.SDK_IO_ERROR);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public List<String> queryClientGroupIpList(String groupId, long jobId) {
    	
    	client.setTarget("sdkManager.do").setSubmitAction("event_submit_do_query_client_group_ip_list");
    	
    	client.addParameter("clientGroup", groupId);
    	client.addParameter("jobId", String.valueOf(jobId));
    	
    	String postJson = client.doPost();
        
        if(StringUtil.isBlank(postJson)) {
        	logger.error("[DtsCommonSDKManager]: queryClientGroupIpList json isBlank"
        			+ ", groupId:" + groupId 
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson);
        	return new ArrayList<String>();
        }
    	
    	try {
			JSONObject jsonResult = JSON.parseObject(postJson);
			Boolean success = (Boolean)jsonResult.get(Constants.SUCCESS);
			String json = jsonResult.getString("ipList");
			if(success.booleanValue()) {
				return (List<String>)RemotingSerializable.fromJson(json, List.class);
			} else {
				return new ArrayList<String>();
			}
		} catch (Throwable e) {
			logger.error("[DtsCommonSDKManager]: queryClientGroupIpList json isBlank"
        			+ ", groupId:" + groupId 
        			+ ", jobId:" + jobId 
        			+ ", postJson:" + postJson, e);
			return new ArrayList<String>();
		}
    	
    }

    public long getClusterId() {
        return clusterId;
    }

    public void setClusterId(long clusterId) {
        this.clusterId = clusterId;
    }

    public SDKMode getMode() {
        return mode;
    }

    public void setMode(SDKMode mode) {
        this.mode = mode;
    }
}
