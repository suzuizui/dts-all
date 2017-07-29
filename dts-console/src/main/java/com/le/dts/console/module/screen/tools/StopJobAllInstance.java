package com.le.dts.console.module.screen.tools;

import java.util.List;

import com.le.dts.console.remoting.ConsoleRemoting;
import com.le.dts.console.zookeeper.Zookeeper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.service.ServerService;
import com.le.dts.common.util.RandomUtil;

public class StopJobAllInstance {

	private static final Log logger = LogFactory.getLog(StopJobAllInstance.class);
	
	@Autowired
    private ConsoleRemoting consoleRemoting;
	
	@Autowired
	private Zookeeper zookeeper;
	
	public void execute(Context context, @Param(name = "jobId") long jobId,
			@Param(name = "clusterId") String clusterId,
			@Param(name = "serverGroupId") String serverGroupId) {
		
		ServerService serverService = consoleRemoting.proxyInterface(ServerService.class);
		
		final List<String> serverList = zookeeper.getServerGroupIpList(clusterId, serverGroupId);
		
		if(CollectionUtils.isEmpty(serverList)) {
			logger.warn("[Stop4allInstance]: getServerGroupIpList serverList is empty"
					+ ", jobId:" + jobId + ", clusterId:" + clusterId + ", serverGroupId:" + serverGroupId);
			return ;
		}

		String server = RandomUtil.getRandomObj(serverList);
		
		int result = 0;
		try {
			InvocationContext.setRemoteMachine(new RemoteMachine(server, 30 * 1000L));
			result = serverService.stopAllInstance(jobId);
		} catch (Throwable e) {
			logger.error("[Stop4allInstance]: stopAllInstance error"
					+ ", server:" + server 
					+ ", jobId:" + jobId, e);
		}
		
		context.put("result", "result:" + result + ", server:" + server);
	}
	
}
