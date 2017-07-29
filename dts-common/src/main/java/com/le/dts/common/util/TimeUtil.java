package com.le.dts.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;

/**
 * Created by Moshan on 14-11-20.
 */
public class TimeUtil implements Constants {
	
	private static final Log logger = LogFactory.getLog(TimeUtil.class);

	/** 工作开始时间 */
    private static final int START_WORK_TIME = 9;

    /** 工作结束时间 */
    private static final int END_WORK_TIME = 18;

    public static boolean nowIsWorkDayTime(long timeInMillis) {
        return (nowIsWorkDay(timeInMillis) && nowIsWorkTime(timeInMillis));
    }

    public static boolean nowIsWorkDay(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }

        return true;
    }

    public static boolean nowIsWorkTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= START_WORK_TIME && hourOfDay < END_WORK_TIME) {
            return true;
        }

        return false;
    }
	
    public static String date2SecondsString(Date date) {
        return format(date, TIME_FORMAT_SECONDS);
    }
    
    /**
	 * 把时间转换成相应格式的字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		SimpleDateFormat formater = 
				new SimpleDateFormat(format);
		return formater.format(date);
	}
	
	/**
	 * 增加时间
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date increaseDate(Date date, long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, (int)(time / 1000L));
		return calendar.getTime();
	}
	
	/**
	 * String转Date
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date string2Date(String date, String format) {
		
		SimpleDateFormat formater = new SimpleDateFormat(format);
		
		try {
			return formater.parse(date);
		} catch (Throwable e) {
			logger.error("[TimeUtil]: string2Date error, date:" + date + ", format:" + format, e);
		}
		
		return null;
	}
	
	/**
	 * 增加时间
	 * @param date
	 * @param field
	 * @param amount
	 * @return
	 */
	public static Date increaseDate(Date date, int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}
}
