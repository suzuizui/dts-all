package com.le.dts.server.remoting;

import java.lang.reflect.InvocationHandler;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.exception.RemotingConnectException;
import com.le.dts.common.exception.RemotingSendRequestException;
import com.le.dts.common.exception.RemotingTimeoutException;
import com.le.dts.common.remoting.netty.NettyClientConfig;
import com.le.dts.common.remoting.netty.NettyRemotingClient;
import com.le.dts.common.remoting.protocol.RemotingCommand;
import com.le.dts.server.context.ServerContext;
import com.le.dts.server.remoting.proxy.ClientInvocationHandler;

/**
 * 客户端远程通信
 * @author tianyao.myc
 *
 */
public class ClientRemoting implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(ClientRemoting.class);
	
	/** 远程通信客户端 */
	private NettyRemotingClient client = null;
	
	/** 客户端代理调用接口 */
	private InvocationHandler invocationHandler = new ClientInvocationHandler();
	
	/** 服务器列表缓存 */
	private volatile List<String> serverListCache;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		/** 初始化远程通信客户端 */
		initRemotingClient();
		
	}
	
	/**
	 * 初始化远程通信客户端
	 * @throws InitException
	 */
	private void initRemotingClient() throws InitException {
		NettyClientConfig config = new NettyClientConfig();
		
		client = new NettyRemotingClient(config);
		
		try {
			client.start();
		} catch (Throwable e) {
			throw new InitException("[ClientRemoting]: initRemotingClient error", e);
		}
	}
	
	/**
	 * 获取当前服务器集群分组IP列表
	 * @return
	 */
	public List<String> getServerList() {
		if(CollectionUtils.isEmpty(this.serverListCache)) {
			this.serverListCache = zookeeper.getServerList();
		}
		return this.serverListCache;
	}
	
	/**
	 * 代理接口
	 * @param interfaceClass
	 * @return
	 */
	public <T> T proxyInterface(Class<T> interfaceClass) {
		return proxyService.proxyInterface(interfaceClass, invocationHandler);
	}
	
	/**
	 * 远程方法同步调用
	 * @param addr
	 * @param request
	 * @param timeoutMillis
	 * @return
	 * @throws InterruptedException
	 * @throws RemotingConnectException
	 * @throws RemotingSendRequestException
	 * @throws RemotingTimeoutException
	 */
	public RemotingCommand invokeSync(String addr, final RemotingCommand request, long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingSendRequestException,
            RemotingTimeoutException {
		return client.invokeSync(addr, request, timeoutMillis);
	}

	public void setServerListCache(List<String> serverListCache) {
		this.serverListCache = serverListCache;
	}

}
