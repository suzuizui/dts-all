package com.le.dts.common.constants;

import java.nio.charset.Charset;

/**
 * 常量
 * @author tianyao.myc
 *
 */
public interface Constants {

	/** DB appName */
	public static final String DATA_SOURCE_APP_NAME 		= "DTS_SERVER_APP";
	public static final String DATA_SOURCE_APP_NAME_META 	= "DTS_SERVER_META_APP";
	
	/** DB GROUP KEY */
	public static final String DB_GROUP_KEY_META = "DTS_SERVER_META_GROUP";
	
	/** SQL map配置文件 */
	public static final String SQL_MAP_CONFIG_PATH = "sqlMapConfig.xml";

    /** 环境的名字 */
    public static final String DAILY_ENV_NAME = "daily";
    public static final String PERF_ENV_NAME = "perf";
    public static final String PREPUB_ENV_NAME = "prepub";
    public static final String PUBLISH_ENV_NAME = "publish";
    public static final String USA_ENV_NAME = "usa";
    public static final String ALIYUN_ENV_NAME = "aliyun";
    public static final String ALIYUN_TEST_ENV_NAME = "aliyun-test";

    public static final String SH_PREPUB = "上海预发";

	
	/** 存储类型 */
	public static final int STORE_TYPE_MYSQL = 0;
	public static final int STORE_TYPE_HBASE = 1;
	
	/**
	 * 环境信息 0表示公司内，1表示云上
	 */
	public static final int ENVIRONMENT_INNER = 0;
	public static final int ENVIRONMENT_CLOUD = 1;
	
	public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
	
	/** 配置文件 */
	public static final String DTS_INI = "dts.ini";
	
	/** 文件分隔符 */
	public static final String FILE_SEPARATOR = "file.separator";
	
	/** 用户home目录 */
	public static final String USER_HOME = "user.home";
	
	/** DTS配置文件路径 */
	public static final String DTS_CONFIG = "dtsConfig";
	
	/** 聚石塔 DTS配置文件路径 */
	public static final String JST_DTS_CONFIG = "config";
	
	/**
	 * 配置项DTS_INI
	 */
	public static final String DTS_BASE_SECTION 						= "baseSection";
	public static final String CONFIG_ITEM_STORE_TYPE 					= "storeType";
	public static final String CONFIG_ITEM_ENVIRONMENT 					= "environment";
	public static final String CONFIG_ITEM_LISTENER_PORT 				= "listenerPort";
	public static final String CONFIG_ITEM_REMOTING_THREADS 			= "remotingThreads";
	public static final String CONFIG_ITEM_HEART_BEAT_INTERVAL_TIME 	= "heartBeatIntervalTime";
	public static final String CONFIG_ITEM_HEART_BEAT_CHECK_TIMEOUT 	= "heartBeatCheckTimeout";
	public static final String CONFIG_ITEM_ZK_HOSTS 					= "zkHosts";
	public static final String CONFIG_ITEM_ZK_ROOT_PATH 				= "namespace";
	public static final String CONFIG_ITEM_ZK_SESSION_TIMEOUT 			= "zkSessionTimeout";
	public static final String CONFIG_ITEM_ZK_CONNECTION_TIMEOUT 		= "zkConnectionTimeout";
	public static final String CONFIG_ITEM_CLUSTER_ID 					= "clusterId";
	public static final String CONFIG_ITEM_SERVER_GROUP_ID 				= "serverGroupId";
	public static final String CONFIG_ITEM_DESCRIPTION 					= "description";
	public static final String CONFIG_ITEM_JOB_BACKUP_AMOUNT 			= "jobBackupAmount";
	public static final String CONFIG_ITEM_CHECK_JOB_INTERVAL_TIME 		= "checkJobIntervalTime";
	public static final String CONFIG_ITEM_COMPENSATION_INTERVAL_TIME 	= "compensationIntervalTime";
	public static final String CONFIG_ITEM_COMPENSATION_THREADS 		= "compensationThreads";

	/** 默认监听端口 */
	public static final int DEFAULT_LISTENER_PORT = 53014;
	
	/** 请求码 */
	public static final int REQUEST_CODE = 0;
	
	/** 可用处理器个数 */
	public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	
	/** 默认远程通信服务线程数量 */
	public static final int DEFAULT_REMOTING_THREADS = AVAILABLE_PROCESSORS * 4;
	
	/** 默认补偿线程数量 */
	public static final int DEFAULT_COMPENSATION_THREADS = 4;
	
	/** 空格分隔符 */
	public static final String BLANK_SPLIT = " ";
	
