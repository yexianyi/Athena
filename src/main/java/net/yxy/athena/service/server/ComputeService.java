package net.yxy.athena.service.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;


public class ComputeService {
	private final NovaApi novaApi;
	private final Set<String> regions;
	
	public ComputeService() {
		Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());

		String provider = "openstack-nova";
		String identity = "admin:admin"; // tenantName:userName
		String credential = "admin";

		novaApi = ContextBuilder.newBuilder(provider).endpoint("http://10.140.253.30:5000/v2.0/")
				.credentials(identity, credential).modules(modules).buildApi(NovaApi.class);
		regions = novaApi.getConfiguredRegions();
	}
	
	public List<Server> listServers() {
		List<Server> serverList = new ArrayList<Server>() ;
		for (String region : regions) {
			ServerApi serverApi = novaApi.getServerApi(region);

			System.out.println("Servers in region:" + region);
			
			for (Server server : serverApi.listInDetail().concat()) {
				System.out.println("  " + server);
				serverList.add(server) ;
			}
		}
		
		return serverList;
	}
	

	public void close() throws IOException {
		Closeables.close(novaApi, true);
	}
	

	public static void main(String[] args) throws IOException {
		ComputeService jcloudsNova = new ComputeService();

		try {
			jcloudsNova.listServers();
			jcloudsNova.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jcloudsNova.close();
		}
	}

}
