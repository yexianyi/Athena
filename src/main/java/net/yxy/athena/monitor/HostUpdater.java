package net.yxy.athena.monitor;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.yxy.athena.db.EmbeddedDBServer;
import net.yxy.athena.service.server.ComputeService;

import org.jclouds.openstack.nova.v2_0.domain.Server;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class HostUpdater implements Runnable {

	private ComputeService cs = new ComputeService() ;
	
	@Override
	public void run() {
		List<Server> list = cs.listServers() ;
		Response.ResponseBuilder response = Response.ok(list).type(MediaType.APPLICATION_JSON);
		String jsonRsp = response.build().getEntity().toString() ;
		
		System.out.println(jsonRsp) ;
		EmbeddedDBServer.acquire() ;
		
		ODocument doc = new ODocument("Person");
		doc.field("name", "test");
		doc.field("surname", "test");
		doc.field("city", new ODocument("City").field("name", "Rome").field("country", "Italy"));
		doc.save();
		
		
	}
	
	
	public static void main(String[] args){
		HostUpdater updater  = new HostUpdater() ;
		Thread th = new Thread(updater) ;
		th.start();
	}

}