	/** 心跳线程数量 */
	public static final int HEART_BEAT_THREAD_AMOUNT = 2;
	
	/** ZK节点检查线程数量 */
	public static final int CHECK_ZK_THREAD_AMOUNT = 1;
	
	/** 失败补偿线程数量 */
	public static final int COMPENSATION_THREAD_AMOUNT = 1;

	/** ZK 扫描线程的数量 */
	public static final int ZK_SCANNER_THREAD_AMOUT = 1;
	
	/** Job检查线程数量 */
	public static final int CHECK_JOB_THREAD_AMOUNT = 1;
	
	/** 垃圾清理线程数量 */
	public static final int GC_THREAD_AMOUNT = 1;
	
	/** 心跳线程名称 */
	public static final String HEART_BEAT_THREAD_NAME = "DTS-heart-beat-thread-";
	
	/** 远程通信线程名称 */
	public static final String REMOTING_THREAD_NAME = "DTS-remoting-thread-";
	
	/** Job检查线程名称 */
	public static final String CHECK_JOB_THREAD_NAME = "DTS-check-job-thread";
	
	/** ZK节点检查线程名称 */
	public static final String CHECK_ZK_THREAD_NAME = "DTS-ZK-check-thread";
	
	/** 失败补偿线程名称 */
	public static final String COMPENSATION_THREAD_NAME = "DTS-compensation-thread";
	
	/** 垃圾清理线程名称 */
	public static final String GC_THREAD_NAME = "DTS-gc-thread";
	
	/** 默认心跳时间间隔 */
	public static final long DEFAULT_HEART_BEAT_INTERVAL_TIME = 10 * 1000L;
	
	/** 默认心跳检测超时时间 */
	public static final long DEFAULT_HEART_BEAT_CHECK_TIMEOUT = 5 * 1000L;
	
	/** 默认连接超时时间 */
	public static final long DEFAULT_CONNECTION_TIMEOUT = 3 * 1000L;
	
	/** 空格 */
	public static final String BLANK = " ";
	
	/** NULL */
	public static final String NULL = "NULL";
	
	/** 拆分点 */
	public static final String SPLIT_POINT = "\\.";
	
	/** 点 */
	public static final String POINT = ".";
	
	/** 逗号 */
	public static final String COMMA = ",";
	
	/** 逗号编码 */
	public static final String COMMA_ENCODED = "%2C";
	
	/** 通配符 */
	public static final String WILDCARD = "*";
	
	/** 反斜杠 */
	public static final String FORWARD_SLASH = "/";
	
	/** =号 */
	public final static String EQUAL_CHAR = "=";
	
	/** 冒号 */
	public final static String COLON = ":";
	
	/** 分隔符 */
	public static final String SPLIT_CHAR = "_SPLIT_CHAR_";
	public static final String SPLIT_STRING	= "@AND#";
	
	/** 横线 */
	public static final String HORIZONTAL_LINE = "-";
	
	/** 下划线 */
	public static final String UNDERLINE = "_";
	
	/** 默认根节点名称 */
	public static final String DEFAULT_ZK_ROOT_PATH = "zk-dts-root";
	
	/** 默认ZK会话超时时间 */
	public static final int DEFAULT_ZK_SESSION_TIMEOUT = 10 * 1000;
	
	/** 默认ZK连接超时时间 */
	public static final int DEFAULT_ZK_CONNECTION_TIMEOUT = 10 * 1000;
	
	/** 服务端集群目录 */
	public static final String ZK_SERVER_CLUSTER = "server-cluster";
	
	/** 控制台集群目录 */
	public static final String ZK_CONSOLE_CLUSTER = "console-cluster";
	
	/** 默认远程方法调用超时时间 */
	public static final long DEFAULT_INVOKE_TIMEOUT = 5 * 1000;
	
	/** Job类型 */
	public static final int JOB_TYPE_API_SIMPLE 		= 0;
	public static final int JOB_TYPE_TIMER_SIMPLE 		= 1;
	public static final int JOB_TYPE_API_PARALLEL 		= 2;
	public static final int JOB_TYPE_TIMER_PARALLEL 	= 3;
	public static final int JOB_TYPE_API_ALL_SIMPLE 	= 4;
	public static final int JOB_TYPE_TIMER_ALL_SIMPLE 	= 5;
	
	/** Job状态 */
	public static final int JOB_STATUS_DISABLE 	= 0;
	public static final int JOB_STATUS_ENABLE 	= 1;

