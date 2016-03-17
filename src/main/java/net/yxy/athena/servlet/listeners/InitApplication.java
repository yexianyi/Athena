package net.yxy.athena.servlet.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.yxy.athena.db.EmbeddedDBServer;
import net.yxy.athena.global.Synchronizer;

public class InitApplication implements ServletContextListener {
	
	static private Logger logger = LoggerFactory.getLogger(InitApplication.class); 
	private Synchronizer synchronizer = Synchronizer.createInstance() ;
	
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		synchronizer.start();
		EmbeddedDBServer.startup() ;
		EmbeddedDBServer.importSeedData();
		
	}

	private void startupOrientDB() {
//		ODatabaseDocumentTx database = new ODatabaseDocumentTx("plocal:/temp/db").open("admin", "admin");
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		synchronizer.shutdown() ;
		EmbeddedDBServer.shutdown() ;
	}

}
