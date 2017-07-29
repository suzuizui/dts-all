package com.le.dts.common.domain.store;

import java.util.Date;

/**
 * 标记
 * @author tianyao.myc
 *
 */
public class Tag {

	/** 主键ID */
	private long id;
	
	/** 创建时间 */
	private Date gmtCreate;
	
	/** 修改时间 */
	private Date gmtModified;
	
	/** 标记状态 */
	private int status;
	
	/** 标记内容 */
	private String content;
	
	/** 类型 */
	private int type;
	
	/** 处理次数 */
	private int handleTimes;
	
	/** 下一次处理时间 */
	private Date nextHandleTime;
	
	/** 位点起始偏移量 */
	private long offset;
	
	/** 锁版本 */
	private int lockVersion;

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHandleTimes() {
		return handleTimes;
	}

	public void setHandleTimes(int handleTimes) {
		this.handleTimes = handleTimes;
	}

	public Date getNextHandleTime() {
		return nextHandleTime;
	}

	public void setNextHandleTime(Date nextHandleTime) {
		this.nextHandleTime = nextHandleTime;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLockVersion() {
		return lockVersion;
	}

	public void setLockVersion(int lockVersion) {
		this.lockVersion = lockVersion;
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", gmtCreate=" + gmtCreate + ", gmtModified="
				+ gmtModified + ", status=" + status + ", content=" + content
				+ ", type=" + type + ", handleTimes=" + handleTimes
				+ ", nextHandleTime=" + nextHandleTime + ", offset=" + offset
				+ ", lockVersion=" + lockVersion + "]";
	}

}
