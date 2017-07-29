package com.le.dts.console.gc.timer;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.console.gc.JobInstanceMappingCleanup;

/**
 * JobInstanceMappingCleanup定时器
 * @author tianyao.myc
 *
 */
public class JobInstanceMappingCleanupTimer extends TimerTask {
	
	private static final Log logger = LogFactory.getLog(JobInstanceMappingCleanupTimer.class);

	private final JobInstanceMappingCleanup jobInstanceMappingCleanup;
	
	public JobInstanceMappingCleanupTimer(JobInstanceMappingCleanup jobInstanceMappingCleanup) {
		this.jobInstanceMappingCleanup = jobInstanceMappingCleanup;
	}
	
	@Override
	public void run() {

		try {
			
			//开始清理
			this.jobInstanceMappingCleanup.start();
		} catch (Throwable e) {
			logger.error("[JobInstanceMappingCleanupTimer]: run error", e);
		}

	}

}
