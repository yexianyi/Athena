/**
 * Copyright (c) 2016, Xianyi Ye
 *
 * This project includes software developed by Xianyi Ye
 * yexianyi@hotmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.yxy.athena.service.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import com.orientechnologies.orient.core.record.impl.ODocument;

import net.yxy.athena.db.EmbeddedDBServer;


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
		EmbeddedDBServer.acquire() ;
		
		
		ODocument doc = new ODocument("Person");
		doc.field("name", "test");
		doc.field("surname", "test");
		doc.field("city", new ODocument("City").field("name", "Rome").field("country", "Italy"));
		doc.save();
		
		
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
