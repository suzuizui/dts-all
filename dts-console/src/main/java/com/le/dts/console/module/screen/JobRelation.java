package com.le.dts.console.module.screen;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.config.EnvData;
import com.le.dts.console.util.UserEnvUtil;
import jodd.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.turbine.Context;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.le.dts.common.domain.DtsUser;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Job;
import com.le.dts.console.api.ApiService;
import com.le.dts.console.global.Global;
import com.le.dts.console.login.filter.LoginManager;

/**
 * Job依赖;
 * Created by luliang on 14/12/25.
 */
public class JobRelation {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ApiService apiService;
    
    @Autowired
	private ConsoleConfig consoleConfig;
    
    @Autowired
    private EnvData envData;

    public void execute(Context context,
                        @Param(name = "clusterId") String clusterId) throws IOException {
    	
    	DtsUser dtsUser = Global.getDtsUser(request);
    	
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
        Result<Map<Job, List<Job>>> userRelationJobs = apiService.getUserJobRelations(userId, userCluster.get(Long.valueOf(clusterId)));
        context.put("userJobRelations", userRelationJobs.getData());
        context.put("clusterId", clusterId);
        context.put("serverCluster", userCluster.get(Long.valueOf(clusterId)));
        context.put("userServerCluster", userCluster);
        context.put("outerUser", false);
        context.put("errorMsg", "");
        context.put("timerMsgDomainName", envData.getTimerMsgDomainName());
        context.put("userName", dtsUser.getUserName());

        context.put("showAuth", true);

        context.put("showAdvertisement", false);
    }
}
