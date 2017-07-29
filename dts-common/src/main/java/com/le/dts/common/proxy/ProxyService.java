package com.le.dts.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.remoting.ClassKey;
import com.le.dts.common.domain.result.ResultCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.remoting.ClassKey;
import com.le.dts.common.domain.result.ResultCode;

/**
 * 代理服务
 * @author tianyao.myc
 *
 */
public class ProxyService implements Constants {

	private static final Log logger = LogFactory.getLog(ProxyService.class);
	
	/** 基本类型 */
	public static final Map<String, Class<?>> BASE_CLASS = new HashMap<String, Class<?>>();
	static {
		BASE_CLASS.put("double", 	double.class);
		BASE_CLASS.put("long", 		long.class);
		BASE_CLASS.put("float", 	float.class);
		BASE_CLASS.put("int", 		int.class);
		BASE_CLASS.put("short", 	short.class);
		BASE_CLASS.put("char", 		char.class);
		BASE_CLASS.put("byte", 		byte.class);
		BASE_CLASS.put("boolean", 	boolean.class);
		BASE_CLASS.put("void", 		void.class);
	}
	
	/** 方法缓存 */
	private final Map<ClassKey, Method> methodCache = new HashMap<ClassKey, Method>();
	
	/**
	 * 获取类型
	 * @param parameterTypeString
	 * @return
	 * @throws Throwable
	 */
	public static Class<?> getClass(String parameterTypeString) {
		Class<?> parameterTypeClass = ProxyService.BASE_CLASS.get(parameterTypeString);
		if(parameterTypeClass != null) {
			return parameterTypeClass;
		} else {
			try {
				return Class.forName(parameterTypeString);
			} catch (Throwable e) {
				logger.error("[ProxyService]: getClass error, parameterTypeString:" + parameterTypeString, e);
				return null;
			}
		}
	}
	
	/**
	 * 代理接口
	 * @param interfaceClass
	 * @param invocationHandler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T proxyInterface(Class<T> interfaceClass, InvocationHandler invocationHandler) {
		return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler);
	}
	
	/**
	 * new对象
	 * @param classType
	 * @return
	 */
	public <T> T newInstance(Class<T> classType) {
		try {
			return (T)classType.newInstance();
		} catch (Throwable e) {
			logger.error("[ProxyService]: newInstance error, classType:" + classType.getName(), e);
			return null;
		}
	}
	
	/**
	 * new对象
	 * @param classType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T newInstance(String classType) {
		Class<?> type = null;
		try {
			type = Class.forName(classType);
		} catch (Throwable e) {
			logger.error("[ProxyService]: newInstance Class.forName error, classType:" + classType, e);
			return null;
		}
		try {
			return (T)type.newInstance();
		} catch (Throwable e) {
			logger.error("[ProxyService]: newInstance error, classType:" + classType, e);
			return null;
		}
	}
	
	/**
	 * 调用方法
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @param arguments
	 * @return
	 */
	public Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] arguments) {
		Method method = getMethod(object, methodName, parameterTypes);
		if(null == method) {
			return ResultCode.NO_SUCH_METHOD;
		}
		Object result = null;
		try {
			method.setAccessible(true);
			result = method.invoke(object, arguments);
		} catch (Throwable e) {
			logger.error("[invokeMethod]: error, methodName:" + methodName, e);
		}
		return result;
	}
	
	/**
	 * 获取方法
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	private Method getMethod(Object object, String methodName, Class<?>[] parameterTypes) {
		ClassKey classKey = new ClassKey(object, methodName, parameterTypes);
		Method method = methodCache.get(classKey);
		if(method != null) {
			return method;
		}
		method = tryFindMethod(object, methodName, parameterTypes);
		if(method != null) {
			/** 缓存方法 */
			methodCache.put(classKey, method);
		}
		return method;
	}
	
	/**
	 * 尝试查找方法
	 * @param object
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	private Method tryFindMethod(Object object, String methodName, Class<?>[] parameterTypes) {
		Method method = null;
		try {
			method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
		} catch (Throwable e) {
			logger.error("[tryFindMethod]: failed, methodName:" + methodName, e);
		}
		return method;
	}
	
	/**
	 * 获取接口
	 * @param object
	 * @return
	 */
	public Type[] aquireInterface(Object object) {
		
		List<Type> typeList = new ArrayList<Type>();
		
		Class<?> classType = object.getClass();
		while(classType != null) {
			
			//获取Types
			aquireTypes(typeList, classType);
			
			classType = classType.getSuperclass();
		}
		
		if(typeList.isEmpty()) {
			return new Type[]{};
		}
		
		Type[] typeArray = new Type[typeList.size()];
		typeList.toArray(typeArray);
		
		return typeArray;
	}
	
	/**
	 * 获取Types
	 * @param typeList
	 * @param classType
	 */
	private void aquireTypes(List<Type> typeList, Class<?> classType) {
		
		Type[] types = classType.getGenericInterfaces();
		
		if(types != null && types.length > 0) {
			for(int i = 0 ; i < types.length ; i ++) {
				typeList.add(types[i]);
			}
		}
	}
	
}
