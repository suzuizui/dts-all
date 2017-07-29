package com.le.dts.common.domain.store;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * Job等级枚举
 * @author tianyao.myc
 *
 */
public enum JobLevelEnum implements Constants {

	FINANCE						(1, "金融，资金，财务"),
	
	TRADE_MAIN					(2, "交易主路"),
	
	TRADE_CONNECTION			(3, "交易旁路"),
	
	OTHER						(100, "其他类型");
	
	private int level;
	
	private String description;
	
	private JobLevelEnum(int level, String description) {
		this.level = level;
		this.description = description;
	}
	
	/**
	 * 重写toString方法
	 */
	public String toString() {
		return level + BLANK + description;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