    public static final int JOB_INSTANCE_STATUS_NEW 			= 1;
    public static final int JOB_INSTANCE_STATUS_RUNNING 		= 2;
    public static final int JOB_INSTANCE_STATUS_FINISHED 		= 3;
    public static final int JOB_INSTANCE_STATUS_FAILED 			= 4;
	public static final int JOB_INSTANCE_STATUS_RETRY 			= 5;
	public static final int JOB_INSTANCE_STATUS_RETRYING 		= 6;
	public static final int JOB_INSTANCE_STATUS_RETRY_FINISHED 	= 7;
	public static final int JOB_INSTANCE_STATUS_RETRY_OVER 		= 8;
	public static final int JOB_INSTANCE_STATUS_DELETE_SELF 	= 9;

    /** Task状态 */
    public static final int TASK_STATUS_INIT 					= 0;
    public static final int TASK_STATUS_QUEUE 					= 1;
    public static final int TASK_STATUS_START 					= 2;
    public static final int TASK_STATUS_SUCCESS 				= 3;
    public static final int TASK_STATUS_FAILURE 				= 4;
    public static final int TASK_STATUS_FOUND_PROCESSOR_FAILURE = 5;
    
    /** 任务处理器状态 */
    public static final int TASK_PROCESSOR_STATUS_RUNNING 	= 1;
    public static final int TASK_PROCESSOR_STATUS_STOP 		= 0;

    public static final long JOB_INSTANCE_LOCK_TIMEOUT = 10 * 1000;
    public static final long JOB_INSTANCE_LOAD_TIMEOUT = 3 * 1000;
	
	/** 默认Job备份数量 */
	public static final int DEFAULT_JOB_BACKUP_AMOUNT = 3;
	
	/** 用户信息 */
	public static final String DTS_USER = "dtsUser";
	
	/** 环境信息 */
	public static final String SERVER_CLUSTER = "serverCluster";
	
	/** 每页的行数 */
	public static final int PER_PAGE_COUNT = 10;
	
	/** 默认集群ID 也就是中国公用内部集群ID */
	public static final long DEFAULT_SERVER_CLUSTER_ID = 1;
	
	/** 任务线程名称 */
	public static final String TASK_THREAD_NAME = "DtsTaskProcessor-";
	
	/** 拉取任务线程名称 */
	public static final String PULL_TASK_THREAD_NAME = "DtsPullProcessor-";

	/** 默认页大小 */
	public static final int DEFAULT_PAGE_SIZE = 1000;
	
	/**
	 * 队列大小
	 * 默认缓存10页任务数据
	 */
	public static final int QUEUE_SIZE = 10 * DEFAULT_PAGE_SIZE;
	
	/** 默认消费线程数量 */
	public static final int DEFAULT_CONSUMER_THREAD_AMOUNT = 5;
	
	public static String KEY_LOGING_USER_COOKIE = "login_aliyunid_ticket";
	
	public static String KEY_ALIYUN_LOGIN_URL = "aliyunLoginUrl";
	public static String KEY_ALIYUN_LOGOUT_URL = "aliyunLogoutUrl";
	
	/** 默认任务名称 */
	public static final String DEFAULT_ROOT_LEVEL_TASK_NAME = "defaultTaskName4DtsServerSelf";
	
	/** MD5计算 */
	public static final int CHAR_AMOUNT = 16;
	
	/** MD5默认值 */
	public static final String DEFAULT_TASK_MD5 = "0123456789";
	
	/** 默认检查Job时间间隔 */
	public static final long DEFAULT_CHECK_JOB_INTERVAL_TIME = 10 * 1000L;
	
	/** 默认失败补偿时间间隔 */
	public static final long DEFAULT_COMPENSATION_INTERVAL_TIME = 60 * 1000L;

	/** ZK扫描间隔 */
	public static final long DEFAULT_SCANNER_ZK_TIME = 5 * 1000L;
	
	/** Job实例列表 */
	public static final String ZK_JOB_INSTANCE_LIST = "job-instance-list";
	
	/** 时间格式 */
	public static final String TIME_FORMAT_SECONDS = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_FORMAT_HOUR = "yyMMddHH";
	public static final String TIME_FORMAT_CHART = "yyyy-MM-dd HH:mm";
	
	/** 获取队列元素默认超时时间 */
	public static final long DEFAULT_POLL_TIMEOUT = 10 * 1000L;
	
	/** ZK上的操作Job数据JSON常量字符串 */
	public static final String JOB_OPERATE_KEY = "operate";
	public static final String JOB_OPERATE_VALUE = "value";
	
