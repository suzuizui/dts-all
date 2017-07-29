package com.le.dts.console.module.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.config.EnvData;
import com.le.dts.console.global.Global;
import com.le.dts.console.service.HttpRequestService;
import com.le.dts.console.store.FlowInstanceAccess;
import com.le.dts.console.util.ConsoleUtil;
import com.le.dts.console.util.LoggerUtil;
import com.le.dts.console.util.NumericUtil;
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
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.ClientGroup;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.domain.store.FlowInstance;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.assemble.AssembledDesignatedMachine;
import com.le.dts.common.util.CommonUtil;
import com.le.dts.common.util.GroupIdUtil;
import com.le.dts.console.api.ApiService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JobManageAction {

	private static final Log logger = LogFactory.getLog(JobManageAction.class);
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private FlowInstanceAccess flowInstanceAccess;
	
	@Autowired
	private HttpRequestService httpRequestService;
	
	@Autowired
    private EnvData envData;

	public void doCreateGroup(Context context, Navigator navigator,
			@Param(name = "clusterId ") String clusterId,
			@Param(name = "groupDesc") String groupDesc) {
		JSONObject jsonObject = new JSONObject();
		
		ClientGroup group = new ClientGroup();
		group.setDescription(StringUtils.trim(groupDesc));

		long serverGroupId = apiService.getClusterRandomGroup(Long.valueOf(clusterId));

        if(serverGroupId == 0) {// 异常
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "系统内部异常！CODE[224]");
        } else {
            group.setServerGroupId(Long.valueOf(serverGroupId));
            // 创建一个分组;
            Result<String> result = apiService.createGroup(group, Long.valueOf(clusterId));
            
            LoggerUtil.printLog(logger, request, "JobManageAction.doCreateGroup",
            		new Object[]{"clusterId:" + clusterId, "groupDesc:" + groupDesc, 
            		"result:" + result});
            
            // 返回页面处理;
            if(result.getResultCode() == ResultCode.SUCCESS) {
                jsonObject.put("success", true);
            } else {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
            }
        }
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}

	public void doDeleteGroup(Context context, Navigator navigator,
			@Param(name = "groupId") String groupId) {
		JSONObject jsonObject = new JSONObject();
		DtsUser user = Global.getDtsUser(request);
		Result<String> result = apiService.deleteGroup(groupId, user.getUserId());
		
		LoggerUtil.printLog(logger, request, "JobManageAction.doDeleteGroup", 
        		new Object[]{"groupId:" + groupId, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部删除异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doGetGroupJobs(Context context, Navigator navigator,
			@Param(name = "groupId") String groupId, 
			@Param(name = "page") String page,
			@Param(name = "pageSize") String pageSize, 
			@Param(name = "searchText") String searchText) {
		JSONObject jsonObject = new JSONObject();
		int itemCount = Constants.PER_PAGE_COUNT;
		if(StringUtils.isNotBlank(pageSize)) {
			itemCount = Integer.valueOf(pageSize);
		}
		Result<List<Job>> result = apiService.getGroupJobs(groupId,
				Integer.valueOf(StringUtils.isBlank(page)? "1":page) - 1, itemCount, searchText);
		
		// 转成一个JSON返回
		if(result.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
			JSONArray groupJobsJson = new JSONArray();
			for(Job job: result.getData()) {
				JSONObject json = new JSONObject();
				json.put("jobId", job.getId());
				json.put("jobDesc", job.getDescription());
				json.put("jobType", job.getType());
				json.put("cronExpression", job.getCronExpression());
				json.put("status", job.getStatus());
				groupJobsJson.add(json);
			}
			jsonObject.put("success", true);
			jsonObject.put("groupJobs", groupJobsJson);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部查询异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doGetGroupJobsCount(Context context, Navigator navigator,
			@Param(name = "groupId") String groupId, 
			@Param(name = "searchText") String searchText) {
		
		JSONObject jsonObject = new JSONObject();
		Result<Integer> result = apiService.getGroupJobsCount(groupId, searchText);
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
			jsonObject.put("pageCount", result.getData());
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部查询异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doCreateJob(Context context, Navigator navigator,
			@Param(name = "groupId") String groupId,
			@Param(name = "jobType") int jobType,
			@Param(name = "cronExpression") String cronExpression,
			@Param(name = "jobProcessor") String jobProcessor,
			@Param(name = "firePolicy") String firePolicy,
			@Param(name = "jobDesc") String jobDesc,
            @Param(name = "clusterName") String clusterName,
            @Param(name = "jobArguments") String jobArguments, 
            @Param(name = "jobLevel") int jobLevel) {
		JSONObject jsonObject = new JSONObject();
		Job job = new Job();
		Cluster cluster = GroupIdUtil.getCluster(groupId);
		job.setServerGroupId(Long.valueOf(GroupIdUtil.getClientGroup(groupId).getServerGroupId()));
		job.setClientGroupId(Long.valueOf(GroupIdUtil.getClientGroup(groupId).getId()));
		job.setCreaterId(Global.getDtsUser(request).getUserId());
		job.setType(jobType);
		job.setCronExpression(cronExpression);
		job.setJobProcessor(jobProcessor);
		job.setDescription(jobDesc);
        job.setCreaterId(Global.getDtsUser(request).getUserId());
		job.setMaxInstanceAmount(Integer.valueOf(firePolicy));
        job.setJobArguments(jobArguments);
        job.setLevel(jobLevel);

        // 预发环境先不启用;
        if(StringUtils.equals(clusterName, Constants.SH_PREPUB)) {
            job.setStatus(Constants.JOB_STATUS_DISABLE);
        }

		Result<Long> result = new Result<Long>();
		if(CommonUtil.isApiJob(jobType)) {
				result = apiService.createJob(job, cluster);
		} else {
			if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
				result.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
                jsonObject.put("success", false);
				jsonObject.put("invalid", true);
                jsonObject.put("errMsg", "时间表达式配置不规范！CODE[" + result.getResultCode().getCode() + "]");
                ConsoleUtil.writeJsonToResponse(response, jsonObject);
                return;
			} else {
				result = apiService.createJob(job, cluster);
			}
		}
		
		LoggerUtil.printLog(logger, request, "JobManageAction.doCreateJob", 
        		new Object[]{"job:" + job, "result:" + result});

		// 完全成功
		if(result.getResultCode() == ResultCode.CREATE_JOB_SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}

	public void doDeleteJob(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId) {
		JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
		Result<Long> result = apiService.deleteJob(job);
		
		LoggerUtil.printLog(logger, request, "JobManageAction.doDeleteJob", 
        		new Object[]{"job:" + job, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部删除异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doGetJobConfig(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId) {
		JSONObject jsonObject = new JSONObject();
		Result<Job> result = apiService.getJobConfig(Long.valueOf(jobId));
		if(result.getResultCode() == ResultCode.QUERY_PERSISTENCE_JOB_SUCCESS) {
			Job job = result.getData();
			JSONObject json = new JSONObject();
			json.put("jobId", job.getId());
			json.put("jobType", job.getType());
			json.put("jobDesc", job.getDescription());
			json.put("firePolicy", job.getMaxInstanceAmount());
			json.put("jobProcessor", job.getJobProcessor());
			json.put("cronExpression", job.getCronExpression());
            json.put("jobArguments", job.getJobArguments());
            json.put("level", job.getLevel());
			jsonObject.put("success", true);
			jsonObject.put("jobConfig", json);
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
		}
	}

	public void doUpdateJob(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId,
			@Param(name = "groupId") String groupId,
			@Param(name = "jobType") int jobType,
			@Param(name = "cronExpression") String cronExpression,
			@Param(name = "jobProcessor") String jobProcessor,
			@Param(name = "firePolicy") String firePolicy,
			@Param(name = "jobDesc") String jobDesc,
            @Param(name = "jobArguments") String jobArguments, 
            @Param(name = "jobLevel") int jobLevel) {
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
        job.setLevel(jobLevel);

		Result<Integer> result = new Result<Integer>();
		if(CommonUtil.isApiJob(jobType)) {
			result = apiService.updateJob(job);
		} else {
			if(!CronExpression.isValidExpression(StringUtils.trim(cronExpression))) {
				result.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
                jsonObject.put("success", false);
                jsonObject.put("invalid", true);
                jsonObject.put("errMsg", "时间表达式配置不规范！CODE[" + result.getResultCode().getCode() + "]");
                ConsoleUtil.writeJsonToResponse(response, jsonObject);
                return;
			}  else {
				result = apiService.updateJob(job);
			}
		}
		
		LoggerUtil.printLog(logger, request, "JobManageAction.doUpdateJob", 
        		new Object[]{"job:" + job, "result:" + result});

		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部更新异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doEnableJob(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId) {

		JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
		Result<Integer> result = apiService.enableJob(job);
		
		LoggerUtil.printLog(logger, request, "JobManageAction.doEnableJob", 
        		new Object[]{"job:" + job, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}

	public void doDisableJob(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId) {
		JSONObject jsonObject = new JSONObject();
        Job job = new Job();
        job.setId(Long.valueOf(jobId));
		Result<Integer> result = apiService.disableJob(job);
		
		LoggerUtil.printLog(logger, request, "JobManageAction.doDisableJob", 
        		new Object[]{"job:" + job, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}

    public void doShowDesignatedMachine(Context context, Navigator navigator,
                                      @Param(name = "jobId") String jobId,
                                      @Param(name = "groupId") String groupId) {
        JSONObject jsonObject = new JSONObject();
        Result<List<AssembledDesignatedMachine>> result = apiService.getDesignatedMachine(groupId, Long.valueOf(jobId));
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            List<AssembledDesignatedMachine> assembledDesignatedMachines = result.getData();
            JSONArray jsonArray = new JSONArray();
            for(AssembledDesignatedMachine assembledDesignatedMachine: assembledDesignatedMachines) {
                JSONObject json = new JSONObject();
                json.put("machine", assembledDesignatedMachine.getMachine());
                if(assembledDesignatedMachine.isDesignatedMachine()) {
                    json.put("check", "true");
                } else {
                    json.put("check", "false");
                }
//                json.put("policy", assembledDesignatedMachine.getPolicy());
                jsonArray.add(json);
            }
            jsonObject.put("policy", 0);
            for(AssembledDesignatedMachine assembledDesignatedMachine: assembledDesignatedMachines) {
                if(assembledDesignatedMachine.getPolicy() == 1) {
                    jsonObject.put("policy", 1);
                    break;
                }
            }

            jsonObject.put("data", jsonArray);
        } else {
            jsonObject.put("success", false);
            if(result.getResultCode() == ResultCode.CLIENT_MACHINE_EMPTY) {
                jsonObject.put("errMsg", result.getResultCode().getInformation());
            } else {
                jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
            }
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doDesignatedMachine(Context context, Navigator navigator,
                                  @Param(name = "jobId") String jobId,
                                  @Param(name = "groupId") String groupId,
                                  @Param(name = "data") String data,
                                  @Param(name = "policy") String policy) {
        Result<Integer> result = new Result<Integer>();
        JSONObject jsonObject = new JSONObject();
        JSONArray dataJSON = JSON.parseArray(data);
        List<DesignatedMachine> designatedMachineList = new ArrayList<DesignatedMachine>(dataJSON.size());
        ClientGroup clientGroup = GroupIdUtil.getClientGroup(groupId);
        for(int i = 0; i < dataJSON.size(); i++) {
            DesignatedMachine designatedMachine = new DesignatedMachine();
            designatedMachine.setJobId(Long.valueOf(jobId));
            designatedMachine.setPolicy(Integer.valueOf(policy));
            designatedMachine.setClientGroupId(clientGroup.getId());
            designatedMachine.setMachine((dataJSON.getJSONObject(i).getString("machine")));
            designatedMachineList.add(designatedMachine);
        }
        result = apiService.designatedMachine(groupId, Long.valueOf(jobId), designatedMachineList);
        
        LoggerUtil.printLog(logger, request, "JobManageAction.doDesignatedMachine", 
        		new Object[]{"jobId:" + jobId, "groupId:" + groupId, "data:" + data, "policy:" + policy, "result:" + result});
        
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
        } else {
            jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

    public void doStartFlow(Context context, Navigator navigator,
            @Param(name = "operationObjectId") String operationObjectId, 
            @Param(name = "processCode") String processCode,
            @Param(name = "title") String title,
            @Param(name = "key") String key,
            @Param(name = "value") String value) {
    	
    	JSONObject jsonObject = new JSONObject();
    	
    	String userId = Global.getDtsUser(request).getUserId();
    	if(StringUtils.isBlank(userId)) {
    		jsonObject.put("success", false);
    		jsonObject.put("errMsg", "请重新登录！");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    		return ;
    	}
    	
    	if(NumericUtil.isNumeric(userId) && userId.length() < 6) {
    		for(int i = 0 ; i < 6 - userId.length() ; i ++) {
    			userId = "0" + userId;
    		}
    	}
    	
    	FlowInstance query = new FlowInstance();
    	query.setOperatorId(userId);
    	query.setOperationObjectId(operationObjectId);
    	
    	FlowInstance flowInstance = null;
    	try {
			flowInstance = flowInstanceAccess.query(query);
		} catch (Throwable e) {
			logger.error("[JobManageAction]: query FlowInstance error, query:" + query, e);
		}
    	
    	if(flowInstance != null && (FlowInstance.STATUS_START == flowInstance.getStatus())) {
    		jsonObject.put("success", false);
    		jsonObject.put("errMsg", "流程正在审批中！CODE[" + flowInstance.getFlowInstanceId() + "]");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    		return ;
    	}
    	
    	if(flowInstance != null && (FlowInstance.STATUS_DISAGREE == flowInstance.getStatus())) {
    		
    		//结束流程
    		endFlow(flowInstance);
    		
    		jsonObject.put("success", false);
    		jsonObject.put("errMsg", "流程审批不通过！CODE[" + flowInstance.getFlowInstanceId() + "]");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    		return ;
    	}
    	
    	if(flowInstance != null && (FlowInstance.STATUS_AGREE == flowInstance.getStatus())) {
    		
    		//结束流程
    		endFlow(flowInstance);
    		
    		jsonObject.put("success", true);
    		jsonObject.put("msg", "agree");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    		return ;
    	}
    	
    	flowInstance = query;
    	
    	String flowInstanceId = httpRequestService.startProcessInstance(processCode, title, userId, key, value, envData.getAuthKey());
    	if(StringUtils.isBlank(flowInstanceId)) {
    		jsonObject.put("success", false);
    		jsonObject.put("errMsg", "内外审批流程创建失败！");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    		return ;
    	}
    	
    	flowInstance.setFlowInstanceId(flowInstanceId);
    	flowInstance.setStatus(FlowInstance.STATUS_START);
    	
    	long result = 0L;
    	try {
			result = flowInstanceAccess.insert(flowInstance);
		} catch (Throwable e) {
			logger.error("[JobManageAction]: insert FlowInstance error, flowInstance:" + flowInstance, e);
		}
    	
    	LoggerUtil.printLog(logger, request, "JobManageAction.doStartFlow", 
        		new Object[]{"operationObjectId:" + operationObjectId, "processCode:" + processCode, 
    			"title:" + title, "key:" + key, "value:" + value, "result:" + result});
    	
    	if(result <= 0L) {
    		jsonObject.put("success", false);
    		jsonObject.put("errMsg", "审批记录创建失败！CODE[" + flowInstance.getFlowInstanceId() + "]");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    		return ;
    	} else {
    		jsonObject.put("success", true);
    		jsonObject.put("msg", "开始审批流程！流程代码：[" + flowInstance.getFlowInstanceId() + "]");
    		ConsoleUtil.writeJsonToResponse(response, jsonObject);
    	}
    }
    
    /**
     * 结束流程
     * @param flowInstance
     */
    private void endFlow(FlowInstance flowInstance) {
    	
    	flowInstance.setStatus(FlowInstance.STATUS_END);
    	
    	int result = 0;
    	try {
			result = flowInstanceAccess.update(flowInstance);
		} catch (Throwable e) {
			logger.error("[JobManageAction]: endFlow error, flowInstance:" + flowInstance, e);
		}
    	
    	if(result <= 0) {
    		logger.error("[JobManageAction]: endFlow failed, flowInstance:" + flowInstance);
    	}
    }
    
}
