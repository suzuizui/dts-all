package com.le.dts.common.exception;

/**
 * 字节异常
 * @author tianyao.myc
 *
 */
public class BytesException extends Exception {

	/** 序列化ID */
	private static final long serialVersionUID = 2318171251733081524L;

	public BytesException() {
		super();
	}
	
	public BytesException(String message) {
		super(message);
	}
	
	public BytesException(Throwable error) {
		super(error);
	}
	
	public BytesException(String message, Throwable error) {
		super(message, error);
	}
	
}
