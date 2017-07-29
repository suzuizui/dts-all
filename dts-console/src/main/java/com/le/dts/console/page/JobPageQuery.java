package com.le.dts.console.page;
/**
 * Job分页查询
 * @author luliang.ll
 *
 */
public class JobPageQuery extends BasePageQuery {

	private static final long serialVersionUID = -7739137281119712524L;
	
	private long clientGroupId;
	
	/** Job描述 */
	private String description;
	
	public long getClientGroupId() {
		return clientGroupId;
	}
	public void setClientGroupId(long clientGroupId) {
		this.clientGroupId = clientGroupId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "JobPageQuery [clientGroupId=" + clientGroupId
				+ ", description=" + description + "]";
	}
	
}
