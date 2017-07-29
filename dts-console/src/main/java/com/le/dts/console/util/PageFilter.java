package com.le.dts.console.util;

public abstract class PageFilter {

	private Object src;
	
	public Object getSrc() {
		return src;
	}

	public void setSrc(Object src) {
		this.src = src;
	}

	public PageFilter(Object src) {
		this.src = src;
	}

	public abstract boolean filter(Object target);
}

