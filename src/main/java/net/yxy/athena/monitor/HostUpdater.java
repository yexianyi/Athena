package net.yxy.athena.monitor;

import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.jclouds.openstack.nova.v2_0.domain.Server;

import net.yxy.athena.service.server.ComputeService;

public class HostUpdater implements Runnable {

	private ComputeService cs = new ComputeService() ;
	
	@Override
	public void run() {
		List<Server> list = cs.listServers() ;
		Response.ResponseBuilder response = Response.ok(list).type(MediaType.APPLICATION_JSON);
		String jsonRsp = response.build().getEntity().toString() ;
		System.out.println(jsonRsp) ;
		
	}
	
	
	public static void main(String[] args){
		HostUpdater updater  = new HostUpdater() ;
		Thread th = new Thread(updater) ;
		th.start();
	}

}
