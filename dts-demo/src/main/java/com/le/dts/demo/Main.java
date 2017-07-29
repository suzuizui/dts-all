package com.le.dts.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by gaobo3 on 2016/4/1.
 */
public class Main {
    private static final Log logger = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dts.xml");
        logger.info("Main End");
    }
}
