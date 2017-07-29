package com.le.dts.console.remoting;

import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.exception.RemotingConnectException;
import com.le.dts.common.exception.RemotingSendRequestException;
import com.le.dts.common.exception.RemotingTimeoutException;
import com.le.dts.common.proxy.ProxyService;
import com.le.dts.common.remoting.netty.NettyClientConfig;
import com.le.dts.common.remoting.netty.NettyRemotingClient;
import com.le.dts.common.remoting.protocol.RemotingCommand;
import com.le.dts.console.remoting.proxy.ConsoleInvocationHandler;

/**
 * 客户端远程通信
 * @author tianyao.myc
 *
 */
public class ConsoleRemoting implements Constants {

	private static final Log logger = LogFactory.getLog(ConsoleRemoting.class);
	
	/** 远程通信客户端 */
	private NettyRemotingClient client = null;
	
	/** 代理服务 */
	public static final ProxyService proxyService = new ProxyService();
	
	/** 客户端代理调用接口 */
	private InvocationHandler invocationHandler = new ConsoleInvocationHandler(this);
	
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
			throw new InitException("[ConsoleRemoting]: initRemotingClient error", e);
		}
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
	 * 获取连接
	 * @param addr
	 * @return
	 * @throws InterruptedException
	 */
	public Channel getAndCreateChannel(final String addr) throws InterruptedException {
		return client.getAndCreateChannel(addr);
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

}
