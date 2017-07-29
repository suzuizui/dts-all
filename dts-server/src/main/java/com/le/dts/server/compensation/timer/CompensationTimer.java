package com.le.dts.server.compensation.timer;

import java.util.TimerTask;

import com.le.dts.server.context.ServerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 失败补偿定时器
 * @author tianyao.myc
 *
 */
public class CompensationTimer extends TimerTask implements ServerContext {

	private static final Log logger = LogFactory.getLog(CompensationTimer.class);
	
	@Override
	public void run() {
		try {
			compensation.start();
		} catch (Throwable e) {
			logger.error("[CompensationTimer]: start error", e);
		}
	}

}
