package com.le.dts.console.gc.timer;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.console.gc.JobInstanceSnapshotCleanup;

/**
 * job实例记录清理定时器
 * @author tianyao.myc
 *
 */
public class JobInstanceSnapshotCleanupTimer extends TimerTask {

	private static final Log logger = LogFactory.getLog(JobInstanceSnapshotCleanupTimer.class);
	
	private JobInstanceSnapshotCleanup jobInstanceSnapshotCleanup;
	
	public JobInstanceSnapshotCleanupTimer(JobInstanceSnapshotCleanup jobInstanceSnapshotCleanup) {
		this.jobInstanceSnapshotCleanup = jobInstanceSnapshotCleanup;
	}
	
	@Override
	public void run() {
		try {
			this.jobInstanceSnapshotCleanup.start();
		} catch (Throwable e) {
			logger.error("[JobInstanceSnapshotCleanupTimer]: start error", e);
		}
	}

}
