package com.le.dts.server;

import com.le.dts.server.context.ServerContext;
import com.le.dts.server.state.LivingTaskManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.logger.LoggerCleaner;
import com.le.dts.common.util.PathUtil;

/**
 * DTS服务端程序入口
 * @author tianyao.myc
 *
 */
public class DtsServer implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(DtsServer.class);
	
	//日志清理
	private LoggerCleaner loggerCleaner = new LoggerCleaner(PathUtil.getLoggerPath());
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化简单配置 */
		serverConfig.initSimpleServerConfig();
		
		/** 服务器配置初始化 */
		serverConfig.init();
		
		/** 初始化数据源 */
		dataSource.init();
		
		/** SqlMapClients初始化 */
		sqlMapClients.init();
		
		/** 存储初始化 */
		store.init();
		
		/** 初始化集群信息 */
		serverConfig.initCluster();
		
        //初始化LivingTaskManager
        LivingTaskManager.getSingleton().init();

        /** 客户端远程通信 */
        clientRemoting.init();
        
		/** 服务端远程通信初始化 */
		serverRemoting.init();
		
		/** Zookeeper初始化 */
		zookeeper.init();
		
		/** Job池初始化 */
		jobPool.init();
		
		/** 失败补偿初始化 */
		compensation.init();
		
		//服务器监控初始化
		serverMonitor.init();
		
		//初始化日志清理
		loggerCleaner.init();

//		try {
//			SpasSdkServiceFacade.init(TDS_ALL, serverConfig.getAccessKey(), serverConfig.getSecretkey());
//		} catch (Throwable e) {
//			throw new InitException("[DtsServer]: SpasSdkServiceFacade.init error"
//					+ ", accessKey:" + serverConfig.getAccessKey()
//					+ ", secretkey:" + serverConfig.getSecretkey(), e);
//		}
	}
	
	public static void main(String[] args) {
		
		/** 设置INI配置文件路径 */
		serverConfig.setConfigPath(args[0]);
		
		/** 如果启动入口输入了端口就设置端口 */
		if(args.length > 1) {
			serverConfig.setListenerPort(Integer.parseInt(args[1]));
		}
		
		java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
		
		DtsServer dtsServer = new DtsServer();
		try {
			dtsServer.init();
		} catch (Throwable e) {
			logger.error("[DtsServer]: init error, serverConfig:" + serverConfig.toString(), e);
            System.exit(0);
		}
		logger.warn("[DtsServer]: init over, serverConfig:" + serverConfig.toString());
	}

}
