package com.le.dts.common.comparator;

import java.util.Comparator;

import com.le.dts.common.domain.store.Job;
import com.le.dts.common.domain.store.Job;

/**
 * 比较两个Job
 * @author tianyao.myc
 *
 */
public class JobComparator implements Comparator<Job> {

	/**
	 * 比较两个Job大小 根据id来比较
	 */
	@Override
	public int compare(Job source, Job target) {
		if(source.getId() < target.getId()) {
			return -1;
		}
		if(source.getId() > target.getId()) {
			return 1;
		}
		return 0;
	}

}
