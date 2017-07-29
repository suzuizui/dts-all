package com.le.dts.server.remoting.processor;

import com.le.dts.server.monitor.callback.Display;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.protocol.InvokeMethod;
import com.le.dts.common.exception.InitException;
import com.le.dts.common.helper.RemotingHelper;
import com.le.dts.common.proxy.ProxyService;
import com.le.dts.common.remoting.netty.NettyRequestProcessor;
import com.le.dts.common.remoting.protocol.RemotingCommand;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.BytesUtil;
import com.le.dts.common.util.StringUtil;
import com.le.dts.server.context.ServerContext;

/**
 * 服务端请求处理器
 * @author tianyao.myc
 *
 */
public class ServerRequestProcessor implements NettyRequestProcessor, ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(ServerRequestProcessor.class);
	
	/** 请求队列 */
	private LinkedBlockingQueue<Runnable> requestQueue = null;
	
	public ServerRequestProcessor(LinkedBlockingQueue<Runnable> requestQueue) {
		this.requestQueue = requestQueue;
	}
	
	/**
	 * 初始化
	 * @throws InitException
	 */
	public void init() throws InitException {
		
		//添加展现
		serverMonitor.addDisplay(new Display(){

			@Override
			public String content() {
				
				//检查requestQueue并发出报警信息
				serverMonitor.checkRequestQueueAndAlertMsg(requestQueue.size());
				
				return "requestQueue > size:" + requestQueue.size() + ", remainingCapacity:" + requestQueue.remainingCapacity();
			}
			
		});
		
	}
	
	/**
	 * 处理请求
	 */
	public RemotingCommand processRequest(ChannelHandlerContext ctx,
			RemotingCommand request) throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		byte[] requestBody = request.getBody();
		if(null == requestBody) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ServerRequestProcessor]: requestBody is null, remoteAddress:" + remoteAddress);
			return new RemotingCommand();
		}
		
		String json = null;
		try {
			json = (String)BytesUtil.bytesToObject(requestBody);
		} catch (Throwable e) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ServerRequestProcessor]: bytesToObject error"
					+ ", remoteAddress:" + remoteAddress, e);
			return new RemotingCommand();
		}
		if(StringUtil.isBlank(json)) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ServerRequestProcessor]: json is null"
					+ ", remoteAddress:" + remoteAddress);
			return new RemotingCommand();
		}
		
		InvokeMethod invokeMethod = InvokeMethod.newInstance(json);
		invokeMethod.getRemoteMachine().setChannel(ctx.channel());
		
		/** 设置上下文 */
		InvocationContext.setRemoteMachine(invokeMethod.getRemoteMachine());
		Object result = (Object)proxyService.invokeMethod(serverService, invokeMethod.getMethodName(), invokeMethod.getClassArray(), invokeMethod.getObjectArray());
		/** 清除上下文 */
		InvocationContext.clean();
		
		Class<?> returnClass = ProxyService.getClass(invokeMethod.getReturnType());
		if(void.class == returnClass) {
			return new RemotingCommand();
		}
		
		if(null == result) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ServerRequestProcessor]: result is null, remoteAddress:" + remoteAddress + ", invokeMethod:" + invokeMethod);
			return new RemotingCommand();
		}
		
		byte[] responseBody = null;
		try {
			responseBody = BytesUtil.objectToBytes(RemotingSerializable.toJson(result, false));
		} catch (Throwable e) {
			final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
			logger.error("[ServerRequestProcessor]: objectToBytes error"
					+ ", remoteAddress:" + remoteAddress + ", invokeMethod:" + invokeMethod, e);
			return new RemotingCommand();
		}
		
		RemotingCommand response = new RemotingCommand();
		response.setBody(responseBody);
		
		//方法调用统计
		serverMonitor.methodCount(invokeMethod.getMethodName(), startTime);
		serverMonitor.groupIdMethodCount(invokeMethod.getMethodName() + UNDERLINE + invokeMethod.getRemoteMachine().getGroupId(), startTime);
		
		return response;
	}
	
	public LinkedBlockingQueue<Runnable> getRequestQueue() {
		return requestQueue;
	}

}
