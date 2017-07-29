package com.le.dts.console.module.screen.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.console.store.JobServerRelationAccess;

public class UpdateJobServerRelation {

	private static final Log logger = LogFactory.getLog(UpdateJobServerRelation.class);
	
	@Autowired
	private JobServerRelationAccess jobServerRelationAccess;
	
	public void execute(Context context, 
			@Param(name = "sourceServer") String sourceServer, 
			@Param(name = "targetServer") String targetServer) {
		
		int result = 0;
		try {
			result = jobServerRelationAccess.updateServer(sourceServer, targetServer);
		} catch (Throwable e) {
			logger.error("[UpdateJobServerRelation]: error", e);
		}
		
		context.put("result", result);
	}
	
}
