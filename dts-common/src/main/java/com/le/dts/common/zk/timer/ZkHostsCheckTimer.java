package com.le.dts.common.zk.timer;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.zk.ZkManager;

/**
 * ZkHosts检查定时器
 * @author tianyao.myc
 *
 */
public class ZkHostsCheckTimer extends TimerTask {
	
	private static final Log log = LogFactory.getLog(ZkHostsCheckTimer.class);

	private final ZkManager zkManager;
	
	public ZkHostsCheckTimer(ZkManager zkManager) {
		this.zkManager = zkManager;
	}
	
	@Override
	public void run() {
		try {
			zkManager.initZkClient();
		} catch (Throwable e) {
			log.error("[ZkHostsCheckTimer]: initZkClient error", e);
		}

	}

}
