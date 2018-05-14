package net.yxy.athena2.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.yxy.athena2.model.entity.NodeServerEntity;
import net.yxy.athena2.service.RedisService;

public class RedisServiceTest {
	
	private RedisService rs = null ;
	
	@Before
	public void before() {
		rs = new RedisService() ;
	}
	
	@Test
	public void testInitNodeServer() {
		String[] servers = {"192.168.99.101","192.168.99.102","192.168.99.103"} ;
		RedisService rs = new RedisService() ;
		rs.initNodeServerInfo(servers);
		
		Set<NodeServerEntity> res =  rs.getNodeServers() ;
		assertEquals(servers.length, res.size()) ;
		for(NodeServerEntity server:res) {
			assertNotNull(server.getName());
			assertNotNull(server.getAddr());
			assertNotNull(server.getStatus());
		}
		
	}

}
