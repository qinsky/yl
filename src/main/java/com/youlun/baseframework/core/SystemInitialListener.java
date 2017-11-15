package com.youlun.baseframework.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class SystemInitialListener implements ServletContextListener {
	Logger log = Logger.getLogger(ServletContextEvent.class);
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub  
		SpringContext.initialContext(sce.getServletContext());
		System.getProperties().put("ul.app.path", sce.getServletContext().getRealPath("."));
		//ProcessEngines.init();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		//ProcessEngines.destroy();
	} 
 
}
