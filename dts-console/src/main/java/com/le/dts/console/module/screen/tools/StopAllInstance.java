package com.le.dts.console.module.screen.tools;

import com.le.dts.console.store.JobInstanceSnapshotAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.store.JobInstanceSnapshot;

public class StopAllInstance {

	private static final Log logger = LogFactory.getLog(StopAllInstance.class);
	
	@Autowired
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	public void execute(Context context, @Param(name = "jobId") long jobId, 
			@Param(name = "status") int status) {
		
		JobInstanceSnapshot jobInstanceSnapshot = new JobInstanceSnapshot();
		jobInstanceSnapshot.setJobId(jobId);
		jobInstanceSnapshot.setStatus(status);
		int result = 0;
		try {
			result = jobInstanceSnapshotAccess.updateInstanceStatus4JobId(jobInstanceSnapshot);
		} catch (Throwable e) {
			logger.error("[StopAllInstance]: error", e);
		}
		context.put("result", result);
	}
	
}
