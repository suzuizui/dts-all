package com.le.dts.common.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.Job;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.assemble.WarningNotifier;

/**
 * 检查工具类
 * @author tianyao.myc
 *
 */
public class CheckUtil {

	private static final Log logger = LogFactory.getLog(CheckUtil.class);
	
	/**
	 * 校验Job
	 * @param job
	 * @return
	 */
	public static Result<Boolean> checkJob(Job job) {
		Result<Boolean> checkResult = new Result<Boolean>(false);
		
		if(CommonUtil.isApiJob(job.getType())) {
			checkResult.setResultCode(ResultCode.SUCCESS);
	        checkResult.setData(true);
			return checkResult;
		}
		
		if(! CronExpression.isValidExpression(StringUtil.trim(job.getCronExpression()))) {
			checkResult.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
			return checkResult;
		}
		
		try {
			CronExpression expression = new CronExpression(job.getCronExpression());
			
			Date nextFireTime = expression.getNextValidTimeAfter(new Date());
            if (null == nextFireTime) {
            	checkResult.setResultCode(ResultCode.CRON_EXPRESSION_OBSOLETE);
    			return checkResult;
            }
		} catch (Throwable e) {

			logger.error("[CheckUtil]: getNextValidTimeAfter error, job:" + job, e);

			checkResult.setResultCode(ResultCode.FAILURE);
			return checkResult;
		}
		
		checkResult.setResultCode(ResultCode.SUCCESS);
        checkResult.setData(true);
		return checkResult;
	}

    /**
     * 校验Job
     * @param job
     * @return
     */
    public static Result<Boolean> checkUserConfigJob(Job job) {
        Result<Boolean> result = new Result<Boolean>(false);

        // 检查Job参数;
        // String 类型判断空;
        if(StringUtil.isBlank(job.getDescription())) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("job描述为空!");
            return result;
        }
//        if(StringUtil.isBlank(job.getCreaterId())) {
//            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
//            result.getResultCode().setInformation("创建者ID为空!");
//            return result;
//        }
        if(StringUtil.isBlank(job.getCronExpression())) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("时间表达式为空!");
            return result;
        }
        if(StringUtil.isBlank(job.getJobProcessor())) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("处理器接口不能为空!");
            return result;
        }

        // 类型有没有超出范围;
        if(job.getType() > 3) {
            result.setResultCode(ResultCode.USER_PARAMETER_ERROR);
            result.getResultCode().setInformation("Job 类型设置超出范围!");
            return result;
        }

        // String 类型设置超长检查;

        // 时间表达式检查
        if(!CronExpression.isValidExpression(StringUtil.trim(job.getCronExpression()))) {
            result.setResultCode(ResultCode.CRON_EXPRESSION_ERROR);
            return result;
        }
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(true);
        return result;
    }

}
