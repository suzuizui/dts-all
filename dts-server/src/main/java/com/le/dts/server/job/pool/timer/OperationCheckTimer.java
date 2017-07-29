package com.le.dts.server.job.pool.timer;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.server.context.ServerContext;

/**
 * 操作检查定时器
 * @author tianyao.myc
 *
 */
public class OperationCheckTimer extends TimerTask implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(OperationCheckTimer.class);
	
	@Override
	public void run() {
		try {
			/** 处理各项操作 */
			jobPool.handleOperations();
		} catch (Throwable e) {
			logger.error("[OperationCheckTimer]: handleOperations error", e);
		}
	}

}
