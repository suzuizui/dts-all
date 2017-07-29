package com.le.dts.console.module.screen.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.store.FlowInstance;
import com.le.dts.console.store.FlowInstanceAccess;

public class AutoFlow {

	private static final Log logger = LogFactory.getLog(AutoFlow.class);
	
	@Autowired
	private FlowInstanceAccess flowInstanceAccess;
	
	public void execute(Context context, 
			@Param(name = "flowInstanceId") String flowInstanceId, 
			@Param(name = "status") int status) {
		
		FlowInstance flowInstance = new FlowInstance();
		flowInstance.setFlowInstanceId(flowInstanceId);
		flowInstance.setStatus(status);
		
		int result = 0;
    	try {
			result = flowInstanceAccess.update(flowInstance);
		} catch (Throwable e) {
			logger.error("[AutoFlow]: update error, flowInstance:" + flowInstance, e);
		}
    	
    	if(result <= 0) {
    		logger.error("[AutoFlow]: update failed, flowInstance:" + flowInstance);
    	}
		
    	logger.info("[AutoFlow]: update result:" + result + ", flowInstance:" + flowInstance);
    	
    	context.put("result", result);
	}
	
}
