package com.le.dts.console.module.screen.tools;

import java.util.List;

import com.le.dts.console.store.JobInstanceSnapshotAccess;
import com.le.dts.console.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.store.JobInstanceSnapshot;

public class StopErrorJobInstance {

	private static final Log logger = LogFactory.getLog(StopErrorJobInstance.class);
	
	@Autowired
	private JobInstanceSnapshotAccess jobInstanceSnapshotAccess;
	
	public void execute(Context context, @Param(name = "gmtCreate") final String gmtCreate) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				List<JobInstanceSnapshot> jobInstanceSnapshotList = queryInstance4Stop(gmtCreate);
				while(jobInstanceSnapshotList != null && jobInstanceSnapshotList.size() > 0) {
					int amount = 0;
					for(JobInstanceSnapshot instance : jobInstanceSnapshotList) {
						instance.setStatus(3);
						try {
							Thread.sleep(100L);
						} catch (Throwable e) {
							logger.error("[StopAllInstance]: sleep error", e);
						}
						int result = 0;
						try {
							result = jobInstanceSnapshotAccess.updateInstanceStatus(instance);
						} catch (Throwable e) {
							logger.error("[StopAllInstance]: error", e);
						}
						if(result > 0) {
							amount ++;
						}
					}
					
					logger.info("[StopErrorJobInstance]: amount:" + amount);
					jobInstanceSnapshotList = queryInstance4Stop(gmtCreate);
				}
				
			}
			
		}).start();
		
		context.put("result", "start");
	}
	
	private List<JobInstanceSnapshot> queryInstance4Stop(String gmtCreate) {
		
		JobInstanceSnapshot query = new JobInstanceSnapshot();
		query.setGmtCreate(TimeUtil.string2Date(gmtCreate, "yyyy-MM-dd HH:mm:ss"));
		
		List<JobInstanceSnapshot> jobInstanceSnapshotList = null;
		try {
			jobInstanceSnapshotList = jobInstanceSnapshotAccess.queryInstance4Stop(query);
		} catch (Throwable e) {
			logger.error("[StopErrorJobInstance]: queryInstance4Stop error", e);
		}
		return jobInstanceSnapshotList;
	}
	
}
