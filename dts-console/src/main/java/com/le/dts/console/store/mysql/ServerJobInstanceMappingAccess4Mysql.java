package com.le.dts.console.store.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.le.dts.console.store.ServerJobInstanceMappingAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.exception.AccessException;

/**
 * Created by Moshan on 14-12-15.
 */
public class ServerJobInstanceMappingAccess4Mysql implements ServerJobInstanceMappingAccess {

	@Autowired
	private SqlMapClients sqlMapClients;
	
    @Override public long insert(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException {
    	
        Long result = null;
        try {
            result = (Long) sqlMapClients.getSqlMapClientMeta()
                    .insert("ServerJobInstanceMapping.insert", serverJobInstanceMapping);
        } catch (Throwable e) {
            throw new AccessException("[insert]: error", e);
        }
        if (null == result) {
        	
            return 0L;
        }
        
        return result;

    }

    @Override public void update(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException {
        try {
            sqlMapClients.getSqlMapClientMeta()
                    .update("ServerJobInstanceMapping.update", serverJobInstanceMapping);
        } catch (Throwable e) {
            throw new AccessException("[update]: error", e);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
	public int delete(ServerJobInstanceMapping serverJobInstanceMapping)
			throws AccessException {
    	
    	int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("ServerJobInstanceMapping.delete", serverJobInstanceMapping);
		} catch (Throwable e) {
			throw new AccessException("[delete]: error", e);
		}
		
		return result;
	}

	@Override public ServerJobInstanceMapping findByServer(String server) throws AccessException {
        Map<String, String> queryObj = new HashMap<String, String>();
        queryObj.put("server", server);
        ServerJobInstanceMapping result = null;
        try {
            result = (ServerJobInstanceMapping)sqlMapClients.getSqlMapClientMeta()
                    .queryForObject("ServerJobInstanceMapping.findByServer", queryObj);
        } catch (Throwable e) {
            throw new AccessException("[query]: error", e);
        }
        return result;
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<ServerJobInstanceMapping> loadByServer(String server, long id)
			throws AccessException {

		Map<String, Object> queryObj = new HashMap<String, Object>();
        queryObj.put("server", server);
        queryObj.put("id", id);

        List<ServerJobInstanceMapping> mappingList = null;
		try {
			mappingList = (List<ServerJobInstanceMapping>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("ServerJobInstanceMapping.loadByServer", queryObj);
		} catch (Throwable e) {
			throw new AccessException("[loadByServer]: error", e);
		}
		return mappingList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServerJobInstanceMapping> loadAll(long id)
			throws AccessException {
		
		Map<String, Object> queryObj = new HashMap<String, Object>();
        queryObj.put("id", id);

        List<ServerJobInstanceMapping> mappingList = null;
		try {
			mappingList = (List<ServerJobInstanceMapping>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("ServerJobInstanceMapping.loadAll", queryObj);
		} catch (Throwable e) {
			throw new AccessException("[loadAll]: error", e);
		}
		return mappingList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServerJobInstanceMapping> selectLimit() throws AccessException {
		
		Map<String, Object> queryObj = new HashMap<String, Object>();
		
		List<ServerJobInstanceMapping> mappingList = null;
		try {
			mappingList = (List<ServerJobInstanceMapping>)sqlMapClients.getSqlMapClientMeta()
					.queryForList("ServerJobInstanceMapping.selectLimit", queryObj);
		} catch (Throwable e) {
			throw new AccessException("[selectLimit]: error", e);
		}
		return mappingList;
	}

	@Override
	public int deleteById(ServerJobInstanceMapping serverJobInstanceMapping)
			throws AccessException {
		
		int result = 0;
		try {
			result = sqlMapClients.getSqlMapClientMeta()
					.update("ServerJobInstanceMapping.deleteById", serverJobInstanceMapping);
		} catch (Throwable e) {
			throw new AccessException("[deleteById]: error", e);
		}
		
		return result;
	}
	
}
