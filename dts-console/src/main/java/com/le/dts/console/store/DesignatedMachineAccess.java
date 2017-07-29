package com.le.dts.console.store;

import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.exception.AccessException;

import java.util.List;

/**
 * Created by luliang on 15/3/10.
 */
public interface DesignatedMachineAccess {
    /**
     * 插入
     * @param designatedMachine
     * @return
     * @throws com.alibaba.dts.common.exception.AccessException
     */
    public long insert(DesignatedMachine designatedMachine) throws AccessException;

    /**
     * 根据JobId查询指定机器列表
     * @param query
     * @return
     * @throws AccessException
     */
    public List<DesignatedMachine> queryByJobId(DesignatedMachine query) throws AccessException;

    /**
     * 根据Id查询指定机器列表
     * @param query
     * @return
     * @throws AccessException
     */
    public List<DesignatedMachine> queryDesignatedMachineListById(DesignatedMachine query) throws AccessException;

    /**
     * 根据JobId删除指定机器列表
     * @param designatedMachine
     * @return
     * @throws AccessException
     */
    public int deleteByJobId(DesignatedMachine designatedMachine) throws AccessException;
}
