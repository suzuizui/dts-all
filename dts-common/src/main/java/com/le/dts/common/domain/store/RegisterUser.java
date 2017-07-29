package com.le.dts.common.domain.store;

import java.util.Date;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * 注册用户
 * @author tianyao.myc
 *
 */
public class RegisterUser implements Constants {

	/** 主键ID */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 用户ID */
    private String userId;
    
    /** 用户名称 */
    private String userName;
    
    //密码
    private String password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "RegisterUser [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", userId=" + userId
				+ ", userName=" + userName + ", password=" + password + "]";
	}
	
}
