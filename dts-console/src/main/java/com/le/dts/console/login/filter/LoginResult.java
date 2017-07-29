package com.le.dts.console.login.filter;


public class LoginResult {
	
	private boolean hasLogged;
	private String msg;
	
	public boolean isHasLogged() {
		return hasLogged;
	}
	public void setHasLogged(boolean hasLogged) {
		this.hasLogged = hasLogged;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
