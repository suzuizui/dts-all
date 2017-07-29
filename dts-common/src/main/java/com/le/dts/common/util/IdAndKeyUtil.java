package com.le.dts.common.util;

import java.util.UUID;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * IdAndKey相关工具
 * @author tianyao.myc
 *
 */
public class IdAndKeyUtil implements Constants {
	
	/**
	 * 获取全局唯一ID
	 * @return
	 */
	public static String acquireUniqueId() {
		return UUID.randomUUID().toString().toUpperCase().replace(HORIZONTAL_LINE, "");
	}
	
}
