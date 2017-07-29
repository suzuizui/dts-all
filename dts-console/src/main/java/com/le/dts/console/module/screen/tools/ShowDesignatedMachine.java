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
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.RandomUtil;

public class ShowDesignatedMachine {
	
	private static final Log logger = LogFactory.getLog(ShowDesignatedMachine.class);

	@Autowired
    private ConsoleRemoting consoleRemoting;
	
	@Autowired
	private Zookeeper zookeeper;
	
	public void execute(Context context, 
			@Param(name = "clusterId") final String clusterId, 
			@Param(name = "serverGroupId") String serverGroupId, 
			@Param(name = "clientGroup") String clientGroup) {
		
		ServerService serverService = consoleRemoting.proxyInterface(ServerService.class);
		
		List<String> serverList = zookeeper.getServerGroupIpList(clusterId, serverGroupId);
        if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[ShowDesignatedMachine]: getServerGroupIpList serverList is empty"
					+ ", clientGroup:" + clientGroup);
			return ;
		}
        
        String server = RandomUtil.getRandomObj(serverList);
        
        List<RemoteMachine> remoteMachineList = null;
        try {
			InvocationContext.setRemoteMachine(new RemoteMachine(server, 10 * 1000L));
			remoteMachineList = serverService.getRemoteMachines(clientGroup, 0L);
		} catch (Throwable e) {
			logger.error("[ShowDesignatedMachine]: getRemoteMachines error, clientGroup:" + clientGroup + ", server:" + server, e);
		}
		
        context.put("remoteMachineList", remoteMachineList);
	}
	
}
