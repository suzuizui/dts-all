package com.le.dts.console.module.action;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.util.ConsoleUtil;
import com.le.dts.console.util.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.assemble.JobStatus;
import com.le.dts.console.api.ApiService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 管控页面;
 * 
 * @author luliang.ll
 * 
 */
public class JobControlAction implements Constants {
	
	private static final Log logger = LogFactory.getLog(JobControlAction.class);

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@Autowired
	private ApiService apiService;

	public void doGetGroupJobDetail(Context context, Navigator navigator,
			@Param(name = "groupId") String groupId, @Param(name = "page") String page,
			@Param(name = "pageSize") String pageSize) {
		JSONObject jsonObject = new JSONObject();
		int itemCount = Constants.PER_PAGE_COUNT;
		if(StringUtils.isNotBlank(pageSize)) {
			itemCount = Integer.valueOf(pageSize);
		}
		Result<List<JobStatus>> result = apiService.getGroupJobStatus(groupId,
				Integer.valueOf(StringUtils.isBlank(page)? "1":page) - 1, itemCount, false);
		// 转成一个JSON返回
		if (result.getResultCode() == ResultCode.SUCCESS) {
			JSONArray groupJobsJson = new JSONArray();
			for (JobStatus job : result.getData()) {
				JSONObject json = new JSONObject();
				json.put("jobId", job.getJobId());
				json.put("jobDesc", job.getJobDesc());
				json.put("isRunning", job.getRunningStatus());
				json.put("overallProgress", convertProcess(job.getOverallProgress()));
				groupJobsJson.add(json);
			}
			jsonObject.put("success", true);
			jsonObject.put("jobsDetail", groupJobsJson);
		} else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "系统内部查询异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);

	}

	public void doStartJob(Context context, Navigator navigator,
            @Param(name = "groupId") String groupId,
			@Param(name = "jobId") String jobId) {
		
		JSONObject jsonObject = new JSONObject();
		Result<String> result = apiService.startJob(groupId, Long.valueOf(jobId));
		
		LoggerUtil.printLog(logger, request, "JobControlAction.doStartJob", new Object[]{"groupId:" + groupId, "jobId:" + jobId, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
            if(result.getResultCode() == ResultCode.CLIENT_MACHINE_EMPTY) {
                jsonObject.put("errMsg", "客户端没有机器在运行，无法立即触发任务！CODE[" + result.getResultCode().getCode() + "]");
            } else {
                jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
            }
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}

	public void doStopJob(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId) {
		
		JSONObject jsonObject = new JSONObject();
		Result<String> result = apiService.stopJob(Long.valueOf(jobId));
		
		LoggerUtil.printLog(logger, request, "JobControlAction.doStopJob", new Object[]{"jobId:" + jobId, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doGetJobInstanceStatus(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId, @Param(name = "instanceId") String instanceId) {

		JSONObject jsonObject = new JSONObject();
		Result<ProgressDetail>  instanceStatusResult = apiService.getJobInstanceDetailStatus(Long
				.valueOf(jobId), instanceId);
		if(instanceStatusResult.getResultCode() == ResultCode.SUCCESS) {
			// 构造一个JSONArray将分级进度放进去;
			JSONArray array = new JSONArray();
			ProgressDetail progressDetail = instanceStatusResult.getData();
			ProgressBar totalProgressBar = progressDetail.getTotalProgressBar();
			JSONObject overall = new JSONObject();
			overall.put("totalProgress", new BigDecimal(totalProgressBar.parseProcessValue() * 100)
                    .setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
			long total = totalProgressBar.getTotalAmount();
			if(total > 0) {
				overall.put("totalAmout", totalProgressBar.getTotalAmount());
			} else {
				overall.put("totalAmout", 0);
			}
			long progressed = totalProgressBar.getProcessCount();
			if(progressed > 0) {
				overall.put("completeCount", totalProgressBar.getProcessCount());
			} else {
				overall.put("completeCount", 0);
			}

			jsonObject.put("overalProcess", overall);
			for(ProgressBar subProcessBar: progressDetail.getProgressBarList()) {
				
				JSONObject subJson = new JSONObject();
				if(DEFAULT_ROOT_LEVEL_TASK_NAME.equals(subProcessBar.getName())) {
					subJson.put("layerName", "START任务");
				} else {
					subJson.put("layerName", subProcessBar.getName());
				}
				subJson.put("totalAmout", subProcessBar.getTotalAmount());
				subJson.put("completeCount", subProcessBar.getProcessCount());
				subJson.put("process", new BigDecimal(subProcessBar.parseProcessValue() * 100)
                        .setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
				array.add(subJson);
			}
			jsonObject.put("allStagedProgress", array);
			jsonObject.put("machineProgress", progressDetail.getMachineProgress());
			
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + instanceStatusResult.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}

    public void doGetJobInstanceOveralProgress(Context context, Navigator navigator,
                                             @Param(name = "instanceId") String instanceId) {
        JSONObject jsonObject = new JSONObject();
        Result<Double> result = apiService.getJobInstanceOvaralProgress(Long.valueOf(instanceId));
        if(result.getResultCode() == ResultCode.SUCCESS) {
            jsonObject.put("success", true);
            jsonObject.put("progress", result.getData());

        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
        }
        ConsoleUtil.writeJsonToResponse(response, jsonObject);
    }

	public String convertProcess(Map<Long, Map.Entry<String, String>> map) {
		JSONArray jsonArray = new JSONArray();
		if(map != null) {
			for(Map.Entry<Long, Map.Entry<String, String>> entry: map.entrySet()) {
				JSONObject json = new JSONObject();
				json.put("instanceId", entry.getKey());
                Map.Entry<String, String> ent = entry.getValue();
                if(! "-1".equals(ent.getValue())) {
	                double d = Double.valueOf(ent.getValue()) * 100;
	                BigDecimal bg = new BigDecimal(d);
	                Double t = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
					json.put("process", t);
					json.put("showProcess", true);
                } else {
                	json.put("showProcess", false);
                }
                json.put("timestamp", ent.getKey());
				jsonArray.add(json);
			}
		}
		return jsonArray.toJSONString();
	}
}