	/** 几张操作常量 */
	public static final String JOB_CREATE_OPERATE 			= "create";
	public static final String JOB_UPDATE_OPERATE 			= "update";
	public static final String JOB_DELETE_OPERATE 			= "delete";
	public static final String JOB_INSTANCE_START_OPERATE 	= "instaceStart";
	public static final String JOB_INSTANCE_STOP_OPERATE 	= "instaceStop";
	public static final String JOB_ENABLE_OPERATE 			= "enable";
	public static final String JOB_DISABLE_OPERATE 			= "disable";
    public static final String JOB_RELATION_CREATE          = "createRelation";
    public static final String JOB_RELATION_DELETE          = "deleteRelation";
    public static final String DESIGNATED_MACHINE           = "designatedMachine";

    public static final String JOB_ID_ITEM = "jobId";
    public static final String JOB_RELATION_ID_ITEM = "jobRelationId";
    public static final String FIRE_TIME_ITEM = "fireTime";
    public static final String FIRE_UNIQUE_ID = "uniqueId";
	
	/** 运行状态 */
	public static final int STATUS_STOP 		= 0;
	public static final int STATUS_RUNNING 	= 1;
	
	/** 最大重试次数 */
	public static final int MAX_RETRY_COUNT = 100;
	
	/** 重试时间间隔增长率 */
	public static final double INCREASE_RATE = 1.5;
	
	/** 
	 * 起始重试时间间隔
	 * 以等差数列递增
	 * 根据最大重试次数和重时间间隔增长率以及其实间隔时间计算出最多重试15天时间
	 */
	public static final long START_INTERVAL_TIME = 3 * 60 * 1000L;
	
	/** 总体进度文案 */
	public static final String TOTAL_PROGRESS = "总体进度";
	
	/** 补偿 拉数据睡眠时间 */
	public static final long PULL_SLEEP_TIME = 10 * 1000L;
	
	/** 启动策略 */
	public static final int START_POLICY_SINGLE_INSTANCE 	= 0;
	public static final int START_POLICY_MULTI_INSTANCE 	= 1;
	
	/** 调用来源 */
	public static final int INVOKE_SOURCE_API 	= 0;
	public static final int INVOKE_SOURCE_ACK 	= 1;
	public static final int INVOKE_SOURCE_TIMER = 2;
	
	public static final String TDS_ALL = "dts-all";

    public static final String SUCCESS = "success";

    public static final String ERROR_MSG = "errMsg";

    public static final String DATA = "data";

    public static final String ACCESS_KEY = "accessKey";

    public static final String SECURITY_KEY = "securityKey";

    public static final String TIME_STAMP = "timestamp";

    public static final String GUID = "GUID";

    public static final String USER_KEY = "userIdKey";

    public static final String ALIYUN_ENVKEY = "aliyunEnv";

    public static final String SIGN = "sign";

    public static final String DEFAULT_GROUP_NAME = "默认分组";
    
    /** 默认单个分组服务器数量 */
    public static final int DEFAULT_GROUP_SERVER_AMOUNT = 100;

    /** 客户端集群目录 */
	public static final String ZK_CLIENT_CLUSTER = "client-cluster";
	
	/** 锁目录 */
	public static final String ZK_LOCKS = "locks";
	
	/** 指定机器列表策略 */
	public static final int DESIGNATED_MACHINE_POLICY_MIGTATION 	= 0;//指定的机器列表挂了转移到活着的机器去运行
	public static final int DESIGNATED_MACHINE_POLICY_NOT_MIGTATION = 1;//指定的机器列表挂了不转移到活着的机器去运行
	
	/** 默认TDDL规则配置文件 */
	public static final String DEFAULT_TDDL_APPRULE_FILE = "dts-tddl-apprule.xml";
	
	/** 服务端配置DATA ID 从diamond拿数据 */
	public static final String DTS_SERVER_CONFIG_DATA_ID = "dts_server_config_data_id_2015_02_09";
	
	public static final int POSITION_PROCESSOR 		= 0;
	public static final int POSITION_INIT_METHOD 	= 1;
	public static final int POSITION_BEAN_ID 		= 2;
	
	/** MYSQL默认最大连接数 */
	public static final int DEFAULT_MAX_ACTIVE = 100;
	
	public static final String ENVIRONMENT_JST = "JuShiTa";
	
	public static final String DTS_CLIENT 	= "dts-client";
    public static final String DTS_LOGS 	= "logs";
    public static final String DTS_LOG_EXT 	= ".log";
    
    //换行符
  	public static final String NEWLINE = "\r\n";

}
