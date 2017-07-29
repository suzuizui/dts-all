package com.le.dts.console.module.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.JobRelation;
import com.le.dts.console.api.ApiService;
import com.le.dts.console.util.ConsoleUtil;
import com.le.dts.console.util.LoggerUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * 依赖操作;
 * Created by luliang on 14/12/26.
 */
public class JobRelationAction {
	
	private static final Log logger = LogFactory.getLog(JobRelationAction.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ApiService apiService;

    public void doCreateRelation(Context context, Navigator navigator,
                                 @Param(name = "jobId ") String jobId,
                                 @Param(name = "relationJobId ") String relationJobId) {
        JobRelation jobRelation = new JobRelation();
        jobRelation.setJobId(Long.valueOf(jobId));
        jobRelation.setBeforeJobId(Long.valueOf(relationJobId));

        Result<Long> result = apiService.createJobRelation(jobRelation);
        
        LoggerUtil.printLog(logger, request, "JobRelationAction.doCreateRelation", 
        		new Object[]{"jobId:" + jobId, "relationJobId:" + relationJobId, "result:" + result});
        
        JSONObject json = new JSONObject();
        if(result.getResultCode() == ResultCode.SUCCESS) {
            json.put("success", "true");
        } else {
            json.put("success", "false");
            json.put("errMsg", "系统内部的异常！CODE[" + result.getResultCode().getCode() + "]");
        }

        ConsoleUtil.writeJsonToResponse(response, json);

    }

    public void doDeleteRelation(Context context, Navigator navigator,
                                 @Param(name = "jobId ") String jobId,
                                 @Param(name = "relationJobId ") String relationJobId) {
        JobRelation jobRelation = new JobRelation();
        jobRelation.setJobId(Long.valueOf(jobId));
        jobRelation.setBeforeJobId(Long.valueOf(relationJobId));

        Result<Integer> result = apiService.deleteJobRelation(jobRelation);
        
        LoggerUtil.printLog(logger, request, "JobRelationAction.doDeleteRelation", 
        		new Object[]{"jobId:" + jobId, "relationJobId:" + relationJobId, "result:" + result});
        
        JSONObject json = new JSONObject();
        if(result.getResultCode() == ResultCode.SUCCESS) {
            json.put("success", "true");
        } else {
            json.put("success", "false");
            json.put("errMsg", "系统内部的异常！CODE[" + result.getResultCode().getCode() + "]");
        }

        ConsoleUtil.writeJsonToResponse(response, json);
    }
}
