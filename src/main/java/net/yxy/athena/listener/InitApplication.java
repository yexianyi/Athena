package net.yxy.athena.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import net.yxy.athena.global.Synchronizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitApplication implements ServletContextListener {
	
	static private Logger logger = LoggerFactory.getLogger(InitApplication.class); 
	private Synchronizer synchronizer = Synchronizer.createInstance() ;
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("Startup Synchronizer");
		synchronizer.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("Shutdown Synchronizer");
		synchronizer.shutdown() ;
	}

}
