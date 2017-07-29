package com.le.dts.client.remoting.proxy;

import com.le.dts.client.context.ClientContext;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.remoting.protocol.InvokeMethod;
import com.le.dts.common.fastjson.JSON;
import com.le.dts.common.proxy.ProxyService;
import com.le.dts.common.remoting.protocol.RemotingCommand;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.util.BytesUtil;
import com.le.dts.common.util.RemotingUtil;
import com.le.dts.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 客户端代理调用接口
 * @author tianyao.myc
 *
 */
public class ClientInvocationHandler implements InvocationHandler, ClientContext, Constants {

	private static final Log logger = LogFactory.getLog(ClientInvocationHandler.class);
	
	/**
	 * 拦截方法调用各项参数
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		//TODO 防止debug时清除上线文数据
		if("toString".equals(method.getName())) {
			return this.getClass().getName();
		}
		
		RemoteMachine remoteMachine = InvocationContext.acquireRemoteMachine();
        remoteMachine.setGroupId(clientConfig.getGroupId());
        remoteMachine.setClientId(clientConfig.getClientId());
        remoteMachine.setLocalVersion(clientConfig.getVersion());
        remoteMachine.setCrashRetry(clientConfig.isCrashRetry());
        Channel channel = clientRemoting.getAndCreateChannel(remoteMachine.getRemoteAddress());
        if(null == channel) {
        	logger.error("[ClientInvocationHandler]: getAndCreateChannel error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName());
            InvocationContext.clean();
			return null;
        }
        remoteMachine.setLocalAddress(RemotingUtil.socketAddress2String(channel.localAddress()));
        
		Class<?>[] parameterTypesClass = method.getParameterTypes();
		String[] parameterTypesString = new String[parameterTypesClass.length];
		String[] arguments = new String[parameterTypesClass.length];
		for(int i = 0 ; i < parameterTypesClass.length ; i ++) {
			parameterTypesString[i] = parameterTypesClass[i].getName();
			arguments[i] = RemotingSerializable.toJson(args[i], false);
		}
		InvokeMethod invokeMethod = new InvokeMethod(remoteMachine, method.getName(), parameterTypesString, arguments, method.getReturnType().getName());
		byte[] requestBody = null;
		try {
			requestBody = BytesUtil.objectToBytes(invokeMethod.toString());
		} catch (Throwable e) {
			logger.error("[ClientInvocationHandler]: objectToBytes error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName(), e);
            InvocationContext.clean();
			return null;
		}
		if(null == requestBody) {
			logger.error("[ClientInvocationHandler]: requestBody is null"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName());
            InvocationContext.clean();
			return null;
		}
		RemotingCommand request = new RemotingCommand();
		request.setBody(requestBody);
		RemotingCommand response = null;
		try {
			response = clientRemoting.invokeSync(remoteMachine.getRemoteAddress(), request, remoteMachine.getTimeout());
		} catch (Throwable e) {
			logger.error("[ClientInvocationHandler]: invoke error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName(), e);
		}
        InvocationContext.clean();
		if(null == response) {
			logger.error("[ClientInvocationHandler]: response is null"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName());
			
			return null;
		}
		Class<?> returnClass = ProxyService.getClass(invokeMethod.getReturnType());
		if(void.class == returnClass) {
			
			return null;
		}
		byte[] responseBody = response.getBody();
		if(null == responseBody) {
			logger.error("[ClientInvocationHandler]: responseBody is null"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName());
			
			return null;
		}
		
		String json = null;
		try {
			json = (String)BytesUtil.bytesToObject(responseBody);
		} catch (Throwable e) {
			logger.error("[ClientInvocationHandler]: bytesToObject error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName(), e);
		}
		if(StringUtil.isBlank(json)) {
			logger.error("[ClientInvocationHandler]: json is null"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName());
			
			return null;
		}
		
		Type returnType = method.getGenericReturnType();
		if(returnType instanceof ParameterizedType) {
			return JSON.parseObject(json, (ParameterizedType)returnType);
		}
		return RemotingSerializable.fromJson(json, returnClass);
	}

}
