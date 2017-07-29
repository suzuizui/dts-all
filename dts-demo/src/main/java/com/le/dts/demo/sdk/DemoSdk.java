package com.le.dts.demo.sdk;

import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.store.assemble.JobStatus;
import com.le.dts.common.fastjson.JSON;
import com.le.dts.sdk.DtsCommonSDKManager;
import com.le.dts.sdk.DtsSDKManager;
import com.le.dts.sdk.SDKMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by gaobo3 on 2016/4/28.
 */
public class DemoSdk {
    private static final Log logger = LogFactory.getLog(DemoSdk.class);

    public static void main(String[] args) throws Exception {
        String groupId = "1-1-2-12";
        int jobId = 36;
        DtsSDKManager dtsSDKManager = new DtsCommonSDKManager(SDKMode.DAILY_MODE);
        dtsSDKManager.instanceRunJob(groupId, jobId);
        Result<JobStatus> jobStatusResult = dtsSDKManager.getJobRunningStatus(jobId);
        JobStatus jobStatus = jobStatusResult.getData();
        logger.info("JobStatus: " + JSON.toJSONString(jobStatus));
        Thread.sleep(20 * 1000);
        jobStatusResult = dtsSDKManager.getJobRunningStatus(8);
        jobStatus = jobStatusResult.getData();
        logger.info("JobStatus: " + JSON.toJSONString(jobStatus));
    }
}
