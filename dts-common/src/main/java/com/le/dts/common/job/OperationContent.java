package com.le.dts.common.job;

import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 对Job操作的记录;
 * @author luliang.ll
 *
 */
public class OperationContent {

	private String operate;
	
	private String value;

    public OperationContent() {}
	
	public OperationContent(String operate, String value) {
		this.operate = operate;
		this.value = value;
	}
	
	public static OperationContent newInstance(String json) {
		return RemotingSerializable.fromJson(json, OperationContent.class);
	}
	
	public String toString() {
		return RemotingSerializable.toJson(this, false);
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
