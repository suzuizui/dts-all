package com.le.dts.console.module.screen;

import com.alibaba.citrus.turbine.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by luliang on 15/1/26.
 */
public class SDKManager {

    private static final Log logger = LogFactory.getLog(SDKManager.class);
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    public void execute(Context context) throws IOException {

        logger.info("[SDKManager]url:" + request.getRequestURL());
    }
}
