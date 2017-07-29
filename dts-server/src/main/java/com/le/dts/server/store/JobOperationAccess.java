package com.le.dts.server.store;

import java.util.List;

import com.le.dts.common.domain.store.JobOperation;
import com.le.dts.common.exception.AccessException;

/**
 * Job操作访问接口
 */
public interface JobOperationAccess {

    /**
     * 插入;
     * @param jobOperation
     * @return
     * @throws AccessException
     */
    public long insert(JobOperation jobOperation) throws AccessException;

    /**
     * 根据server查询;
     * @param jobOperation
     * @return
     * @throws AccessException
     */
    public List<JobOperation> queryByServer(JobOperation jobOperation) throws AccessException;

    /**
     * 根据ID删除;
     * @param jobOperation
     * @return
     * @throws AccessException
     */
    public Integer deleteById(JobOperation jobOperation) throws AccessException;
}
