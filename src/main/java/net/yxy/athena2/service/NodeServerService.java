package net.yxy.athena2.service;

import java.util.Map;
import java.util.Set;

import net.yxy.athena2.model.entity.NodeServerEntity;

public class NodeServerService {

	private RedisService rs = new RedisService() ;
	
	public void findProperServer(String dsType, Map<String, String> connMap) {
		Set<NodeServerEntity> nodeServer = rs.getNodeServers() ;
		for(NodeServerEntity server : nodeServer) {
			String addr = server.getAddr() ;
			//TODO: send rest request to each node server; after that an response time is returned.
			float delay = (float) Math.random() ;
		}
	}
	
}
