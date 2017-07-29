package com.le.dts.console.gc.timer;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.console.gc.GarbageCleanup;

/**
 * 垃圾清理定时器
 * @author tianyao.myc
 *
 */
public class GarbageCleanupTimer extends TimerTask {

	private static final Log logger = LogFactory.getLog(GarbageCleanupTimer.class);
	
	private GarbageCleanup garbageCleanup;
	
	public GarbageCleanupTimer(GarbageCleanup garbageCleanup) {
		this.garbageCleanup = garbageCleanup;
	}
	
	@Override
	public void run() {
		try {
			garbageCleanup.start();
		} catch (Throwable e) {
			logger.error("[GarbageCleanupTimer]: start error", e);
		}
	}

}
