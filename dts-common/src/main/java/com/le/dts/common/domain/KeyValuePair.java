package com.le.dts.common.domain;

import java.io.Serializable;

/**
 * 键-值对
 * @author tianyao.myc
 *
 */
public class KeyValuePair<Key, Value> implements Serializable {

	private static final long serialVersionUID = -1318805120637729763L;

	private Key key;
	
	private Value value;

	public KeyValuePair() {
		
	}
	
	public KeyValuePair(Key key, Value value) {
		this.key = key;
		this.value = value;
	}
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "KeyValuePair [key=" + key + ", value=" + value + "]";
	}
	
}
