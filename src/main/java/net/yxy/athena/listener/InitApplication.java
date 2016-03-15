package net.yxy.athena.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.yxy.athena.global.Synchronizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

public class InitApplication implements ServletContextListener {
	
	static private Logger logger = LoggerFactory.getLogger(InitApplication.class); 
	private Synchronizer synchronizer = Synchronizer.createInstance() ;
	
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("Startup Synchronizer");
		synchronizer.start();
		
		launchNoSQLEmbeddedDB() ;
		
	}

	private void launchNoSQLEmbeddedDB() {
		ODatabaseDocumentTx database = new ODatabaseDocumentTx("plocal:/temp/db").open("admin", "admin");
		
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("Shutdown Synchronizer");
		synchronizer.shutdown() ;
	}

}
