package com.le.dts.console.store.mysql;

import java.util.List;

import com.le.dts.console.store.ServerGroupAccess;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.ServerGroup;
import com.le.dts.common.exception.AccessException;
import com.le.dts.console.store.mysql.access.SqlMapClients;

public class ServerGroupAccess4Mysql implements ServerGroupAccess {

	@Autowired
	private SqlMapClients sqlMapClients;
	
	@Override
	public long insert(ServerGroup serverGroup) throws AccessException {
		Long result = null;
		try {
			result = (Long)sqlMapClients.getSqlMapClientMeta()
					.insert("ServerGroup.insert", serverGroup);
		} catch (Throwable e) {
			throw new AccessException("[insert]: error", e);
		}
		if(null == result) {
			return 0L;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServerGroup> query(ServerGroup query) throws AccessException {
		List<ServerGroup> serverGroupList = null;
		try {
			serverGroupList = (List<ServerGroup>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("ServerGroup.query", query);
		} catch (Throwable e) {
			throw new AccessException("[query]: error", e);
		}
		return serverGroupList;
	}

    @Override
    public ServerGroup queryById(ServerGroup query) throws AccessException {
        ServerGroup serverGroup = null;
        try {
            serverGroup = (ServerGroup)sqlMapClients.getSqlMapClientMeta()
                    .queryForObject("ServerGroup.queryById", query);
        } catch (Throwable e) {
            throw new AccessException("[queryById]: error", e);
        }
        return serverGroup;
    }

    @Override
	public int update(ServerGroup serverGroup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("ServerGroup.update", serverGroup);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

	@Override
	public int delete(ServerGroup serverGroup) throws AccessException {
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.delete("ServerGroup.delete", serverGroup);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		return result;
	}

}
