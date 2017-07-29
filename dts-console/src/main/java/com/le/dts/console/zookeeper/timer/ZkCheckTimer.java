package com.le.dts.console.zookeeper.timer;

import java.util.TimerTask;

import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.util.PathUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.zk.ZkManager;

/**
 * ZK节点检查定时器
 * @author tianyao.myc
 *
 */
public class ZkCheckTimer extends TimerTask implements Constants {

	private static final Log logger = LogFactory.getLog(ZkCheckTimer.class);
	
	private Zookeeper zookeeper;
	
	public ZkCheckTimer(Zookeeper zookeeper) {
		this.zookeeper = zookeeper;
	}
	
	@Override
	public void run() {
		String consoleIpPath = PathUtil.getConsoleIpPath(RemotingUtil.getLocalAddress());
		
		ZkManager zkManager = zookeeper.getZkManager();
		
		String data = null;
		try {
			data = zkManager.getData(consoleIpPath);
		} catch (Throwable e) {
			logger.error("[ZkCheckTimer]: getData error, consoleIpPath:" + consoleIpPath, e);
		}
		if(RemotingUtil.getLocalAddress().equals(data)) {
			return ;
		}
		
		try {
			zkManager.publishOrUpdateData(consoleIpPath, RemotingUtil.getLocalAddress(), false);
		} catch (Throwable e) {
			logger.error("[ZkCheckTimer]: publishOrUpdateData error, consoleIpPath:" + consoleIpPath, e);
		}
		
	}

}
