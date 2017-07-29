package com.le.dts.server.context;

import com.le.dts.common.proxy.ProxyService;
import com.le.dts.server.compensation.Compensation;
import com.le.dts.server.config.ServerConfig;
import com.le.dts.server.job.pool.JobPool;
import com.le.dts.server.manager.DesignatedMachineManager;
import com.le.dts.server.manager.JobInstanceManager;
import com.le.dts.server.manager.JobManager;
import com.le.dts.server.manager.JobOperationManager;
import com.le.dts.server.manager.JobRelationManager;
import com.le.dts.server.manager.JobServerRelationManager;
import com.le.dts.server.manager.TaskSnapShotManager;
import com.le.dts.server.manager.WarningSetupManager;
import com.le.dts.server.monitor.ServerMonitor;
import com.le.dts.server.remoting.ClientRemoting;
import com.le.dts.server.remoting.ServerRemoting;
import com.le.dts.server.service.ServerServiceImpl;
import com.le.dts.server.store.Store;
import com.le.dts.server.store.mysql.access.DataSource;
import com.le.dts.server.store.mysql.access.SqlMapClients;
import com.le.dts.server.zookeeper.Zookeeper;

/**
 * 服务器上下文
 * @author tianyao.myc
 *
 */
public interface ServerContext {

	/** 服务器配置 */
	public static final ServerConfig serverConfig = new ServerConfig();
	
	/** 数据源 */
	public static final DataSource dataSource = new DataSource();
	
	/** SqlMapClients */
	public static final SqlMapClients sqlMapClients = new SqlMapClients();
	
	/** 存储 */
	public static final Store store = new Store();
	
	/** 代理服务 */
	public static final ProxyService proxyService = new ProxyService();
	
	/** 客户端远程通信 */
	public static final ClientRemoting clientRemoting = new ClientRemoting();
	
	/** 远程通信 */
	public static final ServerRemoting serverRemoting = new ServerRemoting();
	
	/** 服务端通用基础服务 */
	public static final ServerServiceImpl serverService = new ServerServiceImpl();
	
	/** Zookeeper */
	public static final Zookeeper zookeeper = new Zookeeper();

	/** Job池 */
	public static final JobPool jobPool = new JobPool();

	/** Job管理器 */
	public static final JobManager jobManager = new JobManager();

	public static final JobRelationManager jobRelationManager = new JobRelationManager();
	
    public static final JobInstanceManager jobInstanceManager = new JobInstanceManager();
    
    public static final WarningSetupManager warningSetupManager = new WarningSetupManager();
    
    /** 用户任务执行快照 */
    public static final TaskSnapShotManager taskSnapShotManager = new TaskSnapShotManager();
    
    public static final JobServerRelationManager jobServerRelationManager = new JobServerRelationManager();
    
    /** Job操作管理 */
    public static final JobOperationManager jobOperationManager = new JobOperationManager();
    
    /** 指定机器管理 */
    public static final DesignatedMachineManager designatedMachineManager = new DesignatedMachineManager();
    
    /** 失败补偿 */
    public static final Compensation compensation = new Compensation();
    
    //服务器监控
    public static final ServerMonitor serverMonitor = new ServerMonitor();
	
}
