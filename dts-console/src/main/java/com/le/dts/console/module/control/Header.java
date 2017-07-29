package com.le.dts.console.module.control;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.config.EnvData;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.le.dts.common.domain.DtsUser;
import com.le.dts.console.global.Global;

public class Header {

	@Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;
    
    @Autowired
    private EnvData envData;

    public void execute(Context context) throws IOException {
    	
    	DtsUser dtsUser = Global.getDtsUser(request);
    	
    	context.put("userName", dtsUser.getUserName());
    	context.put("timerMsgDomainName", envData.getTimerMsgDomainName());
    	
    }
	
}
