package com.le.dts.server.monitor.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.le.dts.common.constants.Constants;
import com.le.dts.server.context.ServerContext;

/**
 * 方法调用拦截
 * @author tianyao.myc
 *
 */
public class MethodInvocationHandler implements InvocationHandler, ServerContext, Constants {

	private Object business;//被代理对象
	
	/**
	 * 绑定对象
	 * @param business
	 * @return
	 */
	public Object bind(Object business) { 
		this.business = business;
		return Proxy.newProxyInstance( 
                //被代理类的ClassLoader 
                business.getClass().getClassLoader(), 
                //要被代理的接口,本方法返回对象会自动声称实现了这些接口 
                business.getClass().getInterfaces(), 
                //代理处理器对象 
                this);
	}
	
	/**
	 * 代理方法
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		long startTime = System.currentTimeMillis();
		
		Object result = null;
		
		result = method.invoke(this.business, args);
		
		//方法调用统计
		serverMonitor.methodCount(this.business.getClass().getSimpleName() + POINT + method.getName(), startTime);
		return result;
	}

}
