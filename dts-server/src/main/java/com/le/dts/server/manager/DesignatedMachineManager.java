package com.le.dts.server.manager;

import java.util.List;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.exception.AccessException;
import com.le.dts.common.exception.InitException;
import com.le.dts.server.context.ServerContext;

/**
 * 指定机器管理
 * @author tianyao.myc
 *
 */
public class DesignatedMachineManager implements ServerContext, Constants {

	/**
	 * 加载指定机器列表
	 * @param id
	 * @return
	 * @throws AccessException
	 */
	public List<DesignatedMachine> loadDesignatedMachineList(long id) throws InitException {
		DesignatedMachine query = new DesignatedMachine();
		query.setId(id);
		List<DesignatedMachine> designatedMachineList = null;
		try {
			designatedMachineList = store.getDesignatedMachineAccess().queryDesignatedMachineListById(query);
		} catch (Throwable e) {
			throw new InitException("[DesignatedMachineManager]: queryDesignatedMachineListById error, id:" + id, e);
		}
		return designatedMachineList;
	}
	
}
