package net.yxy.athena.rest.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.nova.v2_0.domain.Server;

import net.yxy.athena.service.server.ComputeService;

@Path("/admin/server")
public class ServerService {
	
	private ComputeService cs = new ComputeService() ;
	
	@GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
	public List<Server> listServers() {
		return cs.listServers() ;
	}


}
