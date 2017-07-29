package com.le.dts.common.util;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * 版本相关工具
 * @author tianyao.myc
 *
 */
public class VersionUtil implements Constants {

	/**
	 * 是否是可以向客户端推送任务的版本
	 * @param version
	 * @return
	 */
	public static boolean isClientPushVersion(String version) {
		
		if(StringUtil.isBlank(version)) {
			return false;
		}
		
		String[] versionArray = version.split(SPLIT_POINT);
		
		if(versionArray.length < 4) {
			return false;
		}
		
		String[] last = versionArray[3].split(HORIZONTAL_LINE);
		
		if(Integer.parseInt(versionArray[0]) >= 0 
				&& Integer.parseInt(versionArray[1]) >= 0 
				&& Integer.parseInt(versionArray[2]) >= 3 
				&& Integer.parseInt(last[0]) >= 6) {
			return true;
		}
		
		return false;
	}
	
}
