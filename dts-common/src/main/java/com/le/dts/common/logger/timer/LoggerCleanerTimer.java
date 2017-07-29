package com.le.dts.common.logger.timer;

import java.util.TimerTask;

import com.le.dts.common.logger.LoggerCleaner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.logger.LoggerCleaner;

/**
 * 日志清理定时器
 * @author tianyao.myc
 *
 */
public class LoggerCleanerTimer extends TimerTask {
	
	private static final Log logger = LogFactory.getLog(LoggerCleanerTimer.class);
	
	private LoggerCleaner loggerCleaner;
	
	public LoggerCleanerTimer(LoggerCleaner loggerCleaner) {
		this.loggerCleaner = loggerCleaner;
	}

	@Override
	public void run() {

		try {
			this.loggerCleaner.start();
		} catch (Throwable e) {
			logger.error("[LoggerCleanerTimer]: run error", e);
		}

	}

}
