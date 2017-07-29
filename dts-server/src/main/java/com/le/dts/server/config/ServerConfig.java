package com.le.dts.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.le.dts.common.util.DiamondHelper;
import com.le.dts.server.context.ServerContext;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.Cluster;
import com.le.dts.common.domain.store.Server;
import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.util.IniUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.StringUtil;

/**
 * 服务器配置
 * @author tianyao.myc
 *
 */
public class ServerConfig implements ServerContext, Constants {

	/**
	 * 存储类型
	 * 默认为Mysql
	 */
	private int storeType = STORE_TYPE_MYSQL;
	
	/**
	 * 运行环境
	 * 默认为公司内部
	 */
	private int environment = ENVIRONMENT_INNER;
	
	/** 监听端口 */
	private int listenerPort = DEFAULT_LISTENER_PORT;
	
	/** 远程通信服务线程数量 */
	private int remotingThreads = DEFAULT_REMOTING_THREADS * 4;

	/** 心跳间隔时间 */
	private long heartBeatIntervalTime = DEFAULT_HEART_BEAT_INTERVAL_TIME;
	
	/** 心跳检测超时时间 */
	private long heartBeatCheckTimeout = 2 * DEFAULT_HEART_BEAT_CHECK_TIMEOUT;
	
	/** ZK地址列表 */
	private String zkHosts;
	
	/** ZK根目录 */
	private String namespace = DEFAULT_ZK_ROOT_PATH;
	
	/** ZK会话超时时间 */
	private int zkSessionTimeout = 3 * DEFAULT_ZK_SESSION_TIMEOUT;
	
	/** ZK连接超时时间 */
	private int zkConnectionTimeout = DEFAULT_ZK_CONNECTION_TIMEOUT;
	
	/** 服务器集群ID */
	private long clusterId;
	
	/** 服务端集群分组ID */
	private long serverGroupId;
	
	/** 集群描述 */
	private String description;
	
	/** INI配置文件路径 */
	private String configPath;
	
	/** Job备份数量 */
	private int jobBackupAmount = DEFAULT_JOB_BACKUP_AMOUNT;
	
	/** 检查Job间隔时间 */
	private long checkJobIntervalTime = DEFAULT_CHECK_JOB_INTERVAL_TIME;
	
	/** 失败补偿间隔时间 */
	private long compensationIntervalTime = DEFAULT_COMPENSATION_INTERVAL_TIME;
	
	/** 访问公钥 */
	private String accessKey;
	
	/** 访问秘钥 */
	private String secretkey;
	
	/** 补偿线程数量 */
	private int compensationThreads = DEFAULT_COMPENSATION_THREADS;
	
	/** 本地地址 */
	private String localAddress;
	
	/** JDBC驱动类名 */
	private String driverClassName = "com.mysql.jdbc.Driver";

	/** 分库数量 */
	private int dynamicDBCount;

	/** 分表数量 */
	private int dynamicTableCount;

	/** MYSQL连接地址 */
	private List<String> url = new ArrayList<String>();
	
	/** MYSQL登录用户名 */
	private List<String> username = new ArrayList<String>();
	
	/** MYSQL登录密码 */
	private List<String> password = new ArrayList<String>();
	
	/** MYSQL最大连接数 */
	private int maxActive = DEFAULT_MAX_ACTIVE;
	
	/** JDBC驱动类名 */
	private String driverClassName4Meta = "com.mysql.jdbc.Driver";
	
	/** MYSQL连接地址 */
	private String url4Meta;
	
	/** MYSQL登录用户名 */
	private String username4Meta;
	
	/** MYSQL登录密码 */
	private String password4Meta;
	
	/** MYSQL最大连接数 */
	private int maxActive4Meta = DEFAULT_MAX_ACTIVE;
	
	/** 简单配置 */
	private SimpleServerConfig simpleServerConfig;
	
	private volatile boolean warningSwitch = true;
	
	private boolean useTddl = true;
	
	private boolean zkHostsAutoChange = true;
	
