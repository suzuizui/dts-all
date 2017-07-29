package com.le.dts.console.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.store.DesignatedMachine;
import com.le.dts.common.exception.InitException;
import com.le.dts.console.store.DesignatedMachineAccess;

/**
 * Created by luliang on 15/3/10.
 */
public class DesignatedMachineManager implements Constants {

    @Autowired
    private DesignatedMachineAccess designatedMachineAccess;

    /**
     * 加载指定机器列表
     * @param jobId
     * @return
     * @throws com.le.dts.common.exception.AccessException
     */
    public List<DesignatedMachine> loadDesignatedMachineList(long jobId) throws InitException {
        DesignatedMachine query = new DesignatedMachine();
        query.setJobId(jobId);
        List<DesignatedMachine> designatedMachineList = null;
        try {
            designatedMachineList = designatedMachineAccess.queryDesignatedMachineListById(query);
        } catch (Throwable e) {
            throw new InitException("[DesignatedMachineManager]: queryDesignatedMachineListById error, jobId:" + jobId, e);
        }
        return designatedMachineList;
    }

    /**
     * 指定机器列表;
     * @param jobId
     * @return
     */
    public boolean deleteDesignnatedMathineList(long jobId) throws InitException {
        DesignatedMachine query = new DesignatedMachine();
        query.setJobId(jobId);
        try {
            designatedMachineAccess.deleteByJobId(query);
            return true;
        } catch (Throwable e) {
            throw new InitException("[DesignatedMachineManager]: deleteDesignatedMachine error, jobId:" + jobId, e);
        }
    }

    /**
     * 创建一条记录;
     * @param designatedMachine
     * @return
     * @throws InitException
     */
    public boolean createDesignnatedMathineList(DesignatedMachine designatedMachine) throws InitException {
        try {
            long result = designatedMachineAccess.insert(designatedMachine);
            if(result == 0) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            throw new InitException("[DesignatedMachineManager]: createDesignatedMachine error, jobId:" + designatedMachine.getJobId(), e);
        }
    }

}
