package com.le.dts.common.util;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.constants.Constants;

/**
 * Created by Moshan on 14-12-17.
 */
public class CommonUtil {

	/**
	 * 是否是简单Job
	 * @param type
	 * @return
	 */
    public static boolean isSimpleJob(int type) {
        return type == Constants.JOB_TYPE_API_SIMPLE
        		|| type == Constants.JOB_TYPE_TIMER_SIMPLE 
        		|| type == Constants.JOB_TYPE_API_ALL_SIMPLE 
        		|| type == Constants.JOB_TYPE_TIMER_ALL_SIMPLE;
    }
    
    /**
     * 是否是API Job
     * @param type
     * @return
     */
    public static boolean isApiJob(int type) {
        return type == Constants.JOB_TYPE_API_SIMPLE 
        		|| type == Constants.JOB_TYPE_API_PARALLEL 
        		|| type == Constants.JOB_TYPE_API_ALL_SIMPLE;
    }
    
    /**
     * 是否是全部机器同时触发执行的Job
     * @param type
     * @return
     */
    public static boolean isAllJob(int type) {
    	return type == Constants.JOB_TYPE_API_ALL_SIMPLE 
        		|| type == Constants.JOB_TYPE_TIMER_ALL_SIMPLE;
    }
    
}
