package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.ServerJobInstanceMapping;
import com.le.dts.common.exception.AccessException;

/**
 * Created by Moshan on 14-12-15.
 */
public interface ServerJobInstanceMappingAccess {

    public long insert(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException;

    public void update(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException;
    
    public int delete(ServerJobInstanceMapping serverJobInstanceMapping) throws AccessException;

    public ServerJobInstanceMapping findByServer(String server) throws AccessException;
    
    public List<ServerJobInstanceMapping> loadByServer(String server, long id) throws AccessException;

}
