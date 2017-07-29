package com.le.dts.console.module.screen.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.util.ConsoleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.console.api.ApiService;
import com.alibaba.fastjson.JSONObject;

public class StopJob {

	private static final Log logger = LogFactory.getLog(StopJob.class);
	
	@Autowired
	private ApiService apiService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	public void execute(Context context, @Param(name = "jobId") String jobId) {
		
		JSONObject jsonObject = new JSONObject();
		Result<String> result = apiService.stopJobBackup(Long.valueOf(jobId));
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部异常！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
		
	}
	
}
