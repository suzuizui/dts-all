package com.le.dts.console.login.filter;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("springFactory")
public class SpringBeanContext implements ApplicationContextAware {

    private Logger log = Logger.getLogger(SpringBeanContext.class);
    
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
	   return SpringBeanContext.applicationContext;
	}

	@Resource
	public void setApplicationContext(ApplicationContext context)
	     throws BeansException {

		SpringBeanContext.applicationContext = context;
		log.info(">>>>>>>>>>>>>>>>>ApplicationContext registed:"
				+ applicationContext);
	}
    
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}


}
