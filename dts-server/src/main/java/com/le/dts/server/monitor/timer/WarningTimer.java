package com.le.dts.server.monitor.timer;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.server.monitor.ServerMonitor;

public class WarningTimer extends TimerTask {
	
	public static final Log logger = LogFactory.getLog(WarningTimer.class);
	
	private ServerMonitor serverMonitor;
	
	public WarningTimer(ServerMonitor serverMonitor) {
		this.serverMonitor = serverMonitor;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void run() {

		ConcurrentHashMap<String, ServerMonitor.Alert> msgTable = this.serverMonitor.getMsgTable();

		try {
			Iterator iterator = msgTable.entrySet().iterator();
			while (iterator.hasNext()) {
			    Map.Entry entry = (Map.Entry)iterator.next();
			    ServerMonitor.Alert alert = (ServerMonitor.Alert)entry.getValue();
			    
			    if(alert.getCounter().get() > 10L) {
			    	this.serverMonitor.getAlertManager().sendSMS(alert.getUser(), alert.getMsg());
			    }
			    
			}
		} catch (Throwable e) {
			logger.error("[WarningTimer]: run error", e);
		} finally {
			msgTable.clear();
		}

	}

}
