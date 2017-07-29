package com.le.dts.console.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.DtsUser;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.console.config.ConsoleConfig;
import com.le.dts.console.global.Global;
import com.le.dts.console.login.filter.LoginManager;

/**
 * 对用户的环境操作;
 * @author luliang.ll
 *
 */
public class UserEnvUtil implements Constants {

	/**
	 * 初始化用户信息
	 * 
	 * @throws IOException
	 */
	public static String initUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		return "10000";
//		if(LoginFilter.DEFAULT_FILTER.equals(LoginFilter.USE_FILTER)) {
//			return Global.getUser(Global.DEFAULT_USER, request).getUserId();
//		}
//
//		if (LoginManager.isInAliyunEnv(request)) {// 阿里云环境;
//			// 阿里云用户
//			LoginResult result = LoginManager.getCookieUser(request);
//			if (result.isHasLogged()) {
//				AliyunUser opsUser = result.getOpsUser();
//				Global.setDtsUser(request, response,
//						new DtsUser(opsUser.getUserId(), opsUser.getUserName()));
//                return opsUser.getUserId();
//			} else {
//				LoginManager.doRedirect(LoginManager.LOGIN_URL,
//						LoginManager.getFullUrl(request), request, response);
//                return null;
//			}
//
//		} else if(request.getRequestURI().indexOf("sdkManager") > 0) {// SDK访问;
//            DtsUser dtsUser = UserUtil.getUserBySDK(request);
//            if(dtsUser != null) {
//                Global.setDtsUser(request, response, dtsUser);
//                return dtsUser.getUserId();
//            } else {
//                // filter拦截不会走到这里;
//                return null;
//            }
//        } else {
//			// 内部用户
//			BucSSOUser user = UserUtil.getUser(request);
//			String name = StringUtil.isBlank(user.getNickNameCn()) ? user
//					.getLastName() : user.getNickNameCn();
//			Global.setDtsUser(request, response, new DtsUser(user.getEmpId(),
//					name));
//            return user.getEmpId();
//		}
	}


    /**
     * 获取用户Id;
     * @param request
     * @return
     * @throws IOException
     */
    public static DtsUser getUserId(HttpServletRequest request) throws IOException {

        return new DtsUser("10000", "zeldia");
//    	if(LoginFilter.DEFAULT_FILTER.equals(LoginFilter.USE_FILTER)) {
//
//			return Global.getUser(Global.DEFAULT_USER, request);
//		}
//
//        if (LoginManager.isInAliyunEnv(request)) {// 阿里云环境;
//            // 阿里云用户
//            LoginResult result = LoginManager.getCookieUser(request);
//            if (result.isHasLogged()) {
//                AliyunUser opsUser = result.getOpsUser();
//                return new DtsUser(opsUser.getUserId(), opsUser.getUserName());
//            } else {
//                return null;
//            }
//
//        } else if(request.getRequestURI().indexOf("sdkManager") > 0) {// SDK访问;
//            DtsUser dtsUser = UserUtil.getUserBySDK(request);
//            if(dtsUser != null) {
//                return dtsUser;
//            } else {
//                // filter拦截不会走到这里;
//                return null;
//            }
//        } else {
//            // 内部用户
//            BucSSOUser user = UserUtil.getUser(request);
//            String name = StringUtil.isBlank(user.getNickNameCn()) ? user
//                    .getLastName() : user.getNickNameCn();
//            return new DtsUser(user.getEmpId(),
//                    name);
//        }
    }

	/**
	 * 初始化默认集群信息
	 */
	public static void initServerCluster(HttpServletRequest request,
			HttpServletResponse response, ConsoleConfig consoleConfig) {
		Cluster defaultServerCluster = consoleConfig
				.getServerClusterMap().get(Constants.DEFAULT_SERVER_CLUSTER_ID);
		Global.setServerCluster(request, response, defaultServerCluster);
	}
	
	/**
	 * 切换内部集群环境设置cookie;
	 * @param request
	 * @param response
	 * @param clusterId
	 */
	public static void setServerCluster(HttpServletRequest request,
			HttpServletResponse response, long clusterId, ConsoleConfig consoleConfig) {
		Cluster defaultServerCluster = consoleConfig
				.getServerClusterMap().get(clusterId);
		Global.setServerCluster(request, response, defaultServerCluster);
	}
}
