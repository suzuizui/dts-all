package com.le.dts.common.zk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.le.dts.common.service.HttpService;
import com.le.dts.common.util.DiamondHelper;
import com.le.dts.common.util.StringUtil;
import com.le.dts.common.zk.timer.ZkHostsCheckTimer;
import jodd.util.Wildcard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModePathAndBytesable;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import com.le.dts.common.service.HttpService;
import com.le.dts.common.util.DiamondHelper;
import com.le.dts.common.util.StringUtil;
import com.le.dts.common.zk.timer.ZkHostsCheckTimer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 和zk交互的manager，封装zk上的一些基础操作，通过zk实现worker、leader、web的互通
 * 已经定义了根路径，为/jingwei-v3，使用时用相对路径即可
 * @author qihao, hanxuan.mh
 * Date: 13-12-10
 */
public class ZkManager {

    private static final Log log = LogFactory.getLog(ZkConfig.class);

    /**
     * 构造时传入的属性，负责从文件或者diamond上读取配置
     */
    private ZkConfig zkConfig;

    private CuratorFramework zkClient;

    private static final AtomicInteger callBackPoolNumber = new AtomicInteger(1);
    private static final AtomicInteger curatorPoolNumber = new AtomicInteger(1);
    private final ExecutorService callBackExecutor = Executors
            .newFixedThreadPool(4, new ZkManagerThreadFactory("zkManager-callBack", callBackPoolNumber));
    private final ExecutorService curatorExecutor = Executors
            .newFixedThreadPool(4, new ZkManagerThreadFactory("curator-Cache", curatorPoolNumber));

    private static final String DEFAULT_ZK_ENCODING = "UTF-8";

    private final ConcurrentMap<String, DataChangeCache> dataChangeMap = Maps.newConcurrentMap();

    private final ConcurrentMap<String, ChildChangeCache> childChangeMap = Maps.newConcurrentMap();

    private final List<ConnectionStateListener> connectionStateListenerList = Lists.newCopyOnWriteArrayList();
    
    private static final long DIAMOND_GET_DATA_TIMEOUT = 10 * 1000;
    
    private String zkClusterDataId = "com.le.zookeeper.dtsZklist";
    
