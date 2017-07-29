package com.le.dts.console.module.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.store.UserGroupRelationAccess;
import com.le.dts.console.util.LoggerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.util.StringUtil;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.console.api.ApiService;
import com.le.dts.console.global.Global;
import com.le.dts.console.util.ConsoleUtil;
import com.alibaba.fastjson.JSONObject;
/**
 * 授权Action;
 * @author luliang.ll
 *
 */
public class JobAuthAction {
	
	private static final Log logger = LogFactory.getLog(JobAuthAction.class);

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private UserGroupRelationAccess userGroupRelationAccess;
	
	public void doGrantAuth(Context context, Navigator navigator,
			@Param(name = "userId") String userId,
			@Param(name = "groupId") String groupId) {
		
		JSONObject jsonObject = new JSONObject();
		String ownerId = Global.getDtsUser(request).getUserId();
		Result<String> result = apiService.grantAuth(ownerId, StringUtil.trim(userId).replaceAll("^(0+)", ""), groupId);
		
		LoggerUtil.printLog(logger, request, "JobAuthAction.doGrantAuth", new Object[]{"userId:" + userId, "groupId:" + groupId, "result:" + result});
		
		if(result.getResultCode() == ResultCode.SUCCESS) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部错误导致授权失败！CODE[" + result.getResultCode().getCode() + "]");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
	public void doDeleteAuth(Context context, Navigator navigator,
			@Param(name = "id") long id) {
		JSONObject jsonObject = new JSONObject();
		
		int result = 0;
		try {
			UserGroupRelation userGroupRelation = new UserGroupRelation();
			userGroupRelation.setId(id);
			result = userGroupRelationAccess.delete(userGroupRelation);
		} catch (Throwable e) {
			logger.error("[JobAuthAction]: delete userGroupRelation error, id:" + id, e);
		}
		
		LoggerUtil.printLog(logger, request, "JobAuthAction.doDeleteAuth", new Object[]{"id:" + id, "result:" + result});
		
		if(result > 0) {
			jsonObject.put("success", true);
		} else {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统内部错误导致取消授权失败！");
		}
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
}
