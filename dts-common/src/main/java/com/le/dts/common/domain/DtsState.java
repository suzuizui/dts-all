package com.le.dts.common.domain;

/**
 * DTS状态
 * @author tianyao.myc
 *
 */
public enum DtsState {

	START(0, "job started!");
	
	private int code;
	
	private String information;
	
	private DtsState(int code, String information) {
		this.code = code;
		this.information = information;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}
	
}
