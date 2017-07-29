package com.le.dts.demo.processor;

import com.le.dts.client.executor.job.processor.SimpleJobProcessor;
import com.le.dts.client.executor.simple.processor.SimpleJobContext;
import com.le.dts.common.domain.result.ProcessResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gaobo3 on 2016/4/1.
 */
public class DemoProcessor implements SimpleJobProcessor {
    private static final Log logger = LogFactory.getLog(DemoProcessor.class);

    @Override
    public ProcessResult process(SimpleJobContext context) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("DemoProcessor Process: " + format.format(new Date()));
        return new ProcessResult(true);
    }
}
