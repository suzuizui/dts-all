package com.le.dts.console.remoting.proxy;

import com.le.dts.console.remoting.ConsoleRemoting;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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


/**
 * 控制台代理调用接口
 * @author tianyao.myc
 *
 */
public class ConsoleInvocationHandler implements InvocationHandler, Constants {

	private static final Log logger = LogFactory.getLog(ConsoleInvocationHandler.class);
	
	private ConsoleRemoting consoleRemoting;
	
	public ConsoleInvocationHandler(ConsoleRemoting consoleRemoting) {
		this.consoleRemoting = consoleRemoting;
	}
	
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
        Channel channel = consoleRemoting.getAndCreateChannel(remoteMachine.getRemoteAddress());
        if(null == channel) {
        	logger.error("[ConsoleInvocationHandler]: getAndCreateChannel error"
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
			logger.error("[ConsoleInvocationHandler]: objectToBytes error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName(), e);
            InvocationContext.clean();
			return null;
		}
		if(null == requestBody) {
			logger.error("[ConsoleInvocationHandler]: requestBody is null"
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
			response = consoleRemoting.invokeSync(remoteMachine.getRemoteAddress(), request, remoteMachine.getTimeout());
		} catch (Throwable e) {
			logger.error("[ConsoleInvocationHandler]: invoke error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName(), e);
		}
        InvocationContext.clean();
		if(null == response) {
			logger.error("[ConsoleInvocationHandler]: response is null"
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
			logger.error("[ConsoleInvocationHandler]: responseBody is null"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName());
			
			return null;
		}
		
		String json = null;
		try {
			json = (String)BytesUtil.bytesToObject(responseBody);
		} catch (Throwable e) {
			logger.error("[ConsoleInvocationHandler]: bytesToObject error"
					+ ", server:" + remoteMachine.getRemoteAddress() 
					+ ", timeout:" + remoteMachine.getTimeout() 
					+ ", methodName:" + method.getName(), e);
		}
		if(StringUtils.isBlank(json)) {
			logger.error("[ConsoleInvocationHandler]: json is null"
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
