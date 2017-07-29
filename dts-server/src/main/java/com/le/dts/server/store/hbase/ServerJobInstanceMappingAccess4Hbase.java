package com.le.dts.server.store.hbase;

import java.util.List;

import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.exception.AccessException;
import com.le.dts.server.store.ServerJobInstanceMappingAccess;

/**
 * Created by Moshan on 14-12-15.
 */
public class ServerJobInstanceMappingAccess4Hbase implements ServerJobInstanceMappingAccess {
    @Override public long insert(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException {
        return 0;
    }

    @Override public void update(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException {

    }

    @Override
	public int delete(ServerJobInstanceMapping serverJobInstanceMapping)
			throws AccessException {
    	return 0;
	}

	@Override public ServerJobInstanceMapping findByServer(String server) throws AccessException {
        return null;
    }

	@Override
	public List<ServerJobInstanceMapping> loadByServer(String server, long id)
			throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
