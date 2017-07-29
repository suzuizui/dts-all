package com.le.dts.common.domain.store;

import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

import java.util.Date;

/**
 * 客户端集群信息
 * @author tianyao.myc
 *
 */
public class ClientGroup {

	/** 主键 */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 服务端集群分组ID */
	private long serverGroupId;

    // 逻辑分组ID
    private long logicalGroupId;
	
	/** 分组描述 */
	private String description;
	
	//安全控制
	private String securityControl;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public long getLogicalGroupId() {
        return logicalGroupId;
    }

    public void setLogicalGroupId(long logicalGroupId) {
        this.logicalGroupId = logicalGroupId;
    }

	public String getSecurityControl() {
		return securityControl;
	}

	public void setSecurityControl(String securityControl) {
		this.securityControl = securityControl;
	}

	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static ClientGroup newInstance(String json) {
        return RemotingSerializable.fromJson(json, ClientGroup.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }
}
