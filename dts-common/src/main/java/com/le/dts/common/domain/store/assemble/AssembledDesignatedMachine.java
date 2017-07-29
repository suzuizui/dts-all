package com.le.dts.common.domain.store.assemble;

/**
 * Created by luliang on 15/3/10.
 */
public class AssembledDesignatedMachine {

    /** 客户端集群ID */
    private long clientGroupId;

    /** job的ID */
    private long jobId;

    /** 机器信息 */
    private String machine;

    /** 策略 */
    private int policy;

    /** 是否指定机器 */
    private boolean isDesignatedMachine = false;

    public long getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(long clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public int getPolicy() {
        return policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
    }

    public boolean isDesignatedMachine() {
        return isDesignatedMachine;
    }

    public void setDesignatedMachine(boolean isDesignatedMachine) {
        this.isDesignatedMachine = isDesignatedMachine;
    }

    @Override
    public String toString() {
        return "DesignatedMachine [clientGroupId="
                + clientGroupId + ", jobId=" + jobId + ", isDesignatedMachine=" + isDesignatedMachine
                + ", machine=" + machine
                + ", policy=" + policy + "]";
    }
}
