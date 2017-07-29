package com.le.dts.common.domain;

import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * DTS用户数据对象
 * @author tianyao.myc
 *
 */
public class DtsUser {

	/** 用户ID */
	private String userId;
	
	/** 用户名称 */
	private String userName;

	public DtsUser() {
		
	}
	
	public DtsUser(String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}
	
	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static DtsUser newInstance(String json) {
		return RemotingSerializable.fromJson(json, DtsUser.class);
	}
	
	/**
	 * 对象转换成json
	 */
	@Override
	public String toString() {
		return RemotingSerializable.toJson(this, false);
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
