package com.le.dts.console.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字相关工具
 * @author tianyao.myc
 *
 */
public class NumericUtil {

	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
        String regEx = "^-?[0-9]+$";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);
        if (mat.find()) {
            return true;
        } else {
            return false;
        }
    }
	
}
