package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * 服务器
 * @author tianyao.myc
 *
 */
public class Server {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 服务器分组ID */
	private long serverGroupId;
	
	/** 服务器信息 */
	private String server;

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

	public long getServerGroupId() {
		return serverGroupId;
	}

	public void setServerGroupId(long serverGroupId) {
		this.serverGroupId = serverGroupId;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", serverGroupId="
				+ serverGroupId + ", server=" + server + "]";
	}
	
}
