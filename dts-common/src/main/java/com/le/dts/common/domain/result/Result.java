package com.le.dts.common.domain.result;

import com.le.dts.common.constants.Constants;

/**
 * 返回值
 * @author tianyao.myc
 *
 */
public class Result<D> implements Constants {

	/** 返回数据 */
	private D data;
	
	/** 返回码 */
	private ResultCode resultCode;
	
	public Result() {
		
	}
	
	/**
	 * 重写toString方法
	 */
	public String toString() {
		return data + BLANK + resultCode;
	}
	
	public Result(D data) {
		this.data = data;
	}
	
	public Result(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
	
	public Result(D data, ResultCode resultCode) {
		this.data = data;
		this.resultCode = resultCode;
	}

	public D getData() {
		return data;
	}

	public void setData(D data) {
		this.data = data;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
	
}
