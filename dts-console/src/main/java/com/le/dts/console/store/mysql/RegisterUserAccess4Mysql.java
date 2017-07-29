package com.le.dts.console.store.mysql;

import com.le.dts.console.store.RegisterUserAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.RegisterUser;
import com.le.dts.common.exception.AccessException;

/**
 * 注册用户访问接口
 * Mysql实现
 * @author tianyao.myc
 *
 */
public class RegisterUserAccess4Mysql implements RegisterUserAccess {
	
	@Autowired
	private SqlMapClients sqlMapClients;

	/**
	 * 插入
	 */
	@Override
	public long insert(RegisterUser registerUser) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("RegisterUser.insert", registerUser);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	/**
	 * 查询
	 */
	@Override
	public RegisterUser queryByUser(RegisterUser query)
			throws AccessException {
		
		RegisterUser registerUser = null;
		try {
			registerUser = (RegisterUser)sqlMapClients.getSqlMapClientMeta()
					.queryForObject("RegisterUser.queryByUser", query);
		} catch (Throwable e) {
			throw new AccessException("[queryByUser]: error", e);
		}
		return registerUser;
	}

}
