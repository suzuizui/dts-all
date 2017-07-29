package com.le.dts.client.remoting.timer;

import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.client.context.ClientContext;

/**
 * 心跳定时器
 * @author tianyao.myc
 *
 */
public class DtsClientHeartBeatTimer extends TimerTask implements ClientContext {

	private static final Log logger = LogFactory.getLog(DtsClientHeartBeatTimer.class);
	
	@Override
	public void run() {
		try {
			
			List<String> serverList = zookeeper.getServerList();
			
			if(CollectionUtils.isEmpty(serverList)) {
				logger.warn("[DtsClientHeartBeatTimer]: serverList is empty, clientConfig:" + clientConfig.toString());
				return ;
			}

			/** 更新服务端地址列表缓存 */
			clientRemoting.setServerListCache(serverList);
			
			for(String server : serverList) {
				
				try {
					clientRemoting.connectServer(server);
				} catch (Throwable e) {
					logger.error("[DtsClientHeartBeatTimer]: connectServer error"
							+ ", server:" + server 
							+ ", clientConfig:" + clientConfig.toString(), e);
				}
			}
		} catch (Throwable e) {
			logger.error("[DtsClientHeartBeatTimer]: run error, clientConfig:" + clientConfig.toString(), e);
		}
	}
	
}
