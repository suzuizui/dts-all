package com.le.dts.console.module.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.api.ApiService;
import com.le.dts.console.util.ConsoleUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.assemble.JobHistoryRecord;
import com.alibaba.fastjson.JSONObject;

/**
 * Job历史记录
 * @author luliang.ll
 * 
 */
public class JobHistoryRecordAction {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@Autowired
	private ApiService apiService;

	public void doGetJobHistory(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId,
			@Param(name = "page") String page,
			@Param(name = "pageSize") String pageSize) {

		JSONObject jsonObject = new JSONObject();
		int itemCount = Constants.PER_PAGE_COUNT;
		if(StringUtils.isNotBlank(pageSize)) {
			itemCount = Integer.valueOf(pageSize);
		}
		Result<JobHistoryRecord> recordResult = apiService.getJobHistoryRecord(
				Long.valueOf(jobId), Integer.valueOf(StringUtils.isBlank(page)? "1":page) - 1, itemCount);
		if(recordResult.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
			jsonObject.put("record", recordResult.getData().getResult());
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部错误导致的异常！CODE[" + recordResult.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doGetJobHistoryCount(Context context, Navigator navigator,
			@Param(name = "jobId") String jobId) {

		JSONObject jsonObject = new JSONObject();
		Result<Integer> result = apiService.getJobHistoryCount(Long.valueOf(jobId));
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
			jsonObject.put("pageCount", result.getData());
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
}
