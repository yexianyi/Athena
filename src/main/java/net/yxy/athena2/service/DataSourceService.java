package net.yxy.athena2.service;

import java.util.Map;
import java.util.Set;

import com.yxy.chukonu.docker.client.conn.DockerConnection;
import com.yxy.chukonu.docker.service.SystemService;
import com.yxy.chukonu.redis.model.dao.RedisDao;

import net.yxy.athena2.model.entity.NodeServerEntity;

public class DataSourceService {
	private SystemService ss ;
	private NodeServerService nss = new NodeServerService() ;
	private RedisDao dao = new RedisDao() ;
	
	
	/**
	 * this method will be run in an independent thread.
	 * @param dsName
	 * @param dsType
	 * @param connMap
	 */
	public void allocateNodeServer(String dsName, String dsType, Map<String, String> connMap) {
		Map<String, Map<String, String>> nodeServer = nss.getAllNodeServers() ;
		for(NodeServerEntity server : nodeServer) {
			String addr = server.getAddr() ;
			//TODO: send rest request to each node server to get delay duration.
			float delay = (float) Math.random() ;
			saveAndUpdateDSNodeServerList(dsName, delay, addr);
		}//end for
		
		
	}
	
	public void saveAndUpdateDSNodeServerList(String dsName, float delay, String addr) {
		dao.insertSortedSet(dsName, delay, addr);
	}
	
	/**
	 * Choose best node server to be set up container
	 * Strategy: 
	 * 		1. response delay time  
	 * 		2. CPU usage percentage
	 * 		3. If CPU usage >= 95%, then find 2nd shortest delay time server
	 * 		4. If all servers' CPU usage > 95%, then find a server randomly.
	 * @param dsName
	 * @return
	 */
	public String findBestNodeServer(String dsName) {
		Set<String> candidates = dao.getSortedSet(dsName) ;
		for(String sAddr:candidates) {
			DockerConnection conn = new DockerConnection(sAddr, "2376", null) ;
			ss = new SystemService(conn) ;
			//get total cpu usage percent of current server
		}
		
		
		return null;
		
		
	}
}
