package com.le.dts.common.util;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 列表相关工具
 * @author tianyao.myc
 *
 */
public class ListUtil {

	public static <T> T acquireLastObject(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static int getSizeAllowNull(List list) {
        if (CollectionUtils.isEmpty(list))
            return 0;
        return list.size();
    }
	
}
