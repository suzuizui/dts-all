package com.le.dts.demo.processor;

import com.le.dts.client.executor.job.processor.ParallelJobProcessor;
import com.le.dts.client.executor.parallel.processor.ParallelJobContext;
import com.le.dts.common.domain.DtsState;
import com.le.dts.common.domain.result.ProcessResult;
import com.le.dts.common.domain.result.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaobo3 on 2016/4/1.
 */
public class DemoParallelProcessor implements ParallelJobProcessor {
    private static final Log logger = LogFactory.getLog(DemoParallelProcessor.class);

    @Override
    public ProcessResult process(ParallelJobContext context) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Object task = context.getTask();
        if (DtsState.START.equals(task)) {
            logger.info("DemoParallelProcessor[一级任务] -- Time: " + format.format(new Date()));

            List<String> tablePartition = new ArrayList<String>();
            tablePartition.add("table_0001");
            tablePartition.add("table_0002");
            tablePartition.add("table_0003");
            tablePartition.add("table_0004");
            Result<Boolean> result = context.dispatchTaskList(tablePartition, "二级任务");
            return new ProcessResult(result.getData());
        } else if (task instanceof String) {
            String tablePartition = (String)task;
            logger.info("DemoParallelProcessor[二级任务] -- Task: " + tablePartition + ", Time: " + format.format(new Date()));

            List<DemoEntity> taskList = new ArrayList<DemoEntity>();
            taskList.add(new DemoEntity(tablePartition, "data_0001"));
            taskList.add(new DemoEntity(tablePartition, "data_0002"));
            taskList.add(new DemoEntity(tablePartition, "data_0003"));
            taskList.add(new DemoEntity(tablePartition, "data_0004"));
            Result<Boolean> result = context.dispatchTaskList(taskList, "三级任务");
        } else if (task instanceof DemoEntity) {
            DemoEntity entity = (DemoEntity)task;
            logger.info("DemoParallelProcessor[三级任务] -- Task: " + entity + ", Time: " + format.format(new Date()));
        }
        return new ProcessResult(true);
    }

    static class DemoEntity implements Serializable {
        private String tableName;
        private String data;

        public DemoEntity(String tableName, String data) {
            this.tableName = tableName;
            this.data = data;
        }

        public String toString() {
            return "{tableName: " + tableName + ", data: " + data + "}";
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
