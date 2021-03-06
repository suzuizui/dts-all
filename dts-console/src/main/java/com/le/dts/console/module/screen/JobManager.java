package com.le.dts.console.module.screen;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.config.EnvData;
import com.le.dts.console.global.Global;
import com.le.dts.console.util.UserEnvUtil;
import jodd.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.assemble.AssembledUserGroup;
import com.le.dts.console.api.ApiService;
import com.le.dts.console.login.filter.LoginManager;

public class JobManager {

	@Autowired  
    private HttpServletRequest request;
	
	@Autowired  
    private HttpServletResponse response;
	
    @Autowired
    private ApiService apiService;
    
    @Autowired
    private EnvData envData;
    
    @Autowired
	private ConsoleConfig consoleConfig;

	public void execute(Context context,
			@Param(name = "clusterId") String clusterId) throws IOException {
		
//		DtsUser dtsUser = Global.getDtsUser(request);
		
		TreeMap<Long, Cluster> userCluster = consoleConfig.getServerClusterMap();
		// 如果传入的是空的就先看cookie中是否有,没有返回第一个元素;
		if (StringUtil.isEmpty(clusterId)) {
			Cluster cluster = Global.getServerCluster(request);
			if(cluster != null) {
				clusterId = String.valueOf(cluster.getId());
			} else {
				clusterId = ((Long) userCluster.keySet().iterator().next())
						.toString();
			}
		}
		// 设置集群环境;
		UserEnvUtil.setServerCluster(request, response, Long.valueOf(clusterId), consoleConfig);
		String userId = UserEnvUtil.initUser(request, response);
		Result<List<AssembledUserGroup>> rss = apiService.getUserGroups(userId, userCluster.get(Long.valueOf(clusterId)));
		List<AssembledUserGroup> userGroups = rss.getData();
		context.put("userGroups", userGroups);
		context.put("clusterId", clusterId);
		context.put("serverCluster", userCluster.get(Long.valueOf(clusterId)));
		context.put("userServerCluster", userCluster);
		context.put("outerUser", false);
        context.put("clusterName", userCluster.get(Long.valueOf(clusterId)).getDescription());
        if(rss.getData() != null && rss.getData().size() != 0) {
            context.put("defaultGroup", rss.getData().get(0).getSystemDefineGroupId());
        }
        
        context.put("flowDomainName", envData.getFlowDomainName());
        context.put("timerMsgDomainName", envData.getTimerMsgDomainName());
        context.put("userName", "zeldia");

		context.put("showAuth", true);

		context.put("showAdvertisement", false);
	}

//	/**
//	 * ID最小的组就是默认组;
//	 * @param userGroups
//	 * @return
//	 */
//	public String getDefaultGroupId(List<AssembledUserGroup> userGroups) {
//
//		if(userGroups == null || userGroups.size() == 0) {
//			return "";
//		}
//		String defaultGroup = "";
//		long minGroupId = GroupIdUtil.getClientGroup(userGroups.get(0).getSystemDefineGroupId()).getId();
//		defaultGroup = userGroups.get(0).getSystemDefineGroupId();
//		for(AssembledUserGroup asm: userGroups) {
//
//			long gId = GroupIdUtil.getClientGroup(asm.getSystemDefineGroupId()).getId();
//			if(gId < minGroupId) {
//				minGroupId = gId;
//				defaultGroup = asm.getSystemDefineGroupId();
//			}
//		}
//		return defaultGroup;
//	}
}
