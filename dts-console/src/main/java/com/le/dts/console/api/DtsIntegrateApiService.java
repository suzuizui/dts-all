package com.le.dts.console.api;

import static com.alibaba.citrus.turbine.util.TurbineUtil.getTurbineRunData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.util.UserEnvUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.Valve;
import com.alibaba.citrus.turbine.TurbineRunData;
import com.alibaba.citrus.util.collection.DefaultMapEntry;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.common.exception.DtsException;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.console.util.ConsoleUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * EDAS 接入;
 * Created by luliang on 15/1/19.
 */
public class DtsIntegrateApiService implements Valve, ApplicationContextAware {

    private static Log logger = LogFactory.getLog(DtsIntegrateApiService.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    private ApplicationContext context;

    @Autowired
    private ApiService apiService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void invoke(PipelineContext pipelineContext) throws Exception {
        TurbineRunData runData = getTurbineRunData(request);
        String target = runData.getTarget();
        Map.Entry<String, Map<String, String>> requestEntry = mapUrl(target);
        if(requestEntry == null) {
            throw new DtsException("Bad request params!");
        }
        Map<String, String> requestParams = requestEntry.getValue();
        // 初始化访问;
        String userId = UserEnvUtil.initUser(request, response);
        JSONObject result = new JSONObject();

        // 适配方法;
        if(StringUtils.equals(requestEntry.getKey(), "initService")) {

            Result<Boolean> initResult = apiService.initDtsService(userId);
            if(initResult.getData()) {
                result.put(Constants.SUCCESS, true);
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "创建失败，请重拾！");
            }

        } else if(StringUtils.equals(requestEntry.getKey(), "getClusterInfo")) {
            Map<Long, Cluster> clusterMap = apiService.getUserClusters();
            if(clusterMap == null || clusterMap.size() == 0) {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "查询数据失败！");
            } else {
                JSONArray jsonArray = new JSONArray();
                List<Cluster> clusterList = new ArrayList<Cluster>();
                for(Cluster cluster: clusterMap.values()) {
                    JSONObject json = JSON.parseObject(cluster.toString());
                    jsonArray.add(json);
                }
                result.put(Constants.SUCCESS, true);
                result.put(Constants.DATA, jsonArray);
            }
        } else if(StringUtils.equals(requestEntry.getKey(), "getUserClusterGroups"))  {
            String clusterId = requestParams.get("clusterId");
            Cluster cluster = new Cluster();
            cluster.setId(Long.valueOf(clusterId));
            Result<List<AssembledUserGroup>> listResult = apiService.getUserGroups(userId, cluster);
            if(listResult.getResultCode() == ResultCode.SUCCESS) {
                result.put("success", true);
                List<AssembledUserGroup> assembledUserGroups = listResult.getData();
                JSONArray jsonArray = new JSONArray();
                for(AssembledUserGroup assembledUserGroup: assembledUserGroups) {
                    JSONObject json = JSON.parseObject(assembledUserGroup.toString());
                    jsonArray.add(json);
                }
                result.put("data", jsonArray);
            } else {
                result.put("success", false);
                result.put("errMsg", "查询用户的数据异常！");
            }

        } else if(StringUtils.equals(requestEntry.getKey(), "getGroupJobs"))  {
            String groupId = requestParams.get("systemDefineGroupId");
            String page = requestParams.get("page");
            String pageSize = requestParams.get("pageSize");
            Result<List<Job>> jobListResult = apiService.getGroupJobs(groupId,
                    Integer.valueOf(page), Integer.valueOf(pageSize), null);
            if(jobListResult.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
                result.put(Constants.SUCCESS, true);
                JSONArray groupJobsJson = new JSONArray();
                for(Job job: jobListResult.getData()) {
                    JSONObject json = new JSONObject();
                    json.put("jobId", job.getId());
                    json.put("jobDesc", job.getDescription());
                    json.put("jobType", job.getType());
                    json.put("cronExpression", job.getCronExpression());
                    json.put("status", job.getStatus());
                    groupJobsJson.add(json);
                }
                result.put(Constants.DATA, groupJobsJson);
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统查询Job出现异常！");
            }

        } else if(StringUtils.equals(requestEntry.getKey(), "getJobInfo"))  {
            String jobId = requestParams.get("jobId");
            Job job = new Job();
            job.setId(Long.valueOf(jobId));
            Result<Job> jobResult = apiService.queryPersistenceJob(job);
            if(jobResult.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
                result.put(Constants.SUCCESS, true);
                result.put(Constants.DATA, jobResult.getData().toString());
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统查询Job出现异常！");
            }
        } else if(StringUtils.equals(requestEntry.getKey(), "createGroup"))  {
            String clusterId = requestParams.get("clusterId");
            String groupDesc = requestParams.get("groupDesc");
            long clusterGroupId = apiService.getClusterRandomGroup(Long.valueOf(clusterId));
            if(clusterGroupId != 0) {
                ClientGroup clientGroup = new ClientGroup();
                clientGroup.setServerGroupId(clusterGroupId);
                clientGroup.setDescription(groupDesc);
                Result<String> createResult = apiService.createGroup(clientGroup, Long.valueOf(clusterId));
                if(createResult.getResultCode() == ResultCode.SUCCESS) {
                    result.put(Constants.SUCCESS, true);
                    JSONObject json = new JSONObject();
                    json.put("systemDefineGroupId", createResult.getData());
                    result.put(Constants.DATA, json);
                } else {
                    result.put(Constants.SUCCESS, false);
                    result.put(Constants.ERROR_MSG, "系统错误导致创建失败！");
                }
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统错误导致创建失败！");
            }

        } else if(StringUtils.equals(requestEntry.getKey(), "deleteGroup"))  {
            String groupId = requestParams.get("groupId");
            Result<String> deleteResult = apiService.deleteGroup(groupId, userId);
            if(deleteResult.getResultCode() == ResultCode.SUCCESS) {
                result.put(Constants.SUCCESS, true);
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统删除组错误！");
            }
        } else if(StringUtils.equals(requestEntry.getKey(), "createJob"))  {
            String groupId = requestParams.get("groupId");
            String jobType = requestParams.get("jobType");
            String cronExpression = requestParams.get("cronExpression");
            String jobProcessor = requestParams.get("jobProcessor");
            String firePolicy = requestParams.get("firePolicy");
            String jobDesc = requestParams.get("jobDesc");
            Job job = new Job();
            job.setDescription(jobDesc);
            job.setCreaterId(userId);
            job.setType(Integer.valueOf(jobType));
            job.setCronExpression(cronExpression);
            job.setJobProcessor(jobProcessor);
            job.setMaxInstanceAmount(Integer.valueOf(firePolicy));
            Cluster cluster = GroupIdUtil.getCluster(groupId);

            Result<Long> createResult = new Result<Long>();
            if(CommonUtil.isApiJob(Integer.valueOf(jobType))) {
                createResult = apiService.createJob(job, cluster);
            } else {
                if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
                    result.put("success", false);
                    result.put("invalid", true);
                    result.put("errMsg", "时间表达式配置不规范！");
                } else {
                    createResult = apiService.createJob(job, cluster);
                }
            }
            // 完全成功
            if(createResult.getResultCode() == ResultCode.CREATE_JOB_SUCCESS) {
                result.put("success", true);
                JSONObject json = new JSONObject();
                json.put("jobId", createResult.getData());
                result.put(Constants.DATA, json);
            } else {
                result.put("success", false);
                if(result.getBoolean("invalid") != true) {
                    result.put(Constants.ERROR_MSG, "系统错误创建失败!");
                }
            }

        } else if(StringUtils.equals(requestEntry.getKey(), "deleteJob"))  {
            String jobId = requestParams.get("jobId");
            Job job = new Job();
            job.setId(Long.valueOf(jobId));
            Result<Long> deleteResult = apiService.deleteJob(job);
            if(deleteResult.getResultCode() == ResultCode.SUCCESS) {
                result.put(Constants.SUCCESS, true);
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统内部删除异常！");
            }
        } else if(StringUtils.equals(requestEntry.getKey(), "updateJob"))  {
            String groupId = requestParams.get("groupId");
            String jobType = requestParams.get("jobType");
            String cronExpression = requestParams.get("cronExpression");
            String jobProcessor = requestParams.get("jobProcessor");
            String firePolicy = requestParams.get("firePolicy");
            String jobDesc = requestParams.get("jobDesc");
            Job job = new Job();
            job.setDescription(jobDesc);
            job.setCreaterId(userId);
            job.setType(Integer.valueOf(jobType));
            job.setCronExpression(cronExpression);
            job.setJobProcessor(jobProcessor);
            job.setMaxInstanceAmount(Integer.valueOf(firePolicy));
            Cluster cluster = GroupIdUtil.getCluster(groupId);

            Result<Integer> updateResult = new Result<Integer>();
            if(CommonUtil.isApiJob(Integer.valueOf(jobType))) {
                updateResult = apiService.updateJob(job);
            } else {
                if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
                    result.put("success", false);
                    result.put("invalid", true);
                    result.put("errMsg", "时间表达式配置不规范！");
                } else {
                    updateResult = apiService.updateJob(job);
                }
            }
            // 完全成功
            if(updateResult.getResultCode() == ResultCode.SUCCESS) {
                result.put("success", true);
            } else {
                result.put("success", false);
                if(result.getBoolean("invalid") != true) {
                    result.put(Constants.ERROR_MSG, "系统错误更新失败!");
                }
            }
        } else if(StringUtils.equals(requestEntry.getKey(), "enableJob"))  {
            String jobId = requestParams.get("jobId");
            Job job = new Job();
            job.setId(Long.valueOf(jobId));
            Result<Integer> enableResult = apiService.enableJob(job);
            if(enableResult.getResultCode() == ResultCode.SUCCESS) {
                result.put(Constants.SUCCESS, true);
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统错误，启用失败！");
            }
        } else if(StringUtils.equals(requestEntry.getKey(), "disableJob"))  {
            String jobId = requestParams.get("jobId");
            Job job = new Job();
            job.setId(Long.valueOf(jobId));
            Result<Integer> disableResult = apiService.disableJob(job);
            if(disableResult.getResultCode() == ResultCode.SUCCESS) {
                result.put(Constants.SUCCESS, true);
            } else {
                result.put(Constants.SUCCESS, false);
                result.put(Constants.ERROR_MSG, "系统错误，停用失败！");
            }
        } else {
            result.put(Constants.SUCCESS, false);
            result.put(Constants.ERROR_MSG, "not support method!");
        }

        ConsoleUtil.writeJsonToResponse(response, result);
    }

    public Map.Entry<String, Map<String, String>> mapUrl(String url) {
        Map<String, String> params = new HashMap<String, String>();
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        int index = StringUtils.indexOf(url, "?");
        if(index < 0) {

        } else {
            String targets = StringUtils.substring(url, 0, index);
            String ps = StringUtils.substring(url, index);
            String[] paths = StringUtils.split(targets, "/");
            String[] paramEntrys = StringUtils.split(ps, "&");
            for(String paramEntry: paramEntrys) {
                String[] entry = StringUtils.split(paramEntry, "=");
                if(entry.length == 2){
                    params.put(entry[0], entry[1]);
                }
            }
            if(paths.length == 4) {
                Map.Entry<String, Map<String, String>> result
                        = new DefaultMapEntry<String, Map<String, String>>(paths[3], params);
                return result;
            }
        }

        return null;
    }
}
