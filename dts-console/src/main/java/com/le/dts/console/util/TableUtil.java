package com.le.dts.console.util;

import java.text.DecimalFormat;

import com.le.dts.common.constants.Constants;

/**
 * 表相关工具
 * @author tianyao.myc
 *
 */
public class TableUtil implements Constants {

	/** 表数量 */
	public static final int TABLE_AMOUNT = 2048;
	
	public static final String PATTERN = "0000";
	public static final DecimalFormat decimalFormat = new DecimalFormat(PATTERN);
	
	/**
	 * 创建db分组
	 * @param index
	 * @return
	 */
	public static String createDbGroup(int index) {
		return "DTS_SERVER_" + decimalFormat.format(index / 64) + "_GROUP";
	}
	
	/**
	 * 创建表
	 * @param index
	 * @return
	 */
	public static String createTable(int index) {
		return "dts_task_snapshot" + UNDERLINE + decimalFormat.format(index);
	}
	
}
