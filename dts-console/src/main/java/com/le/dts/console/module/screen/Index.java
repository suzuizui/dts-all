package com.le.dts.console.module.screen;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.util.UserEnvUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.le.dts.common.constants.Constants;

public class Index implements Constants {

	@Autowired  
    private HttpServletRequest request;
	
	@Autowired  
    private HttpServletResponse response;
	
	@Autowired
	private ConsoleConfig consoleConfig;
	
	public void execute(Context context) throws IOException {
		
		UserEnvUtil.initUser(request, response);
		UserEnvUtil.initServerCluster(request, response, consoleConfig);
	}
	
}
