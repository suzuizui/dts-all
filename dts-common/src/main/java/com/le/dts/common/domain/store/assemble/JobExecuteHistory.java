package com.le.dts.common.domain.store.assemble;

import com.le.dts.common.remoting.protocol.RemotingSerializable;

import java.util.TreeMap;

/**
 * Job的历史执行纪录;
 * Created by luliang on 14/12/29.
 */
public class JobExecuteHistory {

    private long jobId;

    private TreeMap<String, String> historyRecords = new TreeMap<String, String>();

    public TreeMap<String/** 按时间戳排序 */, String> getHistoryRecords() {
        return historyRecords;
    }

    public void setHistoryRecords(TreeMap<String, String> historyRecords) {
        this.historyRecords = historyRecords;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public static JobExecuteHistory newInstance(String json) {
        return RemotingSerializable.fromJson(json, JobExecuteHistory.class);
    }

    /**
     * 对象转换成json
     */
    @Override
    public String toString() {
        return RemotingSerializable.toJson(this, false);
    }
}
