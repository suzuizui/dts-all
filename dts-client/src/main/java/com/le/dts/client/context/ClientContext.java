package com.le.dts.client.context;

import com.le.dts.client.config.ClientConfig;
import com.le.dts.client.executor.Executor;
import com.le.dts.client.executor.job.factory.JobProcessorFactory;
import com.le.dts.client.logger.ExecuteLogger;
import com.le.dts.client.remoting.ClientRemoting;
import com.le.dts.client.service.ClientServiceImpl;
import com.le.dts.client.zookeeper.Zookeeper;
import com.le.dts.common.proxy.ProxyService;
import com.le.dts.common.service.ClientService;
import com.le.dts.common.service.HttpService;

/**
 * 客户端全局上下文
 * 可以放置全局使用的类
 * @author tianyao.myc
 *
 */
public interface ClientContext {

	/** 客户端各项参数配置 */
	public static final ClientConfig clientConfig = new ClientConfig();
	
	/** 代理服务 */
	public static final ProxyService proxyService = new ProxyService();
	
	/** 客户端远程通信 */
	public static final ClientRemoting clientRemoting = new ClientRemoting();
	
	/** 客户端通用基础服务 */
	public static final ClientService clientService = new ClientServiceImpl();
	
	/** Zookeeper */
	public static final Zookeeper zookeeper = new Zookeeper();
	
	/** Job处理器工厂 */
	public static final JobProcessorFactory jobProcessorFactory = new JobProcessorFactory();
	
	/** 执行任务容器 */
	public static final Executor executor = new Executor();
	
	//执行日志
	public static final ExecuteLogger executeLogger = new ExecuteLogger();
	
	public static final HttpService httpService = new HttpService();
	
}
