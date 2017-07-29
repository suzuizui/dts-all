package com.le.dts.console.store;

import com.le.dts.common.domain.store.RegisterUser;
import com.le.dts.common.exception.AccessException;

/**
 * 注册用户访问接口
 * @author tianyao.myc
 *
 */
public interface RegisterUserAccess {

	/**
	 * 插入
	 * @param registerUser
	 * @return
	 * @throws AccessException
	 */
	public long insert(RegisterUser registerUser) throws AccessException;
	
	/**
	 * 查询
	 * @param query
	 * @return
	 * @throws AccessException
	 */
	public RegisterUser queryByUser(RegisterUser query) throws AccessException;
	
}
