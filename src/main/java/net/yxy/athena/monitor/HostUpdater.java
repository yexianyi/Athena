package net.yxy.athena.monitor;

import java.util.List;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import net.yxy.athena.db.EmbeddedDBServer;
import net.yxy.athena.global.Constants;
import net.yxy.athena.service.server.ComputeService;
import net.yxy.athena.util.JSONUtil;

public class HostUpdater extends Thread {

	static private Logger logger = LoggerFactory.getLogger(HostUpdater.class); 
	private ComputeService cs = new ComputeService() ;
	
	private volatile boolean isShutdown = false ;
	
	@Override
	public void run() {
		
//		while(!Thread.currentThread().isInterrupted() && !isShutdown){
			List<Server> list = cs.listServers() ;
			for(Server server: list){
				String json = JSONUtil.convertObj(server) ;
				ODatabaseDocumentTx db = EmbeddedDBServer.acquire() ;
				db.command(new OCommandSQL("insert into Server content "+json)) ;
				logger.debug("insert into Server content "+json);
			}
			
			try {
				Thread.sleep(Constants.SYN_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
	
		
	}
	
	public void shutdown(){
		isShutdown = true ;
	}
	
	
	public static void main(String[] args){
		HostUpdater updater  = new HostUpdater() ;
		Thread th = new Thread(updater) ;
		th.start();
	}

}