    static class ZkManagerThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ZkManagerThreadFactory(String prefix, AtomicInteger poolNumber) {
            if (StringUtil.isBlank(prefix)) {
                this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
            } else {
                this.namePrefix = prefix + "-" + poolNumber.getAndIncrement() + "-thread-";
            }
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, this.namePrefix + this.threadNumber.getAndIncrement());
        }
    }
    
    /** 定时调度服务 */
	private ScheduledExecutorService timerService = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				
				public Thread newThread(Runnable runnable) {
					return new Thread(runnable, "zkHosts-check-Timer-thread");
				}
				
			});
	
	private HttpService httpService = new HttpService();

    public void init() {
        
    	//初始化ZkClient
    	start();
    	
    	if(zkConfig.isZkHostsAutoChange()) {
	    	//初始化ZkHosts检查定时器
	    	initZkHostsCheckTimer();
    	}
    	
    	log.warn("[ZkManager]: init, zkConfig:" + zkConfig);
    }
    
    /**
     * 初始化ZkHosts检查定时器
     */
    private void initZkHostsCheckTimer() {
    	try {
    		timerService.scheduleAtFixedRate(new ZkHostsCheckTimer(this), 10 * 60 * 1000L, 60 * 1000L, TimeUnit.MILLISECONDS);
		} catch (Throwable e) {
			throw new RuntimeException("[ZkManager]: initZkHostsCheckTimer error", e);
		}
    	log.warn("[ZkManager]: initZkHostsCheckTimer success");
    }

    /**
     * 初始化ZkClient
     */
    public void initZkClient() {
    	
    	String zkHost = null;
    	if(ZkConfig.ZK_HOSTS_CONSOLE_SOURCE == zkConfig.getZkHostsSource()) {
    		zkHost = httpService.acquireZkHosts(zkConfig.getDomainName());
    	} else if(ZkConfig.ZK_HOSTS_DIAMOND_SOURCE == zkConfig.getZkHostsSource()) {
    		zkHost = DiamondHelper.getData(zkClusterDataId, DIAMOND_GET_DATA_TIMEOUT);
    	}
    	
    	if(StringUtil.isBlank(zkHost)) {
    		return ;
    	}
    	
    	if(! zkHost.equals(zkConfig.getZkHosts())) {
    		zkConfig.setZkHosts(null);
    	} else {
    		return ;
    	}
    	
    	
    	//释放资源，zkclient关闭，清空map等
		destroy();
    	
		//启动ZkClient
		start();
		
    }
    
    /**
     * 启动ZkClient
     */
    public void start() {
    	if (zkConfig == null) {
            throw new RuntimeException("zkConfig can not be null");
        }
    	
    	if(ZkConfig.ZK_HOSTS_CONSOLE_SOURCE == zkConfig.getZkHostsSource() 
    			&& StringUtil.isBlank(zkConfig.getDomainName())) {
    		throw new RuntimeException("domainName can not be null");
    	}
    	
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(5000, Integer.MAX_VALUE);
        if (StringUtil.isBlank(zkConfig.getZkHosts())) {
        	
        	String zkHost = null;
        	if(ZkConfig.ZK_HOSTS_CONSOLE_SOURCE == zkConfig.getZkHostsSource()) {
        		zkHost = httpService.acquireZkHosts(zkConfig.getDomainName());
        	} else if(ZkConfig.ZK_HOSTS_DIAMOND_SOURCE == zkConfig.getZkHostsSource()) {
        		zkHost = DiamondHelper.getData(zkClusterDataId, DIAMOND_GET_DATA_TIMEOUT);
        	}
        	
        	if(StringUtil.isBlank(zkHost)) {
        		throw new RuntimeException("zkHosts can not be null.");
        	}
        	zkConfig.setZkHosts(zkHost);
        }
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkConfig.getZkHosts()).sessionTimeoutMs(zkConfig.getZkSessionTimeout())
                .connectionTimeoutMs(zkConfig.getZkConnectionTimeout()).retryPolicy(retryPolicy).build();

        zkClient.getConnectionStateListenable().addListener(
                new org.apache.curator.framework.state.ConnectionStateListener() {
                    @Override
                    public void stateChanged(CuratorFramework client, final ConnectionState newState) {
                        for (final ConnectionStateListener listener : connectionStateListenerList) {
                            callBackExecutor.execute(new Runnable() {
                                public void run() {
                                    try {
                                        listener.handleStateChanged(newState);
                                    } catch (Throwable t) {
                                        log.error("Handle Connection State Error! ", t);
                                    }
                                }
                            });
                        }
                    }
                });
        zkClient.start();
        if (zkConfig.getAuthentication() != null) {
            try {
                zkClient.getZookeeperClient().getZooKeeper().addAuthInfo("digest",
                        zkConfig.getAuthentication().getBytes());
            } catch (Exception e) {
                log.error("can not init a authenticated zkClient");
            }
        }
    }
    
    /**
     * 释放资源，zkclient关闭，清空map等
     */
    private void destroy() {
        if (null != zkClient) {
            zkClient.close();
            zkClient = null;
        }
        for (DataChangeCache dataChangeCache : dataChangeMap.values()) {
            try {
                dataChangeCache.destory();
            } catch (Throwable e) {
                log.error("Destory DataChangeCache error path: " + dataChangeCache.getPath());
            }
        }
        dataChangeMap.clear();
        for (ChildChangeCache childChangeCache : childChangeMap.values()) {
            try {
                childChangeCache.destory();
            } catch (Throwable e) {
                log.error("Destory ChildChangeCache error path: " + childChangeCache.getPath());
            }
        }
        childChangeMap.clear();
        connectionStateListenerList.clear();
    }

    /**
     * 查询path下的数据
     * @param path
     * @return
     */
    public String getData(String path) {
        try {
            try {
                byte[] bytes = zkClient.getData().forPath(path);
                return deserialize(bytes);
            } catch (KeeperException.NoNodeException e) {
                return null;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Get Zk Data Error! ", e);
        }
    }

    /**
     * 缓存接口，建议优先使用，zk的变更会自动拉取到本地内存
     * @param path
     * @return
     */
    public String getDataCache(String path) {
        DataChangeCache dataChangeCache = getDataChangeCache(path);
        try {
            return dataChangeCache.getCurrentData();
        } catch (Throwable e) {
            throw new RuntimeException("Get Zk Data From Cache Error! ", e);
        }
    }

    /**
     * 查询path下所有节点列表
     * @param path
     * @return
     */
    public List<String> getChildren(String path) {
        try {
            try {
                List<String> children = zkClient.getChildren().forPath(path);
                return children;
            } catch (KeeperException.NoNodeException e) {
                return null;
            }
        } catch (Throwable e) {
            throw new RuntimeException("Get Zk Data Error! ", e);
        }
    }

    /**
     * 查询path下的所有节点并包含节点中的数据，实时查询zk
     * @param path
     * @return
     */
    public Map<String, String> getChildDatas(String path) {
        return this.getChildDatas(path, null);
    }

    /**
     * 查询path下所有节点列表，缓存接口
     * @param path
     * @return
     */
    public List<String> getChildrenCache(String path) {
        ChildChangeCache childChangeCache = getChildChangeCache(path);
        try {
            return childChangeCache.getCurrentChildren();
        } catch (Throwable e) {
            throw new RuntimeException("Get Zk ChildData From Cache Error! ", e);
        }
    }

    /**
     * 查询path下所有和patten匹配的所有节点，并包含节点中的数据
     * @param path
     * @param pattern
     * @return
     */
    public Map<String, String> getChildDatas(String path, String pattern) {
        try {
            try {
                List<String> childNames = zkClient.getChildren().forPath(path);
                Map<String, String> dataMap = new HashMap<String, String>(childNames.size());
                for (String element : childNames) {
                    if (StringUtil.isNotBlank(element)) {
                        if (StringUtil.isBlank(pattern) || Wildcard.match(element, pattern)) {
                            dataMap.put(element, StringUtil.defaultIfBlank(this.getData(path + "/" + element),
                                    StringUtil.EMPTY_STRING));
                        }
                    }
                }
                return dataMap;
            } catch (KeeperException.NoNodeException e) {
                return Collections.emptyMap();
            }
        } catch (Throwable e) {
            throw new RuntimeException("Get getChildDatas Data Error! ", e);
        }
    }

    /**
     * path是否存在
     * @param path
     * @return
     */
    public boolean isExists(String path) {
        try {
            Stat stat = this.zkClient.checkExists().forPath(path);
            return null != stat;
        } catch (Exception e) {
            throw new RuntimeException("Check Exist Zk Data Error! ", e);
        }
    }

    /**
     * 删除path，如果path不存在，则抛异常
     * @param path
     * @throws Exception
     */
    public void delete(String path) {
        try {
            this.zkClient.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            log.warn("delete path not exists path: " + path);
        } catch (Exception e) {
            throw new RuntimeException("delete path error", e);
        }
    }

    /**
     * 创建一个节点
     * @param path
     * @param data
     * @param isPersistent
     * @throws Exception
     */
    public void publishData(String path, String data, boolean isPersistent) {
        try {
            this.publishData(path, data, isPersistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL);
        } catch (Exception e) {
            throw new RuntimeException("publishData error", e);
        }
    }

    /**
     * 创建一个顺序的节点
     * @param path
     * @param data
     * @param isPersistent
     * @return
     * @throws Exception
     */
    public String publishDataSequential(String path, String data, boolean isPersistent) {
        try {
            return publishData(path, data,
                    isPersistent ? CreateMode.PERSISTENT_SEQUENTIAL : CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            throw new RuntimeException("publishDataSequential error", e);
        }
    }

    private String publishData(String path, String data, CreateMode createMode) {
        ProtectACLCreateModePathAndBytesable<String> protectAble = this.zkClient.create().creatingParentsIfNeeded();
        CreateBuilder builder = (CreateBuilder) protectAble.withMode(createMode);
        try {
            if (null != data) {
                return builder.forPath(path, serialize(data));
            } else {
                return builder.forPath(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("builder.forPath error", e);
        }
    }

    /**
     * 更新节点数据
     * @param path
     * @param data
     * @throws Exception
     */
    public void updateData(String path, String data) {
        try {
            if (null != data) {
                Stat stat = this.zkClient.setData().forPath(path, serialize(data));
            } else {
                this.zkClient.setData().forPath(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("zkClient.setData().forPath", e);
        }
    }

    /**
     * 发布或更新节点数据
     * @param path
     * @param data
     * @throws Exception
     */
    public synchronized void publishOrUpdateData(String path, String data, boolean isPersistent) {
        if (!this.isExists(path)) {
            publishData(path, data, isPersistent);
        } else {
            updateData(path, data);
        }
    }

    /**
     * 添加节点数据变化的listener
     * @param path
     * @param listener
     */
    public void addDataChangeListener(String path, DataChangeListener listener) {
        DataChangeCache context = getDataChangeCache(path);
        context.addDataChangeListener(listener);
    }

    private DataChangeCache getDataChangeCache(String path) {
        DataChangeCache context = dataChangeMap.get(path);
        if (null == context) {
            context = dataChangeMap.putIfAbsent(path,
                    new DataChangeCache(path, new NodeCache(this.zkClient, path), callBackExecutor));
            if (null == context) {
                context = dataChangeMap.get(path).init();
            }
        }
        return context;
    }

    /**
     * 删除节点数据变化的listener
     * @param path
     * @param listener
     */
    public void removeDataChangeListener(String path, DataChangeListener listener) {
        DataChangeCache context = dataChangeMap.get(path);
        if (null != context) {
            context.removeDataChangeListener(listener);
        }
    }

    private static class DataChangeCache {
        private final String path;
        private final NodeCache nodeCache;
        private final Executor executor;
        private final List<DataChangeListener> dataChangeListeners = Lists.newCopyOnWriteArrayList();
        private final NodeCacheListener nodeCacheListener = new NodeCacheListener() {

            public void nodeChanged() throws Exception {
                ChildData childData = nodeCache.getCurrentData();
                final String changedData = null != childData ? deserialize(childData.getData()) : null;
                for (final DataChangeListener listener : dataChangeListeners) {
                    if (null != executor) {
                        executor.execute(new Runnable() {
                            public void run() {
                                try {
                                    listener.handleChangeData(changedData);
                                } catch (Throwable e) {
                                    log.error("Handle DataChange Listener Error! path: " + path, e);
                                }
                            }
                        });
                    } else {
                        try {
                            listener.handleChangeData(changedData);
                        } catch (Throwable e) {
                            log.error("Handle DataChange Listener Error! path: " + path, e);
                        }
                    }
                }
            }
        };

        private DataChangeCache(String path, NodeCache nodeCache, Executor executor) {
            this.path = path;
            this.nodeCache = nodeCache;
            this.executor = executor;
        }

        private void addDataChangeListener(DataChangeListener listener) {
            this.dataChangeListeners.add(listener);
        }

        private void removeDataChangeListener(DataChangeListener listener) {
            this.dataChangeListeners.remove(listener);
        }

        private String getPath() {
            return path;
        }

        private String getCurrentData() throws UnsupportedEncodingException {
            ChildData childData = this.nodeCache.getCurrentData();
            if (null != childData) {
                return deserialize(this.nodeCache.getCurrentData().getData());
            }
            return null;
        }

        private DataChangeCache init() {
            try {
                this.nodeCache.start(true);
                if (null != this.executor) {
                    this.nodeCache.getListenable().addListener(this.nodeCacheListener, executor);
                } else {
                    this.nodeCache.getListenable().addListener(this.nodeCacheListener);
                }
            } catch (Throwable e) {
                log.error("DataChangeContext Start NodeCache Error! path: " + nodeCache.getCurrentData().getPath(),
                        e);
            }
            return this;
        }

        private DataChangeCache destory() throws IOException {
            this.nodeCache.getListenable().removeListener(this.nodeCacheListener);
            this.nodeCache.close();
            return this;
        }
    }

    /**
     * 添加子节点数量变化的listener
     * @param path
     * @param childChangeListener
     */
    public void addChildChangeListener(String path, ChildChangeListener childChangeListener) {
        ChildChangeCache context = getChildChangeCache(path);
        context.addChildChangeListener(childChangeListener);
    }

    private ChildChangeCache getChildChangeCache(String path) {
        ChildChangeCache context = childChangeMap.get(path);
        if (null == context) {
            context = childChangeMap.putIfAbsent(path, new ChildChangeCache(path,
                    new PathChildrenCache(this.zkClient, path, false, false, curatorExecutor), callBackExecutor));
            if (null == context) {
                context = childChangeMap.get(path).init();
            }
        }
        return context;
    }

    /**
     * 删除子节点数量变化的listener
     * @param path
     * @param childChangeListener
     */
    public void removeChildChangeListener(String path, ChildChangeListener childChangeListener) {
        ChildChangeCache context = childChangeMap.get(path);
        if (null != context) {
            context.removeChildChangeListener(childChangeListener);
        }
    }

    private static class ChildChangeCache {
        private final String path;
        private final PathChildrenCache childrenCache;
        private final Executor executor;
        private final List<ChildChangeListener> childrenChangeListeners = Lists.newCopyOnWriteArrayList();
        private final PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {

            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                final PathChildrenCacheEvent.Type type = event.getType();
                switch (type) {
                case CHILD_ADDED:
                case CHILD_REMOVED:
                    final String changePath = event.getData().getPath();
                    for (final ChildChangeListener listener : childrenChangeListeners) {
                        if (null != executor) {
                            executor.execute(new Runnable() {
                                public void run() {
                                    try {
                                        listener.handleChangeEvent(new ChildChangeListener.ChildChangeEvent(
                                                ChildChangeListener.ChildChangeEnum.convertCuratorType(type),
                                                changePath, childrenDatas(childrenCache.getCurrentData())));
                                    } catch (Throwable e) {
                                        log.error("Handle ChildChangeCache Listener Error! path: " + path, e);
                                    }
                                }
                            });
                        } else {
                            try {
                                listener.handleChangeEvent(new ChildChangeListener.ChildChangeEvent(
                                        ChildChangeListener.ChildChangeEnum.convertCuratorType(type), changePath,
                                        childrenDatas(childrenCache.getCurrentData())));
                            } catch (Throwable e) {
                                log.error("Handle ChildChangeCache Listener Error! path: " + path, e);
                            }
                        }
                    }
                    break;
                default:
                    break;
                }
            }
        };

        private ChildChangeCache(String path, PathChildrenCache childrenCache, Executor executor) {
            this.path = path;
            this.childrenCache = childrenCache;
            this.executor = executor;
        }

        private ChildChangeCache init() {
            try {
                this.childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
                if (null != this.executor) {
                    this.childrenCache.getListenable().addListener(this.childrenCacheListener, this.executor);
                } else {
                    this.childrenCache.getListenable().addListener(this.childrenCacheListener);
                }
            } catch (Throwable e) {
                log.error("DataChangeContext Start ChildrenCache Error!: Parent Path: " + path, e);
            }
            return this;
        }

        private void addChildChangeListener(ChildChangeListener childChangeListener) {
            this.childrenChangeListeners.add(childChangeListener);
        }

        private void removeChildChangeListener(ChildChangeListener childChangeListener) {
            this.childrenChangeListeners.remove(childChangeListener);
        }

        private String getPath() {
            return path;
        }

        private List<String> getCurrentChildren() throws UnsupportedEncodingException {
            return childrenDatas(this.childrenCache.getCurrentData());
        }

        private List<String> childrenDatas(List<ChildData> childrenDatas) throws UnsupportedEncodingException {
            final List<String> childrenList = new ArrayList<String>(childrenDatas.size());
            for (ChildData childData : childrenDatas) {
                String childrenPath = StringUtil.substringAfterLast(childData.getPath(), "/");
                childrenList.add(childrenPath);
            }
            return childrenList;
        }

        private ChildChangeCache destory() throws IOException {
            this.childrenCache.getListenable().removeListener(this.childrenCacheListener);
            this.childrenCache.close();
            return this;
        }
    }

    private static String deserialize(byte[] bytes) throws UnsupportedEncodingException {
        return null != bytes ? new String(bytes, DEFAULT_ZK_ENCODING) : null;
    }

    private static byte[] serialize(String data) throws UnsupportedEncodingException {
        return data.getBytes(DEFAULT_ZK_ENCODING);
    }

    public InterProcessMutex createLock(String path) {
        return new InterProcessMutex(zkClient, path);
    }

    /**
     * 添加链接状态的listener
     * @param connectionStateListener
     */
    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        this.connectionStateListenerList.add(connectionStateListener);
    }

    /**
     * 删除链接状态的listener
     * @param connectionStateListener
     */
    public void removeConnectionStateListener(ConnectionStateListener connectionStateListener) {
        this.connectionStateListenerList.remove(connectionStateListener);
    }

    /**
     * just for test
     */
    public CuratorFramework getZkClient() {
        return zkClient;
    }

    /**
     * 通过zk创建分布式锁。
     */
    public InterProcessSemaphoreV2 createDistributedLocks(String lockPath, int count) {
        return new InterProcessSemaphoreV2(zkClient, lockPath, count);
    }

    public void setZkConfig(ZkConfig zkConfig) {
        this.zkConfig = zkConfig;
    }

    public ZkConfig getZkConfig() {
        return zkConfig;
    }
}