	/**
	 * 初始化简单配置
	 * @throws InitException
	 */
	public void initSimpleServerConfig() throws InitException {
		String json = DiamondHelper.getData(DTS_SERVER_CONFIG_DATA_ID, 10 * 1000);
		if(StringUtil.isNotBlank(json)) {
			try {
				this.simpleServerConfig = SimpleServerConfig.newInstance(json);
			} catch (Throwable e) {
				throw new InitException("[ServerConfig]: initSimpleServerConfig error"
						+ ", data_id:" + DTS_SERVER_CONFIG_DATA_ID, e);
			}
		}
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 * @throws NumberFormatException
	 */
	public void init() throws InitException, NumberFormatException {
		
		if(StringUtil.isBlank(configPath)) {
			throw new InitException("[ServerConfig]: init error, dts.ini configPath is empty, configPath:" + configPath);
		}
		
		Map<String, String> configMap = IniUtil.getIniValuesFromFile(configPath, DTS_BASE_SECTION);
		
		String storeType = configMap.get(CONFIG_ITEM_STORE_TYPE);
		if(StringUtil.isNotBlank(storeType)) {
			this.storeType = Integer.parseInt(storeType);
		}
		
		String environment = configMap.get(CONFIG_ITEM_ENVIRONMENT);
		if(StringUtil.isNotBlank(environment)) {
			this.environment = Integer.parseInt(environment);
		}
		
		String listenerPort = configMap.get(CONFIG_ITEM_LISTENER_PORT);
		if(StringUtil.isNotBlank(listenerPort)) {
			this.listenerPort = Integer.parseInt(listenerPort);
		}
		
		String remotingThreads = configMap.get(CONFIG_ITEM_REMOTING_THREADS);
		if(StringUtil.isNotBlank(remotingThreads)) {
			this.remotingThreads = Integer.parseInt(remotingThreads);
		}
		
		String heartBeatIntervalTime = configMap.get(CONFIG_ITEM_HEART_BEAT_INTERVAL_TIME);
		if(StringUtil.isNotBlank(heartBeatIntervalTime)) {
			this.heartBeatIntervalTime = Long.parseLong(heartBeatIntervalTime);
		}
		
		String heartBeatCheckTimeout = configMap.get(CONFIG_ITEM_HEART_BEAT_CHECK_TIMEOUT);
		if(StringUtil.isNotBlank(heartBeatCheckTimeout)) {
			this.heartBeatCheckTimeout = Long.parseLong(heartBeatCheckTimeout);
		}
		
		String zkHosts = configMap.get(CONFIG_ITEM_ZK_HOSTS);
		if(StringUtil.isNotBlank(zkHosts)) {
			this.zkHosts = zkHosts;
		}
		
		String useTddl = configMap.get("useTddl");
		if(StringUtil.isNotBlank(useTddl)) {
			this.useTddl = Boolean.parseBoolean(useTddl);
		}
		
		String zkHostsAutoChange = configMap.get("zkHostsAutoChange");
		if(StringUtil.isNotBlank(zkHostsAutoChange)) {
			this.zkHostsAutoChange = Boolean.parseBoolean(zkHostsAutoChange);
		}
		
		String namespace = configMap.get(CONFIG_ITEM_ZK_ROOT_PATH);
		if(StringUtil.isNotBlank(namespace)) {
			this.namespace = namespace;
		}
		
		String zkSessionTimeout = configMap.get(CONFIG_ITEM_ZK_SESSION_TIMEOUT);
		if(StringUtil.isNotBlank(zkSessionTimeout)) {
			this.zkSessionTimeout = Integer.parseInt(zkSessionTimeout);
		}
		
		String zkConnectionTimeout = configMap.get(CONFIG_ITEM_ZK_CONNECTION_TIMEOUT);
		if(StringUtil.isNotBlank(zkConnectionTimeout)) {
			this.zkConnectionTimeout = Integer.parseInt(zkConnectionTimeout);
		}
		
		String clusterId = configMap.get(CONFIG_ITEM_CLUSTER_ID);
		if(StringUtil.isNotBlank(clusterId)) {
			this.clusterId = Long.parseLong(clusterId);
		} else {
			if(this.simpleServerConfig != null && this.simpleServerConfig.getClusterId() > 0) {
				this.clusterId = this.simpleServerConfig.getClusterId();
			} else {
				throw new InitException("[ServerConfig]: init error, clusterId is null"
						+ ", configPath:" + configPath 
						+ ", simpleServerConfig:" + this.simpleServerConfig);
			}
		}
		
		String checkJobIntervalTime = configMap.get(CONFIG_ITEM_CHECK_JOB_INTERVAL_TIME);
		if(StringUtil.isNotBlank(checkJobIntervalTime)) {
			this.checkJobIntervalTime = Long.parseLong(checkJobIntervalTime);
		}
		
		String compensationIntervalTime = configMap.get(CONFIG_ITEM_COMPENSATION_INTERVAL_TIME);
		if(StringUtil.isNotBlank(compensationIntervalTime)) {
			this.compensationIntervalTime = Long.parseLong(compensationIntervalTime);
		}
		
		this.accessKey = "";
		
		this.secretkey = "";
		
		String compensationThreads = configMap.get(CONFIG_ITEM_COMPENSATION_THREADS);
		if(StringUtil.isNotBlank(compensationThreads)) {
			this.compensationThreads = Integer.parseInt(compensationThreads);
		}

		this.localAddress = RemotingUtil.getLocalAddress() + Constants.COLON + this.listenerPort;
		
		String driverClassName = configMap.get("driverClassName");
		if(StringUtil.isNotBlank(driverClassName)) {
			this.driverClassName = driverClassName;
		} else {
			if(this.simpleServerConfig != null && StringUtil.isNotBlank(this.simpleServerConfig.getDriverClassName())) {
				this.driverClassName = this.simpleServerConfig.getDriverClassName();
			}
		}

		String dynamicDBCount = configMap.get("dynamicDBCount");
		if(StringUtil.isNotBlank(dynamicDBCount)) {
			this.dynamicDBCount = Integer.parseInt(dynamicDBCount);
		}
		String dynamicTableCount = configMap.get("dynamicTableCount");
		if(StringUtil.isNotBlank(dynamicTableCount)) {
			this.dynamicTableCount = Integer.parseInt(dynamicTableCount);
		}

		for(int i = 0; i < this.dynamicDBCount; i++) {
			String url = configMap.get("url_" + i);
			if(StringUtil.isNotBlank(url)) {
				this.url.add(url);
			}

			String username = configMap.get("username_" + i);
			if(StringUtil.isNotBlank(username)) {
				this.username.add(username);
			}

			String password = configMap.get("password_" + i);
			if(StringUtil.isNotBlank(password)) {
				this.password.add(password);
			}
		}

		String maxActive = configMap.get("maxActive");
		if(StringUtil.isNotBlank(maxActive)) {
			this.maxActive = Integer.parseInt(maxActive);
		} else {
			if(this.simpleServerConfig != null && this.simpleServerConfig.getMaxActive() > 0) {
				this.maxActive = this.simpleServerConfig.getMaxActive();
			}
		}

		String driverClassName4Meta = configMap.get("driverClassName4Meta");
		if(StringUtil.isNotBlank(driverClassName4Meta)) {
			this.driverClassName4Meta = driverClassName4Meta;
		} else {
			if(this.simpleServerConfig != null && StringUtil.isNotBlank(this.simpleServerConfig.getDriverClassName4Meta())) {
				this.driverClassName4Meta = this.simpleServerConfig.getDriverClassName4Meta();
			}
		}
		
		String url4Meta = configMap.get("url4Meta");
		if(StringUtil.isNotBlank(url4Meta)) {
			this.url4Meta = url4Meta;
		} else {
			if(this.simpleServerConfig != null && StringUtil.isNotBlank(this.simpleServerConfig.getUrl4Meta())) {
				this.url4Meta = this.simpleServerConfig.getUrl4Meta();
			}
		}
		
		String username4Meta = configMap.get("username4Meta");
		if(StringUtil.isNotBlank(username4Meta)) {
			this.username4Meta = username4Meta;
		} else {
			if(this.simpleServerConfig != null && StringUtil.isNotBlank(this.simpleServerConfig.getUsername4Meta())) {
				this.username4Meta = this.simpleServerConfig.getUsername4Meta();
			}
		}
		
		String password4Meta = configMap.get("password4Meta");
		if(StringUtil.isNotBlank(password4Meta)) {
			this.password4Meta = password4Meta;
		} else {
			if(this.simpleServerConfig != null && StringUtil.isNotBlank(this.simpleServerConfig.getPassword4Meta())) {
				this.password4Meta = this.simpleServerConfig.getPassword4Meta();
			}
		}
		
		String maxActive4Meta = configMap.get("maxActive4Meta");
		if(StringUtil.isNotBlank(maxActive4Meta)) {
			this.maxActive4Meta = Integer.parseInt(maxActive4Meta);
		} else {
			if(this.simpleServerConfig != null && this.simpleServerConfig.getMaxActive4Meta() > 0) {
				this.maxActive4Meta = this.simpleServerConfig.getMaxActive4Meta();
			}
		}
	}
	
	/**
	 * 初始化集群信息
	 * @throws InitException
	 */
	public void initCluster() throws InitException {
		Cluster cluster = acquireCluster();
		if(null == cluster 
				|| cluster.getJobBackupAmount() <=0 
				|| StringUtil.isBlank(cluster.getDescription())) {
			throw new InitException("[ServerConfig]: initCluster cluster error, cluster:" + cluster);
		}
		this.setJobBackupAmount(cluster.getJobBackupAmount());
		this.setDescription(cluster.getDescription());
		
		Server server = acquireServer();
		if(null == server) {
			List<ServerGroup> serverGroupList = acquireServerGroupList();
			if(CollectionUtils.isEmpty(serverGroupList)) {
				/** 初始化机器分组 */
				initServerGroup();
			} else {
				for(ServerGroup serverGroup : serverGroupList) {
					long count = countServers(serverGroup.getId());
					if(count < DEFAULT_GROUP_SERVER_AMOUNT) {
						/** 创建Server */
						createServer(serverGroup.getId());
						this.setServerGroupId(serverGroup.getId());
						return ;
					}
				}
				/** 初始化机器分组 */
				initServerGroup();
			}
		} else {
			this.setServerGroupId(server.getServerGroupId());
		}
	}
	

	/**
	 * 初始化机器分组
	 * @throws InitException
	 */
	private void initServerGroup() throws InitException {
		long serverGroupId = createServerGroup();
		if(serverGroupId <= 0L) {
			throw new InitException("[ServerConfig]: createServerGroup failed, serverConfig:" + this);
		}
		/** 创建Server */
		createServer(serverGroupId);
		this.setServerGroupId(serverGroupId);
	}
	
	/**
	 * 查询分组列表
	 * @return
	 * @throws InitException
	 */
	private List<ServerGroup> acquireServerGroupList() throws InitException {
		ServerGroup query = new ServerGroup();
		query.setClusterId(this.getClusterId());
		List<ServerGroup> serverGroupList = null;
		try {
			serverGroupList = store.getServerGroupAccess().query(query);
		} catch (Throwable e) {
			throw new InitException("[ServerConfig]: acquireServerGroupList error, query:" + query, e);
		}
		return serverGroupList;
	}
	
	/**
	 * 创建ServerGroup
	 * @return
	 * @throws InitException
	 */
	private long createServerGroup() throws InitException {
		ServerGroup serverGroup = new ServerGroup();
		serverGroup.setClusterId(this.getClusterId());
		long result = 0L;
		try {
			result = store.getServerGroupAccess().insert(serverGroup);
		} catch (Throwable e) {
			throw new InitException("[ServerConfig]: createServerGroup error, serverGroup:" + serverGroup, e);
		}
		return result;
	}
	
	/**
	 * 获取Server
	 * @return
	 * @throws InitException
	 */
	private Server acquireServer() throws InitException {
		Server query = new Server();
		query.setServer(this.getLocalAddress());
		Server server = null;
		try {
			server = store.getServerAccess().query(query);
		} catch (Throwable e) {
			throw new InitException("[ServerConfig]: acquireServer error, query:" + query, e);
		}
		return server;
	}
	
	/**
	 * 创建Server
	 * @param serverGroupId
	 * @throws InitException
	 */
	private void createServer(long serverGroupId) throws InitException {
		Server server = new Server();
		server.setServerGroupId(serverGroupId);
		server.setServer(this.getLocalAddress());
		long result = 0L;
		try {
			result = store.getServerAccess().insert(server);
		} catch (Throwable e) {
			throw new InitException("[ServerConfig]: createServer error, server:" + server, e);
		}
		if(result <= 0L) {
			throw new InitException("[ServerConfig]: createServer failed, server:" + server);
		}
	}
	
	/**
	 * 计算Server数量
	 * @param serverGroupId
	 * @return
	 * @throws InitException
	 */
	private long countServers(long serverGroupId) throws InitException {
		Server query = new Server();
		query.setServerGroupId(serverGroupId);
		long result = 0L;
		try {
			result = store.getServerAccess().countServers(query);
		} catch (Throwable e) {
			throw new InitException("[ServerConfig]: countServers error, query:" + query, e);
		}
		return result;
	}
	
	/**
	 * 获取集群信息
	 * @return
	 * @throws InitException
	 */
	private Cluster acquireCluster() throws InitException {
		Cluster query = new Cluster();
		query.setId(this.getClusterId());
		Cluster cluster = null;
		try {
			cluster = store.getClusterAccess().queryById(query);
		} catch (Throwable e) {
			throw new InitException("[ServerConfig]: acquireCluster error, query:" + query, e);
		}
		return cluster;
	}
	
	public int getStoreType() {
		return storeType;
	}

	public void setStoreType(int storeType) {
		this.storeType = storeType;
	}

	public int getEnvironment() {
		return environment;
	}

	public void setEnvironment(int environment) {
		this.environment = environment;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public void setListenerPort(int listenerPort) {
		this.listenerPort = listenerPort;
	}

	public int getRemotingThreads() {
		return remotingThreads;
	}

	public void setRemotingThreads(int remotingThreads) {
		this.remotingThreads = remotingThreads;
	}

	public long getHeartBeatIntervalTime() {
		return heartBeatIntervalTime;
	}

	public void setHeartBeatIntervalTime(long heartBeatIntervalTime) {
		this.heartBeatIntervalTime = heartBeatIntervalTime;
	}

	public long getHeartBeatCheckTimeout() {
		return heartBeatCheckTimeout;
	}

	public void setHeartBeatCheckTimeout(long heartBeatCheckTimeout) {
		this.heartBeatCheckTimeout = heartBeatCheckTimeout;
	}

	public String getZkHosts() {
		return zkHosts;
	}

	public void setZkHosts(String zkHosts) {
		this.zkHosts = zkHosts;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public int getZkSessionTimeout() {
		return zkSessionTimeout;
	}

	public void setZkSessionTimeout(int zkSessionTimeout) {
		this.zkSessionTimeout = zkSessionTimeout;
	}

	public int getZkConnectionTimeout() {
		return zkConnectionTimeout;
	}

	public void setZkConnectionTimeout(int zkConnectionTimeout) {
		this.zkConnectionTimeout = zkConnectionTimeout;
	}

	public long getClusterId() {
		return clusterId;
	}

	public void setClusterId(long clusterId) {
		this.clusterId = clusterId;
	}

	public long getServerGroupId() {
		return serverGroupId;
	}

	public void setServerGroupId(long serverGroupId) {
		this.serverGroupId = serverGroupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public int getJobBackupAmount() {
		return jobBackupAmount;
	}

	public void setJobBackupAmount(int jobBackupAmount) {
		this.jobBackupAmount = jobBackupAmount;
	}

	public long getCheckJobIntervalTime() {
		return checkJobIntervalTime;
	}

	public void setCheckJobIntervalTime(long checkJobIntervalTime) {
		this.checkJobIntervalTime = checkJobIntervalTime;
	}

	public long getCompensationIntervalTime() {
		return compensationIntervalTime;
	}

	public void setCompensationIntervalTime(long compensationIntervalTime) {
		this.compensationIntervalTime = compensationIntervalTime;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}

	public int getCompensationThreads() {
		return compensationThreads;
	}

	public void setCompensationThreads(int compensationThreads) {
		this.compensationThreads = compensationThreads;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public int getDynamicDBCount() {
		return dynamicDBCount;
	}

	public int getDynamicTableCount() {
		return dynamicTableCount;
	}

	public List<String> getUrl() {
		return url;
	}

	public List<String> getUsername() {
		return username;
	}

	public List<String> getPassword() {
		return password;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public String getDriverClassName4Meta() {
		return driverClassName4Meta;
	}

	public void setDriverClassName4Meta(String driverClassName4Meta) {
		this.driverClassName4Meta = driverClassName4Meta;
	}

	public String getUrl4Meta() {
		return url4Meta;
	}

	public void setUrl4Meta(String url4Meta) {
		this.url4Meta = url4Meta;
	}

	public String getUsername4Meta() {
		return username4Meta;
	}

	public void setUsername4Meta(String username4Meta) {
		this.username4Meta = username4Meta;
	}

	public String getPassword4Meta() {
		return password4Meta;
	}

	public void setPassword4Meta(String password4Meta) {
		this.password4Meta = password4Meta;
	}

	public int getMaxActive4Meta() {
		return maxActive4Meta;
	}

	public void setMaxActive4Meta(int maxActive4Meta) {
		this.maxActive4Meta = maxActive4Meta;
	}

	public boolean isWarningSwitch() {
		return warningSwitch;
	}

	public void setWarningSwitch(boolean warningSwitch) {
		this.warningSwitch = warningSwitch;
	}

	public boolean isUseTddl() {
		return useTddl;
	}

	public void setUseTddl(boolean useTddl) {
		this.useTddl = useTddl;
	}

	public boolean isZkHostsAutoChange() {
		return zkHostsAutoChange;
	}

	public void setZkHostsAutoChange(boolean zkHostsAutoChange) {
		this.zkHostsAutoChange = zkHostsAutoChange;
	}

	@Override
	public String toString() {
		return "ServerConfig [storeType=" + storeType + ", environment="
				+ environment + ", listenerPort=" + listenerPort
				+ ", remotingThreads=" + remotingThreads
				+ ", heartBeatIntervalTime=" + heartBeatIntervalTime
				+ ", heartBeatCheckTimeout=" + heartBeatCheckTimeout
				+ ", zkHosts=" + zkHosts + ", namespace=" + namespace
				+ ", zkSessionTimeout=" + zkSessionTimeout
				+ ", zkConnectionTimeout=" + zkConnectionTimeout
				+ ", clusterId=" + clusterId + ", serverGroupId="
				+ serverGroupId + ", description=" + description
				+ ", configPath=" + configPath + ", jobBackupAmount="
				+ jobBackupAmount + ", checkJobIntervalTime="
				+ checkJobIntervalTime + ", compensationIntervalTime="
				+ compensationIntervalTime + ", accessKey=" + accessKey
				+ ", secretkey=" + secretkey + ", compensationThreads="
				+ compensationThreads + ", driverClassName=" + driverClassName
				+ ", url=" + url + ", username=" + username + ", password="
				+ password + ", maxActive=" + maxActive
				+ ", driverClassName4Meta=" + driverClassName4Meta
				+ ", url4Meta=" + url4Meta + ", username4Meta=" + username4Meta
				+ ", password4Meta=" + password4Meta + ", maxActive4Meta="
				+ maxActive4Meta + ", simpleServerConfig=" + simpleServerConfig
				+ ", warningSwitch=" + warningSwitch + ", useTddl=" + useTddl
				+ ", zkHostsAutoChange=" + zkHostsAutoChange + "]";
	}

}
