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
import net.yxy.athena2.model.entity.NodeServerEntity;
import net.yxy.athena2.service.NodeServerService;

public class NodeServerServiceTest extends BaseTest{
	
	private RedisDao dao = new RedisDao() ;
	private NodeServerService nss = null ;
	
	@Before
	public void before() {
		dao.clearDB(); 
		nss = new NodeServerService() ;
	}
	
	@Test
	public void testInitNodeServer() {
		NodeServerEntity[] servers = {host1, host2, host3} ;
		nss.addNodeServersInfo(servers);
		
		Map<String, Map<String, String>> res =  nss.getAllNodeServers() ;
		assertEquals(servers.length, res.size()) ;
		for(NodeServerEntity server:servers) {
			assertEquals(server.getAddr(), res.get(Constants.NODE_SERVER_KEY+server.getAddr()).get(Constants.NODE_SERVER_ADDR_KEY));
		}
		
	}
	
	
	@Test
	public void testGetNodeServer() {
		NodeServerEntity[] servers = {host1, host2, host3} ;
		nss.addNodeServersInfo(servers);
		
		assertEquals("192.168.99.101", nss.getServerByName("192.168.99.101").get(Constants.NODE_SERVER_ADDR_KEY));
		assertNull(nss.getServerByName("192.168.99.104"));
		
	}
	
	
	@Test
	public void testGetAllNodeServerAddrs() {
		NodeServerEntity[] servers = {host1, host2, host3} ;
		nss.addNodeServersInfo(servers);
		Set<String> addrs = nss.getAllServerAddrs() ;
		assertEquals(servers.length, addrs.size());
		for(NodeServerEntity add:servers) {
			assertTrue(addrs.contains(add));
		}
		
	}
	
	@Test
	public void testGetAvilableNodeServer() {
		NodeServerEntity[] servers = {host1, host2, host3} ;
		nss.addNodeServersInfo(servers);
		Map<String, Map<String, String>> availServers = nss.getAvailableNodeServers() ;
		assertEquals(servers.length, availServers.size());
		
	}

}
