package com.le.dts.console.module.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.global.Global;
import com.le.dts.console.util.ConsoleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.DtsUser;
import com.le.dts.common.domain.store.RegisterUser;
import com.le.dts.common.util.BytesUtil;
import com.le.dts.common.util.StringUtil;
import com.le.dts.console.store.RegisterUserAccess;
import com.alibaba.fastjson.JSONObject;

public class LoginAction implements Constants {

	private static final Log logger = LogFactory.getLog(LoginAction.class);

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private RegisterUserAccess registerUserAccess;
	
	public void doLogin(Context context, Navigator navigator,
			@Param(name = "userName") String userName,
			@Param(name = "password") String password) {
		
		JSONObject jsonObject = new JSONObject();
		
		if(StringUtil.isBlank(userName) || StringUtil.isBlank(password)) {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "用户名和密码不能为空！");
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
			return ;
		}
		
		String md5 = null;
		try {
			md5 = BytesUtil.md5(password.getBytes());
		} catch (Throwable e) {
			logger.error("[LoginAction]: md5 error"
					+ ", userName:" + userName 
					+ ", password:" + password, e);
		}
		
		if(StringUtil.isBlank(md5)) {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "密码加密失败！");
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
			return ;
		}
		
		RegisterUser query = new RegisterUser();
		query.setUserName(userName);
		query.setPassword(md5);
		
		RegisterUser registerUser = null;
		try {
			registerUser = registerUserAccess.queryByUser(query);
		} catch (Throwable e) {
			
			logger.error("[LoginAction]: queryByUser error"
					+ ", query:" + query, e);
			
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "系统出错，请稍后再试！");
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
			return ;
		}
		
		if(null == registerUser) {
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "用户名或密码不对，请修改后再试！");
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
			return ;
		}
		
		DtsUser dtsUser = new DtsUser(registerUser.getUserId(), registerUser.getUserName());
		
		try {
			Global.setUser(Global.DEFAULT_USER, dtsUser, request, response);
		} catch (Throwable e) {
			logger.error("[LoginAction]: setUser error"
					+ ", dtsUser:" + dtsUser, e);
			
			jsonObject.put("success", false);
			jsonObject.put("errMsg", "登录初始化失败，请稍后再试！");
			ConsoleUtil.writeJsonToResponse(response, jsonObject);
			return ;
		}
		
		jsonObject.put("success", true);
		ConsoleUtil.writeJsonToResponse(response, jsonObject);
	}
	
}
