package net.yxy.athena.monitor;

import java.util.List;

import net.yxy.athena.db.EmbeddedDBServer;
import net.yxy.athena.db.util.JSONUtil;
import net.yxy.athena.service.server.ComputeService;

import org.jclouds.openstack.nova.v2_0.domain.Server;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.OCommandSQL;

public class HostUpdater implements Runnable {

	private ComputeService cs = new ComputeService() ;
	
	@Override
	public void run() {
		ComputeService cs = new ComputeService() ;
		List<Server> list = cs.listServers() ;
		for(Server server: list){
			String json = JSONUtil.convertObj(server) ;
			ODatabaseDocumentTx db = EmbeddedDBServer.acquire() ;
			db.command(new OCommandSQL("insert into Server content "+json)) ;
		}
	
		
	}
	
	
	public static void main(String[] args){
		HostUpdater updater  = new HostUpdater() ;
		Thread th = new Thread(updater) ;
		th.start();
	}

}
