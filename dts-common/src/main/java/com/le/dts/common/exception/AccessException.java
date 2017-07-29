package com.le.dts.common.exception;

/**
 * 访问异常
 * @author tianyao.myc
 *
 */
public class AccessException extends Exception {

	/** 序列化ID */
	private static final long serialVersionUID = 8419168590542007604L;

	public AccessException() {
		super();
	}
	
	public AccessException(String message) {
		super(message);
	}
	
	public AccessException(Throwable error) {
		super(error);
	}
	
	public AccessException(String message, Throwable error) {
		super(message, error);
	}
	
}
