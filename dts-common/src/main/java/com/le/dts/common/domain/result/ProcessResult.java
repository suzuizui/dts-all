package com.le.dts.common.domain.result;

/**
 * 处理结果
 * @author tianyao.myc
 *
 */
public class ProcessResult {

	/** 是否执行成功 */
	private boolean success = true;
	
	/** 重试次数 */
	private int retryCount;

	@SuppressWarnings("unused")
	private ProcessResult() {
		
	}
	
	public ProcessResult(boolean success) {
		this.success = success;
	}
	
	public ProcessResult(boolean success, int retryCount) {
		this.success = success;
		this.retryCount = retryCount;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

}
