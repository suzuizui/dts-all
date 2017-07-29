package com.le.dts.console.module.screen;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.config.EnvData;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.console.api.ApiService;

public class Login {

	@Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ApiService apiService;
    
    @Autowired
	private ConsoleConfig consoleConfig;
    
    @Autowired
    private EnvData envData;

    public void execute(Context context, @Param(name = "fullUrl") String fullUrl) {
    	context.put("fullUrl", fullUrl);
    }
	
}
