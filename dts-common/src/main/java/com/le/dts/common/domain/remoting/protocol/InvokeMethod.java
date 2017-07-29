package com.le.dts.common.domain.remoting.protocol;

import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.proxy.ProxyService;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.domain.remoting.RemoteMachine;

/**
 * 调用方法
 * @author tianyao.myc
 *
 */
public class InvokeMethod {

	/** 远端机器信息 */
	private RemoteMachine remoteMachine;
	
	/** 调用方法 */
	private String methodName;
	
	/** 参数类型 */
	private String[] parameterTypes;
	
	/** 参数 */
	private String[] arguments;
	
	/** 返回类型 */
	private String returnType;
	
	/** 参数类型数组 不进行序列化传送 */
	private transient Class<?>[] classArray;
	
	/** 参数数组 不进行序列化传送 */
	private transient Object[] objectArray;

	public InvokeMethod() {
		
	}
	
	public InvokeMethod(RemoteMachine remoteMachine, String methodName, String[] parameterTypes, String[] arguments, String returnType) {
		this.remoteMachine = remoteMachine;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.arguments = arguments;
		this.returnType = returnType;
	}
	
	/**
	 * 初始化参数
	 * @return
	 */
	public void initParameters() {
		this.classArray = new Class<?>[this.parameterTypes.length];
		this.objectArray = new Object[this.parameterTypes.length];
		for(int i = 0 ; i < this.parameterTypes.length ; i ++) {
			this.classArray[i] = ProxyService.getClass(this.parameterTypes[i]);
			this.objectArray[i] = RemotingSerializable.fromJson(this.arguments[i], this.classArray[i]);
		}
	}
	
	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static InvokeMethod newInstance(String json) {
		InvokeMethod invokeMethod = RemotingSerializable.fromJson(json, InvokeMethod.class);
		/** 信息反转 */
		invokeMethod.getRemoteMachine().reversal();
		/** 初始化参数 */
		invokeMethod.initParameters();
		return invokeMethod;
	}
	
	/**
	 * 对象转换成json
	 */
	@Override
	public String toString() {
		return RemotingSerializable.toJson(this, false);
	}

	public RemoteMachine getRemoteMachine() {
		return remoteMachine;
	}

	public void setRemoteMachine(RemoteMachine remoteMachine) {
		this.remoteMachine = remoteMachine;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public String[] getArguments() {
		return arguments;
	}

	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public Class<?>[] getClassArray() {
		return classArray;
	}

	public void setClassArray(Class<?>[] classArray) {
		this.classArray = classArray;
	}

	public Object[] getObjectArray() {
		return objectArray;
	}

	public void setObjectArray(Object[] objectArray) {
		this.objectArray = objectArray;
	}

}
