package com.le.dts.common.domain.remoting;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;


/**
 * Class Key
 * @author tianyao.myc
 *
 */
public class ClassKey implements Constants {

	/** 对象 */
	private Object object;
	
	/** 方法名称 */
	private String methodName;
	
	/** 类型 */
	private Class<?>[] classType;

	public ClassKey(Object object, String methodName, Class<?>[] classType) {
		this.object = object;
		this.methodName = methodName;
		this.classType = classType;
	}
	
	/**
	 * 重写equals方法
	 */
	public boolean equals(Object object) {
		if(null == object) {
			return false;
		}
		if(! (object instanceof ClassKey)) {
			return false;
		}
		ClassKey classKey = (ClassKey)object;
		if(! classKey.toString().equals(this.toString())) {
			return false;
		}
		return true;
	}
	
	/**
	 * 重写hashCode方法
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	/**
	 * 重写toString方法
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(object.toString() + BLANK_SPLIT);
		stringBuilder.append(methodName + BLANK_SPLIT);
		for(int i = 0 ; i < classType.length ; i ++) {
			stringBuilder.append(classType[i].getName() + BLANK_SPLIT);
		}
		return stringBuilder.toString();
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getClassType() {
		return classType;
	}

	public void setClassType(Class<?>[] classType) {
		this.classType = classType;
	}
	
}
