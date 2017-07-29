package com.le.dts.console.store.mysql;

import java.util.List;

import com.le.dts.console.store.UserGroupRelationAccess;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.UserGroupRelation;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.mysql.access.SqlMapClients;

/**
 *
 * Created by luliang on 14/12/22.
 */
public class UserGroupRelationAccess4Mysql implements UserGroupRelationAccess {

    @Autowired
    private SqlMapClients sqlMapClients;

    @Override
    public long insert(UserGroupRelation userGroupRelation) throws AccessException {
        Long result = null;
        try {
            result = (Long)sqlMapClients.getSqlMapClientMeta().insert(
                    "UserGroupRelation.insert", userGroupRelation);
        } catch (Throwable e) {
            throw new AccessException("[insert]: error", e);
        }
        if (null == result) {
            return 0L;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<UserGroupRelation> query(UserGroupRelation query) throws AccessException {
        List<UserGroupRelation> userGroupRelationList = null;
        try {
            userGroupRelationList = (List<UserGroupRelation>) sqlMapClients
                    .getSqlMapClientMeta().queryForList("UserGroupRelation.queryByUserId",
                            query);
        } catch (Throwable e) {
            throw new AccessException("[queryByUserId]: error", e);
        }
        return userGroupRelationList;
    }

    @SuppressWarnings("unchecked")
	@Override
	public List<UserGroupRelation> queryByGroupId(UserGroupRelation query)
			throws AccessException {
    	List<UserGroupRelation> userGroupRelationList = null;
        try {
            userGroupRelationList = (List<UserGroupRelation>) sqlMapClients
                    .getSqlMapClientMeta().queryForList("UserGroupRelation.queryByGroupId",
                            query);
        } catch (Throwable e) {
            throw new AccessException("[queryByGroupId]: error", e);
        }
        return userGroupRelationList;
	}

	@Override
    public int update(UserGroupRelation userGroupRelation) throws AccessException {
        int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta().update(
                    "UserGroupRelation.update", userGroupRelation);
        } catch (Throwable e) {
            throw new AccessException("[update]: error", e);
        }
        return result;
    }

    @Override
    public int delete(UserGroupRelation userGroupRelation) throws AccessException {
        int result = 0;
        try {
            result = sqlMapClients.getSqlMapClientMeta().delete(
                    "UserGroupRelation.delete", userGroupRelation);
        } catch (Throwable e) {
            throw new AccessException("[delete]: error", e);
        }
        return result;
    }
}
