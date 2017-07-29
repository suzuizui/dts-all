package com.le.dts.console.store.mysql;

import java.util.List;

import com.le.dts.console.store.JobOperationAccess;
import com.le.dts.console.store.mysql.access.SqlMapClients;
import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.domain.store.JobOperation;
import com.le.dts.common.exception.AccessException;

/**
 * Created by luliang on 15/1/15.
 */
public class JobOperationAccess4Mysql implements JobOperationAccess {

    @Autowired
    private SqlMapClients sqlMapClients;

    @Override
    public long insert(JobOperation jobOperation) throws AccessException {
        Long result = null;
        try {
            result = (Long)sqlMapClients.getSqlMapClientMeta()
                    .insert("JobOperation.insert", jobOperation);
        } catch (Throwable e) {
            throw new AccessException("[insert]: error", e);
        }
        if(null == result) {
            return 0L;
        }
        return result;
    }

    @Override
    public List<JobOperation> queryByServer(JobOperation jobOperation) throws AccessException {
        List<JobOperation> jobOperationList = null;
        try {
            jobOperationList = (List<JobOperation>)sqlMapClients.getSqlMapClientMeta()
                    .queryForList("JobOperation.queryByServer", jobOperation);
        } catch (Throwable e) {
            throw new AccessException("[queryByServer]: error", e);
        }
        return jobOperationList;
    }

    @Override
    public Integer deleteById(JobOperation jobOperation) throws AccessException {
        int result = 0;
        try {
            result = (Integer)sqlMapClients.getSqlMapClientMeta()
                    .delete("JobOperation.delete", jobOperation);
        } catch (Throwable e) {
            throw new AccessException("[delete]: error", e);
        }
        return result;
    }
}
