package net.yxy.athena2.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena.global.Constants;
import net.yxy.athena2.service.NodeServerService;

public class NodeServerServiceTest {
	
	private RedisDao dao = new RedisDao() ;
	private NodeServerService nss = null ;
	
	@Before
	public void before() {
		dao.clearDB(); 
		nss = new NodeServerService() ;
	}
	
	@Test
	public void testInitNodeServer() {
		String[] servers = {"192.168.99.101","192.168.99.102","192.168.99.103"} ;
		nss.initNodeServerInfo(servers);
		
		Map<String, Map<String, String>> res =  nss.getAllNodeServers() ;
		assertEquals(servers.length, res.size()) ;
		for(String addr:servers) {
			assertEquals(addr, res.get(Constants.NODE_SERVER_KEY+addr).get(Constants.NODE_SERVER_ADDR_KEY));
		}
		
	}
	
	
	@Test
	public void testGetNodeServer() {
		String[] servers = {"192.168.99.101","192.168.99.102","192.168.99.103"} ;
		nss.initNodeServerInfo(servers);
		
		assertEquals("192.168.99.101", nss.getServerByName("192.168.99.101").get(Constants.NODE_SERVER_ADDR_KEY));
		assertNull(nss.getServerByName("192.168.99.104"));
		
	}
	
	
	@Test
	public void testGetAllNodeServerAddrs() {
		String[] servers = {"192.168.99.101","192.168.99.102","192.168.99.103"} ;
		nss.initNodeServerInfo(servers);
		Set<String> addrs = nss.getAllServerAddrs() ;
		assertEquals(servers.length, addrs.size());
		for(String add:servers) {
			assertTrue(addrs.contains(add));
		}
		
	}
	
	@Test
	public void testGetAvilableNodeServer() {
		String[] servers = {"192.168.99.101","192.168.99.102","192.168.99.103"} ;
		nss.initNodeServerInfo(servers);
		Map<String, Map<String, String>> availServers = nss.getAvailableNodeServers() ;
		assertEquals(servers.length, availServers.size());
		
	}

}
