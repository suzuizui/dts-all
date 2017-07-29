package com.le.dts.console.module.screen.tools;

import java.util.List;

import com.le.dts.console.remoting.ConsoleRemoting;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.service.ServerService;

public class WarningSwitch {

	private static final Log logger = LogFactory.getLog(WarningSwitch.class);

	@Autowired
    private ConsoleRemoting consoleRemoting;
	
	@Autowired
	private Zookeeper zookeeper;
	
	public void execute(Context context, 
			@Param(name = "clusterId") final String clusterId, 
			@Param(name = "serverGroupId") String serverGroupId, 
			@Param(name = "warningSwitch") boolean warningSwitch) {
		
		ServerService serverService = consoleRemoting.proxyInterface(ServerService.class);
		
		List<String> serverList = zookeeper.getServerGroupIpList(clusterId, serverGroupId);
        if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[WarningSwitch]: getServerGroupIpList serverList is empty");
			context.put("result", "[WarningSwitch]: getServerGroupIpList serverList is empty");
			return ;
		}
		
        int counter = 0;
        for(String server : serverList) {
        	
        	Result<Boolean> setResult = null;
        	try {
				InvocationContext.setRemoteMachine(new RemoteMachine(server, 10 * 1000L));
				setResult = serverService.warningSwitch(warningSwitch);
			} catch (Throwable e) {
				logger.error("[WarningSwitch]: warningSwitch error", e);
			}
        	
        	if(setResult.getData().booleanValue()) {
        		counter ++;
        	}
        }
        
        context.put("result", "serverList.size():" + serverList.size() + ", counter:" + counter);
	}
	
}
